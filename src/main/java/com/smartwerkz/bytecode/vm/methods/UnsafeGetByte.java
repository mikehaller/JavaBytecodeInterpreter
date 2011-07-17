package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.Unsafe;

public class UnsafeGetByte implements NativeMethod {

	private final Unsafe unsafe;

	public UnsafeGetByte(Unsafe unsafe) {
		this.unsafe = unsafe;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaLong address = (JavaLong) frame.getLocalVariables().getLocalVariable(1);
		byte value = unsafe.getByte(address.longValue());
		otherOperandStack.push(new JavaInteger(value));
	}

}
