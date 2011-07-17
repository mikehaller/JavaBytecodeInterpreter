package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.OptimizingResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class LongerTests {
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
		// vm.initSunJDK();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		return result.getResult();
	}

	@Test
	public void testStringConcatenationWithInteger() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return \"foo\" + \"bar\" + 3;");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "foobar3"), executeJavaCode(vm, src.toString()));
	}

	@Test
	public void testStringConcatenationWithBoolean() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return \"foo\" + \"bar\" + true;");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "foobartrue"), executeJavaCode(vm, src.toString()));
	}

	/**
	 * <pre>
	 * Dump of Object:
	 *  Object:	25131
	 *  Class:	java/lang/NullPointerException
	 *  Field 'detailMessage':	com.smartwerkz.bytecode.primitives.JavaString@b4231f[Unable to invoke method on NULL reference 'com.smartwerkz.bytecode.primitives.JavaNullReference@4e93[class=void]']
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testException2() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static void test() {");
		src.append("  java.lang.String.class.getResourceAsStream(null);");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		String asStringValue = executeJavaCode(vm, src.toString()).asStringValue();
		assertTrue(asStringValue.contains("NullPointerException"));
	}

	@Test
	public void testByteBuffer() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return java.nio.Bits.byteOrder();");
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
		assertEquals("BIG_ENDIAN", ((JavaObjectReference) result.getResult()).getValueOfField("name").asStringValue());
	}

	@Test
	public void testGetCharsets() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return java.nio.charset.Charset.forName(\"UTF-8\").name();");
		src.append("}");
		cc.addMethod(CtNewMethod.make(src.toString(), cc));
		byte[] b = cc.toBytecode();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();
		vm.initSunJDK();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		assertEquals(new JavaString(vm, "UTF-8"), result.getResult());
	}

	@Test
	public void testGetDeclaredConstructors() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static int test() {");
		src.append("  return org.example.Test.class.getDeclaredConstructors().length;");
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
		assertEquals(new JavaInteger(2), result.getResult());
	}

	@Test
	public void testStringFormat() throws Exception {
		assertEquals("foo-bar", String.format("%s-%s", new Object[] { "foo", "bar" }));

		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return String.format(\"%s-%s\",new Object[]{\"foo\",\"bar\"});");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		vm.initSunJDK();
		JavaObject executeJavaCode = executeJavaCode(vm, src.toString());
		if (executeJavaCode instanceof JavaObjectReference) {
			JavaObjectReference javaObjectReference = (JavaObjectReference) executeJavaCode;
			String buildMessage = ExceptionFactory.buildMessage(vm, javaObjectReference);
			System.out.println(buildMessage);
		}
		assertEquals(new JavaString(vm, "foo-bar"), executeJavaCode);
	}
}
