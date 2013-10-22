package org.hanns.logic.gates.impl;

import org.hanns.logic.gates.MisoGate;
import org.ros.namespace.GraphName;

public class XOR extends MisoGate{

	@Override
	public boolean copute(boolean a, boolean b) {
		if(a & b)
			return false;
		return (a | b);
	}
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("XOR"); }
}
