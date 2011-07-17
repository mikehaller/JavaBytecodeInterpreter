package com.smartwerkz.bytecode.primitives;

import com.smartwerkz.bytecode.vm.Classes;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class JavaNullReference extends JavaObjectReference {

	/**
	 * DONT CALL THIS CONSTRUCTOR, use VirtualMachine.NULL
	 * @param vm
	 * @param classes 
	 */
	public JavaNullReference(VirtualMachine vm, Classes classes) {
		super(classes.primitives().voidClass());
	}

	@Override
	public String asStringValue() {
		return "null";
	}

	@Override
	public JavaObject copy() {
		// TODO: This should throw a NullPointerException anyway
		// because it means someone called "null.clone()" ?!
		return this;
	}
}
