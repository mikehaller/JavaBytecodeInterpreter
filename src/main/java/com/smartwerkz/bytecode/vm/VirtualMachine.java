package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.CodeDumper;
import com.smartwerkz.bytecode.ExceptionFactory;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.controlflow.ControlFlowException;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.instructions.Processor;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.methods.AccessControllerDoPrivileged;
import com.smartwerkz.bytecode.vm.methods.ArrayNewArrayMehod;
import com.smartwerkz.bytecode.vm.methods.ClassClone;
import com.smartwerkz.bytecode.vm.methods.ClassDesiredAssertionStatusMethod;
import com.smartwerkz.bytecode.vm.methods.ClassForName0Method;
import com.smartwerkz.bytecode.vm.methods.ClassGetComponentType;
import com.smartwerkz.bytecode.vm.methods.ClassGetDeclaredConstructors;
import com.smartwerkz.bytecode.vm.methods.ClassGetDeclaredFieldsMethod;
import com.smartwerkz.bytecode.vm.methods.ClassGetDeclaredMethodsMethod;
import com.smartwerkz.bytecode.vm.methods.ClassGetModifiers;
import com.smartwerkz.bytecode.vm.methods.ClassGetName0;
import com.smartwerkz.bytecode.vm.methods.ClassGetPrimitiveClassMethod;
import com.smartwerkz.bytecode.vm.methods.ClassGetRawAnnotations;
import com.smartwerkz.bytecode.vm.methods.ClassGetSunConstantPool;
import com.smartwerkz.bytecode.vm.methods.ClassGetSuperclass;
import com.smartwerkz.bytecode.vm.methods.ClassIsArray;
import com.smartwerkz.bytecode.vm.methods.ClassIsInterface;
import com.smartwerkz.bytecode.vm.methods.ClassIsPrimitive;
import com.smartwerkz.bytecode.vm.methods.ClassLoaderFindBootstrapClassMethod;
import com.smartwerkz.bytecode.vm.methods.ClassLoaderFindLoadedClass0Method;
import com.smartwerkz.bytecode.vm.methods.DoubleToRawLongBitsMethod;
import com.smartwerkz.bytecode.vm.methods.FileDescriptorSetMethod;
import com.smartwerkz.bytecode.vm.methods.FileOutputStreamOpenAppendMethod;
import com.smartwerkz.bytecode.vm.methods.FileOutputStreamOpenMethod;
import com.smartwerkz.bytecode.vm.methods.FileOutputStreamWriteBytesMethod;
import com.smartwerkz.bytecode.vm.methods.FillInStackTraceMethod;
import com.smartwerkz.bytecode.vm.methods.FloatToRawIntBitsMethod;
import com.smartwerkz.bytecode.vm.methods.GenericReturnMethod;
import com.smartwerkz.bytecode.vm.methods.GetCallerClassMethod;
import com.smartwerkz.bytecode.vm.methods.GetClassLoaderMethod;
import com.smartwerkz.bytecode.vm.methods.GetFileSystemMethod;
import com.smartwerkz.bytecode.vm.methods.GetStackAccessControlContextMethod;
import com.smartwerkz.bytecode.vm.methods.NOPNativeMethod;
import com.smartwerkz.bytecode.vm.methods.NativeConstructorAccessorImplNewInstance;
import com.smartwerkz.bytecode.vm.methods.NativeMethod;
import com.smartwerkz.bytecode.vm.methods.ObjectGetClassMethod;
import com.smartwerkz.bytecode.vm.methods.ObjectHashMethod;
import com.smartwerkz.bytecode.vm.methods.ReflectionGetClassAccessFlagsMethod;
import com.smartwerkz.bytecode.vm.methods.ResourceBundleGetClassContext;
import com.smartwerkz.bytecode.vm.methods.SignalFindSignal;
import com.smartwerkz.bytecode.vm.methods.SignalHandle0;
import com.smartwerkz.bytecode.vm.methods.SignalRaise0;
import com.smartwerkz.bytecode.vm.methods.StringInternMethod;
import com.smartwerkz.bytecode.vm.methods.SunDownloadManagerGetDebugKey;
import com.smartwerkz.bytecode.vm.methods.SystemArraycopyMethod;
import com.smartwerkz.bytecode.vm.methods.SystemCurrentTimeMillisMethod;
import com.smartwerkz.bytecode.vm.methods.SystemInitPropertiesMethod;
import com.smartwerkz.bytecode.vm.methods.SystemSetErr0;
import com.smartwerkz.bytecode.vm.methods.SystemSetIn0;
import com.smartwerkz.bytecode.vm.methods.SystemSetOut0;
import com.smartwerkz.bytecode.vm.methods.ThreadCurrentThreadMethod;
import com.smartwerkz.bytecode.vm.methods.ThreadInterruptMethod;
import com.smartwerkz.bytecode.vm.methods.ThreadIsAliveMethod;
import com.smartwerkz.bytecode.vm.methods.ThreadIsInterruptedMethod;
import com.smartwerkz.bytecode.vm.methods.ThreadStartMethod;
import com.smartwerkz.bytecode.vm.methods.UnsafeAllocateMemory;
import com.smartwerkz.bytecode.vm.methods.UnsafeCompareAndSwapInt;
import com.smartwerkz.bytecode.vm.methods.UnsafeFreeMemory;
import com.smartwerkz.bytecode.vm.methods.UnsafeGetByte;
import com.smartwerkz.bytecode.vm.methods.UnsafeObjectFieldOffsetMethod;
import com.smartwerkz.bytecode.vm.methods.UnsafePutLong;
import com.smartwerkz.bytecode.vm.methods.UnsafePutObject;
import com.smartwerkz.bytecode.vm.os.FileDescriptors;
import com.smartwerkz.bytecode.vm.os.FileSystem;

