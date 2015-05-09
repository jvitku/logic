package org.hanns.logic.crisp.gates;

import org.apache.commons.logging.Log;
import org.hanns.logic.gates.SisoAbstractGate;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.util.SL;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class SisoGate extends SisoAbstractGate<std_msgs.Float32MultiArray> {

	private final boolean SEND = false; 		// send periodically each "time step"?
	private final int sleepTime = 10000;		// everything handled by listeners

	// ROS stuff
	Subscriber<std_msgs.Float32MultiArray> subscriberA;
	Publisher<std_msgs.Float32MultiArray> publisher;
	Log log;
	
	public final String aT = "logic/gates/ina";
	public final String yT = "logic/gates/outa";

	private boolean a = false, y=false;
	
	/**
	 * implement this in order to make computation 
	 * @param a input value A
	 * @return output value Y
	 */
	protected abstract boolean compute(boolean a);

	protected void send(){
		super.awaitCommunicationReady();

		std_msgs.Float32MultiArray out = publisher.newMessage();
		out.setData(new float[]{toFl(y)});
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + SL.toStr(out.getData()) + " !! on topic: "+yT);
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
				a = toB(message.getData());
				y = compute(a);
				send();
			}
		});

		// register publisher
		publisher = connectedNode.newPublisher(yT, std_msgs.Bool._TYPE);		
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
					out.setData(new float[]{toFl(y)});
					publisher.publish(out);
					log.info("Publishing this: \"" + SL.toStr(out.getData()) + " !! on topic: "+yT);
				}
				Thread.sleep(sleepTime);
			}
		});
	}
}
