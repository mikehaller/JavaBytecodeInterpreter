package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *  CONSTANT_Float_info {
 *     	u1 tag;
 *     	u4 bytes;
 *     }
 * 
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantFloatInfo {

	private float value;

	public ConstantFloatInfo(DataInputStream dis) throws IOException {
		value = dis.readFloat();
	}
	@Override
	public String toString() {
		return "Float '"+value+"'";
	}
	public float getValue() {
		return value;
	}
}
