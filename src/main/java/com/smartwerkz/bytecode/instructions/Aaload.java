package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Load reference from array.
 * 
 * <p>
 * The arrayref must be of type reference and must refer to an array whose
 * components are of type reference. The index must be of type int. Both
 * arrayref and index are popped from the operand stack. The reference value in
 * the component of the array at index is retrieved and pushed onto the operand
 * stack.
 * 
 * <p>
 * If arrayref is null, aaload throws a NullPointerException. Otherwise, if
 * index is not within the bounds of the array referenced by arrayref, the
 * aaload instruction throws an ArrayIndexOutOfBoundsException.
 * 
 * @author mhaller
 */
public class Aaload extends BasicInstruction {

	public Aaload() {
		super(0x32, "aaload", "Load reference from array");
	}

	/**
	 * ..., arrayref, index > ..., value
	 */
	@Override
	public void perform(VirtualMachine vm, OperandStack operandStack) {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObjectReference value = (JavaObjectReference) arrayref.get(index.intValue());
		operandStack.push(value);
	}

}
