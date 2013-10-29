package org.hanns.logic.gates;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.testsuit.topicParticipant.ConnectedParticipantPublisher;


public class SisoGateTester extends GateTester{

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("SisoGateTester"); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		log = connectedNode.getLog();

		super.connectGateOutput(connectedNode);

		this.connectGateInput(connectedNode);

		// wait for preconditions: registered to master and some subscriber connected 
		super.waitForCommunicationReady();

		super.ready = true;
	}

	/**
	 * Send the data to be computed by a remote ROS node (logic gate).
	 * 
	 * @param a inputA
	 * @return output after the corresponding message is received or fail after some time
	 */
	public boolean computeRemotely(boolean a){

		super.waitForReady();
		
		this.sendA(a);

		super.awaitResponse();
		return super.response;
	}

	private void sendA(boolean a){
		std_msgs.Bool mess = publisherA.newMessage();
		mess.setData(a);
		super.waitingForResponse = true;
		publisherA.publish(mess);
	}
	private void connectGateInput(ConnectedNode connectedNode){
		// build and register both publishers
		publisherA = connectedNode.newPublisher(aT, std_msgs.Bool._TYPE);

		super.participants.registerParticipant(
				new ConnectedParticipantPublisher<std_msgs.Bool>(publisherA));
	}
}
