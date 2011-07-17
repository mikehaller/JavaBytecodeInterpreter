package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.classfile.Classfile;

public class ClassArea {

	private final RuntimeConstantPool runtimeConstantPool;
	private final MethodCode methodCode = new MethodCode();
	private final AttributesAndFieldValues values = new AttributesAndFieldValues();
	private final Classfile classfile;

	public ClassArea(VirtualMachine vm, Classfile classfile, BootstrapClassloader bcl) {
		if (classfile == null) {
			throw new IllegalArgumentException("A ClassArea requires a Classfile");
		}
		if (bcl == null) {
			throw new IllegalArgumentException("A ClassArea requires a ClassLoader");
		}
		this.classfile = classfile;
		runtimeConstantPool = new RuntimeConstantPool(vm, classfile, classfile.getConstantPool());
	}

	public RuntimeConstantPool getRuntimeConstantPool() {
		return runtimeConstantPool;
	}

	public MethodCode getMethodCode() {
		return methodCode;
	}

	public Classfile getClassfile() {
		return classfile;
	}
}
