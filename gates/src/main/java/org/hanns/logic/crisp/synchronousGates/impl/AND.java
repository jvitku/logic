package org.hanns.logic.crisp.synchronousGates.impl;

import org.hanns.logic.crisp.synchronousGates.MisoGate;
import org.ros.namespace.GraphName;

public class AND extends MisoGate{

	private int step = 0;
	
	@Override
	public boolean compute(boolean a, boolean b) { 

		System.out.println(step+++" AND: computing "+a+" and "+b+" = "+(a && b));
		return (a &&  b);
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("AND"); }

}
