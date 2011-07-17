package com.smartwerkz.bytecode.vm;

import java.io.IOException;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.vm.memory.Heap;

public class RuntimeDataArea {

	private final VMLog log = new VMLog(RuntimeDataArea.class.getName(), false);

	private final MethodArea methodArea;
	private final Heap heap = new Heap();
	private final BootstrapClassloader bcl;
	private final VirtualMachine vm;
	private Threads threads;

	public RuntimeDataArea(VirtualMachine vm) {
		if (vm == null)
			throw new IllegalArgumentException("The VirtualMachine must not be null");
		this.vm = vm;
		this.bcl = vm.getBootstrapClassloader();
		this.methodArea = new MethodArea(vm);
		this.threads = new Threads(vm, this, bcl, methodArea, heap);
	}

	public Threads getThreads() {
		return threads;
	}
	
	public VirtualMachine vm() {
		return vm;
	}

	public Heap getHeap() {
		return heap;
	}

	public MethodArea getMethodArea() {
		return methodArea;
	}

	public BootstrapClassloader getBootstrapClassLoader() {
		return bcl;
	}

	public Classfile loadClass(VirtualThread thread, String mainClassName) {
		try {
			Classfile loaded = bcl.load(mainClassName);
			if (methodArea.contains(loaded))
				return loaded;

			log.debug("Loading class '%s': ", mainClassName, loaded);
			String superClassName = loaded.getSuperClassName();
			if (superClassName != null && !methodArea.hasClass(superClassName)) {
				log.debug("Loading superclass '%s' of class '%s'", superClassName, mainClassName);
				loadClass(thread, superClassName);
			}

			methodArea.add(loaded, bcl);

			if (loaded.getThisClassName().equals("java/lang/System")) {
				MethodInfo methodInfo = loaded.getMethods(vm).findMethodInfo(vm, "initializeSystemClass");
				thread.execute(loaded, methodInfo, new JavaObject[0]);
			}

			link(thread, loaded);
			initialize(thread, loaded);
			return loaded;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void link(VirtualThread thread, Classfile cf) throws IOException {
		log.debug("Linking class: %s", cf.getThisClassName());
		verification();
		preparation(thread, cf);
	}

	private void verification() {
	}

	private void preparation(VirtualThread thread, Classfile cf) throws IOException {
		log.debug("Preparing class: %s", cf.getThisClassName());
		createAndInitializeStaticFieldDefaultValues(thread, cf);
	}

	private void createAndInitializeStaticFieldDefaultValues(VirtualThread thread, Classfile cf) throws IOException {
		cf.getFields().initialize(vm, thread, this);
		cf.getMethods(vm).initialize(vm, thread, this);
	}

	private void initialize(VirtualThread thread, Classfile cf) throws IOException {
		log.debug("TODO Initializing class: %s", cf.getThisClassName());
	}

}
