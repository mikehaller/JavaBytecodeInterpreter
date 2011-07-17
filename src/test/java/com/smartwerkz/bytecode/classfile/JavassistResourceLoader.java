package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import com.smartwerkz.bytecode.vm.ResourceLoader;

public class JavassistResourceLoader implements ResourceLoader {

	private final ClassPool javassistClassPool;

	public JavassistResourceLoader(ClassPool javassistClassPool) {
		this.javassistClassPool = javassistClassPool;
	}

	@Override
	public InputStream open(String resourceName) {
		String withDots = resourceName.replaceAll(Pattern.quote("/"), ".");
		String classname = withDots.replaceAll(Pattern.quote(".class"), "");
		try {
			CtClass ctClass = javassistClassPool.get(classname);
			return new ByteArrayInputStream(ctClass.toBytecode());
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
		return null;
	}

}
