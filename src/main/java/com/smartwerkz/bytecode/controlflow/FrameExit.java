package com.smartwerkz.bytecode.controlflow;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;

public class FrameExit {

	private final JavaObject result;
	private boolean exception;

	// TODO: Remove constructor and replace with factory methods, so we can ensure isException()
	
	public FrameExit() {
		this.result = null;
	}

	public FrameExit(JavaObject result) {
		this.result = result;
	}

	public boolean hasReturnValue() {
		return result != null;
	}

	public boolean isException() {
		return exception;
	}

	public JavaObject getResult() {
		return result;
	}

	@Override
	public String toString() {
		return String.format("FrameExit[javaObject=%s]", result);
	}

	public static FrameExit createFromException(JavaObjectReference javaException) {
		FrameExit frameExit = new FrameExit(javaException);
		frameExit.exception = true;
		return frameExit;
	}

}
