package org.hanns.logic.fuzzy.membership.impl;

import org.hanns.logic.fuzzy.membership.Linear;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;


/**
 * Fuzzy linear increasing membership function. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class IncreasingLinear extends Linear{

	/**
	 * Compute fuzzy increasing linear membership function. 
	 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
	 */
	@Override
	public float compute() {
		if(x<alpha)
			return 0;
		
		if(x>beta)
			return 1;
		
		return ((x-alpha)/(beta-alpha));
	}

	/**
	 * If the constraint is violated, do not know where to place the place for alpha and beta,
	 * so choose the average value.
	 */
	@Override
	public void checkRanges() {
		if(beta < alpha){
			alpha = super.getAverage(alpha, beta);
			beta = alpha;
		}
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyIncreasingLinMembership"); }

	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		super.nodeIsPrepared();
	}

	
}
