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
public class ConstantLongInfo {

	private long value;

	public ConstantLongInfo(DataInputStream dis) throws IOException {
		value = dis.readLong();
	}

	@Override
	public String toString() {
		return "Long '"+value+"'";
	}

	public long getValue() {
		return value;
	}
}
