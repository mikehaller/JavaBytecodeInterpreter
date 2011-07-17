package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.MethodArea;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.SymbolicMethodReference;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.VirtualThread;

public class Methods implements Iterable<MethodInfo> {

	private final VMLog log = new VMLog(Methods.class.getName(),false);

	private final List<MethodInfo> methods = new ArrayList<MethodInfo>();

	private final String className;

	public Methods(String className, ConstantPool constantPool, DataInputStream dis) {
		try {
			this.className = className;
			int methodsCount = dis.readUnsignedShort();
			log.debug("Reading %d methods of class '%s'", methodsCount, className);
			for (int i = 0; i < methodsCount; i++) {
				MethodInfo methodInfo = new MethodInfo(className, constantPool, dis);
				methods.add(methodInfo);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MethodInfo info : methods) {
			if (sb.length() != 0)
				sb.append(",");
			sb.append(info.getMethodName());
		}
		return sb.toString();
	}

	public int size() {
		return methods.size();
	}

	public MethodInfo getMethodInfo(int index) {
		return methods.get(index);
	}

	// public MethodInfo getMethodInfo(String methodName, String descriptor) {
	// for (MethodInfo otherMethod : methods) {
	// // TODO: Signature?!
	// if (methodName.equals(otherMethod.getMethodName()) &&
	// descriptor.equals(otherMethod.getDescriptorName())) {
	// return otherMethod;
	// }
	// }
	// throw new
	// NoSuchMethodError(String.format("Unable to find method [%s] in class [%s]",
	// methodName, className));
	// }

	public void initialize(VirtualMachine vm, VirtualThread thread, RuntimeDataArea runtimeDataArea) throws IOException {
		for (MethodInfo otherMethod : methods) {
			if (otherMethod.isStaticInitializer()) {
				String methodName = otherMethod.getMethodName();
				log.debug("Executing static initializer: %s", methodName);
				MethodArea methodArea = runtimeDataArea.getMethodArea();
				ClassArea classArea = methodArea.getClassArea(otherMethod);
				RuntimeConstantPool rcp = classArea.getRuntimeConstantPool();
				Frame frame = new Frame(vm, thread, runtimeDataArea, rcp, otherMethod);
				frame.execute();
			}
		}

	}

	public MethodInfo getMethodInfo(SymbolicMethodReference staticMethodRef) {
		String methodName = staticMethodRef.getMethodName();
		for (MethodInfo otherMethod : methods) {
			// TODO: Signature?! Best-matching strategy?
			if (methodName.equals(otherMethod.getMethodName())
					&& staticMethodRef.getMethodType().equals(otherMethod.getDescriptorName())) {
				return otherMethod;
			}
		}
		return null;
	}

	public MethodInfo findMethodInfo(VirtualMachine vm, String methodName) {
		for (MethodInfo otherMethod : methods) {
			if (methodName.equals(otherMethod.getMethodName())) {
				return otherMethod;
			}
		}
		return null;
	}

	public List<MethodInfo> getAllMethods() {
		return methods;
	}

	@Override
	public Iterator<MethodInfo> iterator() {
		return methods.iterator();
	}

}
