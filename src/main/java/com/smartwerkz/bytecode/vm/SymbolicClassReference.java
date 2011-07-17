package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.classfile.ConstantClassInfo;
import com.smartwerkz.bytecode.classfile.ConstantPool;
import com.smartwerkz.bytecode.classfile.ConstantUTF8Info;

public class SymbolicClassReference {

	private String className;

	public SymbolicClassReference(int index, ConstantPool constantPool) {
		if (constantPool == null) {
			throw new IllegalArgumentException("ConstantPool must not be null");
		}
		ConstantClassInfo classInfoEntry = constantPool.getClassInfoEntry(index);
		if (classInfoEntry == null) {
			throw new IllegalStateException("Unable to find symbolic class reference at index " + index);
		}
		ConstantUTF8Info utf8Info = constantPool.getUTF8Info(classInfoEntry.getIndex());
		className = utf8Info.getValue();
		// ConstantNameAndTypeInfo nameAndTypeInfo = constantPool
		// .getNameAndTypeInfo(classInfoEntry.getIndex());
		// className = constantPool.getUTF8Info(nameAndTypeInfo.getNameIndex())
		// .getValue();
		// String descriptorName = constantPool.getUTF8Info(
		// nameAndTypeInfo.getDescriptorIndex()).getValue();
	}

	public String getClassName() {
		return className;
	}

}
