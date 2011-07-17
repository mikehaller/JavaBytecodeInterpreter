package com.smartwerkz.bytecode.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;

public class LocalVariables {

	private static final VMLog log = new VMLog(LocalVariables.class.getSimpleName(), true);

	private final JavaObject[] variables;

	public LocalVariables(int maxLocals) {
		int effectiveMaxLocals = Math.max(10, maxLocals);
		this.variables = new JavaObject[effectiveMaxLocals];
	}

	public JavaObject getLocalVariable(int i) {
		if (variables[i] == null) {
			throw new IllegalStateException("Access to non-existent local variable " + i);
		}
		return variables[i];
	}

	public void setLocalVariable(int i, JavaObject pop) {
		if (i >= variables.length) {
			throw new ArrayIndexOutOfBoundsException("i=" + i + " length=" + variables.length + " obj=" + pop);
		}
		if (pop instanceof JavaObject) {
			variables[i] = pop;
		} else {
			throw new IllegalStateException("Local variables must be stored as JavaObject");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < variables.length; i++) {
			if (variables[i] != null) {
				sb.append("\n\tLocal Variable ");
				sb.append(i);
				sb.append("\t=\t");
				sb.append(variables[i]);
			}
		}
		return sb.toString();
	}

	public int count() {
		int count = 0;
		for (int i = 0; i < variables.length; i++) {
			if (variables[i] != null)
				count++;
		}
		return count;
	}

	public void populate(final int parameterCount, final OperandStack operandStack) {
		List<JavaObject> parameters = new ArrayList<JavaObject>(parameterCount);
		for (int i = 0; i < parameterCount; i++) {
			parameters.add(operandStack.pop());
		}
		Collections.reverse(parameters);
		int index = 0;
		for (JavaObject object : parameters) {
			variables[index] = object;
			if (object instanceof JavaLong || object instanceof JavaDouble) {
				index++;
			}
			index++;
		}
		// log.debug("Populated local variables: %s", toString());
	}

	public void populate(LocalVariables source) {
		for (int i = 0; i < source.variables.length; i++) {
			variables[i] = source.variables[i];
		}
	}

	public String printOneLine() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < variables.length; i++) {
			if (variables[i]!=null) {
				sb.append(i);
				sb.append("=[");
				sb.append(variables[i].asStringValue().replaceAll(Pattern.quote("\n"), ""));
				sb.append("] ");
			}
		}
		return sb.toString();
	}

	public boolean hasVariable(int i) {
		return variables[i] != null;
	}
}
