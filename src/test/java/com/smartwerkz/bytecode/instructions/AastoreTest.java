package com.smartwerkz.bytecode.instructions;

import static org.junit.Assert.*;
import org.junit.Test;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.instructions.Aastore;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class AastoreTest {

	/**
	 * arrayref, index, value
	 */
	@Test
	public void testPerform() {
		VirtualMachine vm = new VirtualMachine();
		Classfile cf = new LazyClassfile("foobar");
		OperandStack stack = new OperandStack(1);

		JavaArray arrayref = new JavaArray(vm,cf, 1);
		JavaInteger index = new JavaInteger(0);
		JavaObjectReference value = new JavaObjectReference(cf);

		stack.push(arrayref);
		stack.push(index);
		stack.push(value);

		Aastore aastore = new Aastore();
		aastore.perform(vm, stack);

		assertEquals(value, arrayref.get(index.intValue()));
	}

	@Test
	public void testClassCastException() throws Exception {
		fail("Write test for ClassCastException");
	}

	@Test
	public void testArrayIndexOutOfBoundsException() throws Exception {
		fail("Write test");
	}

	@Test
	public void testNullPointerExceptionIfArrayIsNull() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		OperandStack stack = new OperandStack(3);
		stack.push(vm.objects().nullReference());
		stack.push(new JavaInteger(0));
		stack.push(vm.objects().nullReference());
		Aastore aastore = new Aastore();
		aastore.perform(vm, stack);
		assertException("java/lang/NullPointerException", stack.pop());

	}

	private void assertException(String expectedExceptionClassName, JavaObject exceptionObject) {
		JavaObjectReference excObjRef = (JavaObjectReference) exceptionObject;
		assertEquals(expectedExceptionClassName,excObjRef.getClassFile().getThisClassName());
	}
}
