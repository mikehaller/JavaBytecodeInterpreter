package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *    member_value {
 *         u1 tag;
 *         union {
 *             u2   const_value_index;
 *             {
 *                 u2   type_name_index;
 *                 u2   const_name_index;
 *             } enum_const_value;
 *             u2   class_info_index;
 *             annotation annotation_value; 
 *             {
 *                 u2    num_values;
 *                 member_value values[num_values];
 *             } array_value;
 *         } value;
 *     }
 * </pre>
 * 
 * <pre>
 * The tag item indicates the member type of this member-value pair. The letters 'B', 'C', 'D', 'F', 'I', 'J', 'S', and 'Z' indicate a primitive type. These letters are interpreted as BaseType characters (Table 4.2). The other legal values for tag are listed with their interpretations in this table: 
 * tag value	Member Type
 * 's' 	String
 * 'e' 	enum constant
 * 'c' 	class
 * '@' 	annotation type
 * '[' 	array
 * </pre>
 * 
 * @author mhaller
 * 
 */
public class MemberValue {

	public MemberValue(ConstantPool constantPool, DataInputStream dis) {
		try {
			int tag = dis.readUnsignedByte();
			switch (tag) {
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'Z':
			case 's':
				int constValueIndex = dis.readUnsignedShort();
				Object annotationValue = constantPool.getEntry(constValueIndex);
				break;
			case 'e':
				// enum_const_value
				int typeNameIndex = dis.readUnsignedShort();
				int constNameIndex = dis.readUnsignedShort();
				break;
			case 'c':
				int classInfoIndex = dis.readUnsignedShort();
				ConstantClassInfo classInfo = constantPool.getClassInfoEntry(classInfoIndex);
				break;
			case '@': // Nsted annotation
				AnnotationInfo ai = new AnnotationInfo(constantPool, dis);
				break;
			case '[':
				int numValues = dis.readUnsignedShort();
				for (int i = 0; i < numValues; i++) {
					MemberValue memberValue = new MemberValue(constantPool, dis);
				}
				break;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
