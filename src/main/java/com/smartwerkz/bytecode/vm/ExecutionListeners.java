package com.smartwerkz.bytecode.vm;

import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.CodeDumper.Opcode;

public class ExecutionListeners {

	private List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

	public void add(ExecutionListener listener) {
		listeners.add(listener);
	}

	public void notifyFrameEntry(Frame frame) {
		for (ExecutionListener listener : listeners) {
			listener.notifyFrameEntry(frame);
		}
	}

	public void notifyBeforeInterpret(Frame frame) {
		for (ExecutionListener listener : listeners) {
			listener.notifyBeforeInterpret(frame);
		}
	}

	public void notifyBeforeOpcodeExecution(Frame frame, Opcode opCodeDesc) {
		for (ExecutionListener listener : listeners) {
			listener.notifyBeforeOpcodeExecution(frame, opCodeDesc);
		}
	}

	public void notifyAfterInterpret(Frame frame, String opCode) {
		for (ExecutionListener listener : listeners) {
			listener.notifyAfterInterpret(frame, opCode);
		}
	}

	public void notifyFrameExit(Frame frame) {
		for (ExecutionListener listener : listeners) {
			listener.notifyFrameExit(frame);
		}
	}

}
