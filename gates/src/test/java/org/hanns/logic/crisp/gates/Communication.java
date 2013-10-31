package org.hanns.logic.crisp.gates;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import org.ros.node.NodeMain;

import ctu.nengoros.RosRunner;


public class Communication extends ctu.nengoros.nodes.RosCommunicationTest{
	
	@Test
	public void launchDemoNode(){
		RosRunner rr = runNode("org.hanns.logic.crisp.gates.DemoPublisher");
		//NodeMain node = rr.getNode();
		assertTrue(rr.isRunning());
		
		sleep(100);
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}
	/**/

	@Test 
	public void launchMisoGateTester(){
		RosRunner rr = runNode("org.hanns.logic.crisp.gates.MisoGateTester");
		
		MisoGateTester mgt = startMisoGateTester(rr);
		mgt.requireGateRunning = false; // we do not have any gate running here..
		
		assertTrue(rr.isRunning());
		
		mgt.awaitCommunicationReady();
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}/**/
	

	@Test
	public void and(){
		misoGateTest("org.hanns.logic.crisp.gates.impl.AND", 0);
	}

	@Test
	public void nand(){
		misoGateTest("org.hanns.logic.crisp.gates.impl.NAND", 1);
	}

	@Test
	public void or(){
		misoGateTest("org.hanns.logic.crisp.gates.impl.OR", 2);
	}

	@Test
	public void xor(){
		misoGateTest("org.hanns.logic.crisp.gates.impl.XOR", 3);
	}
	@Test
	public void not(){
		// run the node for testing
		RosRunner gate = runNode("org.hanns.logic.crisp.gates.impl.NOT");

		RosRunner rr = runNode("org.hanns.logic.crisp.gates.SisoGateTester");

		NodeMain node = rr.getNode();
		SisoGateTester st = null;

		if(node instanceof SisoGateTester){
			st = (SisoGateTester)node;
		}else{
			fail("Launched node SisoGateTester, but found this one!: "+
					node.getClass().getCanonicalName());
		}

		ArrayList<Boolean> sentData = new ArrayList<Boolean>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		boolean sent = false;

		// generate randomly 100 samples and send them
		int noSamples = 100;
		Random r = new Random();

		for(int i=0; i<noSamples; i++){
			sent = r.nextBoolean();
			sentData.add(sent);
			boolean tmp = st.computeRemotely(sent);
			receivedData.add(tmp);
		}

		System.out.println("Simulation ENDED, so checking data now!!!");

		assertEquals(sentData.size(), receivedData.size());

		for(int i=0; i<sentData.size(); i++){
			assertEquals(receivedData.get(i),Logic.not(sentData.get(i)));
		}

		rr.stop();
		gate.stop();
	}
/**/
	private void misoGateTest(String gateClassName, int operationNo){
		// run the node for testing
		RosRunner gate = runNode(gateClassName);

		RosRunner rr = runNode("org.hanns.logic.crisp.gates.MisoGateTester");
		MisoGateTester mt = startMisoGateTester(rr);

		ArrayList<Boolean[]> sentData = new ArrayList<Boolean[]>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		this.obtainData(mt, sentData, receivedData);

		System.out.println("Simulation ENDED, so checking data now!!!");

		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new Logic(operationNo), sentData, receivedData);

		rr.stop();
		gate.stop();
	}



	/**
	 * check if all results are consistent with the chosen logical operation
	 * @param l
	 * @param sentData
	 * @param receivedData
	 */
	private void testOperations(Logic l, 
			ArrayList<Boolean[]>sentData, 
			ArrayList<Boolean> receivedData){

		for(int i=0; i<sentData.size(); i++){
			System.out.println("checking no."+i+" of: "+sentData.size()+" => ("+
					sentData.get(i)[0]+","+sentData.get(i)[1]+") == "+receivedData.get(i));
			assertEquals(
					receivedData.get(i),
					l.compute(sentData.get(i)[0], sentData.get(i)[1]));
		}
	}

	private void obtainData(MisoGateTester mt,
			ArrayList<Boolean[]> sentData, 
			ArrayList<Boolean> receivedData){

		Boolean[] sent = new Boolean[]{false, false};

		// no of samples / 2
		for(int i=0; i<5; i++){
			// which input to change now?
			for(int j=0; j<2; j++){
				// flip the actual one and send it
				if(sent[j])
					sent[j] = false;
				else
					sent[j] = true;

				sentData.add(sent.clone());

				receivedData.add(mt.computeRemotely(sent[0], sent[1]));
			}
		}
	}

	private MisoGateTester startMisoGateTester(RosRunner rr){
		NodeMain node = rr.getNode();
		MisoGateTester mt = null;
		if(node instanceof MisoGateTester){
			mt = (MisoGateTester)node;
		}else{
			fail("Launched node MisoGateTester, but found this one!: "+
					node.getClass().getCanonicalName());
			return null;
		}
		return mt;
	}

}

