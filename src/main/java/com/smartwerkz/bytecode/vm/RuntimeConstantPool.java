package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.ConstantClassInfo;
import com.smartwerkz.bytecode.classfile.ConstantDoubleInfo;
import com.smartwerkz.bytecode.classfile.ConstantFieldrefInfo;
import com.smartwerkz.bytecode.classfile.ConstantFloatInfo;
import com.smartwerkz.bytecode.classfile.ConstantIntegerInfo;
import com.smartwerkz.bytecode.classfile.ConstantLongInfo;
import com.smartwerkz.bytecode.classfile.ConstantNameAndTypeInfo;
import com.smartwerkz.bytecode.classfile.ConstantPool;
import com.smartwerkz.bytecode.classfile.ConstantStringInfo;
import com.smartwerkz.bytecode.classfile.ConstantUTF8Info;
import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaShort;
import com.smartwerkz.bytecode.primitives.JavaString;

public class RuntimeConstantPool {

	private final ConstantPool constantPool;
	private final Map<String, JavaObject> fieldValues = new HashMap<String, JavaObject>();
	private final Classfile classfile;
	private final BootstrapClassloader bcl;
	private final VirtualMachine vm;

	public RuntimeConstantPool(VirtualMachine vm, Classfile classfile, ConstantPool constantPool) {
		if (classfile == null)
			throw new IllegalArgumentException("A RuntimeConstantPool requires a Classfile");
		if (constantPool == null)
			throw new IllegalArgumentException("A RuntimeConstantPool requires a ConstantPool");
		if (vm == null)
			throw new IllegalArgumentException("A RuntimeConstantPool requires a VM");
		this.vm = vm;
		this.classfile = classfile;
		this.constantPool = constantPool;
		this.bcl = vm.getBootstrapClassloader();
	}

	public Classfile getClassfile() {
		return classfile;
	}

	/**
	 * A symbolic reference to a field of a class (§2.9) or an interface
	 * (§2.13.3.1) is derived from a CONSTANT_Fieldref_info structure (§4.4.2)
	 * in the binary representation of a class or interface. Such a reference
	 * gives the name and descriptor of the field, as well as a symbolic
	 * reference to the class or interface in which the field is to be found.
	 * 
	 * @param index
	 * @return a symbolic field reference
	 */
	public SymbolicFieldReference getSymbolicFieldReference(int index) {
		return new SymbolicFieldReference(index, constantPool);
	}

	public SymbolicMethodReference getSymbolicMethodReference(int index) {
		return new SymbolicMethodReference(index, constantPool);
	}

	public SymbolicClassReference getSymbolicClassReference(int index) {
		return new SymbolicClassReference(index, constantPool);
	}

	public ConstantPool getConstantPool() {
		return constantPool;
	}

