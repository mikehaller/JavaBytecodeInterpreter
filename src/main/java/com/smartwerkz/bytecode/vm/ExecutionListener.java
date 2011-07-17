package com.smartwerkz.bytecode.vm;

import com.smartwerkz.bytecode.CodeDumper.Opcode;

public interface ExecutionListener {

	void notifyFrameEntry(Frame frame);
	void notifyBeforeInterpret(Frame frame);
	void notifyBeforeOpcodeExecution(Frame frame, Opcode opCodeDesc);
	void notifyAfterInterpret(Frame frame, String opCode);
	void notifyFrameExit(Frame frame);


}
