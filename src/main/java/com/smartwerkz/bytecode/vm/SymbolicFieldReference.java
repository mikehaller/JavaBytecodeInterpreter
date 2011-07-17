package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.classfile.ConstantClassInfo;
import com.smartwerkz.bytecode.classfile.ConstantFieldrefInfo;
import com.smartwerkz.bytecode.classfile.ConstantNameAndTypeInfo;
import com.smartwerkz.bytecode.classfile.ConstantPool;

public class SymbolicFieldReference {

	private String className;
	private String fieldName;
	private String fieldType;
	private final int fieldIndex;

	public SymbolicFieldReference(String className, String fieldName,
			String fieldType) {
		super();
		this.className = className;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldIndex = -1;
	}

	public SymbolicFieldReference(int index, ConstantPool constantPool) {
		this.fieldIndex = index;
		ConstantFieldrefInfo fieldref = constantPool.getFieldRef(index);
		ConstantClassInfo classInfo = constantPool.getClassInfoEntry(fieldref
				.getClassIndex());
		ConstantNameAndTypeInfo nameType = constantPool
				.getNameAndTypeInfo(fieldref.getNameAndTypeIndex());
		className = constantPool.getUTF8Info(classInfo.getIndex()).getValue();
		fieldName = constantPool.getUTF8Info(nameType.getNameIndex())
				.getValue();
		fieldType = constantPool.getUTF8Info(nameType.getDescriptorIndex())
				.getValue();
	}

	public int getFieldIndex() {
		return fieldIndex;
	}
	
	public String getClassName() {
		return className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		return String.format("%s.%s#%s", className, fieldName, fieldType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result
				+ ((fieldType == null) ? 0 : fieldType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SymbolicFieldReference other = (SymbolicFieldReference) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (fieldType == null) {
			if (other.fieldType != null)
				return false;
		} else if (!fieldType.equals(other.fieldType))
			return false;
		return true;
	}

	
}
