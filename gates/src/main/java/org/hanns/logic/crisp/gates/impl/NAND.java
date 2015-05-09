package org.hanns.logic.crisp.gates.impl;

import org.hanns.logic.crisp.gates.MisoGate;
import org.ros.namespace.GraphName;

public class NAND extends MisoGate{

	@Override
	public boolean compute(boolean a, boolean b) { 
		
		System.out.println("NAND: computing "+a+" and "+b);
		return !(a && b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("NAND"); }
}
