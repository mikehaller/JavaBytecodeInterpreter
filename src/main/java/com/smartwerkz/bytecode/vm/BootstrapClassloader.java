package com.smartwerkz.bytecode.vm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.DefaultClassfile;
import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.classfile.PrimitiveClassfile;
import com.smartwerkz.bytecode.controlflow.ControlFlowException;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;

public final class BootstrapClassloader {

	private static final Logger log = LoggerFactory.getLogger(BootstrapClassloader.class);

	private final Map<String, Classfile> classLoaderCache = new HashMap<String, Classfile>();
	private final JavaObjectReference javaObjectReference;
	private final VirtualMachine vm;

	private Classpath classpath = new Classpath();

	public BootstrapClassloader(VirtualMachine vm) {
		if (vm == null)
			throw new IllegalArgumentException();
		
		this.vm = vm;

		classpath.addLoader(new HostVMResourceLoader());

		// Singletons
		classLoaderCache.put("C", vm.classes().primitives().charClass());
		classLoaderCache.put("B", vm.classes().primitives().byteClass());
		classLoaderCache.put("I", vm.classes().primitives().intClass());
		classLoaderCache.put("J", vm.classes().primitives().longClass());
		classLoaderCache.put("Z", vm.classes().primitives().booleanClass());
		classLoaderCache.put("F", vm.classes().primitives().floatClass());
		classLoaderCache.put("D", vm.classes().primitives().doubleClass());
		classLoaderCache.put("V", vm.classes().primitives().voidClass());

		classLoaderCache.put("char", vm.classes().primitives().charClass());
		classLoaderCache.put("byte", vm.classes().primitives().byteClass());
		classLoaderCache.put("int", vm.classes().primitives().intClass());
		classLoaderCache.put("long", vm.classes().primitives().longClass());
		classLoaderCache.put("boolean", vm.classes().primitives().booleanClass());
		classLoaderCache.put("float", vm.classes().primitives().floatClass());
		classLoaderCache.put("double", vm.classes().primitives().doubleClass());
		classLoaderCache.put("void", vm.classes().primitives().voidClass());

		classLoaderCache.put("[C", vm.classes().arrays().charArrayClass());
		classLoaderCache.put("[B", vm.classes().arrays().byteArrayClass());
		classLoaderCache.put("[I", vm.classes().arrays().intArrayClass());
		classLoaderCache.put("[J", vm.classes().arrays().longArrayClass());
		classLoaderCache.put("[Z", vm.classes().arrays().booleanArrayClass());
		classLoaderCache.put("[F", vm.classes().arrays().floatArrayClass());
		classLoaderCache.put("[D", vm.classes().arrays().doubleArrayClass());

		LazyClassfile cf = new LazyClassfile("java/lang/ClassLoader");
		javaObjectReference = new JavaObjectReference(cf);
	}

	public JavaObject getAsJavaObjectReference() {
		return javaObjectReference;
	}

	// public Classfile load(byte[] classfileByteCode) {
	// return load(new ByteArrayInputStream(classfileByteCode));
	// }
	// public Classfile load(InputStream classFile) {
	// DefaultClassfile classfile = new DefaultClassfile(classFile);
	// if (cache.containsKey(classfile.getThisClassName())) {
	// return cache.get(classfile.getThisClassName());
	// }
	// cache.put(classfile.getThisClassName(), classfile);
	// return classfile;
	// }

	public Classfile load(String classname) {
		if (classLoaderCache.containsKey(classname))
			return classLoaderCache.get(classname);

		if (classname.contains(".")) {
			throw new IllegalArgumentException("Class name must be given in '/' notation");
		}

		// if (classname.equals("com/smartwerkz/bytecode/HelloWorld")) {
		// DefaultClassfile classfile = new
		// DefaultClassfile("HelloWorld.class.binary");
		// cache.put(classname, classfile);
		// return classfile;
		// }

		if (classname.startsWith("[")) {
			return new PrimitiveClassfile(classname);
		}

		for (ResourceLoader loader : classpath.loaders()) {
			String resourceName = classname + ".class";
			InputStream resource = loader.open(resourceName);
			if (resource != null) {
				DefaultClassfile classfile = new DefaultClassfile(resource);
				log.debug("Loading new class file '{}'", classfile.getThisClassName());
				classLoaderCache.put(classfile.getThisClassName(), classfile);
				return classfile;
			}
		}

		Classfile noClassDefFoundErrorClass = load("java/lang/NoClassDefFoundError");
		JavaObjectReference javaException = new JavaObjectReference(noClassDefFoundErrorClass);
		javaException.setValueOfField("detailMessage", new JavaString(vm, classname));
		throw new ControlFlowException(javaException);
	}

	public void setClasspath(Classpath classpath) {
		this.classpath = classpath;
	}

}
