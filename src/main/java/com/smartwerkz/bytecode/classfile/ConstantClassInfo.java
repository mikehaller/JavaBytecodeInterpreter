package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 * 		 CONSTANT_Class_info {
 * 		    	u1 tag;
 * 		    	u2 name_index;
 * 		    }
 * 
 * </pre>
 * 
 * @author mhaller
 */
public class ConstantClassInfo {

	private int nameIndex;

	public ConstantClassInfo(DataInputStream dis) throws IOException {
		nameIndex = dis.readUnsignedShort();
	}
	
	@Override
	public String toString() {
		return "Class->"+nameIndex;
	}

	public int getIndex() {
		return nameIndex;
	}

}
