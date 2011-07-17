package com.smartwerkz.bytecode.primitives;

public class JavaByte extends JavaInteger {

	private final byte byteValue;

	public JavaByte(byte charValue) {
		super(charValue);
		this.byteValue = charValue;
	}
	
	public JavaByte(int charValue) {
		super(charValue);
		this.byteValue = (byte) charValue;
	}

	public byte byteValue() {
		return byteValue;
	}
	
	@Override
	public String asStringValue() {
		return Byte.toString(byteValue);
	}
	
	@Override
	public JavaObject copy() {
		return this;
	}

}
