package com.smartwerkz.bytecode.vm.methods;

import java.util.List;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.JavaStack;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class GetCallerClassMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaInteger callerDepth = (JavaInteger) frame.getLocalVariables().getLocalVariable(0);
		JavaStack stack = frame.getVirtualThread().getStack();
		List<Frame> frames = stack.getFrames();
		Frame frame2 = frames.get(callerDepth.intValue() - 1);
		String callerClassName = frame2.getCurrentMethodInfo().getClassName();
		Classfile cf = rda.loadClass(frame.getVirtualThread(), callerClassName);
		operandStack.push(cf.getAsJavaClassReference());
	}
}