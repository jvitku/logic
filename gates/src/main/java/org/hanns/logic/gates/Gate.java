package org.hanns.logic.gates;

import org.apache.commons.logging.Log;
import org.ros.node.AbstractNodeMain;
import org.ros.node.topic.Publisher;

/**
 * Abstract logical gate.
 * 
 * @author Jaroslav Vitku
 *
 * @param <T>
 */
public abstract class Gate<T> extends AbstractNodeMain {

	protected boolean SEND = false; 
	
	// everything handled by listeners
	protected final int sleepTime = 100;

	protected Publisher<T> publisher;

	protected Log log;

	public final String yT = "logic/gates/outa";

	protected volatile boolean inited = false;
	
	protected abstract void send();

}
