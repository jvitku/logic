package org.hanns.logic.gates;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.CountDownPublisherListener;
import org.ros.node.topic.CountDownSubscriberListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * This thing is able to test ROS node which is a type of MISO logic gate.
 * 
 * It registers publishers/subscriber and awaits someone to connect to everything. 
 * 
 * Then the method computeRemotely can test computations on remote ROS node. 
 * 
 * Since the communication is asynchronous and both publishers are independent, this will work 
 * only if one publisher is changed at a time.  
 *  
 * @author Jaroslav Vitku
 *
 */
public class MisoTester extends AbstractNodeMain {


	// ROS stuff
	Publisher<std_msgs.Bool> publisherA, publisherB;
	Subscriber<std_msgs.Bool> subscriber;
	Log log;

	public final String aT = "logic/gates/ina";		// output topic in fact here
	public final String bT = "logic/gates/inb";
	public final String yT = "logic/gates/outa";	// input..

	private ArrayList<Boolean> received;
	private ArrayList<Boolean[]> sent;

	private volatile boolean waiting = false;	// waiting for response (solution) from a ROS node
	private volatile boolean inited = false;

	private final int waittime = 100;	
	private final int maxtime = 10000;	// wait 5 seconds max for a response

	private boolean lastReceived = false;
	
	public boolean isInited(){ return inited; }

	/**
	 * One message sent, one message should arrive, so wait for it. 
	 */
	public void awaitMessage(){
		int poc = 0;

		while(waiting){
			if(waittime*poc++ > maxtime){
				fail("Message from ROS node not received in given time of "+maxtime+" ms");
				System.out.println("Message from ROS node not received in given time of "+maxtime+" ms");
				return;
			}
			
			try {
				Thread.sleep(waittime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail("Could not sleep");
			}
		}
	}

	private void awaitInit(){
		int poc = 0;

		while(!inited){
			if(poc++*waittime > maxtime){
				System.err.println("MISOTester: giving up waiting!!");
				fail("MISOTester: giving up waiting for init");
				return;	
			}
			System.out.println("MisoTester: waiting for INIT.. "+waittime*poc++ +" ms "+inited);
			try {
				Thread.sleep(waittime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail("Could not sleep");
			}
		}
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("MisoTesterNode"); }


	/**
	 * This method is allowed to change only ONE value at a time, because of 
	 * asynchronous inputs. The test case should also wait between changing values 
	 * some reasonable time, that is: MisoGate.getSleepTime+[time for communication] 
	 * 
	 * @param val value to be set
	 * @param inputA do we want to change the input A?
	 */
	public boolean computeRemotely(Boolean val, Boolean inputA){

		this.awaitInit();

		if(inputA){
			std_msgs.Bool mess = publisherA.newMessage();

			mess.setData(val);
			waiting = true;
			publisherA.publish(mess);
			this.awaitMessage();
			return this.lastReceived;
		}else{
			std_msgs.Bool mess = publisherB.newMessage();	
			mess.setData(val);
			waiting = true;
			publisherB.publish(mess);
			this.awaitMessage();
			return this.lastReceived;
		}
	}/**/

	public ArrayList<Boolean> getReceived(){ return received; }
	public ArrayList<Boolean[]> getSent(){ return sent; }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		log = connectedNode.getLog();

		// build publishers
		publisherA = connectedNode.newPublisher(aT, std_msgs.Bool._TYPE);
		publisherB = connectedNode.newPublisher(bT, std_msgs.Bool._TYPE);

		// check their state
		CountDownPublisherListener<std_msgs.Bool> cdplA = CountDownPublisherListener.<std_msgs.Bool>newDefault();
		publisherA.addListener(cdplA);
		CountDownPublisherListener<std_msgs.Bool> cdplB = CountDownPublisherListener.<std_msgs.Bool>newDefault();
		publisherA.addListener(cdplB);

		// build subscriber
		subscriber = connectedNode.newSubscriber(yT,std_msgs.Bool._TYPE);
		// check the subscribers status
		CountDownSubscriberListener<std_msgs.Bool> sl = CountDownSubscriberListener.<std_msgs.Bool>newDefault();
		subscriber.addSubscriberListener(sl);

		subscriber.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(std_msgs.Bool message) {
				boolean b = message.getData();
				lastReceived = b;
				waiting = false;
			}
		});

		// wait for  publishers to be registered by the master
		try {
			cdplA.awaitMasterRegistrationSuccess();
			cdplB.awaitMasterRegistrationSuccess();
		} catch (InterruptedException e1) {
			fail("Could not register one or both publishers to the master!");
			e1.printStackTrace();
		}

		// for both publishers: we should have connected one subscriber, wait for them!
		try {
			cdplA.awaitNewSubscriber();
		} catch (InterruptedException e1) {
			fail("Could not find any subsscriber for publisherA, did you start any?");
			System.err.println("Could not find any subsscriber for publisherA, did you start any?");
			e1.printStackTrace();
		}
		try {
			cdplB.awaitNewSubscriber();
		} catch (InterruptedException e1) {
			fail("Could not find any subsscriber for publisherB, did you start any?");
			System.err.println("Could not find any subsscriber for publisherB, did you start any?");
			e1.printStackTrace();
		}

		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX publisher(s) registered");
		
		// subscriber started already?
		try {
			sl.awaitMasterRegistrationSuccess();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("could not register with a master!!!");
			System.err.println("could not register with a master!!!");
		}
		// one publisher (the tested one) should be found here, wait for him!
		try {
			sl.awaitNewPublisher();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Error waiting new publisher for me!!!");
			fail("Error waiting new publisher for me!!!");
		}

		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  subscriber(s) inited");
		this.inited = true;

		connectedNode.executeCancellableLoop(new CancellableLoop() {

			@Override
			protected void loop() throws InterruptedException {

				//log.info("MisoTester: hi, I am still here");
				//System.out.println("MisoTester: hi, I am still here");
				Thread.sleep(100);
			}
		});
	}

	@Override
	public void onError(Node node, Throwable throwable) {
		System.err.println("On Error method was called, exiting!");
		fail("On Error method was called, exiting!");
	}

}


