package com.smartwerkz.bytecode.tutorial1;

import java.util.concurrent.atomic.AtomicLong;

import com.smartwerkz.bytecode.CodeDumper.Opcode;
import com.smartwerkz.bytecode.vm.Frame;

public abstract class InstructionInvocationConstraint extends ExecutionListenerAdapter {

	private final int maxLoops;
	private final String opCodeName;

	public InstructionInvocationConstraint(String opCodeName, int maxInvocations) {
		this.opCodeName = opCodeName;
		this.maxLoops = maxInvocations;
	}

	@Override
	public void notifyFrameEntry(Frame frame) {
		frame.setCustomProperty("counter", new AtomicLong(0));
	}

	@Override
	public void notifyBeforeOpcodeExecution(Frame frame, Opcode opCodeDesc) {
		if (opCodeDesc.opCodeName.equals(opCodeName)) {
			AtomicLong counter = (AtomicLong) frame.getCustomProperty("counter");
			long jumps = counter.incrementAndGet();
			if (jumps >= maxLoops) {
				onMaxInvocationsReached();
			}
		}
	}

	protected abstract void onMaxInvocationsReached();

}
