package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

/**
 * private static native int getClassAccessFlags(Class paramClass);
 * 
 * @author mhaller
 */
public class ReflectionGetClassAccessFlagsMethod implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaClassReference clazz = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);
		int accessFlags = clazz.getClassFile().getAccessFlags().getAccessFlags();
//		operandStack.push(new JavaInteger(accessFlags));
		operandStack.push(new JavaInteger(1));
	}

}
