package org.hanns.logic.gates;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class MisoTester extends AbstractNodeMain {

	// node setup
	private final boolean SEND = false;
	private final int sleepTime = 1000;

	// ROS stuff
	Publisher<std_msgs.Bool> publisherA, publisherB;
	Subscriber<std_msgs.Bool> subscriber;
	Log log;

	public final String aT = "logic/gates/ina";		// output topic in fact here
	public final String bT = "logic/gates/inb";
	public final String yT = "logic/gates/outa";	// input..

	private volatile boolean inited = false;

	private ArrayList<Boolean> received;
	private ArrayList<Boolean[]> sent;

	private Boolean[] lastSent;

	public boolean isInited(){ return inited; }

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("MisoTesterNode"); }
	

	/**
	 * This method is allowed to change only ONE value at a time, because of 
	 * asynchronous inputs. The test case should also wait between changing values 
	 * some reasonable time, that is: MisoGate.getSleepTime+[time for communication] 
	 * 
	 * @param val value to be set
	 * @param inputA do we want to change the input A?
	 
	public void sendToCompute(Boolean val, Boolean inputA){
		if(inputA){
			std_msgs.Bool mess = publisherA.newMessage();	
			mess.setData(val);
			publisherA.publish(mess);
			lastSent[0] = val;
		}else{
			std_msgs.Bool mess = publisherB.newMessage();	
			mess.setData(val);
			publisherB.publish(mess);
			lastSent[1] = val;
		}
		log.info("MisoTester: published this value: "+val+" to input A? "+inputA+
				" lastSent message (current state) is now: ["+lastSent[0]+","+lastSent[1]+"]");
	}*/

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		log = connectedNode.getLog();

		received = new ArrayList<Boolean>();
		sent = new ArrayList<Boolean[]>();

		lastSent = new Boolean[]{false,false};	// assume that node has zeros on inputs

		publisherA = connectedNode.newPublisher(aT, std_msgs.Bool._TYPE);
		publisherB = connectedNode.newPublisher(bT, std_msgs.Bool._TYPE);
		subscriber = connectedNode.newSubscriber(yT, std_msgs.Bool._TYPE);

		subscriber.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(std_msgs.Bool message) {
				boolean b = message.getData();
				received.add(b);
				
				log.info("MisoTester: jus received this: "+b+" and lastSent (current state) "+
						"is now ["+lastSent[0]+","+lastSent[1]+"]");
			}
		});	

		this.inited = true;
		
		connectedNode.executeCancellableLoop(new CancellableLoop() {

			@Override
			protected void loop() throws InterruptedException {
				log.info("hi, I am still here");
				Thread.sleep(100);
			}
		});
	}

}


