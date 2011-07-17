package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class UnsafeObjectFieldOffsetMethod implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		// public native long objectFieldOffset(Field paramField);
		JavaObjectReference field = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(1);
		operandStack.push(new JavaLong(field.getInstanceId()));
	}

}
