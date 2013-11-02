package org.hanns.logic.fuzzy.membership.impl;

import org.hanns.logic.fuzzy.membership.Membership;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

/**
 * Compute triangle fuzzy membership function.
 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
 * 
 * @author Jaroslav Vitku
 */
public class Triangle extends Membership {

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

		super.initAlpha(connectedNode);
		super.initBeta(connectedNode);
		this.initGamma(connectedNode);

		super.nodeIsPrepared();	// indicate that everything is configured
	}

}
