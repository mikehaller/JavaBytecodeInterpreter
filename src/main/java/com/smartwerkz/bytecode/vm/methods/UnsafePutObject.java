package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.Unsafe;

public class UnsafePutObject implements NativeMethod {

	private final Unsafe unsafe;

	public UnsafePutObject(Unsafe unsafe) {
		this.unsafe = unsafe;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaLong address = (JavaLong) frame.getLocalVariables().getLocalVariable(1);
		JavaObject value = frame.getLocalVariables().getLocalVariable(2);
		unsafe.putObject(address.longValue(), value);
	}

}
