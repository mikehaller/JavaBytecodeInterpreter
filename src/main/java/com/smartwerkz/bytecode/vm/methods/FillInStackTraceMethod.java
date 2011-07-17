package com.smartwerkz.bytecode.vm.methods;

import java.util.List;

import com.smartwerkz.bytecode.classfile.Attributes;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.JavaStack;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public final class FillInStackTraceMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObjectReference jor = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		populateException(rda, frame, jor);
		frame.getOperandStack().push(jor);
	}

	public void populateException(RuntimeDataArea rda, Frame frame, JavaObjectReference exception) {
		VirtualMachine vm = frame.getVirtualMachine();
		JavaStack stack = frame.getVirtualThread().getStack();
		Classfile cf = rda.loadClass(frame.getVirtualThread(), "java/lang/StackTraceElement");
		List<Frame> frames = stack.getFrames();
		JavaArray stackTraceArray = new JavaArray(vm,cf, frames.size());
		exception.setValueOfField("stackTrace", stackTraceArray);
		int i = 0;
		for (Frame frame2 : frames) {
			Classfile dc = frame2.getThisClass();
			JavaObjectReference ste = new JavaObjectReference(cf);
			MethodInfo currentMethodInfo = frame2.getCurrentMethodInfo();
			String declaringClass = currentMethodInfo.getClassName();
			ste.setValueOfField("declaringClass", new JavaString(vm, declaringClass));
			ste.setValueOfField("methodName", new JavaString(vm, currentMethodInfo.getMethodName()));
			Attributes attributes = dc.getAttributes();
			String sourceFileName = attributes.getSourceFileAttribute(dc);
			if (sourceFileName != null) {
				ste.setValueOfField("fileName", new JavaString(vm, sourceFileName));
			} else {
				ste.setValueOfField("fileName", new JavaString(vm, "Unknown Source"));
			}

			// TODO: Line numbers are screwed up and do not match the bytecode!!
			int programCounter = frame2.getLastSuccessProgramCounter();
			int lineNumber = currentMethodInfo.getLineNumber(programCounter);
			ste.setValueOfField("lineNumber", new JavaInteger(lineNumber));
			stackTraceArray.set(i++, ste);
		}

	}
}