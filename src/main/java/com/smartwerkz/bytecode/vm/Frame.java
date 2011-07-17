package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.ExceptionFactory;
import com.smartwerkz.bytecode.ExecutionEngine;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.CodeAttribute;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.controlflow.ControlFlowException;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaNullReference;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;

public class Frame {

	private static final Logger log = LoggerFactory.getLogger(Frame.class);

	public static final Object NO_RESULT = new Object();
	public static AtomicLong frameSequence = new AtomicLong(0);

	private final Map<String, Object> customProperties = new HashMap<String, Object>();

	private final LocalVariables localVariables;
	private final OperandStack operandStack;
	private final RuntimeDataArea rda;
	private final RuntimeConstantPool runtimeConstantPool;
	private final VirtualThread virtualThread;
	private final MethodInfo methodInfo;
	private final VirtualMachine vm;
	private final long frameId;

	// For exception stack traces and LineNumberTable
	private int lastSuccessProgramCounter;

	public Frame(VirtualMachine vm, VirtualThread virtualThread, RuntimeDataArea rda,
			RuntimeConstantPool runtimeConstantPool, MethodInfo methodInfo) {
		if (vm == null)
			throw new IllegalArgumentException("Every Frame must be connected to a VirtualMachine instance");
		if (virtualThread == null)
			throw new IllegalArgumentException("Every Frame must be connected to a Thread instance");
		if (methodInfo == null)
			throw new IllegalArgumentException("Need MethodInfo to execute a method");
		if (rda == null)
			throw new IllegalArgumentException("RuntimeDataArea must not be null");
		if (runtimeConstantPool == null)
			throw new IllegalArgumentException("RuntimeConstantPool must not be null");
		this.frameId = frameSequence.incrementAndGet();
		this.vm = vm;
		this.virtualThread = virtualThread;
		this.methodInfo = methodInfo;
		this.rda = rda;
		this.runtimeConstantPool = runtimeConstantPool;

		// Limit the size of operand stack and local variables list per method
		CodeAttribute code = methodInfo.getCode();
		if (code != null) {
			int maxLocals = code.getMaxLocals();
			localVariables = new LocalVariables(maxLocals);
			int maxStack = code.getMaxStack();
			operandStack = new OperandStack(maxStack);
		} else {
			// Defaults, e.g. for native methods or static methods etc.
			localVariables = new LocalVariables(10);
			operandStack = new OperandStack(10);
		}
	}

	public MethodInfo getCurrentMethodInfo() {
		return methodInfo;
	}

	public OperandStack getOperandStack() {
		return operandStack;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Frame #" + frameId);
		sb.append(" in method: ");
		sb.append(methodInfo.getFullName());
		return sb.toString();
	}

	public void initializeLocalVariablesFromOperandStack(OperandStack previousOperandStack,
			JavaObjectReference targetObject, MethodInfo methodInfo) {
		int parameterCount = methodInfo.getParameterCount();
		JavaObject[] params = new JavaObject[parameterCount + 1];
		for (int i = parameterCount; i >= 0; i--) {
			JavaObject param = previousOperandStack.pop();
			params[i] = param;
		}
		for (int i = methodInfo.getParameterCount(); i > 0; i--) {
			getLocalVariables().setLocalVariable(i, params[i]);
		}
		getLocalVariables().setLocalVariable(0, targetObject);
	}

	public void initializeLocalVariablesFromArray(JavaObjectReference targetObject, MethodInfo methodInfo2,
			JavaObjectReference parameterArray) {
		getLocalVariables().setLocalVariable(0, targetObject);

		if (parameterArray instanceof JavaNullReference) {
			return;
		}
		JavaArray arr = (JavaArray) parameterArray;
		int parameterCount = methodInfo.getParameterCount();
		if (parameterCount != arr.length()) {
			throw new IllegalStateException(
					"Reflection call 'newInstance' to constructor does not have correct number of parameters");
		}
		for (int i = 1; i < methodInfo.getParameterCount();) {
			getLocalVariables().setLocalVariable(i, arr.get(i - 1));
		}
	}

