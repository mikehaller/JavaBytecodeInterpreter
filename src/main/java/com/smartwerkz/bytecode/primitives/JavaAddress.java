package com.smartwerkz.bytecode.primitives;

public class JavaAddress implements JavaObject {

	private final int address;

	public JavaAddress(int address) {
		this.address = address;
	}

	@Override
	public String asStringValue() {
		return "JavaAddress["+address+"]";
	}

	@Override
	public JavaObject copy() {
		return new JavaAddress(address);
	}

	public int getAddress() {
		return address;
	}

}
