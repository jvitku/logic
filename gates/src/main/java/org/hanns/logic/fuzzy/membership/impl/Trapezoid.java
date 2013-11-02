package org.hanns.logic.fuzzy.membership.impl;

import org.hanns.logic.fuzzy.membership.Membership;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

public class Trapezoid extends Membership{
	
	
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
		if(beta < alpha){
			alpha = super.getAverage(alpha, beta);
			beta = alpha;
		}
		// probably cannot move beta already
		if(gamma < beta){
			gamma = beta;
		}
		
		if(delta<gamma)
			delta = gamma;
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		super.initAlpha(connectedNode);
		super.initBeta(connectedNode);
		super.initGamma(connectedNode);
		super.initDelta(connectedNode);
		
		super.nodeIsPrepared();	// indicate that everything is configured
	}
	
	
}
