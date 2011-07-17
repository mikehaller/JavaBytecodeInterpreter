package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualThread;

public class ThreadStartMethod implements NativeMethod {

	private final VMLog log = new VMLog(ThreadStartMethod.class.getName());

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaObjectReference threadReference = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);
		JavaArray threadName = (JavaArray) threadReference.getValueOfField("name");
		VirtualThread newThread = new VirtualThread(frame.getVirtualMachine(), rda, threadName.asStringValue(),
				threadReference);
		log.debug("Starting new Thread: %s", threadReference);
		rda.getThreads().startThread(newThread);
	}

}