	public JavaObject getValue(int index) {
		Object entry = constantPool.getEntry(index);
		if (entry instanceof ConstantStringInfo) {
			ConstantStringInfo constantStringInfo = (ConstantStringInfo) entry;
			ConstantUTF8Info utf8Info = constantPool.getUTF8Info(constantStringInfo.getStringIndex());
			String value = utf8Info.getValue();
			StringPool stringPool = vm.getStringPool();
			return stringPool.intern(new JavaString(vm, value));
		} else if (entry instanceof ConstantIntegerInfo) {
			ConstantIntegerInfo constantIntegerInfo = (ConstantIntegerInfo) entry;
			int value = constantIntegerInfo.getValue();
			return new JavaInteger(value);
		} else if (entry instanceof ConstantFloatInfo) {
			ConstantFloatInfo constant = (ConstantFloatInfo) entry;
			float value = constant.getValue();
			return new JavaFloat(value);
		} else if (entry instanceof ConstantLongInfo) {
			ConstantLongInfo constant = (ConstantLongInfo) entry;
			long value = constant.getValue();
			return new JavaLong(value);
		} else if (entry instanceof ConstantDoubleInfo) {
			ConstantDoubleInfo constant = (ConstantDoubleInfo) entry;
			double value = constant.getValue();
			return new JavaDouble(value);
		} else if (entry instanceof ConstantFieldrefInfo) {
			ConstantFieldrefInfo constant = (ConstantFieldrefInfo) entry;
			ConstantClassInfo classInfoEntry = constantPool.getClassInfoEntry(constant.getClassIndex());
			ConstantNameAndTypeInfo nameAndType = constantPool.getNameAndTypeInfo(constant.getNameAndTypeIndex());

			String className = constantPool.getUTF8Info(classInfoEntry.getIndex()).getValue();
			String fieldName = constantPool.getUTF8Info(nameAndType.getNameIndex()).getValue();
			String fieldDescriptor = constantPool.getUTF8Info(nameAndType.getDescriptorIndex()).getValue();

			Object object = fieldValues.get(fieldName);
			throw new UnsupportedOperationException();
		} else if (entry instanceof ConstantUTF8Info) {
			ConstantUTF8Info constant = (ConstantUTF8Info) entry;
			String value = constant.getValue();
			return new JavaString(vm, value);
		} else if (entry instanceof ConstantClassInfo) {
			ConstantClassInfo constantClassInfo = (ConstantClassInfo) entry;
			int index2 = constantClassInfo.getIndex();
			ConstantUTF8Info className = constantPool.getUTF8Info(index2);
			// TODO: Must be a "java.lang.Class" object, not a String!
			String value = className.getValue();
			Classfile classfile = bcl.load(value);
			return classfile.getAsJavaClassReference();
		} else {
			throw new UnsupportedOperationException("TODO: " + entry.getClass().getName() + " == " + entry);
		}

	}

	public void setValue(String fieldName, JavaObject newValue) {
		if (newValue == null) {
			throw new IllegalStateException();
		}
		fieldValues.put(fieldName, newValue);
	}

	public JavaObject internalGetValue(Frame frame, String fieldName, String fieldDescriptor) {
		return fieldValues.get(fieldName);
	}

	public JavaObject getValue(Frame frame, String fieldName, String fieldDescriptor) {
		JavaObject javaObject = fieldValues.get(fieldName);

		// TODO: Step 1: Find in parent class
		RuntimeDataArea rda = frame.getVirtualMachine().getRuntimeData();
		MethodArea globalMethodArea = rda.getMethodArea();
		for (Classfile parentClass : classfile.getParentClasses(rda, frame)) {
			ClassArea otherClassArea = globalMethodArea.getClassArea(parentClass);
			RuntimeConstantPool otherRCP = otherClassArea.getRuntimeConstantPool();
			JavaObject value = otherRCP.internalGetValue(frame, fieldName, fieldDescriptor);
			if (value != null) {
				return value;
			}
		}

		// Step 2: Return default types
		if (javaObject == null) {
			if (fieldDescriptor.equals("J")) {
				return new JavaLong(0);
			} else if (fieldDescriptor.startsWith("L")) {
				return vm.objects().nullReference();
			} else if (fieldDescriptor.startsWith("[L")) {
				return vm.objects().nullReference();
			} else if (fieldDescriptor.startsWith("Z")) {
				return new JavaInteger(0);
			} else if (fieldDescriptor.startsWith("I")) {
				return new JavaInteger(0);
			} else if (fieldDescriptor.startsWith("S")) {
				return new JavaShort((short) 0);
			} else if (fieldDescriptor.startsWith("B")) {
				return new JavaInteger(0);
			} else if (fieldDescriptor.startsWith("F")) {
				return new JavaFloat(0.0f);
			} else if (fieldDescriptor.startsWith("D")) {
				return new JavaDouble(0.0d);
			} else {
				throw new UnsupportedOperationException("Unknown field type: " + fieldDescriptor);
			}
		}
		return javaObject;
	}

}
