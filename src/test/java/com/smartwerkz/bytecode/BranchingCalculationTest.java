package com.smartwerkz.bytecode;

import static org.junit.Assert.*;

import org.junit.Test;

public class BranchingCalculationTest {

	@Test
	public void testOffsetCalculation3() throws Exception {
		assertEquals(-408, branch(0xfe, 0x68));
	}

	@Test
	public void testOffsetCalculation2() throws Exception {
		assertEquals(409, branch(0x01, 0x99));
	}

	public static int branch(int byte1, int byte2) {
		return branch((byte) byte1, (byte) byte2);
	}

	public static short branch(byte byte1, byte byte2) {
		int branchbyte1 = unsignedByteToInt(byte1);
		int branchbyte2 = unsignedByteToInt(byte2);
		return (short) ((branchbyte1 << 8) + (branchbyte2 << 0));
	}

	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

}
