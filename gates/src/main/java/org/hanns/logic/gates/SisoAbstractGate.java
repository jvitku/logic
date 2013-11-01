package org.hanns.logic.gates;

import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class SisoAbstractGate<T> extends Gate<T> {

	// data communication channel
	protected Subscriber<T> subscriberA;
	public final String aT = "logic/gates/ina";	// topic
	
	// data configuration topic (optional)
	public final String acT = "logic/gates/inca";
	
	// output data channel
	protected Publisher<T> publisherA;
	public final String outaT = "logic/gates/outa";
	
	
	/**
	 * implement this in order to make computation 
	 * @param a input value A
	 * @return output value Y
	 */
	//public abstract boolean compute(T a);

}
