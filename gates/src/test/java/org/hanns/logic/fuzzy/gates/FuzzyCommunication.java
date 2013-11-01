package org.hanns.logic.fuzzy.gates;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import org.ros.node.NodeMain;

import ctu.nengoros.RosRunner;

/**
 * Test the correct communication and operation of all Fuzzy Logic Gates.
 * 
 * @author Jaroslav Vitku
 */
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

	
	@Test
	public void FuzzyNOT(){
		
		RosRunner gate = runNode("org.hanns.logic.fuzzy.gates.impl.NOT");
		
		RosRunner rr = runNode("org.hanns.logic.fuzzy.gates.SisoFuzzyGateTester");
		NodeMain node = rr.getNode();
		SisoFuzzyGateTester mt = null;
		if(node instanceof SisoFuzzyGateTester){
			mt = (SisoFuzzyGateTester)node;
		}else{
			fail("Launched node SisoFuzzyGateTester, but found this one!: "+
					node.getClass().getCanonicalName());
		}
		assertTrue(gate.isRunning());
		assertTrue(rr.isRunning());

		ArrayList<Float> receivedData = new ArrayList<Float>();
		ArrayList<Float> sentData = new ArrayList<Float>(50);
		
		appendRandomFloatData(sentData,30,-1);
		appendRandomFloatData(sentData,10,5);
		appendRandomFloatData(sentData,5,100);

		for(int i=0; i<sentData.size(); i++){
			receivedData.add(mt.computeRemotely(sentData.get(i)));
		}
		
		assertEquals(sentData.size(), receivedData.size());

		FuzzyLogic l = new FuzzyLogic(2);
		
		for(int i=0; i<sentData.size(); i++){
			System.out.println("checking no."+i+" of: "+sentData.size()+" => operation("+
					sentData.get(i) +" == "+receivedData.get(i));
			assertTrue(
					receivedData.get(i) == l.compute(sentData.get(i),0));
		}
		
		// after switching these two lines, core throws warning "no publisher for topic.."
		rr.stop();	
		gate.stop();
		
		assertFalse(gate.isRunning());
		assertFalse(rr.isRunning());
	}
	
	
	private void misoFuzzyGateTest(String gateClassName, int operationNo){
		// run the node for testing
		RosRunner gate = runNode(gateClassName);

		RosRunner rr = runNode("org.hanns.logic.fuzzy.gates.MisoFuzzyGateTester");
		MisoFuzzyGateTester mt = startMisoFuzzyGateTester(rr);

		ArrayList<Float> receivedData = new ArrayList<Float>();
		
		ArrayList<Float[]> sentData = new ArrayList<Float[]>(50);
		
		float a = r.nextFloat(), b=r.nextFloat();
		Float[] tmp = new Float[]{new Float(a), new Float(b)};
		tmp[r.nextInt(2)] = new Float(0);	// at least one value should be 0 at the beginning
		sentData.add(tmp.clone());
				
		this.appendRandomData(sentData,30,-1);
		this.appendRandomData(sentData,10,5);
		this.appendRandomData(sentData,5,100);

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

		for(int i=0; i<sentData.size(); i++){
			System.out.println("checking no."+i+" of: "+sentData.size()+" => operation("+
					sentData.get(i)[0]+","+sentData.get(i)[1]+") == "+receivedData.get(i));
			assertTrue(
					receivedData.get(i) ==
					l.compute(sentData.get(i)[0], sentData.get(i)[1]));
		}
	}

	private void appendRandomFloatData(ArrayList<Float> out, int howMuch, int range){
		
		for(int i=0; i<howMuch; i++){
			
			if(range>0)
				out.add(range/2-r.nextFloat()*range);
			else
				out.add(r.nextFloat());
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
