package org.hanns.logic.crisp.gates;

import static org.junit.Assert.*;

import org.hanns.logic.crisp.gates.impl.AND;
import org.hanns.logic.crisp.gates.impl.NAND;
import org.hanns.logic.crisp.gates.impl.NOT;
import org.hanns.logic.crisp.gates.impl.OR;
import org.hanns.logic.crisp.gates.impl.XOR;
import org.junit.Test;

/**
 * Test the method solve for all gates
 * 
 * @author Jaroslav Vitku
 */
public class Operations{
	@Test
	public void and(){
		AND a = new AND();
		assertTrue(a.compute(true, true));
		assertFalse(a.compute(true, false));
		assertFalse(a.compute(false, true));
		assertFalse(a.compute(false, false));
		
		// a bit redundant..
		assertEquals(a.compute(true, true), 	Logic.and(true, true));
		assertEquals(a.compute(true, false), Logic.and(true, false));
		assertEquals(a.compute(false, true), Logic.and(false, true));
		assertEquals(a.compute(false, false),Logic.and(false, false));
	}
	
	@Test
	public void or(){
		OR a = new OR();
		assertTrue(a.compute(true, true));
		assertTrue(a.compute(true, false));
		assertTrue(a.compute(false, true));
		assertFalse(a.compute(false, false));
		
		assertEquals(a.compute(true, true), 	Logic.or(true, true));
		assertEquals(a.compute(true, false), Logic.or(true, false));
		assertEquals(a.compute(false, true), Logic.or(false, true));
		assertEquals(a.compute(false, false),Logic.or(false, false));
	}

	@Test
	public void xor(){
		XOR a = new XOR();	
		assertFalse(a.compute(true, true));
		assertTrue(a.compute(true, false));
		assertTrue(a.compute(false, true));
		assertFalse(a.compute(false, false));
		
		assertEquals(a.compute(true, true), 	Logic.xor(true, true));
		assertEquals(a.compute(true, false), Logic.xor(true, false));
		assertEquals(a.compute(false, true), Logic.xor(false, true));
		assertEquals(a.compute(false, false),Logic.xor(false, false));
	}

	@Test
	public void nand(){
		NAND a = new NAND();	
		assertFalse(a.compute(true, true));
		assertTrue(a.compute(true, false));
		assertTrue(a.compute(false, true));
		assertTrue(a.compute(false, false));
		
		assertEquals(a.compute(true, true), 	Logic.nand(true, true));
		assertEquals(a.compute(true, false), Logic.nand(true, false));
		assertEquals(a.compute(false, true), Logic.nand(false, true));
		assertEquals(a.compute(false, false),Logic.nand(false, false));
	}
	
	@Test
	public void not(){
		NOT a = new NOT();	
		assertFalse(a.compute(true));
		assertTrue(a.compute(false));
		
		assertEquals(a.compute(true), Logic.not(true));
		assertEquals(a.compute(false), Logic.not(false));
	}
}
