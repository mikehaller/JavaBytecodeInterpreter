package com.smartwerkz.bytecode.tutorial1;

import static org.junit.Assert.assertSame;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaNullReference;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.OptimizingResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * This tutorial shows that with a bytecode interpreter, we can intercept at
 * instruction level and stop an endlessly running program.
 * 
 * @author mhaller
 */
public class Tutorial1Test {

	@Test
	public void testDetectEndlessLoops() throws Exception {
		// Step 1: Dynamically generate a bogus class for demo purposes
		ClassPool cp = new ClassPool(false);
		CtClass objClazz = cp.makeClass("java.lang.Object");
		objClazz.addConstructor(CtNewConstructor.defaultConstructor(objClazz));
		CtClass cc = cp.makeClass("org.example.Endless");

		StringBuilder src = new StringBuilder();
		src.append("public static void endless() {");
		src.append("  int cnt=0;");
		src.append("  do {");
		src.append("    cnt++;");
		src.append("  } while(true);");
		src.append("}");

		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();

		// Step 2: Now let's start up the Virtual Java Virtual Machine.
		VirtualMachine vm = new VirtualMachine();
		vm.start();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		// This constraint prevents any method from
		// jumping more than 10 times using "GOTO"
		final JavaNullReference reason = vm.objects().nullReference();
//		vm.addExecutionListener(new InstructionInvocationConstraint("goto", 10) {
//			@Override
//			protected void onMaxInvocationsReached() {
//				throw new ControlFlowException(reason);
//			}
//		});

		// Step 3: Execute the endless loop
		FrameExit result = vm.execute("org/example/Endless", "endless", new JavaObject[] { new JavaInteger(1),
				new JavaInteger(2) });
		assertSame(reason, result.getResult());
	}

}
