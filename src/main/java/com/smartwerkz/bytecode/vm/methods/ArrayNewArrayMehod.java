package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ArrayNewArrayMehod implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaClassReference componentType = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);
		JavaInteger length = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		Classfile arrayType = componentType.getClassFile();
		JavaArray array = new JavaArray(rda.vm(),arrayType, length.intValue());
		operandStack.push(array);
	}

}
