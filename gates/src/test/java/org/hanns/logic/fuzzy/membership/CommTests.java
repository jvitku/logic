package org.hanns.logic.fuzzy.membership;

import static org.junit.Assert.*;

import org.junit.Test;

import ctu.nengoros.RosRunner;

public class CommTests extends ctu.nengoros.nodes.RosCommunicationTest{

	@Test
	public void increasingLinear() {
		//this.misoFuzzyGateTest("org.hanns.logic.fuzzy.gates.impl.AND", 0);
		
		RosRunner gate = runNode("org.hanns.logic.fuzzy.membership.impl.IncreasingLinear");

		RosRunner rr = runNode("org.hanns.logic.fuzzy.membership.TwoParamTester");
		TwoParamTester mt = (TwoParamTester)rr.getNode();

		// setup the node (online)
		mt.changeMembershipFcnParameter(0,11); // TODO: is here the averaging beta<alpha OK?
		mt.changeMembershipFcnParameter(10,11);
		
		// boundaries
		assertTrue(mt.computeRempotely(10)==0);
		assertTrue(mt.computeRempotely(11)==1.0);
		
		System.out.println("---------------0 "+mt.computeRempotely(0f));
		System.out.println("---------------11 "+mt.computeRempotely(11f));
		System.out.println("---------------5 "+mt.computeRempotely(5f));
		System.out.println("----------------5000 "+mt.computeRempotely(-5000f));
		System.out.println("----------------200 "+mt.computeRempotely(200f));
		System.out.println("----------------10.25 "+mt.computeRempotely(10.25f));
		System.out.println("----------------10.5 "+mt.computeRempotely(10.5f));
		System.out.println("----------------10.75 "+mt.computeRempotely(10.75f));
		

		
		// out of..
		assertTrue(mt.computeRempotely(5)==0f);
		assertTrue(mt.computeRempotely(-5000)==0f);
		assertTrue(mt.computeRempotely(200)==1f);
		
		// inside
		assertTrue(mt.computeRempotely(10.5f) == 0.5f);
		assertTrue(mt.computeRempotely(10.75f) == 0.75f);
		
		rr.stop();
		gate.stop();
	}
	/**/
	@Test
	public void decreasingLinear() {
		//this.misoFuzzyGateTest("org.hanns.logic.fuzzy.gates.impl.AND", 0);
		
		RosRunner gate = runNode("org.hanns.logic.fuzzy.membership.impl.DecreasingLinear");

		RosRunner rr = runNode("org.hanns.logic.fuzzy.membership.TwoParamTester");
		TwoParamTester mt = (TwoParamTester)rr.getNode();

		// setup the node (online)
		mt.changeMembershipFcnParameter(-2,0); // TODO: is here the averaging beta<alpha OK?
		mt.changeMembershipFcnParameter(-2,-1);
		
		assertTrue(mt.computeRempotely(10)==0);
		assertTrue(mt.computeRempotely(11)==0);
		
		assertTrue(mt.computeRempotely(5)==0);
		assertTrue(mt.computeRempotely(-5000)==1);
		assertTrue(mt.computeRempotely(200)==0);
		
		assertTrue(mt.computeRempotely(-2.5f) == 1);
		assertTrue(mt.computeRempotely(-2) == 1);
		assertTrue(mt.computeRempotely(-1) == 0);
		assertTrue(mt.computeRempotely(-1.5f) == 0.5);
		
		rr.stop();
		gate.stop();
	}
	
	/**
	 * Testing of triangle could be more extensive.
	 */
	@Test
	public void triangleTest() {
		//this.misoFuzzyGateTest("org.hanns.logic.fuzzy.gates.impl.AND", 0);
		
		RosRunner gate = runNode("org.hanns.logic.fuzzy.membership.impl.Triangular");
		RosRunner rr = runNode("org.hanns.logic.fuzzy.membership.ThreeParamTester");
		ThreeParamTester mt = (ThreeParamTester)rr.getNode();
		
		// setup the node (online)
		mt.changeMembershipFcnParameter(-0.5f,0,0); 
		mt.changeMembershipFcnParameter(-0.5f,0,0.5f);
		
		assertTrue(mt.computeRempotely(10)==0);
		assertTrue(mt.computeRempotely(-10)==0);
		assertTrue(mt.computeRempotely(5)==0);
		assertTrue(mt.computeRempotely(-5000)==0);
		assertTrue(mt.computeRempotely(200)==0);
		
		assertTrue(mt.computeRempotely(-0.5f) == 0);
		assertTrue(mt.computeRempotely(0.5f) == 0);
		
		assertTrue(mt.computeRempotely(0) == 1);
		
		assertTrue(mt.computeRempotely(-0.25f) == 0.5f);
		assertTrue(mt.computeRempotely(0.25f) == 0.5f);
		
		rr.stop();
		gate.stop();
	}
	
	/**
	 * Testing of trapezoid could be more extensive too.
	 */
	@Test
	public void TrapezoidTest() {
		
		RosRunner gate = runNode("org.hanns.logic.fuzzy.membership.impl.Trapezoid");
		RosRunner rr = runNode("org.hanns.logic.fuzzy.membership.FourParamTester");
		FourParamTester mt = (FourParamTester)rr.getNode();
		
		// setup the node (online)
		mt.changeMembershipFcnParameter(-1f,	0,		0		,0);
		mt.changeMembershipFcnParameter(-1f,	-0.5f,	0		,0);
		mt.changeMembershipFcnParameter(-1f,	-0.5f,	0.5f	,0);
		mt.changeMembershipFcnParameter(-1f,	-0.5f,	0.5f	,1);
		
		
		assertTrue(mt.computeRempotely(10)==0);
		assertTrue(mt.computeRempotely(-10)==0);
		assertTrue(mt.computeRempotely(5)==0);
		assertTrue(mt.computeRempotely(-5000)==0);
		assertTrue(mt.computeRempotely(200)==0);
		
		assertTrue(mt.computeRempotely(-0.5f) == 1);
		assertTrue(mt.computeRempotely(0.5f) == 1);
		assertTrue(mt.computeRempotely(0) == 1);
			
		assertTrue(mt.computeRempotely(-1f) == 0f);
		assertTrue(mt.computeRempotely(1f) == 0f);
		
		assertTrue(mt.computeRempotely(-0.75f) == 0.5f);
		assertTrue(mt.computeRempotely(0.75f) == 0.5f);
		rr.stop();
		gate.stop();
	}/**/
}
