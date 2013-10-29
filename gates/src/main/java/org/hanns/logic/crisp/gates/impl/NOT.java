package org.hanns.logic.crisp.gates.impl;

import org.hanns.logic.crisp.gates.SisoGate;
import org.ros.namespace.GraphName;

public class NOT extends SisoGate{
	
	@Override
	public boolean copute(boolean a) { return !a; }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("NOT"); }

}
