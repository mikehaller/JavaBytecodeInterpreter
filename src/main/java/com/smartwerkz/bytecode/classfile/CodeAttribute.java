package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 *   Code_attribute {
 *     	u2 attribute_name_index;
 *     	u4 attribute_length;
 *     	u2 max_stack;
 *     	u2 max_locals;
 *     	u4 code_length;
 *     	u1 code[code_length];
 *     	u2 exception_table_length;
 *     	{    	u2 start_pc;
 *     	      	u2 end_pc;
 *     	      	u2  handler_pc;
 *     	      	u2  catch_type;
 *     	}	exception_table[exception_table_length];
 *     	u2 attributes_count;
 *     	attribute_info attributes[attributes_count];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class CodeAttribute {

	private int maxStack;
	private int maxLocals;
	private int codeLength;
	private byte[] codeArray;
	private int exceptionTableLength;
	private int attributesCount;

	private final ExceptionTable exceptionTable = new ExceptionTable();
	private final Attributes attributes;

	public CodeAttribute(ConstantPool constantPool, byte[] value) {
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(value));
			maxStack = dis.readUnsignedShort();
			maxLocals = dis.readUnsignedShort();
			codeLength = dis.readInt();
			codeArray = new byte[codeLength];
			dis.read(codeArray);
			exceptionTableLength = dis.readUnsignedShort();
			for (int i = 0; i < exceptionTableLength; i++) {
				int startPc = dis.readUnsignedShort();
				int endPc = dis.readUnsignedShort();
				int handlerPc = dis.readUnsignedShort();
				int catchType = dis.readUnsignedShort();
				exceptionTable.registerHandler(startPc, endPc, handlerPc, catchType);
			}
			attributes = new Attributes(constantPool, dis);
			dis.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getMaxLocals() {
		return maxLocals;
	}
	
	public int getMaxStack() {
		return maxStack;
	}
	
	public byte[] getCode() {
		return codeArray;
	}

	public ExceptionTable getExceptionTable() {
		return exceptionTable;
	}

	public LineNumberTable getLineNumberTable() {
		AttributeInfo attribute = attributes.getAttribute("LineNumberTable");
		if (attribute==null) {
			return null;
		}
		return new LineNumberTable(attribute);
	}

}
