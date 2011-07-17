package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class FileSystemTest {
	@Test
	public void testFileToString() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return new java.io.File(\".\").toString();");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaString(vm, "."), result.getResult());
	}

	@Test
	public void testFileGetAbsolutePath() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return new java.io.File(\".\").getAbsolutePath();");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();
		vm.initSunJDK();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		JavaString interned = vm.getStringPool().intern((JavaObjectReference) result.getResult());
		assertEquals("/", interned.asStringValue());
	}

	@Test
	public void testURIToString() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return java.net.URI.create(\"foo\").toString();");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaString(vm, "foo"), result.getResult());
	}

}
