package com.smartwerkz.bytecode.primitives;

public class JavaChar extends JavaInteger {

	private final char charValue;

	public JavaChar(char charValue) {
		super(charValue);
		this.charValue = charValue;
	}
	
	public JavaChar(int charValue) {
		super(charValue);
		this.charValue = (char) charValue;
	}

	public char charValue() {
		return charValue;
	}
	
	@Override
	public String asStringValue() {
		return Character.toString(charValue);
	}
	
	@Override
	public JavaObject copy() {
		return this;
	}

}
