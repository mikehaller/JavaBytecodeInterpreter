package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class FileOutputStreamOpenAppendMethod implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables lv = frame.getLocalVariables();
		JavaObjectReference thisFileOutputStream = (JavaObjectReference) lv.getLocalVariable(0);
		JavaObject name = lv.getLocalVariable(1);
		// Does nothing really...
	}

}
