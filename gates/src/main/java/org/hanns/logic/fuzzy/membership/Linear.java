package org.hanns.logic.fuzzy.membership;

import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

/**
 * Compute triangle fuzzy membership function.
 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
 * 
 * @author Jaroslav Vitku
 */
public abstract class Linear extends Membership {

	protected float alpha=0, beta=0;
	protected Subscriber<Float32MultiArray> alphaSub, betaSub;
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		alphaSub = connectedNode.newSubscriber(confAT, Float32MultiArray._TYPE);
		betaSub = connectedNode.newSubscriber(confBT, Float32MultiArray._TYPE);
		
		// after receiving new configuration, recompute and re-send new data
		alphaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				alpha = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
		
		betaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				beta = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
	}
	
}
