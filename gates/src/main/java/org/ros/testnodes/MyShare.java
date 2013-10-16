package org.ros.testnodes;

/**
 * This is simple demo for running the project
 * 
 * @author j
 */

public class MyShare {
	
	
	public static void main(String[] args){
		System.out.println("Hello world!");
	}
	
	/**
	 * can be accessed from another project (testing)
	 * @param a
	 */
	public void sharedPrinter(String a){
		System.out.println("shared printer: printing your message: "+a);
	}

}
