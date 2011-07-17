package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ThreadInterruptMethod implements NativeMethod {

	// private final VMLog log = new
	// VMLog(ThreadInterruptMethod.class.getName());

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObjectReference threadReference = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		rda.getThreads().interrupt(threadReference);
	}

}
