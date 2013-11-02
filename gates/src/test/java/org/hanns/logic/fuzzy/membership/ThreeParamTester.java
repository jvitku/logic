package org.hanns.logic.fuzzy.membership;

import static org.junit.Assert.fail;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import ctu.nengoros.nodes.topicParticipant.ConnectedParticipantPublisher;
import ctu.nengoros.nodes.topicParticipant.ParticipantPublisher;

public class ThreeParamTester extends FcnAbstractTester{

	protected Publisher<std_msgs.Float32MultiArray> gammaPub;
	
	protected void checkCorrectChanges(float alpha, float beta){
		if(alpha!=currentAlpha && beta!=currentBeta){
			System.err.println("Parameter values should be changed only one at a time for consistent results!");
			fail("Parameter values should be changed only one at a time for consistent results!");
		}
	}

	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);

		super.nodeIsPrepared();
	}
	
	@Override
	protected void connectTestedNodeConfigs(ConnectedNode connectedNode){
		// build and register both publishers
		alphaPub = connectedNode.newPublisher(confAT, std_msgs.Float32MultiArray._TYPE);
		betaPub = connectedNode.newPublisher(confBT, std_msgs.Float32MultiArray._TYPE);
		gammaPub = connectedNode.newPublisher(confCT, std_msgs.Float32MultiArray._TYPE);
		
		if(requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(gammaPub));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(gammaPub));
		}
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("threeParamsTester"); }

	@Override
	protected void send() {
		fail("this method is not used unfortunatelly");
	}
}
