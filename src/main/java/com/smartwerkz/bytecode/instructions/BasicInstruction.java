package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

abstract class BasicInstruction implements Instruction {

	private final String operationName;
	private final int opCode;

	public BasicInstruction(int opCode, String operationName, String description) {
		this.opCode = opCode;
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
	
	public int getOpCode() {
		return opCode;
	}

	@Override
	public void perform(VirtualMachine vm, OperandStack operandStack) {
		throw new UnsupportedOperationException("The byte code operation '" + operationName
				+ "' has not been implemented yet.");
	}

}
