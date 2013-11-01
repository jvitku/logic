package org.hanns.logic.fuzzy.gates;

import java.util.Random;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import static org.junit.Assert.fail;
import ctu.nengoros.nodes.topicParticipant.ConnectedParticipantPublisher;
import ctu.nengoros.nodes.topicParticipant.ParticipantPublisher;

public class MisoFuzzyGateTester extends GateTester{

	// initial conditions for gates is this on inputs:
	private float[] lastSent = new float[]{0, 0};

	private Random r = new Random();

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("MisoFuzzyGateTesterX"); }

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
	public float computeRemotely(float a, float b){

		super.awaitCommunicationReady();

		this.checkForCorrectChanges(a, b);

		if(a != lastSent[0]){			// a changed?
			this.sendA(a);
		}else if(b != lastSent[1]){		// b changed?
			this.sendB(b);
		}else{			// the same computation again? choose input to be resent randomly
			if(r.nextBoolean()){
				this.sendA(lastSent[0]);
			}else{
				this.sendB(lastSent[1]);
			}
		}

		super.awaitResponse();
		return super.response;
	}

	private void sendA(float a){
		std_msgs.Float32MultiArray mess = publisherA.newMessage();
		mess.setData(new float[]{a});
		lastSent[0] = a;
		super.waitingForResponse = true;
		publisherA.publish(mess);
	}

	private void sendB(float b){
		std_msgs.Float32MultiArray mess = publisherB.newMessage();
		mess.setData(new float[]{b});
		lastSent[1] = b;
		super.waitingForResponse = true;
		publisherB.publish(mess);
	}

	/**
	 * The user is allowed to change only ONE input at a time. Because inputs are asynchronous.
	 * Fail if both values are changed.
	 * 
	 * @param a inputA
	 * @param b inputB
	 */
	private void checkForCorrectChanges(float a, float b){
		if(a!=lastSent[0] && b!=lastSent[1]){
			System.err.println("Due to asynchronous inputs, it is not allowed to change both "
					+ "inputs at a time!");
			fail("Due to asynchronous inputs, it is not allowed to change both inputs at a time!");
		}
	}

	private void connectGateInputs(ConnectedNode connectedNode){
		// build and register both publishers
		publisherA = connectedNode.newPublisher(aT, std_msgs.Float32MultiArray._TYPE);
		publisherB = connectedNode.newPublisher(bT, std_msgs.Float32MultiArray._TYPE);

		if(super.requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(publisherA));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(publisherB));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(publisherA));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(publisherB));
		}
	}
}
