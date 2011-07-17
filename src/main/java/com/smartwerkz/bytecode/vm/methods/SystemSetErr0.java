package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class SystemSetErr0 implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObject newStdErrPrintStream = frame.getLocalVariables().getLocalVariable(0);
		ClassArea classArea = rda.getMethodArea().getClassArea("java/lang/System");
		RuntimeConstantPool rcp = classArea.getRuntimeConstantPool();
		rcp.setValue("err", newStdErrPrintStream);
	}

}
