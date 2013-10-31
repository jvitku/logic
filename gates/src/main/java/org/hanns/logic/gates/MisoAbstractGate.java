package org.hanns.logic.gates;

import org.ros.node.topic.Subscriber;

/**
 * 
 * Abstract logical MISO gate extends SISO gate with one input.
 * 
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoAbstractGate<T> extends Gate<T> {

	protected Subscriber<T> subscriberA, subscriberB;
	public final String bT = "logic/gates/inb";
	public final String aT = "logic/gates/ina";
	
	//public abstract boolean compute(T a, T b);
}
