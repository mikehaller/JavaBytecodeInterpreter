package com.smartwerkz.bytecode.vm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import com.smartwerkz.bytecode.classfile.Attributes;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;

public class JavaStack {

	private final Deque<Frame> frames = new ArrayDeque<Frame>();

	public Frame getCurrentFrame() {
		return frames.getFirst();
	}

	public void push(Frame frame) {
		frames.push(frame);
	}

	public List<Frame> getFrames() {
		ArrayList<Frame> arrayList = new ArrayList<Frame>();
		Iterator<Frame> iterator = frames.iterator();
		while (iterator.hasNext()) {
			Frame frame = iterator.next();
			arrayList.add(frame);
		}
		return arrayList;
	}

	public Frame pop() {
		return frames.pop();
	}

	public String dumpStack() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Frame frame2 : frames) {
			i++;
			sb.append(i);
			sb.append(": ");
			Classfile dc = frame2.getThisClass();
			MethodInfo currentMethodInfo = frame2.getCurrentMethodInfo();
			sb.append(currentMethodInfo.getClassName());
			sb.append("#");
			sb.append(currentMethodInfo.getMethodName());
			sb.append(" ");
			Attributes attributes = dc.getAttributes();
			String sourceFileName = attributes.getSourceFileAttribute(dc);
			sb.append("(");
			if (sourceFileName != null) {
				sb.append(sourceFileName);
			} else {
				sb.append("Unknown Source");
			}
			int programCounter = frame2.getLastSuccessProgramCounter();
			int lineNumber = currentMethodInfo.getLineNumber(programCounter);
			sb.append(":");
			sb.append(lineNumber);
			sb.append(" PC=");
			sb.append(programCounter);
			sb.append(") Locals: ");
			
			LocalVariables localVariables = frame2.getLocalVariables();
			sb.append(localVariables.printOneLine());
			
			sb.append("\n");
		}
		return sb.toString();
	}

}
