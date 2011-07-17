package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class CommandLineArgumentsTest {

	@Test
	public void testNoArgs() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();
		
		JavaArray params = new JavaArray(vm,vm.classes().stringClass());
		JavaObject[] parameters = new JavaObject[] { params };

		vm.execute("org/example/CommandLine", "main", parameters);
		vm.stop();
		assertEquals("Command Line Arguments\r\n0\r\nEnd of Command Line Arguments", vm.getScreen()
				.asString().trim());
	}

	@Test
	public void testOneArgument() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();

		JavaArray params = new JavaArray(vm,vm.classes().stringClass(), 1);
		params.set(0, new JavaString(vm,"foo"));
		JavaObject[] parameters = new JavaObject[] { params };

		vm.execute("org/example/CommandLine", "main", parameters);
		vm.stop();
		assertEquals("Command Line Arguments\r\n1\r\nArgument 0\r\nfoo\r\nEnd of Command Line Arguments", vm.getScreen()
				.asString().trim());
	}

}