public class VirtualMachine {

	private final VMLog log = new VMLog(VirtualMachine.class.getName());

	// Hardware stuff
	private final Screen screen = new Screen();
	private final FileSystem fileSystem = new FileSystem();
	private final FileDescriptors fileDescriptors = new FileDescriptors(fileSystem);
	private final Unsafe unsafe = new Unsafe();

	// VM stuff
	private final Classes classes = new Classes(this);
	private final Objects objects = new Objects(classes, this);
	private final BootstrapClassloader bcl = new BootstrapClassloader(this);
	private final RuntimeDataArea runtimeData = new RuntimeDataArea(this);
	private final StringPool stringPool = new StringPool(this);
	private final CodeDumper codeDumper = new CodeDumper();
	private final Processor processor = new Processor();

	// Special Features of this VM
	private final Map<String, NativeMethod> natives = new HashMap<String, NativeMethod>();
	private final Map<String, NativeMethod> replacements = new HashMap<String, NativeMethod>();

	private final Profiling profiling = new Profiling();

	private boolean started;
	private VirtualThread mainThread;

	public VirtualMachine() {
		setupNatives();
		setupReplacements();
	}

	public Objects objects() {
		return objects;
	}

	public Classes classes() {
		return classes;
	}

	private void setupNatives() {
		natives.put("sun/misc/VM#initialize(()V)", new NOPNativeMethod());
		natives.put("sun/misc/Unsafe#objectFieldOffset((Ljava/lang/reflect/Field;)J)",
				new UnsafeObjectFieldOffsetMethod());
		natives.put("sun/misc/Unsafe#registerNatives(()V)", new NOPNativeMethod());
		natives.put("sun/misc/Unsafe#compareAndSwapInt((Ljava/lang/Object;JII)Z)", new UnsafeCompareAndSwapInt(unsafe));

		natives.put("sun/misc/Unsafe#allocateMemory((J)J)", new UnsafeAllocateMemory(unsafe));
		natives.put("sun/misc/Unsafe#freeMemory((J)V)", new UnsafeFreeMemory(unsafe));
		natives.put("sun/misc/Unsafe#putLong((JJ)V)", new UnsafePutLong(unsafe));
		natives.put("sun/misc/Unsafe#getByte((J)B)", new UnsafeGetByte(unsafe));
		natives.put("sun/misc/Unsafe#putObject((Ljava/lang/Object;JLjava/lang/Object;)V)", new UnsafePutObject(unsafe));

		natives.put("sun/io/Win32ErrorMode#setErrorMode((J)J)", new GenericReturnMethod(0L));
		natives.put("sun/misc/Signal#handle0((IJ)J)", new SignalHandle0());
		natives.put("sun/misc/Signal#raise0((I)V)", new SignalRaise0());
		natives.put("sun/misc/Signal#findSignal((Ljava/lang/String;)I)", new SignalFindSignal());

		natives.put("sun/reflect/Reflection#getCallerClass((I)Ljava/lang/Class;)", new GetCallerClassMethod());
		natives.put("sun/reflect/Reflection#getClassAccessFlags((Ljava/lang/Class;)I)",
				new ReflectionGetClassAccessFlagsMethod());
		natives.put(
				"sun/reflect/NativeConstructorAccessorImpl#newInstance0((Ljava/lang/reflect/Constructor;[Ljava/lang/Object;)Ljava/lang/Object;)",
				new NativeConstructorAccessorImplNewInstance());
		natives.put("sun/jkernel/DownloadManager#getDebugKey(()Z)",new SunDownloadManagerGetDebugKey());
		natives.put("sun/jkernel/DownloadManager#isWindowsVista(()Z)",new GenericReturnMethod(0));
		natives.put("sun/jkernel/DownloadManager#getCurrentProcessId(()I)",new GenericReturnMethod(0));
		// Fake DownloadManagers state to prevent Threads
		natives.put("sun/jkernel/DownloadManager#isJREComplete(()Z)",new GenericReturnMethod(1));

		natives.put("java/lang/Object#registerNatives(()V)", new NOPNativeMethod());
		natives.put("java/lang/Object#hashCode(()I)", new ObjectHashMethod());
		natives.put("java/lang/Object#getClass(()Ljava/lang/Class;)", new ObjectGetClassMethod());

		natives.put("java/io/WinNTFileSystem#initIDs(()V)", new NOPNativeMethod());
		natives.put("java/io/Win32FileSystem#initIDs(()V)", new NOPNativeMethod());
		natives.put("java/io/FileInputStream#initIDs(()V)", new NOPNativeMethod());
		natives.put("java/io/FileDescriptor#initIDs(()V)", new NOPNativeMethod());
		natives.put("java/io/FileOutputStream#initIDs(()V)", new NOPNativeMethod());
		natives.put("java/io/FileOutputStream#writeBytes(([BII)V)", new FileOutputStreamWriteBytesMethod());
		natives.put("java/io/FileOutputStream#open((Ljava/lang/String;)V)", new FileOutputStreamOpenMethod());
		natives.put("java/io/FileOutputStream#openAppend((Ljava/lang/String;)V)", new FileOutputStreamOpenAppendMethod());
		natives.put("java/io/FileOutputStream#close0(()V)", new NOPNativeMethod());
		natives.put("java/io/ObjectStreamClass#initNative(()V)", new NOPNativeMethod());
		natives.put("java/io/FileDescriptor#set((I)J)", new FileDescriptorSetMethod(fileDescriptors));
		natives.put("java/io/FileSystem#getFileSystem(()Ljava/io/FileSystem;)", new GetFileSystemMethod());
		natives.put("java/io/Console#istty(()Z)", new GenericReturnMethod(true));

		natives.put("java/lang/System#registerNatives(()V)", new NOPNativeMethod());
		natives.put("java/lang/System#arraycopy((Ljava/lang/Object;ILjava/lang/Object;II)V)",
				new SystemArraycopyMethod());
		natives.put("java/lang/System#currentTimeMillis(()J)", new SystemCurrentTimeMillisMethod());
		natives.put("java/lang/System#initProperties((Ljava/util/Properties;)Ljava/util/Properties;)",
				new SystemInitPropertiesMethod());
		natives.put("java/lang/System#setIn0((Ljava/io/InputStream;)V)", new SystemSetIn0());
		natives.put("java/lang/System#setOut0((Ljava/io/PrintStream;)V)", new SystemSetOut0());
		natives.put("java/lang/System#setErr0((Ljava/io/PrintStream;)V)", new SystemSetErr0());

		// TODO: This MUST halt the current VM...
		natives.put("java/lang/Shutdown#halt0((I)V)", new NOPNativeMethod());
		natives.put("java/lang/Shutdown#runAllFinalizers(()V)", new NOPNativeMethod());

		natives.put("java/lang/Runtime#freeMemory(()L)", new GenericReturnMethod(Long.MAX_VALUE));
		natives.put("java/lang/Runtime#maxMemory(()L)", new GenericReturnMethod(Long.MAX_VALUE));
		natives.put("java/lang/Runtime#totalMemory(()L)", new GenericReturnMethod(Long.MAX_VALUE));
		natives.put("java/lang/Runtime#availableProcessors(()I)", new GenericReturnMethod(1));
		natives.put("java/lang/Runtime#gc(()V)", new NOPNativeMethod());
		natives.put("java/lang/Runtime#runFinalization0(()V)", new NOPNativeMethod());

		natives.put("java/lang/Thread#registerNatives(()V)", new NOPNativeMethod());
		natives.put("java/lang/Thread#isAlive(()Z)", new ThreadIsAliveMethod());
		natives.put("java/lang/Thread#isInterrupted((Z)Z)", new ThreadIsInterruptedMethod());
		natives.put("java/lang/Thread#interrupt0(()V)", new ThreadInterruptMethod());
		natives.put("java/lang/Thread#start0(()V)", new ThreadStartMethod());
		natives.put("java/lang/Thread#currentThread(()Ljava/lang/Thread;)", new ThreadCurrentThreadMethod());
		natives.put("java/lang/Thread#setPriority0((I)V)", new NOPNativeMethod());

		natives.put("java/lang/String#intern(()Ljava/lang/String;)", new StringInternMethod(stringPool));

		natives.put("java/lang/Class#getDeclaredFields0((Z)[Ljava/lang/reflect/Field;)",
				new ClassGetDeclaredFieldsMethod(this));
		natives.put("java/lang/Class#getDeclaredMethods0((Z)[Ljava/lang/reflect/Method;)",
				new ClassGetDeclaredMethodsMethod(this));
		natives.put("java/lang/Class#getDeclaredConstructors0((Z)[Ljava/lang/reflect/Constructor;)",
				new ClassGetDeclaredConstructors());
		natives.put("java/lang/Class#getModifiers(()I)", new ClassGetModifiers());
		natives.put("java/lang/Class#getName0(()Ljava/lang/String;)", new ClassGetName0());
		natives.put("java/lang/Object#clone(()Ljava/lang/Object;)", new ClassClone());
		natives.put("java/lang/Class#isArray(()Z)", new ClassIsArray());
		natives.put("java/lang/Class#isPrimitive(()Z)", new ClassIsPrimitive());
		natives.put("java/lang/Class#isInterface(()Z)", new ClassIsInterface());
		natives.put("java/lang/Class#getComponentType(()Ljava/lang/Class;)", new ClassGetComponentType(this));
		natives.put("java/lang/Class#getSuperclass(()Ljava/lang/Class;)", new ClassGetSuperclass());
		natives.put("java/lang/Class#desiredAssertionStatus0((Ljava/lang/Class;)Z)",
				new ClassDesiredAssertionStatusMethod());
		natives.put("java/lang/Class#getClassLoader0(()Ljava/lang/ClassLoader;)", new GetClassLoaderMethod());
		natives.put("java/lang/Class#getPrimitiveClass((Ljava/lang/String;)Ljava/lang/Class;)",
				new ClassGetPrimitiveClassMethod(this));
		natives.put("java/lang/Class#forName0((Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;)",
				new ClassForName0Method());
		natives.put("java/lang/Class#registerNatives(()V)", new NOPNativeMethod());
		natives.put("java/lang/Class#getRawAnnotations(()[B)", new ClassGetRawAnnotations());
		natives.put("java/lang/Class#getConstantPool(()Lsun/reflect/ConstantPool;)", new ClassGetSunConstantPool());

		natives.put("java/lang/ClassLoader#registerNatives(()V)", new NOPNativeMethod());
		natives.put("java/lang/ClassLoader#findLoadedClass0((Ljava/lang/String;)Ljava/lang/Class;)", new ClassLoaderFindLoadedClass0Method());
		natives.put("java/lang/ClassLoader#findBootstrapClass((Ljava/lang/String;)Ljava/lang/Class;)", new ClassLoaderFindBootstrapClassMethod());

		natives.put("java/lang/Float#floatToRawIntBits((F)I)", new FloatToRawIntBitsMethod());
		natives.put("java/lang/Double#doubleToRawLongBits((D)J)", new DoubleToRawLongBitsMethod());
		natives.put("java/lang/Throwable#fillInStackTrace(()Ljava/lang/Throwable;)", new FillInStackTraceMethod());

		natives.put("java/util/ResourceBundle#getClassContext(()[Ljava/lang/Class;)", new ResourceBundleGetClassContext());
		
		natives.put(
				"java/security/AccessController#getStackAccessControlContext(()Ljava/security/AccessControlContext;)",
				new GetStackAccessControlContextMethod());
		natives.put(
				"java/security/AccessController#doPrivileged((Ljava/security/PrivilegedAction;)Ljava/lang/Object;)",
				new AccessControllerDoPrivileged());
		// TODO: Müsste eigentlich eine eigene Method-Impl sein für
		// doPrivileged(PrivilegedExceptionAction)
		natives.put(
				"java/security/AccessController#doPrivileged((Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;)",
				new AccessControllerDoPrivileged());
		natives.put(
				"java/security/AccessController#doPrivileged((Ljava/security/PrivilegedAction;Ljava/security/AccessControlContext;)Ljava/lang/Object;)",
				new AccessControllerDoPrivileged());
		natives.put(
				"java/security/AccessController#doPrivileged((Ljava/security/PrivilegedExceptionAction;Ljava/security/AccessControlContext;)Ljava/lang/Object;)",
				new AccessControllerDoPrivileged());
		
		natives.put("java/lang/reflect/Array#newArray((Ljava/lang/Class;I)Ljava/lang/Object;)",
				new ArrayNewArrayMehod());
	}

