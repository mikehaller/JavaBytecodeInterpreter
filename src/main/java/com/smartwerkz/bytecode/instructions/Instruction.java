package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Interface for byte code operations.
 * 
 * @author mhaller
 */
public interface Instruction {

	void perform(VirtualMachine vm, OperandStack operandStack);
	
}
