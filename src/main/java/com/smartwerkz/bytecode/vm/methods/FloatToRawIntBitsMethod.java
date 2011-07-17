package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class FloatToRawIntBitsMethod implements NativeMethod {
	// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/lang/Float.java#Float.floatToRawIntBits%28float%29
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaFloat bits = (JavaFloat) frame.getLocalVariables().getLocalVariable(0);
		int floatToRawIntBits = Float.floatToRawIntBits(bits.floatValue());
		operandStack.push(new JavaInteger(floatToRawIntBits));
	}
}