package org.hanns.logic.gates;

import org.ros.node.topic.Subscriber;

/**
 * 
 * Abstract logical MISO gate extends SISO gate with one input.
 * 
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoAbstractGate<T> extends SisoAbstractGate<T> {

	protected Subscriber<T> subscriberA, subscriberB;
	
	// data input topics
	public final String inBT = "logic/gates/inb";
	public final String inCT = "logic/gates/inc";
	public final String indDT = "logic/gates/ind";
	public final String indET = "logic/gates/ine";
	public final String indFT = "logic/gates/inf";
	
	// data output topic 
	public final String outBT = "logic/gates/outb";
	public final String outCT = "logic/gates/outc";
	public final String outDT = "logic/gates/outd";
	public final String outET = "logic/gates/oute";
	public final String outFT = "logic/gates/outf";
	
	// configuration input topics
	public final String confBT = "logic/gates/confb";
	public final String confCT = "logic/gates/confc";
	public final String confDT = "logic/gates/confd";
	public final String confET = "logic/gates/confe";
	public final String confFT = "logic/gates/conff";
}
