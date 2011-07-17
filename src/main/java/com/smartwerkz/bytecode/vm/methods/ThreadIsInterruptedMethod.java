package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ThreadIsInterruptedMethod implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObjectReference threadReference = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		JavaInteger clearInterrupted = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		if (rda.getThreads().isInterrupted(threadReference)) {
			operandStack.push(new JavaInteger(1));
		} else {
			operandStack.push(new JavaInteger(0));
		}
		
		if (clearInterrupted.intValue()==1) {
			rda.getThreads().clearInterruptedFlag(threadReference);
		}
	}

}
