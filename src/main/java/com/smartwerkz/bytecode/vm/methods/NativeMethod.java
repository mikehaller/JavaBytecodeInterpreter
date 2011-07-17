package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;


public interface NativeMethod {

	void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack);
	
}
