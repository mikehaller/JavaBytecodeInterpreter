package com.smartwerkz.bytecode.vm;

import java.util.concurrent.LinkedBlockingDeque;

import com.smartwerkz.bytecode.primitives.JavaObject;

public class OperandStack {

	// private final VMLog log = new VMLog(OperandStack.class.getName(), false);

	private final LinkedBlockingDeque<JavaObject> stack;

	private int effectiveMaxStack;

	public OperandStack(int maxStack) {
		effectiveMaxStack = maxStack > 0 ? (maxStack + 1) : 10;
		stack = new LinkedBlockingDeque<JavaObject>(effectiveMaxStack);
	}

	public JavaObject peekFirst() {
		return stack.peekFirst();
	}

	public void push(JavaObject object) {
		if (object == null)
			throw new IllegalArgumentException("null not allowed on the operand stack, use JavaNullReference");
		if (stack.remainingCapacity() == 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("The OperandStack's maximum size has been reached.\n");
			sb.append("max_stack=" + effectiveMaxStack + "\n");
			sb.append("size=" + stack.size() + "\n");
			sb.append("pushed object=" + object + "\n");
			sb.append("objects on stack=" + stack + "\n");
			throw new IllegalStateException(sb.toString());
		}
		stack.push(object);
	}

	public JavaObject pop() {
		if (stack.isEmpty()) {
			throw new IllegalStateException("Cannot pop from operand stack, as the stack is empty.");
		}

		return stack.pop();
	}

	public void clear() {
		stack.clear();
	}

}
