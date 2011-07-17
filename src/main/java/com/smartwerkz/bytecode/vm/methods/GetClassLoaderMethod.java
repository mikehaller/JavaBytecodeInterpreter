package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;

public final class GetClassLoaderMethod implements NativeMethod {
	private final VMLog log = new VMLog(GetClassLoaderMethod.class.getName());

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaClassReference localVariable = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);
		// log.debug("Getting ClassLoader of '%s'", localVariable);
		Classfile cf = rda.loadClass(frame.getVirtualThread(), "java/lang/ClassLoader");
		// TODO: Return "null" for system classes
		// operandStack.push(new JavaObjectReference(cf));

		// Pushing a NULL ClassLoader means all Classes are loaded
		// by the bootstrap classloader - circumventing the security stuff.
		operandStack.push(rda.vm().objects().nullReference());
	}
}