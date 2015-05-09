package org.hanns.logic.crisp.synchronousGates;

import org.hanns.logic.gates.MisoAbstractGate;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;

import ctu.nengoros.util.SL;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoGate extends MisoAbstractGate<std_msgs.Float32MultiArray> {


	private boolean a = false,b = false, y=false;

	protected void send(){
		super.awaitCommunicationReady();

		std_msgs.Float32MultiArray out = publisher.newMessage();
		out.setData(new float[]{toFl(y)});
		
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}
	
	public abstract boolean compute(boolean a, boolean b);
	
	public int getSleepTime(){ return this.sleepTime; }

	public boolean toBoolean(float f){ return f>=0.5; }
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		// register subscribers
		subscriberA = connectedNode.newSubscriber(inAT, std_msgs.Float32MultiArray._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				if(message.getData().length !=2){
					System.err.println("ERROR: this gate must have two inputs");
				}
				a = toBoolean(message.getData()[0]);
				b = toBoolean(message.getData()[1]);
				y = compute(a,b);
				send();
				System.out.println("received data on AAAA; responding to: ("+a+","+b+")="+y);
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
					out.setData(new float[]{toFl(y)});
					publisher.publish(out);
					log.info("Publishing this: \"" + SL.toStr(out.getData()) + " !! on topic: "+yT);
				}
				//System.out.println("Hi I am Miso gate and I am here");
				Thread.sleep(sleepTime);
			}
		});
	}
}
