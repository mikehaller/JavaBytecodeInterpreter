package com.smartwerkz.bytecode;

import static org.junit.Assert.assertTrue;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class PatternTest {
	@Test
	public void testPatternCompile() throws Exception {
		StringBuilder src = new StringBuilder();
		src.append("public static Object test() {");
		src.append("  return java.util.regex.Pattern.compile(\"%(\\\\d+\\\\$)?([-#+ 0,(\\\\<]*)?(\\\\d+)?(\\\\.\\\\d+)?([tT])?([a-zA-Z%])\");");
		src.append("}");

		VirtualMachine vm = new VirtualMachine();
		JavaObject result = executeJavaCode(vm, src.toString());
		assertTrue(result.asStringValue().contains("java/util/regex/Pattern"));
		assertTrue(result.asStringValue().contains("localCount"));
		assertTrue(result.asStringValue().contains("patternLength"));
		assertTrue(result.asStringValue().contains("cursor"));
		assertTrue(result.asStringValue().contains("56"));
		assertTrue(result.asStringValue().contains("capturingGroupCount"));
		assertTrue(result.asStringValue().contains("7"));
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

}
