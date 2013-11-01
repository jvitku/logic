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
	public final String bT = "logic/gates/inb";
	public final String cT = "logic/gates/inc";
	public final String dT = "logic/gates/ind";
	public final String eT = "logic/gates/ine";
	public final String fT = "logic/gates/inf";
	
	// data output topic 
	public final String outbT = "logic/gates/outb";
	public final String outcT = "logic/gates/outc";
	public final String outdT = "logic/gates/outd";
	public final String outeT = "logic/gates/oute";
	public final String outfT = "logic/gates/outf";
	
	// configuration input topics
	public final String bcT = "logic/gates/inbc";
	public final String ccT = "logic/gates/incc";
	public final String dcT = "logic/gates/indc";
	public final String ecT = "logic/gates/inec";
	public final String fcT = "logic/gates/infc";
}
