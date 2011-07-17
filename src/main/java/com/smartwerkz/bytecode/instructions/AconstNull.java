package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Push the <tt>null</tt> object reference onto the operand stack.
 * 
 * <p>
 * The Java virtual machine does not mandate a concrete value for null.
 * 
 * @author mhaller
 */
public class AconstNull extends BasicInstruction {

	public AconstNull() {
		super(0x01, "aconst_null","Push null");
	}

	@Override
	public void perform(VirtualMachine vm, OperandStack operandStack) {
		operandStack.push(vm.objects().nullReference());
	}

}
