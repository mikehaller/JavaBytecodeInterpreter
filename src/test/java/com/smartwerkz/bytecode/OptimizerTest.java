package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.OptimizingResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class OptimizerTest {
	@Test
	public void testNOPs() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaInteger(3), execute(vm, Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.IADD, Opcodes.IRETURN));
		assertEquals(4, vm.getProfiling().getTotalCount());
	}

	@Test
	public void testOptimizedHelloWorld() throws Exception
	{
		VirtualMachine vm = new VirtualMachine();
		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new HostVMResourceLoader()));
		vm.setClasspath(classpath);
		vm.start();
		vm.execute("com/smartwerkz/bytecode/HelloWorld");
		assertEquals("Hello World!\n", vm.getScreen().asString());
	}

	private JavaObject execute(VirtualMachine vm, int... opcodes) {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "org/example/Foo", null, "java/lang/Object", null);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V",
				null, null);
		mv.visitMaxs(2, 2);
		mv.visitCode();
		for (int opcode : opcodes) {
			mv.visitInsn(opcode);
		}
		mv.visitEnd();
		cw.visitEnd();
		byte[] b = cw.toByteArray();

		Classpath classpath = new Classpath();
		classpath.addLoader(new OptimizingResourceLoader(new ByteArrayResourceLoader(b)));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);

		vm.start();

		return vm.execute("org/example/Foo").getResult();
	}
}
