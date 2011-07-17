package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class SystemArraycopyMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaInteger length = (JavaInteger) frame.getLocalVariables().getLocalVariable(4);
		JavaInteger destpos = (JavaInteger) frame.getLocalVariables().getLocalVariable(3);
		JavaArray dest = (JavaArray) frame.getLocalVariables().getLocalVariable(2);
		JavaInteger srcpos = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		JavaArray src = (JavaArray) frame.getLocalVariables().getLocalVariable(0);
		for (int i = 0; i < length.intValue(); i++) {
			dest.set(destpos.intValue() + i, src.get(srcpos.intValue() + i));
		}
	}
}