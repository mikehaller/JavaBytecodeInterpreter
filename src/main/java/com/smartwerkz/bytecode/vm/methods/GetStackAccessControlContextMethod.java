package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class GetStackAccessControlContextMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		// Just return NULL because
		// "there is only privileged system code"
		operandStack.push(rda.vm().objects().nullReference());
	}
}