package com.smartwerkz.bytecode.vm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.memory.Heap;

public class Threads {

	private final VMLog log = new VMLog(Threads.class.getName());

	private final MethodArea methodArea;
	private final Heap heap;
	private VirtualThread mainThread;
	private final BootstrapClassloader bcl;
	private final RuntimeDataArea rda;
	private final VirtualMachine vm;

	/** List of all Thread objects: created, started and stopped */
	private final List<VirtualThread> threadAreas = new ArrayList<VirtualThread>();
	/** Contains only started threads */
	private final HashSet<JavaObjectReference> aliveThreads = new HashSet<JavaObjectReference>();
	/** Contains the threads which have been 'interrupted' */
	private final HashSet<JavaObjectReference> interruptedThreads = new HashSet<JavaObjectReference>();

	public Threads(VirtualMachine vm, RuntimeDataArea rda, BootstrapClassloader bcl, MethodArea methodArea, Heap heap) {
		this.vm = vm;
		this.rda = rda;
		this.bcl = bcl;
		this.methodArea = methodArea;
		this.heap = heap;

		mainThread = createThread("main");
	}

	/**
	 * Returns the number of Threads.
	 * 
	 * Once the VM is started, this should be at least 1.
	 * 
	 * @return the number of Threads
	 */
	public int size() {
		return threadAreas.size();
	}

	private VirtualThread createThread(String threadName) {
		// TODO: Get the method-reference to the "main()" method
		// and pass it to main-VirtualThread
		JavaObjectReference threadObj = new JavaObjectReference(new LazyClassfile("java/lang/Thread"));
		
		// TODO: We should invoke constructors here...
		threadObj.setValueOfField("priority", new JavaInteger(1));
		threadObj.setValueOfField("name", JavaArray.str2char(vm,threadName));
		threadObj.setValueOfField("group", new JavaObjectReference(bcl.load("java/lang/ThreadGroup")));
		
		mainThread = new VirtualThread(vm, rda, threadName, threadObj);
		threadAreas.add(mainThread);
		return mainThread;
	}

	public void startThread(VirtualThread newThread) {
		// TODO: Spawn new OS Thread and associate with VirtualThread?
		log.debug("**** CREATING A THREAD? ****");
	}

	public void shutdownThreadsAndWait() {
		for (VirtualThread thread : threadAreas) {
			thread.shutdownAndWait();
		}
		threadAreas.clear();
	}

	public VirtualThread getMainThread() {
		return mainThread;
	}

	public boolean isAlive(JavaObjectReference threadReference) {
		return aliveThreads.contains(threadReference);
	}

	public void markStarted(JavaObjectReference threadReference) {
		aliveThreads.add(threadReference);
	}

	public void markStopped(JavaObjectReference threadReference) {
		aliveThreads.remove(threadReference);
	}

	public void interrupt(JavaObjectReference threadReference) {
		interruptedThreads.add(threadReference);
	}
	
	public boolean isInterrupted(JavaObjectReference threadReference) {
		return interruptedThreads.contains(threadReference);
	}

	public void clearInterruptedFlag(JavaObjectReference threadReference) {
		interruptedThreads.remove(threadReference);
	}

}
