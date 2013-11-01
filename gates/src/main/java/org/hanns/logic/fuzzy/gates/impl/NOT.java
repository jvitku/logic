package org.hanns.logic.fuzzy.gates.impl;

import org.hanns.logic.fuzzy.gates.SisoGate;
import org.ros.namespace.GraphName;

public class NOT extends SisoGate{
	
	@Override
	public float compute(float a) { return 1-a; }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyNOT"); }

}
