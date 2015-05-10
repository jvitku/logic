package org.hanns.logic.utils.evaluators.ros;


import java.util.ArrayList;
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
public class MSENode extends AbstractConfigurableHannsNode{

	public static final String name = "MSENode";

	public static final String topicDataInSupervised = "topicDataInSupervised";

	// how many layers has the feed forward network that is used?
	public static final String delayConf = "delay";
	public static final int DEF_DELAY = 1;

	protected int delay;
	protected int dataSize;
	protected int step = 0;
	protected float mse = 0;
	protected float[] data; //expected;//data, 

	protected ArrayList<float[]> expectedBuffer;
	protected int dataBufferLen = 10; 
	
	public boolean dataReceived = false, supervisedReceived = false;

	protected ProsperityObserver o;						// not used, 1-MSE published directly 

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

	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		this.registerProsperityObserver();
	}

	/**
	 * Instatntiate the Observer {@link #o} to the resider one. 
	 */
	protected void registerProsperityObserver(){}

	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Dimension of input data (MSE is computed accross this vector");
		paramList.addParam(delayConf, ""+DEF_DELAY, "Depth of the feedforward network (synchronization of data)");

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
		delay = r.getMyInteger(delayConf, DEF_DELAY);
		if(delay<0){
			System.err.println("Incorrect delay of the network, will use the default one: "+DEF_DELAY);
			delay = DEF_DELAY;
		}
		if(delay >= dataBufferLen){
			dataBufferLen = delay+1;
		}
		dataSize = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		
		if(dataSize<=0){
			System.err.println("Incorrect dimension of the data, will use the default one: "+DEF_NOINPUTS);
			dataSize = DEF_NOINPUTS;
		}
		
		//data = new float[dataSize];
		data = new float[dataSize];
		expectedBuffer = new ArrayList<float[]>(dataBufferLen);
		mse = 0;
	}


	/**
	 * Two data inputs of the same size, one supervised, one current.
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		//dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				//System.err.println("RECEIVED data of value.. "+SL.toStr(data));

				System.err.println("MSE -  - --- received this array on A "+SL.toStr(data));
				
				if(data.length != dataSize)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+ dataSize);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0  && step >0)
						System.out.println(me+"<-"+topicDataIn+" Received new data &" +
								" state description "+SL.toStr(data));

					setData(data);
					dataReceived = true;
					if(bothReceived()){
						onNewDataReceived();
						clearReceived();
					}
				}
			}
		});

		Subscriber<std_msgs.Float32MultiArray> dataSub2 = 
				connectedNode.newSubscriber(topicDataInSupervised, std_msgs.Float32MultiArray._TYPE);

		dataSub2.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				//System.err.println("RECEIVED data of value.. "+SL.toStr(data));

				System.err.println("MSE -  - --- received this array on B "+SL.toStr(data));
				
				if(data.length != dataSize)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+ dataSize);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0  && step >0)
						System.out.println(me+"<-"+topicDataIn+" Received new data &" +
								" state description "+SL.toStr(data));
					
					setExpected(data);
					supervisedReceived = true;
					if(bothReceived()){
						onNewDataReceived();
						clearReceived();
					}
				}
			}
		});
	}

	public void onNewDataReceived(){
		step++;
		
		if(expectedBuffer.size() >= delay+1){
			mse += this.computeDifferences(expectedBuffer.get(delay), data);	
		}
		
		this.publishProsperity();
	}
	
	public float computeDifferences(float[] data, float[] expected){
		float out =0;
		float diff;
		for(int i=0; i<data.length; i++){
			
			diff = data[i] - expected[i];
			out += diff*diff;
		}
		return out/data.length;
	}

	public void setExpected(float[] d){
		while(expectedBuffer.size() >= dataBufferLen){
			expectedBuffer.remove(expectedBuffer.size()-1);
		}
		float[] data = new float[d.length];
		for(int i=0; i<data.length; i++){
			data[i] = d[i];
		}
		expectedBuffer.add(0, data);
	}
	
	
	public void setData(float[] d){
		if(d.length != data.length){
			System.err.println("unexpected length of the data");
		}
		for(int i=0; i<data.length; i++){
			data[i] = d[i];
		}
	}

	public boolean bothReceived(){ return dataReceived && supervisedReceived; }

	public void clearReceived(){ 
		this.dataReceived = false;
		this.supervisedReceived = false;
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
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();
		float prosp;
		if(mse==0){
			prosp = 1;
		}else{
			prosp = 1/mse;	// the higher the MSE, the lower the prosperity
		}
		float[] data = new float[]{prosp};
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

	@Override
	public String listParams() {
		return null;
	}

	@Override
	public void hardReset(boolean arg0) {
		this.clearReceived();
	}

	@Override
	public void softReset(boolean arg0) {
		this.clearReceived();
	}

	@Override
	public float getProsperity() {
		return 1-mse;
	}
}


