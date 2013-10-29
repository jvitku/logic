package org.hanns.logic.crisp.gates;

import org.hanns.logic.gates.MisoAbstractGate;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;

import std_msgs.Bool;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoGate extends MisoAbstractGate<std_msgs.Bool> {


	private boolean a = false,b = false, y=false;
	private volatile boolean inited = false;

	protected void send(){
		if(!inited)
			return;

		std_msgs.Bool out = publisher.newMessage();
		out.setData(y);
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}

	public int getSleepTime(){ return this.sleepTime; }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		// register subscribers
		subscriberA = connectedNode.newSubscriber(aT, std_msgs.Bool._TYPE);
		subscriberB = connectedNode.newSubscriber(bT, std_msgs.Bool._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(Bool message) {
				a = message.getData();
				y = copute(a,b);
				send();
				//System.out.println("received data on AAAA; responding to: ("+a+","+b+")="+y);
			}
		});
		subscriberB.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(Bool message) {
				b = message.getData();
				y = copute(a,b);
				send();
				//System.out.println("received data on BBBB; responding to: ("+a+","+b+")="+y);			
			}
		});

		// register publisher
		publisher = connectedNode.newPublisher(yT, std_msgs.Bool._TYPE);		
		inited = true;


		// infinite loop
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {	
			}

			@Override
			protected void loop() throws InterruptedException {

				if(SEND){
					std_msgs.Bool out = publisher.newMessage();
					out.setData(y);
					publisher.publish(out);
					log.info("Publishing this: \"" + out.getData() + " !! on topic: "+yT);
				}
				//System.out.println("Hi I am Miso gate and I am here");
				Thread.sleep(sleepTime);
			}
		});
	}
}
