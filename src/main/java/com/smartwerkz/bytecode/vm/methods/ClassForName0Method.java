package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.ExceptionFactory;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.controlflow.ControlFlowException;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ClassForName0Method implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		LocalVariables vars = frame.getLocalVariables();
		JavaObjectReference strClassName = (JavaObjectReference) vars.getLocalVariable(0);
		JavaInteger initialize = (JavaInteger) vars.getLocalVariable(1);
		JavaObjectReference classloader = (JavaObjectReference) vars.getLocalVariable(2);
		JavaString interned = rda.vm().getStringPool().intern(strClassName);
		try {
			String classNameToLoad = interned.asStringValue().replace('.', '/');
			Classfile cf = rda.loadClass(frame.getVirtualThread(), classNameToLoad);
			operandStack.push(new JavaClassReference(cf));
		} catch (ControlFlowException cfe) {
			ExceptionFactory exceptionFactory = new ExceptionFactory();
			JavaObjectReference javaException = exceptionFactory.createClassNotFoundException(rda, frame, interned.asStringValue());
			throw new ControlFlowException(javaException);
		}
	}

}
