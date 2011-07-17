package com.smartwerkz.bytecode.classfile;

import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.SymbolicClassReference;

public class ExceptionHandler {

	private final int startPc;
	private final int endPc;
	private final int handlerPc;
	private final int catchType;

	public ExceptionHandler(final int startPc, final int endPc, final int handlerPc, final int catchType) {
		this.startPc = startPc;
		this.endPc = endPc;
		this.handlerPc = handlerPc;
		this.catchType = catchType;
	}

	public String print() {
		return String.format("%d %d %d %d", startPc, endPc, handlerPc, catchType);
	}

	public boolean matches(RuntimeDataArea rda, Frame frame, int currentAddress, RuntimeConstantPool rcp, JavaObjectReference javaException) {
		if (currentAddress <= startPc) {
			return false;
		}
		if (currentAddress > endPc) {
			return false;
		}
		if (catchType==0) {
			// Catch *ANY* exception - special case
			return true;
		}
		SymbolicClassReference symbolicClassReference = rcp.getSymbolicClassReference(catchType);
		String className = symbolicClassReference.getClassName();
		if (javaException.getClassFile().isInstanceOf(rda,frame,className)) {
			return true;
		}
		return false;
	}

	public int getHandlerProgramCounter() {
		return handlerPc;
	}

}
