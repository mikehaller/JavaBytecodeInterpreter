package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.Unsafe;

public class UnsafePutLong implements NativeMethod {

	private final Unsafe unsafe;

	public UnsafePutLong(Unsafe unsafe) {
		this.unsafe = unsafe;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaLong address = (JavaLong) frame.getLocalVariables().getLocalVariable(1);
		JavaLong value = (JavaLong) frame.getLocalVariables().getLocalVariable(3);
		unsafe.putLong(address.longValue(), value.longValue());
	}

}
