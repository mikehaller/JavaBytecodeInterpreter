package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.classfile.ConstantClassInfo;
import com.smartwerkz.bytecode.classfile.ConstantInterfacemethodrefInfo;
import com.smartwerkz.bytecode.classfile.ConstantMethodrefInfo;
import com.smartwerkz.bytecode.classfile.ConstantNameAndTypeInfo;
import com.smartwerkz.bytecode.classfile.ConstantPool;
import com.smartwerkz.bytecode.classfile.ConstantUTF8Info;
import com.smartwerkz.bytecode.classfile.Descriptor;

public class SymbolicMethodReference {

	private String className;
	private String methodName;
	private String methodType;
	private String fullName;
	private int countParameters;

	public SymbolicMethodReference(int index, ConstantPool constantPool) {
		Object entry = constantPool.getEntry(index);
		if (entry instanceof ConstantMethodrefInfo) {
			ConstantMethodrefInfo methodref = constantPool.getMethodRef(index);
			ConstantClassInfo classInfo = constantPool.getClassInfoEntry(methodref.getClassIndex());
			ConstantNameAndTypeInfo nameType = constantPool.getNameAndTypeInfo(methodref.getNameAndTypeIndex());
			className = constantPool.getUTF8Info(classInfo.getIndex()).getValue();
			methodName = constantPool.getUTF8Info(nameType.getNameIndex()).getValue();
			methodType = constantPool.getUTF8Info(nameType.getDescriptorIndex()).getValue();
		} else if (entry instanceof ConstantUTF8Info) {
			// Special method, e.g. "<clinit>" constructor or something
			ConstantUTF8Info constantUTF8Info = (ConstantUTF8Info) entry;
			methodName = constantUTF8Info.getValue();
			className = null;
			methodType = null;
		} else if (entry instanceof ConstantNameAndTypeInfo) {
			// Special method, e.g. "<clinit>" constructor or something
			ConstantNameAndTypeInfo nameType = (ConstantNameAndTypeInfo) entry;
			methodName = constantPool.getUTF8Info(nameType.getNameIndex()).getValue();
			className = null;
			methodType = constantPool.getUTF8Info(nameType.getDescriptorIndex()).getValue();
		} else if (entry instanceof ConstantInterfacemethodrefInfo) {
			ConstantInterfacemethodrefInfo methodref = (ConstantInterfacemethodrefInfo) entry;
			ConstantClassInfo classInfo = constantPool.getClassInfoEntry(methodref.getClassIndex());
			ConstantNameAndTypeInfo nameType = constantPool.getNameAndTypeInfo(methodref.getNameAndTypeIndex());
			className = constantPool.getUTF8Info(classInfo.getIndex()).getValue();
			methodName = constantPool.getUTF8Info(nameType.getNameIndex()).getValue();
			methodType = constantPool.getUTF8Info(nameType.getDescriptorIndex()).getValue();
		}

		else {
			throw new UnsupportedOperationException("Unknown constant pool entry for a symbol method reference: "
					+ entry.getClass().getName());
		}

		fullName = className + "#" + methodName + "(" + methodType + ")";
		countParameters = Descriptor.countParameters(methodType);
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	/**
	 * List of parameter types in brackets, and the return type at the end.
	 * <tt>(Ljava/lang/String;)V</tt>
	 * 
	 * @return
	 */
	public String getMethodType() {
		return methodType;
	}

	public String getFullName() {
		return fullName;
	}

	public int getParameterCount() {
		return countParameters;
	}

}