	private void setupReplacements() {
		replacements.put("java/lang/System#loadLibrary((Ljava/lang/String;)V)", new NOPNativeMethod());
	}

	public RuntimeDataArea getRuntimeData() {
		return runtimeData;
	}

	public void start() {
		started = true;
		mainThread = runtimeData.getThreads().getMainThread();
		// initSunJDK();
	}

	public void stop() {
		started = false;
		runtimeData.getThreads().shutdownThreadsAndWait();
	}

	public boolean isStarted() {
		return started;
	}

	public void initSunJDK() {
		MethodInfo initMethod = classes().systemClass().getMethods(this).findMethodInfo(this, "initializeSystemClass");
		mainThread.execute(classes().systemClass(), initMethod, new JavaObject[0]);
	}

	public FrameExit execute(String mainClassName) {
		return execute(mainClassName, "main", new JavaObject[0]);
	}

	/**
	 * 
	 * @param mainClassName
	 *            the Class to invoke
	 * @param mainMethodName
	 *            the method to invoke (this is normally "main", but the VM can
	 *            also call other static methods)
	 * @param parameters
	 *            the method parameters)
	 * @return the return value, if any
	 */
	public FrameExit execute(String mainClassName, String mainMethodName, JavaObject[] parameters) {
		if (!started) {
			throw new IllegalStateException("VM is not started. Call init() methods and then start()");
		}

		FrameExit result;
		try {
			Classfile cf = runtimeData.loadClass(mainThread, mainClassName);
			MethodInfo methodInfo = cf.getMethods(this).findMethodInfo(this, mainMethodName);
			if (methodInfo == null) {
				throw new NoSuchMethodError(mainClassName + "#" + mainMethodName);
			}
			result = mainThread.execute(cf, methodInfo, parameters);

			// Intern Strings before the exit the VM
			if (result.getResult() instanceof JavaObjectReference) {
				JavaObjectReference javaObjectReference = (JavaObjectReference) result.getResult();
				if (javaObjectReference.getClassFile().equals(classes().stringClass())) {
					JavaString stringResult = getStringPool().intern(javaObjectReference);
					result = new FrameExit(stringResult);
				}
			}

		} catch (ControlFlowException cfe) {
			JavaObjectReference javaException = cfe.getJavaException();
			screen.print(ExceptionFactory.buildMessage(this, javaException));
			result = FrameExit.createFromException(javaException);
		} finally {
			// TODO: printHistogram();
		}

		return result;
	}

