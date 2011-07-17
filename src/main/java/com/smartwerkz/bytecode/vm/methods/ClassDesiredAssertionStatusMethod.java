package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class ClassDesiredAssertionStatusMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		// JavaObject classObject =
		// frame.getLocalVariables().getLocalVariable(0);
		// Push "false", that means "disable asserts"
		operandStack.push(new JavaInteger(0));
	}
}