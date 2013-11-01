package org.hanns.logic.fuzzy.membership.impl;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32;

public class Trapezoid extends Triangle{
	
	private float delta=0;
	private Subscriber<Float32> deltaSub;
	
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
		
		deltaSub = connectedNode.newSubscriber(dcT, Float32._TYPE);
		
		deltaSub.addMessageListener(new MessageListener<Float32>() {
			@Override
			public void onNewMessage(Float32 message) {
				delta = message.getData();
				compute();
				send();
			}
		});
		
		super.nodeIsPrepared();	// indicate that everything is configured
	}
	
}
