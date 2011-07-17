package com.smartwerkz.bytecode.instructions;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Load char from array.
 * 
 * <p>
 * The arrayref must be of type reference and must refer to an array whose
 * components are of type char. The index must be of type int. Both arrayref and
 * index are popped from the operand stack. The component of the array at index
 * is retrieved and zero-extended to an int value. That value is pushed onto the
 * operand stack.
 * 
 * <p>
 * If arrayref is null, caload throws a NullPointerException. Otherwise, if
 * index is not within the bounds of the array referenced by arrayref, the
 * caload instruction throws an ArrayIndexOutOfBoundsException.
 * 
 * @author mhaller
 */
public class Caload extends BasicInstruction {

	public Caload() {
		super(0x34, "caload", "Load char from array");
	}

	@Override
	public void perform(VirtualMachine vm, OperandStack operandStack) {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObject value = arrayref.get(index.intValue());
		operandStack.push(value);
	}

}
