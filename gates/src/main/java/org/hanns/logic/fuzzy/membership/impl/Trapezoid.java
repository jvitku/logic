package org.hanns.logic.fuzzy.membership.impl;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

public class Trapezoid extends Triangle{
	
	private float delta=0;
	private Subscriber<Float32MultiArray> deltaSub;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyTrapezoidMembership"); }

	/**
	 * Compute triangle membership function,
	 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf 
	 */
	@Override
	public float compute() {
		if(x<alpha || x>=delta)					// out of range
			return 0;
		
		if(x<beta)								// go up
			return ((x-alpha)/(beta-alpha));
		
		if(x<gamma)								
			return 1;
		
		return -((x-delta)/(delta-gamma));		// go down
	}
	
	@Override
	public void checkRanges(){
		super.checkRanges();
		
		if(delta<gamma)
			delta = gamma;
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		deltaSub = connectedNode.newSubscriber(confDT, Float32MultiArray._TYPE);
		
		deltaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				delta = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
		
		super.nodeIsPrepared();	// indicate that everything is configured
	}
	
}
