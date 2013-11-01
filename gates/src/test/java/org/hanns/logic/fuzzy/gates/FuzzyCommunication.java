package org.hanns.logic.fuzzy.gates;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import org.ros.node.NodeMain;

import ctu.nengoros.RosRunner;

public class FuzzyCommunication extends ctu.nengoros.nodes.RosCommunicationTest{


	private Random r = new Random();
	
	@Test
	public void FuzzyAND() {
		this.misoFuzzyGateTest("org.hanns.logic.fuzzy.gates.impl.AND", 0);
	}

	@Test
	public void FuzzyOR() {
		this.misoFuzzyGateTest("org.hanns.logic.fuzzy.gates.impl.OR", 1);
	}

	private void misoFuzzyGateTest(String gateClassName, int operationNo){
		// run the node for testing
		RosRunner gate = runNode(gateClassName);

		RosRunner rr = runNode("org.hanns.logic.fuzzy.gates.MisoFuzzyGateTester");
		MisoFuzzyGateTester mt = startMisoFuzzyGateTester(rr);

		ArrayList<Float> receivedData = new ArrayList<Float>();
		
		ArrayList<Float[]> sentData = new ArrayList<Float[]>(100);
		
		float a = r.nextFloat(), b=r.nextFloat();
		Float[] tmp = new Float[]{new Float(a), new Float(b)};
		tmp[r.nextInt(2)] = new Float(0);	// at least one value should be 0 at the beginning
		sentData.add(tmp.clone());
		
		this.appendRandomData(sentData,70,-1);
		this.appendRandomData(sentData,20,5);
		this.appendRandomData(sentData,10,100);

		for(int i=0; i<sentData.size(); i++){
			receivedData.add(mt.computeRemotely(sentData.get(i)[0], sentData.get(i)[1]));
		}

		System.out.println("Simulation ENDED, so checking data now!!!");

		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new FuzzyLogic(operationNo), sentData, receivedData);

		rr.stop();
		gate.stop();
	}

	/**
	 * check if all results are consistent with the chosen logical operation
	 * @param l
	 * @param sentData
	 * @param receivedData
	 */
	private void testOperations(FuzzyLogic l, 
			ArrayList<Float[]>sentData, ArrayList<Float> receivedData){

		System.out.println("data size "+sentData.size()+" "+receivedData.size());
		
		for(int i=0; i<sentData.size(); i++){
			System.out.println("checking no."+i+" of: "+sentData.size()+" => operation("+
					sentData.get(i)[0]+","+sentData.get(i)[1]+") == "+receivedData.get(i));
			assertTrue(
					receivedData.get(i) ==
					l.compute(sentData.get(i)[0], sentData.get(i)[1]));
		}
	}

	/**
	 * Generates random data from given range and appends to out 
	 * @param out list of data to be extended, has to contain at least one sample!
	 * @param howMuch how much samples to make
	 * @param range range (e.g. 100 means -50;50), -1 means range in <0,1>
	 */
	private void appendRandomData(ArrayList<Float[]> out, int howMuch, int range){
		assertTrue(out.size()>0);
		
		Float[] tmp = out.get(out.size()-1).clone();	// get the last sample
		for(int i=0; i<howMuch; i++){
			// randomly choose which one to randomly generate
			if(range>0)
				tmp[r.nextInt(2)] = range/2-r.nextFloat()*range;
			else
				tmp[r.nextInt(2)] = r.nextFloat();
			out.add(tmp.clone());		
		}
	}



	private MisoFuzzyGateTester startMisoFuzzyGateTester(RosRunner rr){
		NodeMain node = rr.getNode();
		MisoFuzzyGateTester mt = null;
		if(node instanceof MisoFuzzyGateTester){
			mt = (MisoFuzzyGateTester)node;
		}else{
			fail("Launched node MisoFuzzyGateTester, but found this one!: "+
					node.getClass().getCanonicalName());
			return null;
		}
		return mt;
	}

}
