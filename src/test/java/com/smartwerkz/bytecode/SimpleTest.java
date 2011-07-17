package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.smartwerkz.bytecode.classfile.JavassistResourceLoader;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.OptimizingResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class SimpleTest {

	@Test
	public void testAddInteger() throws Exception {
		assertEquals(new JavaInteger(3), execute(Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.IADD, Opcodes.IRETURN));
	}

	@Test
	public void testAddLong() throws Exception {
		assertEquals(new JavaLong(2), execute(Opcodes.LCONST_1, Opcodes.LCONST_1, Opcodes.LADD, Opcodes.LRETURN));
	}

	@Test
	public void testAddFloats() throws Exception {
		assertEquals(new JavaFloat(3.0f), execute(Opcodes.FCONST_1, Opcodes.FCONST_2, Opcodes.FADD, Opcodes.FRETURN));
	}

	@Test
	public void testAddDoubles() throws Exception {
		assertEquals(new JavaDouble(2.0d), execute(Opcodes.DCONST_1, Opcodes.DCONST_1, Opcodes.DADD, Opcodes.DRETURN));
	}

	@Test
	public void testReturnString() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return \"foo\";");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();
		// vm.initSunJDK();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		String asStringValue = result.getResult().asStringValue();
		assertEquals("foo", asStringValue);
	}

	@Test
	public void testThrowNewUnsupportedCharsetException() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Throwing");
		StringBuilder src = new StringBuilder();
		src.append("public static void throwException() {");
		src.append("  throw new java.nio.charset.UnsupportedCharsetException(\"test\");");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();

		FrameExit result = vm.execute("org/example/Throwing", "throwException", new JavaObject[0]);
		String asStringValue = result.getResult().asStringValue();
		assertTrue(asStringValue.contains("java/nio/charset/UnsupportedCharsetException"));
		assertTrue(asStringValue.contains("test"));
	}

	@Test
	public void testTryFinally() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		{
			StringBuilder src = new StringBuilder();
			src.append("public static int in() {");
			src.append("  return 3; ");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		{
			StringBuilder src = new StringBuilder();
			src.append("public static int out() {");
			src.append("  return 4; ");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		{
			StringBuilder src = new StringBuilder();
			src.append("public static int test() {");
			src.append("  try { in(); } finally { out(); } ");
			src.append("  return 4;");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaInteger(4), result.getResult());
	}

	@Test
	public void testLongParametersForStaticMethod() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		{
			StringBuilder src = new StringBuilder();
			src.append("public static long second(long first, long second, long third) {");
			src.append("  return second; ");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		{
			StringBuilder src = new StringBuilder();
			src.append("public static long test() {");
			src.append("  return second(1L,2L,3L);");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();
		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();
		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaLong(2L), result.getResult());
	}

	@Test
	public void testLongParametersForObject() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass inner = cp.makeClass("org.example.Holder");
		{
			StringBuilder src = new StringBuilder();
			src.append("public Holder(){}");
			inner.addConstructor(CtNewConstructor.make(src.toString(), inner));
		}
		{
			StringBuilder src = new StringBuilder();
			src.append("public long second(long first, long second, long third) {");
			src.append("  return second; ");
			src.append("}");
			inner.addMethod(CtNewMethod.make(src.toString(), inner));
		}
		CtClass cc = cp.makeClass("org.example.Test");
		{
			StringBuilder src = new StringBuilder();
			src.append("public static long test() {");
			src.append("  return new org.example.Holder().second(1L,2L,3L);");
			src.append("}");
			cc.addMethod(CtNewMethod.make(src.toString(), cc));
		}
		VirtualMachine vm = new VirtualMachine();
		Classpath classpath = new Classpath();
		classpath.addLoader(new JavassistResourceLoader(cp));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();
		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaLong(2L), result.getResult());
	}

	@Test
	public void testCompareNulls() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static boolean test() {");
		src.append("  Object a = null;");
		src.append("  Object b = null;");
		src.append("  return a == b;");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaInteger(1), result.getResult());
	}

	@Test
	public void testBranch() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static boolean test() {");
		src.append("  int x = 0;");
		src.append("  return x == 0 ? true : false;");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaInteger(1), executeJavaCode(vm, src.toString()));
	}

	@Test
	public void testException1() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static void test() {");
		src.append("  throw new java.lang.IllegalStateException(\"foobar\");");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		String asStringValue = executeJavaCode(vm, src.toString()).asStringValue();
		assertTrue(asStringValue.contains("foobar"));
		assertTrue(asStringValue.contains("IllegalStateException"));
	}

	@Test
	public void testException3() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static void test() {");
		src.append("  throw new java.lang.IllegalStateException(\"foo\", new java.lang.IllegalArgumentException(\"bar\"));");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		String asStringValue = executeJavaCode(vm, src.toString()).asStringValue();
		assertTrue(asStringValue.contains("foo"));
		assertTrue(asStringValue.contains("IllegalStateException"));

		StringBuilder expected = new StringBuilder();
		expected.append("**** JAVA EXCEPTION ****\n");
		expected.append("java/lang/IllegalStateException: foo\n");
		expected.append(" [0] at java/lang/Throwable.<init>(Throwable.java:158)\n");
		expected.append(" [1] at java/lang/Exception.<init>(Exception.java:59)\n");
		expected.append(" [2] at java/lang/RuntimeException.<init>(RuntimeException.java:61)\n");
		expected.append(" [3] at java/lang/IllegalStateException.<init>(IllegalStateException.java:58)\n");
		expected.append(" [4] at org/example/Test.test(Test.java:-1)\n");
		expected.append("*** CAUSED BY ***\n");
		expected.append("java/lang/IllegalArgumentException: bar\n");
		expected.append(" [0] at java/lang/Throwable.<init>(Throwable.java:158)\n");
		expected.append(" [1] at java/lang/Exception.<init>(Exception.java:41)\n");
		expected.append(" [2] at java/lang/RuntimeException.<init>(RuntimeException.java:43)\n");
		expected.append(" [3] at java/lang/IllegalArgumentException.<init>(IllegalArgumentException.java:36)\n");
		expected.append(" [4] at org/example/Test.test(Test.java:-1)\n");
		expected.append("**** JAVA EXCEPTION ****\n");
		System.err.println(vm.getScreen().asString());
		assertEquals(expected.toString(), vm.getScreen().asString());
	}

	@Test
	public void testStringConcatenation() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return \"foo\" + \"bar\";");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "foobar"), executeJavaCode(vm, src.toString()));
	}

	@Test
	public void testStringEscaping() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return \"\\\\\";");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "\\"), executeJavaCode(vm, src.toString()));
	}

	private JavaObject executeJavaCode(VirtualMachine vm, String methodBody) throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		CtMethod method = CtNewMethod.make(methodBody, cc);
		cc.addMethod(method);
		byte[] b = cc.toBytecode();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		return result.getResult();
	}

	private JavaObject execute(int... opcodes) {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "org/example/Foo", null, "java/lang/Object", null);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V",
				null, null);
		// mv.visitMaxs(2, 2);
		mv.visitCode();
		for (int opcode : opcodes) {
			mv.visitInsn(opcode);
		}
		mv.visitEnd();
		cw.visitEnd();
		byte[] b = cw.toByteArray();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();

		return vm.execute("org/example/Foo").getResult();
	}

}
