package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Store into reference array.
 * 
 * <p>
 * The arrayref must be of type reference and must refer to an array whose
 * components are of type reference. The index must be of type int and value
 * must be of type reference. The arrayref, index, and value are popped from the
 * operand stack. The reference value is stored as the component of the array at
 * index.
 * 
 * @author mhaller
 */
public class Aastore extends BasicInstruction {

	public Aastore() {
		super(0x53, "aastore", "Store into reference array");
	}

	/**
	 * ..., arrayref, index, value ... >
	 */
	@Override
	public void perform(VirtualMachine vm, OperandStack operandStack) {
		JavaObjectReference objectRef = (JavaObjectReference) operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		// TODO: Verify the "assignment compatibility" of the types
		arrayref.set(index.intValue(), objectRef);
	}

}
