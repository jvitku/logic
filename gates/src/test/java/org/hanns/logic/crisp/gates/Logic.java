package org.hanns.logic.crisp.gates;

import static org.junit.Assert.*;

public class Logic {

	public static boolean and(boolean a, boolean b){ return a & b; }	

	public static boolean nand(boolean a, boolean b){ return !(a & b); }

	public static boolean or(boolean a, boolean b){ return a | b; }

	public static boolean xor(boolean a, boolean b){ return a ^ b; }

	public static boolean not(boolean a){ return !a; };



	private final int which;

	/**
	 * Use the selected type of computation
	 * 
	 * @param which select the type of computation, from zero it is: and,nand,or,xor,not 
	 */
	public Logic(int which){
		this.which = which;
		if(which>4){
			System.err.println("Logic: operation out of range!");
			fail("Logic: compute method chosen incorrectly");
		}
	}

	public boolean compute(boolean a, boolean b){
		switch (which){
		case 0:
			return and(a,b);
		case 1:
			return nand(a,b);
		case 2:
			return or(a,b);
		case 3:
			return xor(a,b);
		case 4:
			return not(a);
		}
		return false;
	}

}