	/**
	 * Prints a histogram of all executes opcodes
	 */
	private void printHistogram() {
		log.debug("Histogram: %s", profiling.printOpcodesHistogram());
	}

	public Profiling getProfiling() {
		return profiling;
	}

	/**
	 * Returns the screen-content as String, e.g. what has been written to
	 * "System.out".
	 * 
	 * @return
	 */
	public Screen getScreen() {
		return screen;
	}

	public StringPool getStringPool() {
		return stringPool;
	}

	public boolean hasReplacement(MethodInfo methodInfo) {
		return replacements.containsKey(methodInfo.getFullName()) || natives.containsKey(methodInfo.getFullName());
	}

	public FrameExit executeReplacement(RuntimeDataArea rda, Frame currentFrame, MethodInfo replacedMethodInfo) {
		NativeMethod method = replacements.get(replacedMethodInfo.getFullName());
		if (method == null) {
			method = natives.get(replacedMethodInfo.getFullName());
		}
		// log.debug("Invoking replacement method: %s", method);
		OperandStack operandStack = currentFrame.getOperandStack();
		method.execute(rda, currentFrame, operandStack);
		if (operandStack.peekFirst() != null) {
			return new FrameExit(operandStack.pop());
		}
		return new FrameExit();
	}

	public BootstrapClassloader getBootstrapClassloader() {
		return bcl;
	}

	public void setClasspath(Classpath cp) {
		bcl.setClasspath(cp);
	}

	public CodeDumper getCodeDumper() {
		return codeDumper;
	}

	public Processor getProcessor() {
		return processor;
	}

}
