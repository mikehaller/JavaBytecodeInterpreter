package org.example.junit;

import junit.framework.TestCase;

import org.junit.Test;

public class SimpleJUnit3Test extends TestCase {

	@Test
	public void testFoo() throws Exception {
		assertTrue(true);
	}

	@Test
	public void testBar() throws Exception {
		assertTrue(false);
	}
	
}
