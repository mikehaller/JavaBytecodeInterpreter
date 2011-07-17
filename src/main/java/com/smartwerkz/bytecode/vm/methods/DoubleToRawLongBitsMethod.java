package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class DoubleToRawLongBitsMethod implements NativeMethod {
	// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/lang/Float.java#Float.floatToRawIntBits%28float%29
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaDouble bits = (JavaDouble) frame.getLocalVariables().getLocalVariable(0);
		long doubleToRawLongBits = Double.doubleToRawLongBits(bits.doubleValue());
		operandStack.push(new JavaLong(doubleToRawLongBits));
	}
}