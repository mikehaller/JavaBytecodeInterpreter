package com.smartwerkz.bytecode;

import org.junit.Test;

import com.smartwerkz.bytecode.vm.VirtualMachine;

public class FinallyTest {

	@Test
	public void testExecuteHelloWorld() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.execute("com/smartwerkz/bytecode/Finally");
		vm.stop();
	}

}
