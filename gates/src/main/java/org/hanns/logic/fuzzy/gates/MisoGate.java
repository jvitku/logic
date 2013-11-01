package org.hanns.logic.fuzzy.gates;

import org.hanns.logic.gates.MisoAbstractGate;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;

/**
 * Pass two membership functions (automatically cut-off to range of <0;1>) and the gate 
 * will return the result of particular fuzzy operation.
 * 
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoGate extends MisoAbstractGate<std_msgs.Float32MultiArray> {


	private float a = 0,b = 0, y=0;

	protected void send(){
		super.awaitCommunicationReady();

		std_msgs.Float32MultiArray out = publisher.newMessage();
		out.setData(new float[]{y});
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}

	public abstract float compute(float a, float b);
	
	public int getSleepTime(){ return this.sleepTime; }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		// register subscribers
		subscriberA = connectedNode.newSubscriber(aT, std_msgs.Float32MultiArray._TYPE);
		subscriberB = connectedNode.newSubscriber(bT, std_msgs.Float32MultiArray._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				a = cutOff(message.getData()[0]);
				y = compute(a,b);
				send();
				//System.out.println("received data on AAAA; responding to: ("+a+","+b+")="+y);
			}
		});
		subscriberB.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				b = cutOff(message.getData()[0]);
				y = compute(a,b);
				send();
				//System.out.println("received data on BBBB; responding to: ("+a+","+b+")="+y);			
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
				//System.out.println("Hi I am Miso gate and I am here");
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
