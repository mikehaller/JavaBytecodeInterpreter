package com.smartwerkz.bytecode.vm.memory;

import java.nio.ByteBuffer;

public class Heap {
	// young generation: short-lived objects which are quickly garbage collected
	// "old generation" aka "tenured generation": objects which will live long
	// permanent generation: class files
	
	private ByteBuffer content;
	
	public Heap() {
	}
	
}
