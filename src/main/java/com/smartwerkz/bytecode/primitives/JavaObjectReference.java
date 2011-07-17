package com.smartwerkz.bytecode.primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.SymbolicFieldReference;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.VirtualThread;

/**
 * Represents a java.lang.Object object in the virtual virtual machine.
 * 
 * <p>
 * This implements the magic behind foo.getClass()
 * 
 * @author mhaller
 */
public class JavaObjectReference implements JavaObject {

	private static final Logger log = LoggerFactory.getLogger(JavaObjectReference.class);

	private static final AtomicLong counter = new AtomicLong(0);

	private final Map<String, JavaObject> fieldValues = new HashMap<String, JavaObject>();

	private final Classfile classfile;

	private long instanceId;

	public JavaObjectReference(Classfile classfile) {
		this(classfile, true);
	}

	public JavaObjectReference(Classfile classfile, boolean validate) {
		if (validate) {
			if (classfile == null) {
				throw new IllegalArgumentException("Cannot create object from NULL class");
			}
			if (classfile.getAccessFlags().isAbstract()) {
				throw new IllegalStateException("You cannot create an instance of an abstract class: " + classfile);
			}
		}

		this.classfile = classfile;
		this.instanceId = counter.incrementAndGet();
	}

	@Override
	public JavaObject copy() {
		JavaObjectReference copy = new JavaObjectReference(classfile, false);
		Set<Entry<String, JavaObject>> entrySet = fieldValues.entrySet();
		Map<JavaObject, JavaObject> visited = new HashMap<JavaObject, JavaObject>();
		for (Entry<String, JavaObject> entry : entrySet) {
			JavaObject fieldValue = entry.getValue();
			if (visited.containsKey(fieldValue)) {
				JavaObject fieldCopy = fieldValue.copy();
				visited.put(fieldValue, fieldCopy);
			}
			JavaObject fieldCopy = visited.get(fieldValue);
			copy.fieldValues.put(entry.getKey(), fieldCopy);
		}
		return copy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (instanceId ^ (instanceId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaObjectReference other = (JavaObjectReference) obj;
		if (instanceId != other.instanceId)
			return false;
		return true;
	}

	public void setValueOfField(SymbolicFieldReference fieldRef, JavaObject value) {
		fieldValues.put(fieldRef.getFieldName(), value);
	}

	public JavaObject getValueOfField(SymbolicFieldReference fieldRef) {
		return fieldValues.get(fieldRef.getFieldName());
	}

	public JavaObject getValueOfField(String fieldName) {
		return fieldValues.get(fieldName);
	}

	public void setValueOfField(String fieldName, JavaObject value) {
		fieldValues.put(fieldName, value);
	}

	public Classfile getClassFile() {
		return classfile;
	}

	public long getInstanceId() {
		return instanceId;
	}

	@Override
	public String toString() {
		if (classfile == null) {
			return String.format("%s[no class]", super.toString());
		}
		return String.format("%s[class=%s]", super.toString(), classfile.getThisClassName());
	}

	@Override
	public String asStringValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("Dump of Object:\n");
		sb.append(String.format(" Object:\t%d\n", instanceId));
		sb.append(String.format(" Class:\t%s\n", classfile.getThisClassName()));
		if (classfile.getThisClassName().equals("java/lang/String")) {
			JavaArray javaObject = (JavaArray) fieldValues.get("value");
			StringBuilder strValue = new StringBuilder();
			for (int i = 0; i < javaObject.length(); i++) {
				JavaInteger c = (JavaInteger) javaObject.get(i);
				strValue.append((char) c.intValue());
			}
			sb.append(String.format(" String:\t%s\n", strValue.toString()));
		}
		for (Entry<String, JavaObject> entry : fieldValues.entrySet()) {
			sb.append(String.format(" Field '%s':\t%s\n", entry.getKey(), entry.getValue()));
		}
		return sb.toString();
	}

	public JavaObject invoke(VirtualMachine vm, String methodName, JavaObject... parameters) {
		log.debug("Reflectively invoking method '{}' on object '{}'", methodName, this);
		RuntimeDataArea rda = vm.getRuntimeData();
		VirtualThread mainThread = vm.getRuntimeData().getThreads().getMainThread();

		Methods methods2 = classfile.getMethods(vm);
		MethodInfo invokeMethodInfo = methods2.findMethodInfo(vm, methodName);
		Classfile effectiveClassName = classfile;
		String effectiveClass = classfile.getThisClassName();
		while (invokeMethodInfo == null || invokeMethodInfo.getCode() == null) {
			if (effectiveClassName.getSuperClassName() == null) {
				break;
			}
			effectiveClass = effectiveClassName.getSuperClassName();
			effectiveClassName = rda.loadClass(mainThread, effectiveClass);
			invokeMethodInfo = effectiveClassName.getMethods(vm).findMethodInfo(vm, methodName);
		}

		if (invokeMethodInfo == null) {
			log.error("Unable to find method '{}' on object '{}'", methodName, this);
		}
		
		if (vm.hasReplacement(invokeMethodInfo)) {
			return vm.executeReplacement(rda, mainThread.getStack().getCurrentFrame(), invokeMethodInfo).getResult();
		} else {
			ClassArea classArea = rda.getMethodArea().getClassArea(effectiveClassName);
			RuntimeConstantPool rcp = classArea.getRuntimeConstantPool();
			Frame newFrame = new Frame(vm, mainThread, rda, rcp, invokeMethodInfo);
			LocalVariables localVariables = newFrame.getLocalVariables();
			localVariables.setLocalVariable(0, this);
			for (int i = 1; i <= parameters.length; i++) {
				localVariables.setLocalVariable(i, parameters[i - 1]);
			}
			FrameExit frameResult = newFrame.execute();
			return frameResult.getResult();
		}
	}

}
