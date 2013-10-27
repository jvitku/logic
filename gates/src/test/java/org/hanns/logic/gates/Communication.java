package org.hanns.logic.gates;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ctu.nengoros.Jroscore;
import ctu.nengoros.RosRunner;

public class Communication {

	static Jroscore jr;

	public static final int runTime = 500;
	
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
	

	/**
	 * Test whether correct node can be instantiated, started, stopped..
	 */
	@Test
	public void launchGenuineNode(){
		assertTrue(jr.isRunning());
		RosRunner rr = null;
		
		try {
			rr = new RosRunner("org.hanns.logic.gates.DemoPublisher");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Common node could not be launched, classpath problems possibly..?");
		}
		assertFalse(rr.isRunning());
		rr.start();
		assertTrue(rr.isRunning());
		
		sleep(runTime);
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}
	
	@Test
	public void launchMisoTester(){
		assertTrue(jr.isRunning());
		RosRunner rr = null;
		
		try {
			rr = new RosRunner("org.hanns.logic.gates.MisoTester");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Common node could not be launched, classpath problems possibly..?");
		}
		assertFalse(rr.isRunning());
		rr.start();
		assertTrue(rr.isRunning());
		
		sleep(runTime);
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}/**/
	
	
/*
	@Test
	public void and() {
		
		if(true)
			return;
		
		String[] argv = new String[]{"org.hanns.logic.gates.DemoPublisher"};
		//try {
		//RosRun.main(new String[]{"ctu.hanns.logic.gates.MisoTester"});

		CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));
		String nodeClassName = loader.getNodeClassName();
		System.out.println("Loading node class: " + loader.getNodeClassName());
		NodeConfiguration nodeConfiguration = loader.build();

		NodeMain nodeMain = null;
		try {
			nodeMain = loader.loadClass(nodeClassName);
		} catch (ClassNotFoundException e) {
			throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
		} catch (InstantiationException e) {
			throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
		} catch (IllegalAccessException e) {
			throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
		}

		Preconditions.checkState(nodeMain != null);
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		nodeMainExecutor.execute(nodeMain, nodeConfiguration);

		//} catch (Exception e) {
		//	e.printStackTrace();
		//	fail("could not start tester node");
		//	}

	}/**/


	@AfterClass
	public static void stopCore(){
		System.out.println("=============== Stopping the core after tests!!!!");
		assertTrue(jr.isRunning());
		jr.shutDown();
		assertFalse(jr.isRunning());
	}
}
