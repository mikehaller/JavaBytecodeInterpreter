package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * <pre>
 *     CONSTANT_String_info {
 *     	u1 tag;
 *     	u2 string_index;
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantStringInfo {

	private int stringIndex;

	public ConstantStringInfo(DataInputStream dis) throws IOException {
		stringIndex = dis.readUnsignedShort();
	}

	@Override
	public String toString() {
		return String.format("->%d", stringIndex);
	}

	public int getStringIndex() {
		return stringIndex;
	}

}
