package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassClone implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaObject thisObject = frame.getLocalVariables().getLocalVariable(0);
		JavaObject copy = thisObject.copy();
		otherOperandStack.push(copy);
	}

}
