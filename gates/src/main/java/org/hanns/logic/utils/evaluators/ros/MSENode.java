package org.hanns.logic.utils.evaluators.ros;


import java.util.LinkedList;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * This node serves for supervised learning by EA, it has two inputs of dim "dimension". 
 * One receives expected values, the other one receives data from the designed system. 
 * The node computes MSE and its prosperity output value is defined as 1-MSE. 
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class MSENode extends AbstractConfigurableHannsNode{

	public static final String name = "MSENode";

	// how many layers has the feed forward network that is used?
	public static final String depthConf = "depth";
	public static final String topicDepth = conf+depthConf;
	public static final int DEF_DEPTH = 1;

	protected int depth;
	protected int dataSize;
	protected int step = 0;

	protected ProsperityObserver o;						// observes the prosperity of node

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		log = connectedNode.getLog();

		log.info(me+"started, parsing parameters");
		this.registerParameters();
		paramList.printParams();
		this.parseParameters(connectedNode);
		this.registerObservers();

		System.out.println(me+"initializing ROS Node IO");

		this.registerSimulatorCommunication(connectedNode);
		this.buildProsperityPublisher(connectedNode);
		this.buildDataIO(connectedNode);

		super.fullName = super.getFullName(connectedNode);

		step = 0;
		System.out.println(me+"Node configured and ready now!");
	}

	public abstract void onNewDataReceived(float[] data);

	
	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		this.registerProsperityObserver();
	}

	/**
	 * Instatntiate the Observer {@link #o} to the resider one. 
	 */
	protected abstract void registerProsperityObserver();

	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Dimension of input data (MSE is computed accross this vector");
		paramList.addParam(depthConf, ""+DEF_DEPTH, "Depth of the feedforward network (synchronization of data)");
		
		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		// RL parameters (default alpha and gamma, but can be also modified online)
		depth = r.getMyInteger(depthConf, DEF_DEPTH);
		if(depth<=0){
			System.err.println("Incorrect depth of the network, will use the default one: "+DEF_DEPTH);
			depth = DEF_DEPTH;
		}
		dataSize = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		
		if(dataSize<=0){
			System.err.println("Incorrect dimension of the data, will use the default one: "+DEF_NOINPUTS);
			dataSize = DEF_NOINPUTS;
		}
		System.out.println(me+"Creating data structures.");
	}


	/**
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				//System.err.println("RECEIVED data of value.. "+SL.toStr(data));

				if(data.length != dataSize)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+ dataSize);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0  && step >0)
						System.out.println(me+"<-"+topicDataIn+" Received new data &" +
								" state description "+SL.toStr(data));

					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	/**
	 * @param connectedNode
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){
	}

	/**
	 * If the prosperity observer has no childs, publish its value. 
	 * If the prosperity observer has childs, publish its value on the first
	 * position and values of its childs in the vector.
	 */
	@Override
	public void publishProsperity(){

		float[] data;
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();

		if(o.getChilds() == null){
			data = new float[]{o.getProsperity()};
		}else{
			ProsperityObserver[] childs = o.getChilds();	
			data = new float[childs.length+1];
			data[0] = o.getProsperity();

			for(int i=0; i<childs.length; i++){
				data[i+1] = childs[i].getProsperity();
			}
		}
		fl.setData(data);
		prospPublisher.publish(fl);
	}

	@Override
	public ProsperityObserver getProsperityObserver() { return o; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(prospPublisher==null)
			return false;
		if(dataPublisher==null)
			return false;
		if(this.o == null)
			return false;
		return true;
	}

	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public LinkedList<Observer> getObservers() { return null; }

	private boolean lg = false;
	public void logg(String what) {
		if(lg)
			System.out.println(" ------- "+what);		
	}
}


