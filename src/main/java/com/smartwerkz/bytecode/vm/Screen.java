package com.smartwerkz.bytecode.vm;

public class Screen {

	// TODO: Make this a rolling buffer
	private final StringBuilder content = new StringBuilder();
	
	public String asString() {
		return content.toString();
	}

	public void print(String output) {
		content.append(output);
	}

}
