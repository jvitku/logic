package org.hanns.logic.crisp.synchronousGates.impl;

import org.hanns.logic.crisp.synchronousGates.MisoGate;
import org.ros.namespace.GraphName;

public class AND extends MisoGate{

	@Override
	public boolean compute(boolean a, boolean b) { 

		System.out.println("AND: computing "+a+" and "+b);
		return (a ||  b);
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("AND"); }

}
