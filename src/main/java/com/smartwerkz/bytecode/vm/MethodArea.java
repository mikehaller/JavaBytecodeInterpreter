package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;

/**
 * Shared by all Threads!
 * 
 * @author mhaller
 */
public class MethodArea {

	private VMLog log = new VMLog(MethodArea.class.getName(), false);

	private Map<String, ClassArea> classAreas = new HashMap<String, ClassArea>();

	private final VirtualMachine vm;

	public MethodArea(VirtualMachine vm) {
		if (vm == null)
			throw new IllegalArgumentException("A MethodArea requires a VM");
		this.vm = vm;
	}

	public RuntimeConstantPool getRuntimeConstantPool(MethodInfo methodInfo) {
		if (!classAreas.containsKey(methodInfo.getClassName())) {
			throw new IllegalStateException("Unable to find ClassArea in MethodArea for class: "
					+ methodInfo.getClassName() + ". Got: " + classAreas);
		}
		return classAreas.get(methodInfo.getClassName()).getRuntimeConstantPool();
	}

	public ClassArea getClassArea(MethodInfo methodInfo) {
		return classAreas.get(methodInfo.getClassName());
	}

	public ClassArea getClassArea(String classname) {
		if (!classAreas.containsKey(classname)) {
			BootstrapClassloader bcl = vm.getRuntimeData().getBootstrapClassLoader();
			Classfile cf = bcl.load(classname);
			add(cf,bcl);
		}
		return classAreas.get(classname);
	}
	
	public ClassArea getClassArea(Classfile parentClass) {
		return classAreas.get(parentClass.getThisClassName());
	}

	public void add(Classfile cf, BootstrapClassloader bcl)  {
		log.debug("Creating ClassArea for class '%s'", cf.getThisClassName());
		classAreas.put(cf.getThisClassName(), new ClassArea(vm, cf, bcl));
	}

	public boolean hasClass(String className) {
		return classAreas.containsKey(className);
	}

	public boolean contains(Classfile loaded) {
		return classAreas.containsKey(loaded.getThisClassName());
	}

	public MethodInfo getMethodInfo(VirtualMachine vm, SymbolicMethodReference staticMethodRef) {
		ClassArea classArea = classAreas.get(staticMethodRef.getClassName());
		if (classArea == null)
			return null;
		Classfile classfile = classArea.getClassfile();
		Methods methods = classfile.getMethods(vm);
		return methods.getMethodInfo(staticMethodRef);
	}


}
