package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Represents a java.lang.Class of an array such as "byte[].class"
 * 
 * @author mhaller
 */
public class PrimitiveClassfile implements Classfile {

	private final String clazzName;
	private JavaClassReference javaClassReference;

	public PrimitiveClassfile(String clazzName) {
		this.clazzName = clazzName;
		javaClassReference = new JavaClassReference(this);
	}

	@Override
	public JavaClassReference getAsJavaClassReference() {
		return javaClassReference;
	}

	@Override
	public String getThisClassName() {
		return this.clazzName;
	}

	@Override
	public Methods getMethods(VirtualMachine vm) {
		return new Methods(clazzName, getConstantPool(), createEmptyStream());
	}

	public JavaObject getDefaultValue() {
		return new JavaInteger(0);
	}

	@Override
	public ConstantPool getConstantPool() {
		return new EmptyConstantPool();
	}

	@Override
	public Fields getFields() {
		return new Fields(getConstantPool(), createEmptyStream());
	}

	private static DataInputStream createEmptyStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		try {
			dos.writeShort(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
	}

	@Override
	public String getSuperClassName() {
		return "java/lang/Object";
	}

	@Override
	public ClassAccessFlags getAccessFlags() {
		return new ClassAccessFlags(0);
	}

	@Override
	public boolean isInstanceOf(RuntimeDataArea rda, Frame frame, String className) {
		if (clazzName.equals(className)) {
			return true;
		}
		return false;
	}

	@Override
	public List<Classfile> getParentClasses(RuntimeDataArea rda, Frame frame) {
		return Collections.singletonList(rda.vm().classes().objectClass());
	}

	@Override
	public Attributes getAttributes() {
		return null;
	}

}
