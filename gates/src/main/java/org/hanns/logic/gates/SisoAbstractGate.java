package org.hanns.logic.gates;

import org.ros.node.topic.Subscriber;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class SisoAbstractGate<T> extends Gate<T> {

	protected Subscriber<T> subscriberA;
	public final String aT = "logic/gates/ina";
	
	/**
	 * implement this in order to make computation 
	 * @param a input value A
	 * @return output value Y
	 */
	//public abstract boolean compute(T a);

}
