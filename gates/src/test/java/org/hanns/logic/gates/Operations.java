package org.hanns.logic.gates;

import static org.junit.Assert.*;
import org.hanns.logic.gates.impl.AND;
import org.hanns.logic.gates.impl.NAND;
import org.hanns.logic.gates.impl.NOT;
import org.hanns.logic.gates.impl.OR;
import org.hanns.logic.gates.impl.XOR;
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
		assertTrue(a.copute(true, true));
		assertFalse(a.copute(true, false));
		assertFalse(a.copute(false, true));
		assertFalse(a.copute(false, false));
		
		// a bit redundant..
		assertEquals(a.copute(true, true), 	Logic.and(true, true));
		assertEquals(a.copute(true, false), Logic.and(true, false));
		assertEquals(a.copute(false, true), Logic.and(false, true));
		assertEquals(a.copute(false, false),Logic.and(false, false));
	}
	
	@Test
	public void or(){
		OR a = new OR();
		assertTrue(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertFalse(a.copute(false, false));
		
		assertEquals(a.copute(true, true), 	Logic.or(true, true));
		assertEquals(a.copute(true, false), Logic.or(true, false));
		assertEquals(a.copute(false, true), Logic.or(false, true));
		assertEquals(a.copute(false, false),Logic.or(false, false));
	}

	@Test
	public void xor(){
		XOR a = new XOR();	
		assertFalse(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertFalse(a.copute(false, false));
		
		assertEquals(a.copute(true, true), 	Logic.xor(true, true));
		assertEquals(a.copute(true, false), Logic.xor(true, false));
		assertEquals(a.copute(false, true), Logic.xor(false, true));
		assertEquals(a.copute(false, false),Logic.xor(false, false));
	}

	@Test
	public void nand(){
		NAND a = new NAND();	
		assertFalse(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertTrue(a.copute(false, false));
		
		assertEquals(a.copute(true, true), 	Logic.nand(true, true));
		assertEquals(a.copute(true, false), Logic.nand(true, false));
		assertEquals(a.copute(false, true), Logic.nand(false, true));
		assertEquals(a.copute(false, false),Logic.nand(false, false));
	}
	
	@Test
	public void not(){
		NOT a = new NOT();	
		assertFalse(a.copute(true));
		assertTrue(a.copute(false));
		
		assertEquals(a.copute(true), Logic.not(true));
		assertEquals(a.copute(false), Logic.not(false));
	}
}
