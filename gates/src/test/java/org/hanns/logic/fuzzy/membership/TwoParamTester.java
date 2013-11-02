package org.hanns.logic.fuzzy.membership;

import static org.junit.Assert.fail;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

public class TwoParamTester extends FcnAbstractTester{


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
	public GraphName getDefaultNodeName() { return GraphName.of("twoParamsTester"); }

	@Override
	protected void send() {
		fail("this method is not used unfortunatelly");
	}
}
