package org.hanns.logic.fuzzy.membership.impl;

import org.hanns.logic.fuzzy.membership.Membership;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;


/**
 * Fuzzy linear decreasing membership function. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class DecreasingLinear extends Membership{
	
	/**
	 * Compute fuzzy decreasing linear membership function. 
	 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
	 */
	@Override
	public float compute() {
		if(x<alpha)
			return 1;
		
		if(x>beta)
			return 0;
		
		return (1-((x-alpha)/(beta-alpha)));
	}
	
	@Override
	public void checkRanges() {
		if(beta < alpha){
			//alpha = super.getAverage(alpha, beta);
			alpha = beta;
		}
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyDecreasingLinMembership"); }

	@Override
	public void onStart(ConnectedNode connectedNode){
		log = connectedNode.getLog();
		super.getDataChannel(connectedNode);
		
		this.initAlpha(connectedNode);
		this.initBeta(connectedNode);
		
		super.nodeIsPrepared();
	}
}
