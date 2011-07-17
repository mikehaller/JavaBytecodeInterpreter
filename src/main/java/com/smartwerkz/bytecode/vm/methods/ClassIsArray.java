package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassIsArray implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables localVariables = frame.getLocalVariables();
		JavaClassReference clazz = (JavaClassReference) localVariables.getLocalVariable(0);
		boolean isArray = clazz.getClassFile().getThisClassName().startsWith("[");
		if (isArray) {
			operandStack.push(new JavaInteger(1));
		} else {
			operandStack.push(new JavaInteger(0));
		}
	}

}
