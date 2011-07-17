package com.smartwerkz.bytecode.vm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VirtualMachineTest {

	@Test
	public void testExecuteHelloWorld() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();
		vm.execute("com/smartwerkz/bytecode/HelloWorld");
		assertEquals("Hello World!\n", vm.getScreen().asString());
	}

}
