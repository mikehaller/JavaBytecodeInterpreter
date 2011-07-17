package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassLoaderFindLoadedClass0Method implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		// TODO: Eigentlich sollte man hier das Class object zurückliefern
		operandStack.push(frame.getVirtualMachine().objects().nullReference());
	}

}
