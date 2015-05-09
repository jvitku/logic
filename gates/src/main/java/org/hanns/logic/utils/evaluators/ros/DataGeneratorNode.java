package org.hanns.logic.utils.evaluators.ros;

import java.util.LinkedList;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * Generates given sequence of integer values. 
 * It has own data input, which takes any message of any length,
 * and triggers publishing of new data sample from a given data series.
 *  
 * @author Jaroslav Vitku
 *
 */
public class DataGeneratorNode extends AbstractConfigurableHannsNode{

	public static final String name = "DataGenerator";

	// dataPub publishes input data, dataPubSolution then publishes expected result of the operation
	// (e.g. XOR(a,b)) 
	protected Publisher<std_msgs.Float32MultiArray> dataPubSolution;
	public static final String topicDataSolution = "topicDataSolution";

	protected int[][] dataSeries;			// sequence of input data
	protected int[][] dataSeriesSol;	// sequence of solution (supervised) data

	protected int step = 0;

	protected ProsperityObserver o;						// not used 

	public static final int DEF_NOOUTPUTS = 2;
	public static final int DEF_NOOUTPUTSSOL = 1;
	public static final String noOutputsSolConf = "noOutputsSol";
	public int noOutputs, noOutputsSol;

	// value table for two variables
	public static final String dataConf = "data";
	public static final int[] DEF_VEC = new int[]{0,0,0,1,1,0,1,1};

	public static final String dataConfSol = "dataSol";
	public static final int[] DEF_VECSOL = new int[]{0,1,1,0};	// XOR operation

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
		paramList.addParam(noOutputsConf, ""+DEF_NOOUTPUTS,"Dimension of output data (input to other nodes)");
		paramList.addParam(noOutputsSolConf, ""+DEF_NOOUTPUTSSOL,"Dimension of solution data (expected solution)");

		paramList.addParam(dataConf, ""+DEF_VEC, "List of integer data that will be published (x1,y1,x2,y2..)");
		paramList.addParam(dataConfSol, ""+DEF_VECSOL, "List of integer data that is expected as solution (a1,b1,a2,b2..)");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		noOutputs = r.getMyInteger(noOutputsConf, DEF_NOOUTPUTS);
		int[] data = r.getMyIntegerList(dataConf, DEF_VEC);

		if(noOutputs<=0){
			System.err.println("WARNING: incorrect no of outputs, will use the default one "+DEF_NOOUTPUTS);
			noOutputs = DEF_NOOUTPUTS;
		}
		if(data.length % noOutputs != 0){
			System.err.println("WARNING: given vector of values is not dividible by given no of outputs: "+noOutputs);
		}

		noOutputsSol = r.getMyInteger(noOutputsSolConf, DEF_NOOUTPUTSSOL);
		int[] dataSol = r.getMyIntegerList(dataConfSol, DEF_VECSOL);

		this.initDataSeries(data, dataSol);

		System.out.println(me+"Creating data structures.");
	}

	private void initDataSeries(int[] parsedData, int[] parsedDataSol){
		int len = parsedData.length/noOutputs;
		int lenSol = parsedDataSol.length/noOutputsSol;

		if(len != lenSol){
			System.err.println("WARNING: the lengths of data series do not match! "
					+len+"!="+lenSol+" Will take into account the shorter one");
			// lenSol will not be used from now on
			if(len > lenSol){
				len = lenSol;
			}
		}

		dataSeries = new int[noOutputs][len];
		int pos = 0;

		for(int i=0; i<noOutputs; i++){
			for(int j=0; j<len; j++){
				dataSeries[i][j] = parsedData[pos++];
			}
		}
		dataSeriesSol = new int[noOutputsSol][len];
		pos = 0;

		for(int i=0; i<noOutputsSol; i++){
			for(int j=0; j<len; j++){
				dataSeriesSol[i][j] = parsedDataSol[pos++];
			}
		}		
	}

	/**
	 * Anything is received on the input is ignored, but triggers the data series publishing
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);
		dataPubSolution =connectedNode.newPublisher(topicDataSolution, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				if(step % logPeriod==0  && step >0)
					System.out.println(me+"<-"+topicDataIn+" Received new data, publishing new set of values");

				onNewDataReceived();
			}
		});
	}

	public void onNewDataReceived(){
		step++;
		std_msgs.Float32MultiArray message = dataPublisher.newMessage();
		std_msgs.Float32MultiArray messageSolution = dataPubSolution.newMessage();

		float[] data = new float[noOutputs];
		for(int i=0; i<data.length; i++){
			data[i] =dataSeries[i][step % dataSeries[0].length]; 
		}

		float[] sol = new float[noOutputsSol];
		for(int i=0; i<sol.length; i++){
			sol[i] =this.dataSeriesSol[i][step % dataSeriesSol[0].length]; 
		}

		message.setData(data);				// publish inputs
		dataPublisher.publish(message);

		messageSolution.setData(sol);		// publish expected solution
		dataPubSolution.publish(messageSolution);
		
		System.out.println("---------- publishing this: "+SL.toStr(data)+" and: "+SL.toStr(sol));
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
		float[] data = new float[]{0};
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
		step= 0;
	}

	@Override
	public void softReset(boolean arg0) {
		step= 0;
	}

	@Override
	public float getProsperity() {
		return 0;
	}
}


