package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.AttributeInfo;
import com.smartwerkz.bytecode.classfile.Attributes;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassGetRawAnnotations implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		VirtualMachine vm = frame.getVirtualMachine();
		LocalVariables localVariables = frame.getLocalVariables();
		JavaClassReference clazz = (JavaClassReference) localVariables.getLocalVariable(0);
		Classfile classFile = clazz.getClassFile();
		Attributes attributes = classFile.getAttributes();
		AttributeInfo annot = attributes.getAttribute("RuntimeVisibleAnnotations");
		if (annot == null) {
			operandStack.push(vm.objects().nullReference());
			return;
		}
		byte[] value = annot.getValue();
		JavaArray array = vm.classes().arrays().convert(value);
		operandStack.push(array);
	}

}
