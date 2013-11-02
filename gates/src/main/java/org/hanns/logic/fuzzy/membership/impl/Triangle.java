package org.hanns.logic.fuzzy.membership.impl;

import org.hanns.logic.fuzzy.membership.Linear;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

/**
 * Compute triangle fuzzy membership function.
 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
 * 
 * @author Jaroslav Vitku
 */
public class Triangle extends Linear {

	protected float gamma=0;
	protected Subscriber<Float32MultiArray> gammaSub;
	
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyTriangleMembership"); }

	/**
	 * Compute triangle membership function,
	 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf 
	 */
	@Override
	protected float compute() {
		if(x<alpha || x>=gamma)
			return 0;
		
		if(x<=beta)
			return ((x-alpha)/(beta-alpha));
		
		return -((x-gamma)/(gamma-beta));
	}
	
	@Override
	public void checkRanges() {
		
		if(beta < alpha){
			alpha = super.getAverage(alpha, beta);
			beta = alpha;
		}
		// probably cannot move beta already
		if(gamma < beta){
			gamma = beta;
		}
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		gammaSub = connectedNode.newSubscriber(confCT, Float32MultiArray._TYPE);
		
		gammaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				gamma = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
		
		super.nodeIsPrepared();	// indicate that everything is configured
	}
	
}
