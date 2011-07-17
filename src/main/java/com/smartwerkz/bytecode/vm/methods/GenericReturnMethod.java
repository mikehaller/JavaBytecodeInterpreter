package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class GenericReturnMethod implements NativeMethod {

	private final JavaObject value;

	public GenericReturnMethod(boolean value) {
		this.value = value ? new JavaInteger(1) : new JavaInteger(0);
	}
	
	public GenericReturnMethod(int value) {
		this.value = new JavaInteger(value);
	}

	public GenericReturnMethod(long value) {
		this.value = new JavaLong(value);
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		operandStack.push(value);
	}

}
