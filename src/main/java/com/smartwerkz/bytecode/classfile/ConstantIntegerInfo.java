package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *     CONSTANT_Integer_info {
 *     	u1 tag;
 *     	u4 bytes;
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantIntegerInfo {

	private int value;

	public ConstantIntegerInfo(DataInputStream dis) throws IOException {
		value = dis.readInt();
	}
	@Override
	public String toString() {
		return "Integer '"+value+"'";
	}

	public int getValue() {
		return value;
	}
	
}
