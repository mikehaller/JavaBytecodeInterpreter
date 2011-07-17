package com.smartwerkz.bytecode.vm.methods;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassGetComponentType implements NativeMethod {

	private final Map<String, JavaClassReference> mapping = new HashMap<String, JavaClassReference>();

	public ClassGetComponentType(VirtualMachine vm) {
		mapping.put("[C", vm.classes().primitives().charClass().getAsJavaClassReference());
		mapping.put("[B", vm.classes().primitives().byteClass().getAsJavaClassReference());
		mapping.put("[I", vm.classes().primitives().intClass().getAsJavaClassReference());
		mapping.put("[Z", vm.classes().primitives().booleanClass().getAsJavaClassReference());
		mapping.put("[F", vm.classes().primitives().floatClass().getAsJavaClassReference());
		mapping.put("[D", vm.classes().primitives().doubleClass().getAsJavaClassReference());
		mapping.put("[J", vm.classes().primitives().longClass().getAsJavaClassReference());
		mapping.put("[V", vm.classes().primitives().voidClass().getAsJavaClassReference());
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables localVariables = frame.getLocalVariables();
		JavaClassReference clazz = (JavaClassReference) localVariables.getLocalVariable(0);
		String clazzName = clazz.getClassFile().getThisClassName();

		// Component type is a primitive class
		if (mapping.containsKey(clazzName)) {
			operandStack.push(mapping.get(clazzName));
			return;
		}

		// TODO: Unclear why we need this special case here.
		if (clazzName.startsWith("[L")) {
			String componentType = clazzName.substring(2);
			Classfile loadClass = rda.loadClass(frame.getVirtualThread(), componentType);
			operandStack.push(loadClass.getAsJavaClassReference());
			return;
		}

		String componentType = clazzName;
		Classfile loadClass = rda.loadClass(frame.getVirtualThread(), componentType);
		operandStack.push(loadClass.getAsJavaClassReference());
	}

}
