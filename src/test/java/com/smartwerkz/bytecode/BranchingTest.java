package com.smartwerkz.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.Opcode;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.smartwerkz.bytecode.classfile.DefaultClassfile;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.VirtualThread;

public class BranchingTest {

	@Test
	public void testLookupSwitchZero() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  int a = 0;");
		src.append("  switch(a) {");
		src.append("  	case 0: return \"Zero\"; ");
		src.append("  	case 1: return \"One\";");
		src.append("  	case 2: return \"Two\";");
		src.append("  	default: return \"Foo\";");
		src.append("  }");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "Zero"), executeJavaCode(vm, src.toString()));
	}

	@Test
	public void testLookupSwitchOne() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  int a = 1;");
		src.append("  switch(a) {");
		src.append("  	case 0: return \"Zero\"; ");
		src.append("  	case 1: return \"One\";");
		src.append("  	case 2: return \"Two\";");
		src.append("  	default: return \"Foo\";");
		src.append("  }");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "One"), executeJavaCode(vm, src.toString()));
	}

	@Test
	public void testLookupSwitchDefault() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static String test() {");
		src.append("  int a = 3;");
		src.append("  switch(a) {");
		src.append("  	case 0: return \"Zero\"; ");
		src.append("  	case 1: return \"One\";");
		src.append("  	case 2: return \"Two\";");
		src.append("  	default: return \"Foo\";");
		src.append("  }");
		src.append("}");
		VirtualMachine vm = new VirtualMachine();
		assertEquals(new JavaString(vm, "Foo"), executeJavaCode(vm, src.toString()));
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
		// vm.initSunJDK();

		FrameExit result = vm.execute("org/example/Test", "test", new JavaObject[0]);
		return result.getResult();
	}

	/**
	 * <pre>
	 * // Compiled from Branching.java (version 1.6 : 50.0, super bit)
	 * public class com.smartwerkz.bytecode.Branching {
	 *   
	 *   // Method descriptor #6 ()V
	 *   // Stack: 1, Locals: 1
	 *   public Branching();
	 *     0  aload_0 [this]
	 *     1  invokespecial java.lang.Object() [8]
	 *     4  return
	 *       Line numbers:
	 *         [pc: 0, line: 3]
	 *       Local variable table:
	 *         [pc: 0, pc: 5] local: this index: 0 type: com.smartwerkz.bytecode.Branching
	 *   
	 *   // Method descriptor #15 ([Ljava/lang/String;)V
	 *   // Stack: 1, Locals: 1
	 *   public static void main(java.lang.String[] args);
	 *     0  invokestatic com.smartwerkz.bytecode.Branching.doit() : int [16]
	 *     3  pop
	 *     4  return
	 *       Line numbers:
	 *         [pc: 0, line: 5]
	 *         [pc: 4, line: 6]
	 *       Local variable table:
	 *         [pc: 0, pc: 5] local: args index: 0 type: java.lang.String[]
	 *   
	 *   // Method descriptor #19 ()I
	 *   // Stack: 2, Locals: 2
	 *   private static int doit();
	 *      0  bipush 10
	 *      2  istore_0 [a]
	 *      3  bipush 15
	 *      5  istore_1 [b]
	 *      6  iload_0 [a]
	 *      7  iload_1 [b]
	 *      8  if_icmple 14
	 *     11  bipush 20
	 *     13  ireturn
	 *     14  bipush 25
	 *     16  ireturn
	 *       Line numbers:
	 *         [pc: 0, line: 9]
	 *         [pc: 3, line: 10]
	 *         [pc: 6, line: 11]
	 *         [pc: 11, line: 12]
	 *         [pc: 14, line: 14]
	 *       Local variable table:
	 *         [pc: 3, pc: 17] local: a index: 0 type: int
	 *         [pc: 6, pc: 17] local: b index: 1 type: int
	 *       Stack map table: number of frames 1
	 *         [pc: 14, append: {int, int}]
	 * }
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBranching() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		DefaultClassfile cf = new DefaultClassfile("Branching.class.binary");
		RuntimeDataArea rda = new RuntimeDataArea(vm);
		RuntimeConstantPool rcp = new RuntimeConstantPool(vm, cf, cf.getConstantPool());
		VirtualThread thread = rda.getThreads().getMainThread();
		rda.loadClass(thread, "com/smartwerkz/bytecode/Branching");
		Frame frame = new Frame(vm, thread, rda, rcp, cf.getMethod(0));
		ExecutionEngine interpreter = new ExecutionEngine(vm, rda, frame, cf.getMethod(1));
		assertEquals("invokestatic	doit", interpreter.interpret());
		assertEquals("pop", interpreter.interpret());
		assertEquals("return", interpreter.interpret());
		assertEquals(null, interpreter.interpret());
	}

	@Test
	public void testBranching3() throws Exception {
		VirtualMachine vm = new VirtualMachine();
		DefaultClassfile cf = new DefaultClassfile("Branching3.class.binary");
		RuntimeDataArea rda = new RuntimeDataArea(vm);
		RuntimeConstantPool rcp = new RuntimeConstantPool(vm, cf, cf.getConstantPool());
		VirtualThread thread = rda.getThreads().getMainThread();
		rda.loadClass(thread, "com/smartwerkz/bytecode/Branching3");
		Frame frame = new Frame(vm, thread, rda, rcp, cf.getMethod(0));
		ExecutionEngine interpreter = new ExecutionEngine(vm, rda, frame, cf.getMethod(1));
		assertEquals("invokestatic	doit", interpreter.interpret());
		assertEquals("pop", interpreter.interpret());
		assertEquals("return", interpreter.interpret());
		assertEquals(null, interpreter.interpret());
	}

	@Test
	public void testTableSwitch() throws Exception {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "org/example/Foo", null, "java/lang/Object", null);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V",
				null, null);
		mv.visitMaxs(2, 2);
		mv.visitCode();
		Label labelA = new Label();
		Label labelB = new Label();
		Label labelC = new Label();
		Label labelDefault = new Label();
		Label[] labels = new Label[4];
		labels[0] = labelA;
		labels[1] = labelB;
		labels[2] = labelC;
		labels[3] = labelDefault;
		mv.visitInsn(Opcode.ICONST_1);
		mv.visitTableSwitchInsn(0, 3, labelDefault, labels);
		mv.visitLabel(labelA);
		mv.visitInsn(Opcode.ICONST_1);
		mv.visitInsn(Opcode.IRETURN);
		mv.visitLabel(labelB);
		mv.visitInsn(Opcode.ICONST_2);
		mv.visitInsn(Opcode.IRETURN);
		mv.visitLabel(labelC);
		mv.visitInsn(Opcode.ICONST_3);
		mv.visitInsn(Opcode.IRETURN);
		mv.visitLabel(labelDefault);
		mv.visitInsn(Opcode.ICONST_4);
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

		vm.getBootstrapClassloader().load("org/example/Foo");
		FrameExit execute = vm.execute("org/example/Foo");
		assertTrue(execute.hasReturnValue());
		assertEquals("2", execute.getResult().asStringValue());
		assertEquals("", vm.getScreen().asString());

	}

}
