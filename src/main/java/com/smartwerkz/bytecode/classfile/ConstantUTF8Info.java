package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *   CONSTANT_Utf8_info {
 *     	u1 tag;
 *     	u2 length;
 *     	u1 bytes[length];
 *     }
 * </pre>
 * 
 * @author mhaller
 * 
 */
public class ConstantUTF8Info {

	private String value;

	public ConstantUTF8Info(DataInputStream dis) throws IOException {
		value = dis.readUTF();
	}
	
	@Override
	public String toString() {
		return value;
	}

	public String getValue() {
		return value;
	}
}
