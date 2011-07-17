package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class ObjectHashMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObject currentObject = frame.getLocalVariables().getLocalVariable(0);
		// TODO: Use our own hashCode implementation?
		int hashCode = currentObject.hashCode();
		operandStack.push(new JavaInteger(hashCode));
	}
}