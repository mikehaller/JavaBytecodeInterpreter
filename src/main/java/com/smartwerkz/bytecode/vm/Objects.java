package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.primitives.JavaNullReference;

public class Objects {

	private final VirtualMachine vm;
	private final Classes classes;

	public Objects(Classes classes, VirtualMachine vm) {
		this.classes = classes;
		this.vm = vm;
	}

	public JavaNullReference nullReference() {
		// Create a new NULL reference each time, so we can
		// back track it later if something happens.
		return new JavaNullReference(vm, classes);
	}

}
