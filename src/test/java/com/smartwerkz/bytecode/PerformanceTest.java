package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.OptimizingResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class PerformanceTest {

	private static final int COUNT = 1000;

	@Test
	public void testJavaBytecodeInterpreter() throws Exception {
		ClassPool cp = new ClassPool(false);
		CtClass objClazz = cp.makeClass("java.lang.Object");
		objClazz.addConstructor(CtNewConstructor.defaultConstructor(objClazz));
		CtClass cc = cp.makeClass("org.example.Foo");
		cc.addMethod(CtNewMethod.make("public static int add(int a, int b){ return a+b;};", cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();
		vm.start();
		
		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		for (int i = 0; i < COUNT; i++) {
			FrameExit result = vm.execute("org/example/Foo", "add", new JavaObject[] { new JavaInteger(1),
					new JavaInteger(2) });
			assertEquals(new JavaInteger(3), result.getResult());
		}
	}

	@Test
	public void testECMAScriptViaScriptEngine() throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("ECMAScript");
		String script = "function add(a,b) { return a+b; }";
		engine.eval(script);
		Invocable inv = (Invocable) engine;

		for (int i = 0; i < COUNT; i++) {
			Double result = (Double) inv.invokeFunction("add", 1, 2);
			assertEquals(3, result.intValue());
		}
	}

	@Test
	public void testDirectJava() throws Exception {
		for (int i = 0; i < COUNT; i++) {
			int x = add(1, 2);
			assertEquals(3, x);
		}
	}

	private int add(final int i, final int j) {
		return new Callable<Integer>() {

			@Override
			public Integer call() {
				return i + j;
			}
		}.call().intValue();
	}
}
