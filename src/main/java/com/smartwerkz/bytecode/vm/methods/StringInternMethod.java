package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.StringPool;

public class StringInternMethod implements NativeMethod {

	private final StringPool stringPool;

	public StringInternMethod(StringPool stringPool) {
		if (stringPool == null) {
			throw new IllegalArgumentException();
		}
		this.stringPool = stringPool;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObject thisObject = frame.getLocalVariables().getLocalVariable(0);
		if (thisObject instanceof JavaObjectReference) {
			JavaObjectReference javaObjectReference = (JavaObjectReference) thisObject;
			operandStack.push(stringPool.intern(javaObjectReference));
		} else if (thisObject instanceof JavaString) {
			JavaString newString = (JavaString) thisObject;
			operandStack.push(stringPool.intern(newString));
		} else {
			throw new IllegalStateException();
		}
	}

}
