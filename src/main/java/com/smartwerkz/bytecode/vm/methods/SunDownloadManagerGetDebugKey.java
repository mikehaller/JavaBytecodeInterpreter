package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class SunDownloadManagerGetDebugKey implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		boolean debugKey = false;
		if (debugKey) {
			operandStack.push(new JavaInteger(1));
		} else {
			operandStack.push(new JavaInteger(0));
		}
	}

}
