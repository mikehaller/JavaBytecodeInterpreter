package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ExecutingJunitTest {

	@Test
	public void testJunitRunnerVersion() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();
		FrameExit execute = vm.execute("junit/runner/Version");
		if (execute.hasReturnValue()) {
			System.err.println(vm.getScreen().asString());
		}
		vm.stop();
		assertEquals("4.8.2", vm.getScreen().asString().trim());
	}

	@Test
	public void testExecuteTestcase() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();

		JavaArray params = new JavaArray(vm, vm.classes().stringClass(), 1);
		params.set(0, new JavaString(vm, "org/example/junit/SimpleJUnit3Test"));
		JavaObject[] parameters = new JavaObject[] { params };

		FrameExit execute = vm.execute("org/junit/runner/JUnitCore", "main", parameters);
		vm.stop();
		if (execute.hasReturnValue()) {
			System.err.println(vm.getScreen().asString());
		}
		
		assertEquals("JUnit version 4.8.2", vm.getScreen().asString().trim());
		fail("Does not execute test methods");
	}

}
