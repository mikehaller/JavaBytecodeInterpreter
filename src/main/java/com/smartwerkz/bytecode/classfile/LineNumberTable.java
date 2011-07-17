package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * <pre>
 *    LineNumberTable_attribute {
 *     	u2 attribute_name_index;
 *     	u4 attribute_length;
 *     	u2 line_number_table_length;
 *     	{  u2 start_pc;	     
 *     	   u2 line_number;	     
 *     	} line_number_table[line_number_table_length];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class LineNumberTable {

	private TreeMap<Integer,Integer> pc2line = new TreeMap<Integer, Integer>(); 
	
	public LineNumberTable(AttributeInfo attribute) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(attribute.getValue()));
		try {
			int lineNumberTableLength = dis.readUnsignedShort();
			for (int i = 0; i < lineNumberTableLength; i++) {
				int startPc = dis.readUnsignedShort();
				int lineNumber = dis.readUnsignedShort();
				pc2line.put(startPc,lineNumber);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int lineNumberForProgramCounter(int programCounter) {
		Entry<Integer, Integer> higherEntry = pc2line.lowerEntry(programCounter);
		if (higherEntry==null) {
			return -1;
		}
		return higherEntry.getValue();
	}
}
