package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.smartwerkz.bytecode.vm.VMLog;

public class ConstantPool {

	private final VMLog log = new VMLog(ConstantPool.class.getName(), false);

	// Constant Pool Tags
	private static final int CONSTANT_Class = 7;
	private static final int CONSTANT_Fieldref = 9;
	private static final int CONSTANT_Methodref = 10;
	private static final int CONSTANT_InterfaceMethodref = 11;
	private static final int CONSTANT_String = 8;
	private static final int CONSTANT_Integer = 3;
	private static final int CONSTANT_Float = 4;
	private static final int CONSTANT_Long = 5;
	private static final int CONSTANT_Double = 6;
	private static final int CONSTANT_NameAndType = 12;
	private static final int CONSTANT_Utf8 = 1;

	private int constantPoolCount;

	private Map<Integer, Object> entries = new HashMap<Integer, Object>();

	public ConstantPool(DataInputStream dis) {
		try {
			constantPoolCount = dis.readUnsignedShort();

			/*
			 * The constant_pool table is indexed from 1 to
			 * constant_pool_count-1.
			 */
			log.debug("Reading %d constant pool entries", constantPoolCount);
			for (int i = 1; i <= (constantPoolCount - 1); i++) {
				int tag = dis.read();
				log.debug("Reading #%d: tag %d", i, tag);
				switch (tag) {
				case CONSTANT_Class:
					entries.put(i, new ConstantClassInfo(dis));
					break;
				case CONSTANT_Fieldref:
					entries.put(i, new ConstantFieldrefInfo(dis));
					break;
				case CONSTANT_Methodref:
					entries.put(i, new ConstantMethodrefInfo(dis));
					break;
				case CONSTANT_InterfaceMethodref:
					entries.put(i, new ConstantInterfacemethodrefInfo(dis));
					break;
				case CONSTANT_String:
					entries.put(i, new ConstantStringInfo(dis));
					break;
				case CONSTANT_Integer:
					entries.put(i, new ConstantIntegerInfo(dis));
					break;
				case CONSTANT_Float:
					entries.put(i, new ConstantFloatInfo(dis));
					break;
				case CONSTANT_Long:
					entries.put(i, new ConstantLongInfo(dis));
					entries.put(++i, null);
					break;
				case CONSTANT_Double:
					entries.put(i, new ConstantDoubleInfo(dis));
					entries.put(++i, null);
					break;
				case CONSTANT_NameAndType:
					entries.put(i, new ConstantNameAndTypeInfo(dis));
					break;
				case CONSTANT_Utf8:
					entries.put(i, new ConstantUTF8Info(dis));
					break;
				default:
					throw new IllegalArgumentException("Unknown constant pool tag: " + tag);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, Object> entry : entries.entrySet()) {
			sb.append(String.format("\n\t%d => [%s]", entry.getKey(), entry.getValue()));
		}
		return sb.toString();
	}

	public int getSize() {
		return constantPoolCount;
	}

	public Object getEntry(int index) {
		if (index < 0)
			throw new IllegalArgumentException("The index to a method in the constant pool must be positive, but is "
					+ index);
		if (!entries.containsKey(index)) {
			throw new IllegalStateException("Constant Pool does not contain a method at position " + index + ", but: "
					+ entries);
		}
		return entries.get(index);
	}

	public ConstantClassInfo getClassInfoEntry(int index) {
		return (ConstantClassInfo) entries.get(index);
	}

	public ConstantUTF8Info getUTF8Info(int index) {
		return (ConstantUTF8Info) entries.get(index);
	}

	public ConstantFieldrefInfo getFieldRef(int index) {
		return (ConstantFieldrefInfo) entries.get(index);
	}

	public ConstantNameAndTypeInfo getNameAndTypeInfo(int index) {
		return (ConstantNameAndTypeInfo) entries.get(index);
	}

	public ConstantMethodrefInfo getMethodRef(int index) {
		if (index < 0)
			throw new IllegalArgumentException("The index to a method in the constant pool must be positive, but is "
					+ index);
		if (!entries.containsKey(index)) {
			throw new IllegalStateException("Constant Pool does not contain a method at position " + index);
		}
		Object object = entries.get(index);
		if (object instanceof ConstantMethodrefInfo) {

		} else {
			throw new IllegalArgumentException("getMethodRef for index " + index + " returned: " + object);
		}
		return (ConstantMethodrefInfo) object;
	}

	public ConstantStringInfo getStringInfo(int index) {
		return (ConstantStringInfo) entries.get(index);
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= (constantPoolCount - 1); i++) {
			sb.append("\n");
			sb.append(String.format("%4d %-20s", i, getEntry(i)));
		}
		return sb.toString();
	}

}
