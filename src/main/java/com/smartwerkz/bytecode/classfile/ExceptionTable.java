package com.smartwerkz.bytecode.classfile;

import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class ExceptionTable {

	private List<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();

	public void registerHandler(int startPc, int endPc, int handlerPc, int catchType) {
		handlers.add(new ExceptionHandler(startPc, endPc, handlerPc, catchType));
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		for (ExceptionHandler handler : handlers) {
			sb.append("\n");
			sb.append(handler.print());
		}
		return sb.toString();
	}

	public ExceptionHandler findHandler(RuntimeDataArea rda, Frame frame, int currentAddress, RuntimeConstantPool rcp, JavaObjectReference javaException) {
		for (ExceptionHandler handler : handlers) {
			if (handler.matches(rda,frame,currentAddress,rcp,javaException)) {
				return handler;
			}
		}
		return null;
	}

}
