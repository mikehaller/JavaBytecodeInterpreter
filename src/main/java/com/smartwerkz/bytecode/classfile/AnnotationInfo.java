package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *   annotation {
 *         u2    type_index;
 *         u2    num_member_value_pairs;
 *         {    u2    member_name_index;
 *              member_value value;
 *         }    member_value_pairs[num_member_value_pairs];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class AnnotationInfo {

	private final List<MemberValue> pairs = new ArrayList<MemberValue>();
	private final String name;

	public AnnotationInfo(ConstantPool constantPool, DataInputStream dis) {
		try {
			int typeIndex = dis.readUnsignedShort();

			this.name =constantPool.getUTF8Info(typeIndex).getValue();

			int numPairs = dis.readUnsignedShort();
			for (int i = 0; i < numPairs; i++) {
				MemberValue memberValue = new MemberValue(constantPool, dis);
				pairs.add(memberValue);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "[name=" + name + ", pairs=" + pairs.toString() + "]";
	}

}
