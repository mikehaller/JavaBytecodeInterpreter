package com.smartwerkz.bytecode.analysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class NullArithmeticExceptionNullTest {
	public static boolean calc(int x, int y) {
		if (x / y == 5)
			return true;
		return false;
	}

	@Test
	public void test_12_0() throws Exception {
		assertTrue(calc(14,0));
	}

}
