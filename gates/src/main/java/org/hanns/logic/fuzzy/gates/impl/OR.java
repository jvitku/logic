package org.hanns.logic.fuzzy.gates.impl;

import org.hanns.logic.fuzzy.gates.MisoGate;
import org.ros.namespace.GraphName;

public class OR extends MisoGate{

	@Override
	public float compute(float a, float b) { return Math.max(a, b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyOR"); }
}
