package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *     attribute_info {
 *     	u2 attribute_name_index;
 *     	u4 attribute_length;
 *     	u1 info[attribute_length];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class AttributeInfo {

	private int nameIndex;
	private byte[] info;
	private String attributeName;

	public AttributeInfo(ConstantPool constantPool, DataInputStream dis) throws IOException {
		nameIndex = dis.readUnsignedShort();
		int attrLength = dis.readInt();
		info = new byte[attrLength];
		dis.read(info);
		
		attributeName = constantPool.getUTF8Info(nameIndex).getValue();
	}
	
	public int getNameIndex() {
		return nameIndex;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public byte[] getValue() {
		byte[] result = new byte[info.length];
		System.arraycopy(info, 0, result, 0, info.length);
		return result;
	}

	@Override
	public String toString() {
		return attributeName;
	}
}
