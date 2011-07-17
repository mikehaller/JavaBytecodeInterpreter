package com.smartwerkz.bytecode.primitives;

import static org.junit.Assert.*;

import org.junit.Test;

import com.smartwerkz.bytecode.vm.VirtualMachine;

public class JavaArrayTest {

	@Test
	public void testCopy() {
		VirtualMachine vm = new VirtualMachine();
		JavaArray array = new JavaArray(vm,vm.classes().primitives().intClass());
		JavaArray copy = (JavaArray) array.copy();
		assertNotSame(copy, array);
		assertEquals(copy.getComponentType(), array.getComponentType());
		assertEquals(copy.length(), array.length());
	}

	@Test
	public void testGetComponentType() {
		VirtualMachine vm = new VirtualMachine();
		JavaArray array = new JavaArray(vm,vm.classes().primitives().intClass());
		assertEquals(vm.classes().primitives().intClass(), array.getComponentType());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetArrayIndexOutOfBounds() {
		VirtualMachine vm = new VirtualMachine();
		JavaArray array = new JavaArray(vm,vm.classes().primitives().intClass());
		array.set(0, new JavaInteger(0));
	}

	@Test
	public void testSet() {
		VirtualMachine vm = new VirtualMachine();
		JavaArray array = new JavaArray(vm,vm.classes().primitives().intClass(), 1);
		array.set(0, new JavaInteger(1));
		JavaInteger javaObject = (JavaInteger) array.get(0);
		assertEquals(1, javaObject.intValue());
	}

	@Test
	public void testLength() {
		VirtualMachine vm = new VirtualMachine();
		JavaArray array = new JavaArray(vm,vm.classes().primitives().intClass(), 2);
		assertEquals(2, array.length());
	}

}
