package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassGetName0 implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables localVariables = frame.getLocalVariables();
		JavaClassReference clazz = (JavaClassReference) localVariables.getLocalVariable(0);
		String clazzName = clazz.getClassFile().getThisClassName();
		operandStack.push(new JavaString(frame.getVirtualMachine(),clazzName));
	}

}
