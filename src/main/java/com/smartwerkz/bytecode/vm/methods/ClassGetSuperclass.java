package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassGetSuperclass implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables localVariables = frame.getLocalVariables();
		JavaClassReference clazz = (JavaClassReference) localVariables.getLocalVariable(0);
		String clazzName = clazz.getClassFile().getSuperClassName();
		if (clazzName == null) {
			operandStack.push(rda.vm().objects().nullReference());
		} else {
			Classfile loadClass = rda.loadClass(frame.getVirtualThread(), clazzName);
			operandStack.push(loadClass.getAsJavaClassReference());
		}
	}

}
