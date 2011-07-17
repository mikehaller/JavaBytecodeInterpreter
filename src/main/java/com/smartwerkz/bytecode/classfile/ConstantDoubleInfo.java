package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 * 
 *     CONSTANT_Long_info {
 *     	u1 tag;
 *     	u4 high_bytes;
 *     	u4 low_bytes;
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantDoubleInfo {

	private double value;

	public ConstantDoubleInfo(DataInputStream dis) throws IOException {
		value = dis.readDouble();
	}
	@Override
	public String toString() {
		return "Double '"+value+"'";
	}
	public double getValue() {
		return value;
	}

}
