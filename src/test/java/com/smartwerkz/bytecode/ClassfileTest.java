package com.smartwerkz.bytecode;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javassist.bytecode.Opcode;

import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.DefaultClassfile;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassfileTest {

	private static final byte[] CAFEBABE = new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };

	@Test
	public void testBootingCreatesMainThread() throws Exception {
		VirtualMachine vm = new VirtualMachine();

		assertFalse(vm.isStarted());
		// Although the VM is not started yet, the Main Thread is already there.
		assertEquals(1, vm.getRuntimeData().getThreads().size());

		vm.start();
		assertTrue(vm.isStarted());
		assertEquals(1, vm.getRuntimeData().getThreads().size());
		assertEquals("main", vm.getRuntimeData().getThreads().getMainThread().getName());
		vm.stop();
		assertFalse(vm.isStarted());
		// Although the VM is not started yet, the Main Thread is already there.
		// but does not stay there if the VM is shut down
		assertEquals(0, vm.getRuntimeData().getThreads().size());
	}

	@Test
	public void testReadClassfile() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		DefaultClassfile cf = new DefaultClassfile("Classfile.class.binary");
		assertArrayEquals(CAFEBABE, cf.getMagicNumber());

		assertEquals(50, cf.getMajorVersion());
		assertEquals(0, cf.getMinorVersion());

		// Actually, there are only 17 entries O_o
		assertEquals(18, cf.getConstantPool().getSize());

		assertTrue(cf.getAccessFlags().isPublic());
		assertFalse(cf.getAccessFlags().isFinal());
		assertTrue(cf.getAccessFlags().isSuper());
		assertFalse(cf.getAccessFlags().isInterface());
		assertFalse(cf.getAccessFlags().isAbstract());

		assertEquals(1, cf.getThisClassIndex());
		assertEquals("com/smartwerkz/bytecode/Classfile", cf.getThisClassName());

		assertEquals(3, cf.getSuperClassIndex());
		assertEquals("java/lang/Object", cf.getSuperClassName());

		assertEquals(0, cf.getInterfaces().size());
		assertEquals(0, cf.getFields().size());
		assertEquals(2, cf.getMethods(vm).size());

		assertEquals("<init>", cf.getMethodName(0));
		assertEquals("validateHeader", cf.getMethodName(1));

		assertEquals(1, cf.getAttributes().size());
		assertEquals("Classfile.java", cf.getAttributes().getSourceFileAttribute(cf));

		assertEquals(5, cf.getMethodCode(0).getCode().length);
	}

	@Test
	public void testAnnotationAttributes() throws Exception {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "org/example/Foo", null, "java/lang/Object", null);

		AnnotationVisitor av = cw.visitAnnotation("java.lang.Deprecated", true);
		av.visitEnd();

		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V",
				null, null);
		mv.visitMaxs(2, 2);
		mv.visitCode();
		mv.visitInsn(Opcode.NOP);
		mv.visitInsn(Opcode.ICONST_1);
		mv.visitInsn(Opcode.IRETURN);
		mv.visitEnd();
		cw.visitEnd();
		byte[] b = cw.toByteArray();
		VirtualMachine vm = new VirtualMachine();

		Classpath classpath = new Classpath();
		classpath.addLoader(new ByteArrayResourceLoader(b));
		classpath.addLoader(new HostVMResourceLoader());
		vm.setClasspath(classpath);
		vm.start();

		Classfile cf = vm.getBootstrapClassloader().load("org/example/Foo");
		assertEquals("[[name=java.lang.Deprecated, pairs=[]]]",cf.getAttributes().getRuntimeVisibleAnnotations().toString());
		
		FrameExit execute = vm.execute("org/example/Foo");
		assertTrue(execute.hasReturnValue());
		assertEquals("", vm.getScreen().asString());
	}

}
