package org.hanns.logic.crisp.gates.impl;

import org.hanns.logic.crisp.gates.MisoGate;
import org.ros.namespace.GraphName;

public class AND extends MisoGate{

	@Override
	public boolean compute(boolean a, boolean b) { return (a && b); }

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("AND"); }

}
