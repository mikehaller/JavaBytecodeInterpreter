package com.smartwerkz.bytecode.vm.methods;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Classes.Primitives;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public final class ClassGetPrimitiveClassMethod implements NativeMethod {

	private final Map<String, JavaClassReference> cache = new HashMap<String, JavaClassReference>();

	public ClassGetPrimitiveClassMethod(VirtualMachine vm) {
		Primitives primitives = vm.classes().primitives();
		cache.put("char", primitives.charClass().getAsJavaClassReference());
		cache.put("byte", primitives.byteClass().getAsJavaClassReference());
		cache.put("int", primitives.intClass().getAsJavaClassReference());
		cache.put("long", primitives.longClass().getAsJavaClassReference());
		cache.put("boolean", primitives.booleanClass().getAsJavaClassReference());
		cache.put("float", primitives.floatClass().getAsJavaClassReference());
		cache.put("double", primitives.doubleClass().getAsJavaClassReference());
		cache.put("short", primitives.shortClass().getAsJavaClassReference());
		cache.put("void", primitives.voidClass().getAsJavaClassReference());
	}

	/**
	 * Return the Virtual Machine's Class object for the named primitive type.
	 * static native Class getPrimitiveClass(String name);
	 */
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaString className = (JavaString) frame.getLocalVariables().getLocalVariable(0);
		JavaClassReference cf = cache.get(className.asStringValue());
		if (cf == null) {
			throw new IllegalArgumentException(className.toString());
		}
		operandStack.push(cf);
	}
}