	public FrameExit execute() {
		if (log.isDebugEnabled()) {
			String indent = String.format("%" + (virtualThread.getStack().getFrames().size() + 1) + "s", "");
			log.debug("{} Entered frame {} in method '{}'", new Object[] { indent, frameId, methodInfo.getFullName() });
		}

		try {
			virtualThread.getStack().push(this);

//			vm.getListeners().notifyFrameEntry(this);

			if (vm.hasReplacement(methodInfo)) {
				log.debug("Executing replacement method for '{}'", methodInfo.getFullName());
				return vm.executeReplacement(rda, this, methodInfo);
			}

			ExecutionEngine engine = new ExecutionEngine(vm, rda, this, methodInfo);

			while (true) {

				// TODO: This is the place where "Endless-Loop Detection"
				// could be implemented.

//				vm.getListeners().notifyBeforeInterpret(this);

				lastSuccessProgramCounter = virtualThread.getProgramCounter().getCurrentAddress();
				String opCode = engine.interpret();
				// log.debug("Executed opCode: %s in '%s'", opCode,
				// methodInfo.getFullName());

//				vm.getListeners().notifyAfterInterpret(this, opCode);

				if (opCode == null) {
					throw new IllegalStateException(
							"Interpreter returned NULL as opcode - bytecode is missing 'return' or 'throw'");
					// return new FrameExit();
				} else if (opCode.equals("return")) {
					// log.debug("Frame has executed a void return statement, continuing execution");
					operandStack.clear();
					return new FrameExit();
				} else if (opCode.equals("ireturn")) {
					JavaInteger pop = (JavaInteger) operandStack.pop();
					// log.debug("Frame has executed an int-return statement: %s",
					// pop);
					operandStack.clear();
					return new FrameExit(pop);
				} else if (opCode.equals("lreturn")) {
					JavaLong pop = (JavaLong) operandStack.pop();
					// log.debug("Frame has executed an long-return statement: %s",
					// pop);
					operandStack.clear();
					return new FrameExit(pop);
				} else if (opCode.equals("dreturn")) {
					JavaDouble pop = (JavaDouble) operandStack.pop();
					// log.debug("Frame has executed an double-return statement: %s",
					// pop);
					operandStack.clear();
					return new FrameExit(pop);
				} else if (opCode.equals("freturn")) {
					JavaFloat pop = (JavaFloat) operandStack.pop();
					// log.debug("Frame has executed an float-return statement: %s",
					// pop);
					operandStack.clear();
					return new FrameExit(pop);
				} else if (opCode.equals("areturn")) {
					JavaObjectReference pop = (JavaObjectReference) operandStack.pop();
					// log.debug("Frame returned an object: [%s], clearing old operand stack",
					// pop);
					operandStack.clear();
					return new FrameExit(pop);
				} else if (opCode.equals("athrow")) {
					JavaObjectReference exception = (JavaObjectReference) operandStack.pop();
					// log.debug("Frame has executed and threw an exception: %s",
					// exception);
					operandStack.clear();
					throw new ControlFlowException(exception);
				}
			}
		} 
		catch (ArithmeticException e){
			JavaObjectReference exception = new ExceptionFactory().convert(rda, this, e);
			throw new ControlFlowException(exception);
		}
		finally {
			Frame lastFrame = virtualThread.getStack().pop();
			if (lastFrame != this) {
				throw new IllegalStateException("Ups, the stack frames are mixed up.\nExpected:" + this + "\nActual:"
						+ lastFrame);
			}

			if (log.isDebugEnabled()) {
				String indent = String.format("%" + (virtualThread.getStack().getFrames().size() + 1) + "s", "");
				log.debug("{} Exited frame {} from method {}", new Object[] { indent, frameId, this.methodInfo });
			}
//			vm.getListeners().notifyFrameExit(this);
		} 
	}

	public RuntimeConstantPool getRuntimeConstantPool() {
		return runtimeConstantPool;
	}

	public VirtualThread getVirtualThread() {
		return virtualThread;
	}

	public LocalVariables getLocalVariables() {
		return localVariables;
	}

	public VirtualMachine getVirtualMachine() {
		return vm;
	}

	public void setCustomProperty(String key, Object value) {
		customProperties.put(key, value);
	}

	public Object getCustomProperty(String key) {
		return customProperties.get(key);
	}

	public JavaObjectReference getThisObject() {
		return (JavaObjectReference) getLocalVariables().getLocalVariable(0);
	}

	public Classfile getThisClass() {
		return runtimeConstantPool.getClassfile();
	}

	public int getLastSuccessProgramCounter() {
		return lastSuccessProgramCounter;
	}

}
