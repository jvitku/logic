package org.hanns.logic.gates;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ros.node.NodeMain;

import ctu.nengoros.Jroscore;
import ctu.nengoros.RosRunner;

public class Communication {

	static Jroscore jr;

	public static final int runTime = 300; // just start and stop it

	@BeforeClass
	public static void startCore(){
		System.out.println("=============== Starting the core to run the network testing!!!!");
		jr = new Jroscore();

		assertFalse(jr.isRunning());
		jr.start();
		assertTrue(jr.isRunning());
	}

	private void sleep(int howlong){
		try {
			Thread.sleep(howlong);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("could not sleep");
		}
	}
/**/
	@Test
	public void launchGenuineNode(){
		RosRunner rr = runNode("org.hanns.logic.gates.DemoPublisher");

		sleep(runTime);

		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}

	@Test
	public void launchMisoTester(){
		RosRunner rr = runNode("org.hanns.logic.gates.MisoTester");

		sleep(runTime);

		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}/**/

	@Test
	public void testAndNode(){

		// run the node for testing
		RosRunner gateAND = runNode("org.hanns.logic.gates.impl.AND");
		
		RosRunner rr = runNode("org.hanns.logic.gates.MisoTester");
		MisoTester mt = startMisoTester(rr);

		ArrayList<Boolean[]> sentData = new ArrayList<Boolean[]>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		this.obtainData(mt, sentData, receivedData);

		System.out.println("Simulation ENDED, so checking data now!!!");
		
		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new Logic(0), sentData, receivedData);
		
		rr.stop();
		gateAND.stop();
	}


	@Test
	public void testNandNode(){

		// run the node for testing
		RosRunner gateNAND = runNode("org.hanns.logic.gates.impl.NAND");
		
		RosRunner rr = runNode("org.hanns.logic.gates.MisoTester");
		MisoTester mt = startMisoTester(rr);

		ArrayList<Boolean[]> sentData = new ArrayList<Boolean[]>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		this.obtainData(mt, sentData, receivedData);

		System.out.println("Simulation ENDED, so checking data now!!!");
		
		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new Logic(1), sentData, receivedData);
		
		rr.stop();
		gateNAND.stop();
	}
	
	@Test
	public void testOrNode(){

		// run the node for testing
		RosRunner gateOR = runNode("org.hanns.logic.gates.impl.OR");
		
		RosRunner rr = runNode("org.hanns.logic.gates.MisoTester");
		MisoTester mt = startMisoTester(rr);

		ArrayList<Boolean[]> sentData = new ArrayList<Boolean[]>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		this.obtainData(mt, sentData, receivedData);

		System.out.println("Simulation ENDED, so checking data now!!!");
		
		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new Logic(2), sentData, receivedData);
		
		rr.stop();
		gateOR.stop();
	}
	
	@Test
	public void testXorNode(){

		// run the node for testing
		RosRunner gateXOR = runNode("org.hanns.logic.gates.impl.XOR");
		
		RosRunner rr = runNode("org.hanns.logic.gates.MisoTester");
		MisoTester mt = startMisoTester(rr);

		ArrayList<Boolean[]> sentData = new ArrayList<Boolean[]>(); 
		ArrayList<Boolean> receivedData = new ArrayList<Boolean>();

		this.obtainData(mt, sentData, receivedData);

		System.out.println("Simulation ENDED, so checking data now!!!");
		
		assertEquals(sentData.size(), receivedData.size());

		this.testOperations(new Logic(3), sentData, receivedData);
		
		rr.stop();
		gateXOR.stop();
	}
	
	
	private void obtainData(MisoTester mt,
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
				
				receivedData.add(mt.computeRemotely(sent[j], j==0));
			}
		}
	}

	private MisoTester startMisoTester(RosRunner rr){
		NodeMain node = rr.getNode();
		MisoTester mt = null;
		if(node instanceof MisoTester){
			mt = (MisoTester)node;
		}else{
			fail("Launched node MisoTester, but found this one!: "+
					node.getClass().getCanonicalName());
			return null;
		}
		return mt;
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

	/**
	 * Run a given node and check if it is running.
	 * 
	 * @param which name of the node
	 * @return RosRunner instance with running node
	 */
	private RosRunner runNode(String which){
		assertTrue(jr.isRunning());
		RosRunner rr = null;

		try {
			rr = new RosRunner(which);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Node named: "+which+" could not be launched..");
		}
		assertFalse(rr.isRunning());
		rr.start();
		assertTrue(rr.isRunning());
		return rr;
	}


	@AfterClass
	public static void stopCore(){
		System.out.println("=============== Stopping the core after tests!!!!");
		assertTrue(jr.isRunning());
		jr.shutDown();
		assertFalse(jr.isRunning());
	}
}
