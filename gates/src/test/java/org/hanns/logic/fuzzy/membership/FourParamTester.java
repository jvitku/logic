package org.hanns.logic.fuzzy.membership;

import static org.junit.Assert.fail;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import ctu.nengoros.network.node.testsuit.topicParticipant.ConnectedParticipantPublisher;
import ctu.nengoros.network.node.testsuit.topicParticipant.ParticipantPublisher;

public class FourParamTester extends FcnAbstractTester{

	protected Publisher<std_msgs.Float32MultiArray> gammaPub, deltaPub;
	protected float currentGamma = 0, currentDelta=0;



	public float changeMembershipFcnParameter(float alpha, float beta, float gamma, float delta){

		super.awaitCommunicationReady();

		this.checkCorrectChanges(alpha, beta, gamma, delta);

		if(alpha !=currentAlpha){
			return this.changeAlpha(alpha);
		}else if(beta != currentBeta){
			return this.changeBeta(beta);
		}else if(gamma != currentGamma){
			return this.changeGamma(gamma);
		}else if(delta != currentDelta){
			return this.changeDelta(delta);
		}

		if(r.nextBoolean())
			return this.changeBeta(beta);
		else
			return this.changeBeta(beta);
	}

	private float changeDelta(float delta){
		std_msgs.Float32MultiArray out = deltaPub.newMessage();
		out.setData(new float[]{delta});
		//this.waitingForResponse = true;
		deltaPub.publish(out);
		currentDelta = delta;
		log.info("Changing remote delta to: \"" + out.getData()+" and waiting for new x");
		//this.awaitResponse();
		return response;
	}

	
	private float changeGamma(float gamma){
		std_msgs.Float32MultiArray out = gammaPub.newMessage();
		out.setData(new float[]{gamma});
		//this.waitingForResponse = true;
		gammaPub.publish(out);
		currentGamma = gamma;
		log.info("Changing remote gamma to: \"" + out.getData()+" and waiting for new x");
		//this.awaitResponse();
		return response;
	}

	protected void checkCorrectChanges(float alpha, float beta, float gamma, float delta){
		int changed = 0;
		if(alpha!=currentAlpha)
			changed++;
		if(beta!=currentBeta)
			changed++;
		if(gamma!=currentGamma)
			changed++;
		if(delta!=currentDelta)
			changed++;
		
		if(changed>1){
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
		deltaPub = connectedNode.newPublisher(confDT, std_msgs.Float32MultiArray._TYPE);

		if(requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(gammaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(deltaPub));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(gammaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(deltaPub));
		}
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("threeParamsTester"); }

	@Override
	protected void send() {
		fail("this method is not used unfortunatelly");
	}
}
