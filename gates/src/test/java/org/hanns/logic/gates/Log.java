package org.hanns.logic.gates;

public class Log {

	public static boolean not(boolean a){ return !a; };
	
	public static boolean xor(boolean a, boolean b){ return a ^ b; }
	
	public static boolean or(boolean a, boolean b){ return a | b; }
	
	public static boolean and(boolean a, boolean b){ return a & b; }
	
	public static boolean nand(boolean a, boolean b){ return !(a & b); }
	
}
