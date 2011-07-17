package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassGetSunConstantPool implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		Classfile cpClazz = rda.loadClass(frame.getVirtualThread(), "sun/reflect/ConstantPool");
		JavaObjectReference objReference = new JavaObjectReference(cpClazz);
		operandStack.push(objReference);
	}

}
