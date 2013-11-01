package org.hanns.logic.fuzzy.membership;

import org.hanns.logic.gates.MisoAbstractGate;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;

/**
 * Pass two membership functions (automatically cut-off to range of <0;1>) and the gate 
 * will return the result of particular fuzzy operation.
 * 
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class Membership extends MisoAbstractGate<std_msgs.Float32> {

	protected float x=0, y=0;						// data

	protected void send(){
		super.awaitCommunicationReady();

		std_msgs.Float32 out = publisher.newMessage();
		out.setData(y);
		publisher.publish(out);
		log.info("Received data, publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}

	protected abstract float compute();

	/**
	 * Each membership function has some constraints on parameter values, check them here.
	 */
	protected abstract void checkRanges();
	
	public int getSleepTime(){ return this.sleepTime; }

	/**
	 * It should be fulfilled that: a>=b, if not
	 * choose average value between these two and set both to this value.
	 * 
	 * @param a
	 * @param b
	 * @return average between a b 	 */
	protected float getAverage(float a, float b){
		if(b<a){
			return b+((a-b)/2);
		}
		System.err.println("Membership functino:correctValues: values are already OK!");
		return -1;
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		this.getDataChannel(connectedNode);

		// add here your configuration channel (in sub-classes)
	}

	/**
	 * Register input/output into the network.
	 * 
	 * @param connectedNode
	 */
	protected void getDataChannel(ConnectedNode connectedNode){
		// data input - x
		subscriberA = connectedNode.newSubscriber(aT, std_msgs.Float32._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Float32>() {
			@Override
			public void onNewMessage(std_msgs.Float32 message) {
				x = message.getData();
				y = compute();
				send();
				//System.out.println("received data on AAAA; responding to: ("+a+","+b+")="+y);
			}
		});
		
		// data output - y
		publisher = connectedNode.newPublisher(outaT, std_msgs.Float32._TYPE);
	}

}
