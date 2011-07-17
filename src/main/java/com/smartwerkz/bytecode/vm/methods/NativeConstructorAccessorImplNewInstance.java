package com.smartwerkz.bytecode.vm.methods;

import java.util.List;

import com.smartwerkz.bytecode.ExecutionEngine;
import com.smartwerkz.bytecode.classfile.ConstantPool;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class NativeConstructorAccessorImplNewInstance implements NativeMethod {

	// (Ljava/lang/reflect/Constructor;[Ljava/lang/Object;)Ljava/lang/Object;
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		// Step 1: Find out which Classfile we're talking about
		JavaObjectReference thisObject = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		JavaObjectReference parameterArray = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(1);
		JavaClassReference newClass = (JavaClassReference) thisObject.getValueOfField("clazz");
		// Step 2: Create a new object reference
		JavaObjectReference jor = new JavaObjectReference(newClass.getClassFile());
		// Step 3: Find the constructor to invoke by using the "slot" ??
		// Step 3: Set the parameters for the constructor class
		// TODO Step 4: Execute the constructor method bytecode

		VirtualMachine vm = rda.vm();
		Methods methods = newClass.getClassFile().getMethods(vm);
		List<MethodInfo> allMethods = methods.getAllMethods();
		for (MethodInfo methodInfo : allMethods) {
			if (methodInfo.getMethodName().equals("<init>")) {
				ConstantPool cp = newClass.getClassFile().getConstantPool();
				RuntimeConstantPool newCP = new RuntimeConstantPool(vm, newClass.getClassFile(), cp);
				Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, newCP, methodInfo);
				newFrame.initializeLocalVariablesFromArray(jor, methodInfo, parameterArray);
				// TODO: Unnecessary "new ExecutionEngine" here, could be done
				// otherwise because
				// ExecutionEngine is pretty expensive with large methods
				ExecutionEngine engine = new ExecutionEngine(vm, rda, newFrame, methodInfo);
				engine.invokeWithExceptionHandling(rda, methodInfo, newFrame);
				break;
			}
		}

		// Step 5: Return it to the operand stack
		frame.getOperandStack().push(jor);
	}

}
