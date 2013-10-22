package org.hanns.logic.gates.impl;

import org.hanns.logic.gates.SisoGate;
import org.ros.namespace.GraphName;

public class NOT extends SisoGate{
	
	@Override
	public boolean copute(boolean a) { return !a; }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("OR"); }

}
