package com.smartwerkz.bytecode.tutorial2;

import static org.junit.Assert.assertEquals;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * This tutorial shows that with a bytecode interpreter, we can intercept at
 * instruction level and stop an endlessly running program.
 * 
 * @author mhaller
 */
public class Tutorial2Test {

	@Test
	public void testDetectEndlessLoops() throws Exception {
		// Step 1: Dynamically generate a bogus class for demo purposes
		ClassPool cp = new ClassPool(true);
		// CtClass objClazz = cp.makeClass("java.lang.Object");
		// objClazz.addConstructor(CtNewConstructor.defaultConstructor(objClazz));
		CtClass cc = cp.makeClass("org.example.Blocking");

		StringBuilder src = new StringBuilder();
		src.append("public static void blocking() {");
		src.append("  new java.util.concurrent.ArrayBlockingQueue(1).take();");
		src.append("}");

		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();

		// Step 2: Now let's start up the Virtual Java Virtual Machine.
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		vm.initSunJDK();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		// Step 3: Execute the endless loop
		FrameExit result = vm.execute("org/example/Blocking", "blocking", new JavaObject[] { new JavaInteger(1),
				new JavaInteger(2) });
		assertEquals("", result.getResult().asStringValue());
	}

}
