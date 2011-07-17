package com.smartwerkz.bytecode.tutorial1;

import com.smartwerkz.bytecode.CodeDumper.Opcode;
import com.smartwerkz.bytecode.vm.ExecutionListener;
import com.smartwerkz.bytecode.vm.Frame;

public abstract class ExecutionListenerAdapter implements ExecutionListener {

	@Override
	public void notifyFrameEntry(Frame frame) {
	}

	@Override
	public void notifyBeforeInterpret(Frame frame) {
	}

	@Override
	public void notifyBeforeOpcodeExecution(Frame frame, Opcode opCodeDesc) {
	}

	@Override
	public void notifyAfterInterpret(Frame frame, String opCode) {
	}

	@Override
	public void notifyFrameExit(Frame frame) {
	}

}
