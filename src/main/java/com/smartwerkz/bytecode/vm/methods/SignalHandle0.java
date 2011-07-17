package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class SignalHandle0 implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		// Return -1 on error, e.g. handler already registered
		// Return 0 for SignalHandler.SIG_DFL ("Default signal handler")
		// Return 1 for SIG_IGN ("Ignore the signal")
		// Return 2 for local signal handler
		// Return any other number for a signal handler ID
		otherOperandStack.push(new JavaLong(0));
	}

}
