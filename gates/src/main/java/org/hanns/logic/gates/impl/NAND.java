package org.hanns.logic.gates.impl;

import org.hanns.logic.gates.MisoGate;
import org.ros.namespace.GraphName;

public class NAND extends MisoGate{

	@Override
	public boolean copute(boolean a, boolean b) { return !(a && b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("NAND"); }
}
