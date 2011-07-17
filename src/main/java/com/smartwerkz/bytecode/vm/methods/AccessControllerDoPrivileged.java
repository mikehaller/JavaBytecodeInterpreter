package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class AccessControllerDoPrivileged implements NativeMethod {

	private final VMLog log = new VMLog(AccessControllerDoPrivileged.class.getName());

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObjectReference targetObject = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		JavaObject result = perform(rda, frame, targetObject);
//		log.debug("PrivilegedAction '%s' returned '%s'", targetObject, result);
		operandStack.push(result);

	}

	public JavaObject perform(RuntimeDataArea rda, Frame frame, JavaObjectReference targetObject) {
		Classfile classFile = targetObject.getClassFile();
		// if (!classFile.doesImplement("java.security.PrivilegedAction")) {
		// throw new IllegalStateException();
		// }
		// TODO: Invoke the 'run()' method
		VirtualMachine vm = rda.vm();
		Methods methods2 = classFile.getMethods(vm);
		MethodInfo invokeMethodInfo = methods2.findMethodInfo(vm, "run");
		ClassArea classArea = rda.getMethodArea().getClassArea(classFile.getThisClassName());
		RuntimeConstantPool rcp = classArea.getRuntimeConstantPool();
		Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, rcp, invokeMethodInfo);
		newFrame.getLocalVariables().setLocalVariable(0, targetObject);
		FrameExit frameResult = newFrame.execute();
		if (!frameResult.hasReturnValue()) {
			throw new IllegalStateException();
		}
		return frameResult.getResult();
	}

}
