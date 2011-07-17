package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class NOPNativeMethod implements NativeMethod {
	private final int parameters;
	private final boolean hasReturn;

	public NOPNativeMethod(int parameters, boolean hasReturn) {
		this.parameters = parameters;
		this.hasReturn = hasReturn;
	}

	public NOPNativeMethod() {
		this(0, false);
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		for (int i = 0; i < parameters; i++)
			operandStack.pop();
		if (hasReturn)
			operandStack.push(rda.vm().objects().nullReference());
	}
}