package org.hanns.logic.crisp.synchronousGates.impl;

import org.hanns.logic.crisp.synchronousGates.MisoGate;
import org.ros.namespace.GraphName;

public class OR extends MisoGate{

	@Override
	public boolean compute(boolean a, boolean b) { 
		
		System.out.println("OR: computing "+a+" and "+b);
		return (a | b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("OR"); }
}
