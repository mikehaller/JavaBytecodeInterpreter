package com.smartwerkz.bytecode.controlflow;

import com.smartwerkz.bytecode.primitives.JavaObjectReference;

public class ControlFlowException extends Error {

	private final JavaObjectReference javaException;

	public ControlFlowException(JavaObjectReference javaException) {
		super(javaException.toString());
		this.javaException = javaException;
	}
	
	public JavaObjectReference getJavaException() {
		return javaException;
	}

}
