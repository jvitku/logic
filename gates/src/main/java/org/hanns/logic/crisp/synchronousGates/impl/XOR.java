package org.hanns.logic.crisp.synchronousGates.impl;

import org.hanns.logic.crisp.synchronousGates.MisoGate;
import org.ros.namespace.GraphName;

public class XOR extends MisoGate{

	@Override
	public boolean compute(boolean a, boolean b) {
		if(a & b)
			return false;
		return (a | b);
	}
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("XOR"); }
}
