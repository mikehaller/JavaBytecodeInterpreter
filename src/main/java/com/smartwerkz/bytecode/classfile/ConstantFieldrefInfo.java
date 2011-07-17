package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 * CONSTANT_Fieldref_info {
 *     	u1 tag;
 *     	u2 class_index;
 *     	u2 name_and_type_index;
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantFieldrefInfo {
	private int classIndex;
	private int nameAndTypeIndex;

	public ConstantFieldrefInfo(DataInputStream dis) throws IOException {
		classIndex = dis.readUnsignedShort();
		nameAndTypeIndex = dis.readUnsignedShort();
	}
	
	@Override
	public String toString() {
		return "Field ->"+nameAndTypeIndex+" in class->"+classIndex;
	}
	
	public int getClassIndex() {
		return classIndex;
	}
	
	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}
}
