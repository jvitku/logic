package org.hanns.logic.fuzzy.gates.impl;

import org.hanns.logic.fuzzy.gates.MisoGate;
import org.ros.namespace.GraphName;

public class AND extends MisoGate{

	@Override
	public float compute(float a, float b) { return Math.min(a, b); }

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyAND"); }

}
