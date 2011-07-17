package com.smartwerkz.bytecode.analysis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.ExceptionFactory;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.combinatorics.Combination;
import com.smartwerkz.combinatorics.ExecutionContext;
import com.smartwerkz.combinatorics.ExecutionTemplate;
import com.smartwerkz.combinatorics.Generator;
import com.smartwerkz.combinatorics.IntRangeVariable;
import com.smartwerkz.combinatorics.JobContext;

public class ByteCodeAnalysisTest {

	@Test
	public void testByteCodeAnalysis() throws Exception {
		final StringBuilder src = new StringBuilder();
		src.append("public static boolean test(int x, int y) {");
		src.append("  if (x/y==5) return true;");
		src.append("  return false;");
		src.append("}");

		final Histogram histogram = new Histogram();

		final VirtualMachine vm = setupVM(src.toString());
		ExecutionTemplate template = new ExecutionTemplate() {
			@Override
			public void execute(JobContext jobContext, ExecutionContext executionContext) {
				try {
					Combination combination = executionContext.getCollectedValues();
					JavaInteger x = new JavaInteger(combination.asInt("x"));
					JavaInteger y = new JavaInteger(combination.asInt("y"));
					FrameExit exit = executeJavaCode(vm, src.toString(), x, y);
					String result = exit.getResult().asStringValue();
					if (exit.isException()) {
						result = ExceptionFactory.buildMessage(vm, (JavaObjectReference) exit.getResult());
					}
					histogram.count(result, "x=" + x.intValue() + "|y=" + y.intValue());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		Generator gen = new Generator(template);
		gen.add(new IntRangeVariable("x", -10, +10));
		gen.add(new IntRangeVariable("y", -10, +10));
//		gen.add(new RandomIntVariable("x", 10));
//		gen.add(new RandomIntVariable("y", 10));
		
		System.out.println("Estimation:" + gen.estimateExecutionTime());
		System.out.println("Iterations:" + gen.estimateIterations());
		gen.execute();
		System.out.println("Histogram:\n" + histogram.dump());
	}

	private FrameExit executeJavaCode(VirtualMachine vm, String methodBody, JavaObject... parameters) throws Exception {
		return vm.execute("org/example/Test", "test", parameters);
	}

	protected VirtualMachine setupVM(String methodBody) throws Exception {
		VirtualMachine vm = new VirtualMachine();
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
		return vm;
	}
}
