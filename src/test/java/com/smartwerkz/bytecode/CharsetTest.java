package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class CharsetTest {

	@Test
	public void testDefaultCharset() throws Exception {
		assertEquals("windows-1252", java.nio.charset.Charset.defaultCharset().name());
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  return java.nio.charset.Charset.defaultCharset().name();");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		vm.initSunJDK();
		assertEquals("UTF-8", executeJavaCode(vm, src.toString()).asStringValue());
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
