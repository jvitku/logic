package org.hanns.logic.fuzzy.gates;

import org.apache.commons.logging.Log;
import org.hanns.logic.gates.SisoAbstractGate;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * Pass one membership function (automatically cut-off to range of <0;1>) and the gate 
 * will return the result of particular unary fuzzy operation.
 * 
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class SisoGate extends SisoAbstractGate<std_msgs.Float32> {

	private final boolean SEND = false; 		// send periodically each "time step"?
	private final int sleepTime = 10000;		// everything handled by listeners

	// ROS stuff
	Subscriber<std_msgs.Float32MultiArray> subscriberA;
	Publisher<std_msgs.Float32MultiArray> publisher;
	Log log;
	
	private float a = 0, y=0;
	
	/**
	 * implement this in order to make computation 
	 * @param a input value A
	 * @return output value Y
	 */
	protected abstract float compute(float a);

	protected void send(){
		super.awaitCommunicationReady();

		std_msgs.Float32MultiArray out = publisher.newMessage();
		out.setData(new float[]{y});
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}
	
	public int getSleepTime(){ return this.sleepTime; }
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		
		// register subscribers
		subscriberA = connectedNode.newSubscriber(aT, std_msgs.Float32MultiArray._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				a = cutOff(message.getData()[0]);
				y = compute(a);
				send();
			}
		});

		// register publisher
		publisher = connectedNode.newPublisher(yT, std_msgs.Float32MultiArray._TYPE);		
		super.nodeIsPrepared();
		
		// infinite loop
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {	
			}

			@Override
			protected void loop() throws InterruptedException {

				if(SEND){
					std_msgs.Float32MultiArray out = publisher.newMessage();
					out.setData(new float[]{y});
					publisher.publish(out);
					log.info("Publishing this: \"" + out.getData() + " !! on topic: "+yT);
				}
				Thread.sleep(sleepTime);
			}
		});
	}
	
	/**
	 * Get the degree of membership and cut-off it into <0;1>
	 * 
	 * @param degree
	 * @return
	 */
	private float cutOff(float degree){
		if(degree<0)
			return 0;
		if(degree>1)
			return 1;
		return degree;
	}
	
}
