package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.Unsafe;
import com.smartwerkz.bytecode.vm.VMLog;

/**
 * Compares the value of the integer field at the specified offset in the
 * supplied object with the given expected value, and updates it if they match.
 * 
 * <tt>boolean 	compareAndSwapInt(java.lang.Object obj, long offset, int expect, int update)</tt>
 * 
 * @author mhaller
 */
public class UnsafeCompareAndSwapInt implements NativeMethod {

	private VMLog log = new VMLog(UnsafeCompareAndSwapInt.class.getName());
	private final Unsafe unsafe;

	public UnsafeCompareAndSwapInt(Unsafe unsafe) {
		this.unsafe = unsafe;
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaInteger update = (JavaInteger) frame.getLocalVariables().getLocalVariable(5);
		JavaInteger expect = (JavaInteger) frame.getLocalVariables().getLocalVariable(4);
		// 3 must be empty, because it's the second half of 'offset' as its a long/double
		JavaLong offset = (JavaLong) frame.getLocalVariables().getLocalVariable(2);
		JavaObjectReference targetObject = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(1);
		JavaObjectReference unsafeObject = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);

		// TODO: Find the 'field' in the 'targetObject' identified by the
		// InstanceID 'offset'
		// and set it to the 'update' value, but only if it's current value is
		// the same
		// as the one in 'expect'

		// TODO: Return 'true' if the value was updated. False if not.

		log.error("*** UNIMPLEMENTED 'Unsafe' METHOD *********");
		if (unsafe.compareAndSwapInt(targetObject, offset.longValue(), expect.intValue(), update.intValue())) {
			operandStack.push(new JavaInteger(1));
		} else {
			operandStack.push(new JavaInteger(0));
		}
	}

}
