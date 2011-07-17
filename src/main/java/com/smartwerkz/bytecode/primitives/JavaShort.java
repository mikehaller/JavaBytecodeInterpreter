package com.smartwerkz.bytecode.primitives;

public class JavaShort extends JavaInteger {

	private final short shortValue;

	public JavaShort(short shortValue) {
		super(shortValue);
		this.shortValue = shortValue;
	}

	public short shortValue() {
		return shortValue;
	}
	
	@Override
	public String asStringValue() {
		return Short.toString(shortValue);
	}

	@Override
	public JavaObject copy() {
		return this;
	}
	
}
