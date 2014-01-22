package org.hanns.logic.fuzzy.gates;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.testsuit.topicParticipant.ConnectedParticipantPublisher;
import ctu.nengoros.network.node.testsuit.topicParticipant.ParticipantPublisher;

/**
 * ROS node for testing a SISO Fuzzy gate, launch, check all connections, send/receive data.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SisoFuzzyGateTester extends GateTester{

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("SisoFuzzyGateTester"); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		log = connectedNode.getLog();

		super.connectGateOutput(connectedNode);

		this.connectGateInputs(connectedNode);

		super.nodeIsPrepared();

		// wait for preconditions: registered to master and some subscriber connected 
		super.awaitCommunicationReady();
	}

	/**
	 * Send the data to be computed by a remote ROS node (logic gate).
	 * 
	 * @param a inputA
	 * @param b inputB
	 * @return output after the corresponding message is received or fail after some time
	 */
	public float computeRemotely(float a){

		super.awaitCommunicationReady();

		this.sendA(a);
		
		super.awaitResponse();
		return super.response;
	}

	private void sendA(float a){
		std_msgs.Float32MultiArray mess = publisherA.newMessage();
		mess.setData(new float[]{a});
		super.waitingForResponse = true;
		publisherA.publish(mess);
	}



	private void connectGateInputs(ConnectedNode connectedNode){
		// build and register both publishers
		publisherA = connectedNode.newPublisher(aT, std_msgs.Float32MultiArray._TYPE);

		if(super.requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(publisherA));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(publisherA));
		}
	}
}
