package org.hanns.logic.crisp.gates;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.nodes.CommunicationAwareNode;
import ctu.nengoros.nodes.topicParticipant.ConnectedParticipantSubscriber;
import ctu.nengoros.nodes.topicParticipant.ParticipantSubscriber;


public abstract class GateTester extends CommunicationAwareNode{
	
	Log log;

	public final String aT = "logic/gates/ina";					//topics
	public final String bT = "logic/gates/inb";
	public final String yT = "logic/gates/outa";

	protected Publisher<std_msgs.Bool> publisherA, publisherB;	//communication participants
	protected Subscriber<std_msgs.Bool> subscriber;
	
	protected int receivedMessages = 0;
	protected volatile boolean waitingForResponse = false;
	protected volatile boolean response = false;
	
	protected final int maxwait = 5000, waittime = 10;	//ms
	
	// check if my communication is connected somewhere?
	public boolean requireGateRunning = true;
	
			
	public boolean somethingReceived(){ return receivedMessages > 0; }

	
	protected void awaitResponse(){
		int poc = 0;

		while(waitingForResponse){
			if(waittime*poc++ > maxwait){
				System.out.println("Message from ROS node not received in given time of "+maxwait+" ms");
				fail("Message from ROS node not received in given time of "+maxwait+" ms");
				return;
			}
			try {
				Thread.sleep(waittime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail("Could not sleep");
			}
		}
	}
	
	/**
	 * Assume that most of gates have one output.
	 * 
	 * @param connectedNode
	 */
	protected void connectGateOutput(ConnectedNode connectedNode){

		// subscribe to gate output
		subscriber = connectedNode.newSubscriber(yT, std_msgs.Bool._TYPE);

		// create listener
		subscriber.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(std_msgs.Bool message) {
				response = message.getData();
				waitingForResponse = false;
				receivedMessages++;
				log.info("Received these data: "+response);
			}
		});

		// this thing ensures that at least one subscriber is registered
		if(this.requireGateRunning)
			super.participants.registerParticipant(
				new ConnectedParticipantSubscriber<std_msgs.Bool>(subscriber));
		else
			super.participants.registerParticipant(
					new ParticipantSubscriber<std_msgs.Bool>(subscriber));
	}
}
