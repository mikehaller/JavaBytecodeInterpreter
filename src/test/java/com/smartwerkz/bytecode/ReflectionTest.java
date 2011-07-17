package com.smartwerkz.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Modifier;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import org.junit.Test;

import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.ByteArrayResourceLoader;
import com.smartwerkz.bytecode.vm.Classpath;
import com.smartwerkz.bytecode.vm.HostVMResourceLoader;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ReflectionTest {

	@Test
	public void testReturnString() throws Exception {
		ClassPool cp = new ClassPool(true);
		CtClass cc = cp.makeClass("org.example.Test");
		StringBuilder src = new StringBuilder();
		src.append("public static boolean test() {");
		src.append("  java.lang.Class paramClass1 = java.lang.ClassLoader.getSystemClassLoader().loadClass(\"sun.nio.cs.FastCharsetProvider\");");
		src.append("  java.lang.Class paramClass2 = java.lang.ClassLoader.getSystemClassLoader().loadClass(\"sun.nio.cs.UTF_8\");");
		src.append("  int modifiers = paramClass2.getModifiers();");
		src.append("  return sun.reflect.Reflection.verifyMemberAccess(paramClass1, paramClass2, null, modifiers);");
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
		String asStringValue = result.getResult().asStringValue();
		assertEquals("true", asStringValue);
	}

	@Test
	public void testReflectionInOracleJVM() throws Exception {
		Class paramClass1 = java.lang.ClassLoader.getSystemClassLoader().loadClass("sun.nio.cs.FastCharsetProvider");
		Class paramClass2 = java.lang.ClassLoader.getSystemClassLoader().loadClass("sun.nio.cs.UTF_8");
		Object paramObject = null;
		int paramClass2Modifiers = paramClass2.getModifiers();
		boolean result = verifyMemberAccess(paramClass1, paramClass2, paramObject, paramClass2Modifiers);
		assertTrue(result);
	}

	private static int getClassAccessFlags(Class paramClass) {
		return paramClass.getModifiers();
	}

	public static boolean verifyMemberAccess(Class paramClass1, Class paramClass2, Object paramObject, int paramInt) {
		int i = 0;
		boolean bool = false;

		if (paramClass1 == paramClass2) {
			return true;
		}

		if (!(Modifier.isPublic(getClassAccessFlags(paramClass2)))) {
			bool = isSameClassPackage(paramClass1, paramClass2);
			i = 1;
			if (!(bool)) {
				return false;
			}

		}

		if (Modifier.isPublic(paramInt)) {
			return true;
		}

		int j = 0;

		if ((Modifier.isProtected(paramInt)) && (isSubclassOf(paramClass1, paramClass2))) {
			j = 1;
		}

		if ((j == 0) && (!(Modifier.isPrivate(paramInt)))) {
			if (i == 0) {
				bool = isSameClassPackage(paramClass1, paramClass2);

				i = 1;
			}

			if (bool) {
				j = 1;
			}
		}

		if (j == 0) {
			return false;
		}

		if (Modifier.isProtected(paramInt)) {
			Class localClass = (paramObject == null) ? paramClass2 : paramObject.getClass();
			if (localClass != paramClass1) {
				if (i == 0) {
					bool = isSameClassPackage(paramClass1, paramClass2);
					i = 1;
				}
				if ((!(bool)) && (!(isSubclassOf(localClass, paramClass1)))) {
					return false;
				}
			}

		}

		return true;
	}

	private static boolean isSameClassPackage(Class paramClass1, Class paramClass2) {
		return isSameClassPackage(paramClass1.getClassLoader(), paramClass1.getName(), paramClass2.getClassLoader(),
				paramClass2.getName());
	}

	static boolean isSubclassOf(Class paramClass1, Class paramClass2) {
		while (paramClass1 != null) {
			if (paramClass1 == paramClass2) {
				return true;
			}
			paramClass1 = paramClass1.getSuperclass();
		}
		return false;
	}

	private static boolean isSameClassPackage(ClassLoader paramClassLoader1, String paramString1,
			ClassLoader paramClassLoader2, String paramString2) {
		if (paramClassLoader1 != paramClassLoader2) {
			return false;
		}
		int i = paramString1.lastIndexOf(46);
		int j = paramString2.lastIndexOf(46);
		if ((i == -1) || (j == -1)) {
			return (i == j);
		}
		int k = 0;
		int l = 0;

		if (paramString1.charAt(k) == '[') {
			do
				++k;
			while (paramString1.charAt(k) == '[');
			if (paramString1.charAt(k) != 'L') {
				throw new InternalError("Illegal class name " + paramString1);
			}
		}
		if (paramString2.charAt(l) == '[') {
			do
				++l;
			while (paramString2.charAt(l) == '[');
			if (paramString2.charAt(l) != 'L') {
				throw new InternalError("Illegal class name " + paramString2);
			}

		}

		int i1 = i - k;
		int i2 = j - l;

		if (i1 != i2) {
			return false;
		}
		return paramString1.regionMatches(false, k, paramString2, l, i1);
	}

}
