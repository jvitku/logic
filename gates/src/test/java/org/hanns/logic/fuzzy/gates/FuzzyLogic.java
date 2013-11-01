package org.hanns.logic.fuzzy.gates;

import static org.junit.Assert.*;

public class FuzzyLogic {

	public static float and(float  a, float  b){ return Math.min(cr(a), cr(b)); }	

	public static float  or(float  a, float  b){ return Math.max(cr(a), cr(b)); }

	public static float  not(float  a){ return 1-cr(a); };

	private static float cr(float a){
		if(a<0)
			return 0;
		if(a>1)
			return 1;
		return a;
	}
	

	private final int which;

	/**
	 * Use the selected type of computation
	 * 
	 * @param which select the type of computation, from zero it is: and,nand,or,xor,not 
	 */
	public FuzzyLogic(int which){
		this.which = which;
		if(which>2 || which<0){
			System.err.println("FuzzyLogic: operation out of range!");
			fail("FuzzyLogic: compute method chosen incorrectly");
		}
	}

	public float compute(float a,float b){
		switch (which){
		case 0:
			return and(a,b);
		case 1:
			return or(a,b);
		case 2:
			return not(a);
		}
		return -1;
	}

}
