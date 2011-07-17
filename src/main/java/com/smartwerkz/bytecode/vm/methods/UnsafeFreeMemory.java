package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.Unsafe;

public class UnsafeFreeMemory implements NativeMethod {

	private final Unsafe unsafe;

	public UnsafeFreeMemory(Unsafe unsafe) {
		this.unsafe = unsafe;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaLong size = (JavaLong) frame.getLocalVariables().getLocalVariable(1);
		unsafe.freeMemory(size.longValue());
	}

}
