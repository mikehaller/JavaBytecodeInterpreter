package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *     CONSTANT_NameAndType_info {
 *     	u1 tag;
 *     	u2 name_index;
 *     	u2 descriptor_index;
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantNameAndTypeInfo {

	private int nameIndex;
	private int descriptorIndex;

	public ConstantNameAndTypeInfo(DataInputStream dis) throws IOException {
		nameIndex = dis.readUnsignedShort();
		descriptorIndex = dis.readUnsignedShort();
	}
	@Override
	public String toString() {
		return "Name->"+nameIndex + " Descriptor->"+descriptorIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}
	
	public int getDescriptorIndex() {
		return descriptorIndex;
	}
	
}
