package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.VirtualThread;

public class LazyClassfile implements Classfile {

	private final String thisClassName;
	private Classfile classfile;
	private JavaClassReference javaClassReference;

	public LazyClassfile(String thisClassName) {
		this.thisClassName = thisClassName;
		javaClassReference = new JavaClassReference(this);
	}

	@Override
	public String getThisClassName() {
		return thisClassName;
	}
	
	@Override
	public boolean equals(Object obj) {
		Classfile other = (Classfile) obj;
		// TODO: Also add the 'classloader' to the comparison
		return getThisClassName().equals(other.getThisClassName());
	}

	@Override
	public Methods getMethods(VirtualMachine vm) {
		lazyInit(vm);
		return classfile.getMethods(vm);
	}

	protected void lazyInit(VirtualMachine vm) {
		if (classfile == null) {
			classfile = loadclass(vm, thisClassName);
		}
	}
	
	private Classfile loadclass(VirtualMachine vm, String clazzName) {
		VirtualThread thread = vm.getRuntimeData().getThreads().getMainThread();
		RuntimeDataArea rda = new RuntimeDataArea(vm);
		return rda.loadClass(thread, clazzName);
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
	public JavaClassReference getAsJavaClassReference() {
		return javaClassReference;
	}

	@Override
	public boolean isInstanceOf(RuntimeDataArea rda, Frame frame, String className) {
		lazyInit(frame.getVirtualMachine());
		return classfile.isInstanceOf(rda, frame, className);
	}

	@Override
	public List<Classfile> getParentClasses(RuntimeDataArea rda, Frame frame) {
		if (classfile!=null) {
			return classfile.getParentClasses(rda, frame);
		}
		return Collections.emptyList();
	}

	@Override
	public Attributes getAttributes() {
		return classfile.getAttributes();
	}

}
