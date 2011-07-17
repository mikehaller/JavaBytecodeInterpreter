package com.smartwerkz.bytecode.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;

public class VirtualThread {

	private static final Logger log = LoggerFactory.getLogger(VirtualThread.class);
	
	private final ProgramCounter pc = new ProgramCounter();
	private final JavaStack stack = new JavaStack();
	private final NativeMethodStack nativeMethodStack = new NativeMethodStack();
	private final String threadName;
	private final RuntimeDataArea rda;
	private final JavaObjectReference javaObjectReference;
	private final VirtualMachine vm;

	public VirtualThread(VirtualMachine vm, RuntimeDataArea rda, String threadName,
			JavaObjectReference javaObjectReference) {
		this.vm = vm;
		this.rda = rda;
		this.threadName = threadName;
		this.javaObjectReference = javaObjectReference;
	}

	public void shutdownAndWait() {
	}

	public String getName() {
		return threadName;
	}

	public ProgramCounter getProgramCounter() {
		return pc;
	}

	public FrameExit execute(Classfile loaded, MethodInfo methodInfo, JavaObject[] parameters) {
		if (methodInfo == null) {
			throw new IllegalArgumentException("Unable to execute NULL method");
		}
		log.debug("Thread '%s' is executing method '%s'", getName(), methodInfo);
		MethodArea methodArea = rda.getMethodArea();
		ClassArea classArea = methodArea.getClassArea(methodInfo);
		if (classArea == null) {
			classArea = new ClassArea(vm, loaded, rda.getBootstrapClassLoader());
		}
		RuntimeConstantPool pool = classArea.getRuntimeConstantPool();
		Frame frame = new Frame(vm, this, rda, pool, methodInfo);
		// TODO: How do we do thread scheduling here?

		int param = 0;
		for (JavaObject parameter : parameters) {
			log.debug("Setting local variable %d to %s", param, parameter);
			frame.getLocalVariables().setLocalVariable(param, parameter);
			param++;
		}

		rda.getThreads().markStarted(javaObjectReference);
		FrameExit result = frame.execute();
		rda.getThreads().markStopped(javaObjectReference);

		if (result.hasReturnValue()) {
			log.error("TODO: Unclear whether the first frame can have a return value. Need to check JVM spec: "
					+ result);
		}

		return result;
	}

	public RuntimeDataArea getRuntimeDataArea() {
		return rda;
	}

	public JavaStack getStack() {
		return stack;
	}

	public JavaObjectReference getThreadAsJavaObjectReference() {
		return javaObjectReference;
	}
}
