package com.smartwerkz.bytecode.primitives;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.PrimitiveClassfile;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class JavaArray extends JavaObjectReference {

	private final HashMap<Integer, JavaObject> arr = new HashMap<Integer, JavaObject>();
	private final int arrayLength;
	private final Classfile arrayType;
	private final VirtualMachine vm;

	public JavaArray(VirtualMachine vm, Classfile arrayType, int arrayLength) {
		super(arrayType, false);
		this.vm = vm;
		this.arrayType = arrayType;
		this.arrayLength = arrayLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((arr == null) ? 0 : arr.hashCode());
		result = prime * result + arrayLength;
		result = prime * result + ((arrayType == null) ? 0 : arrayType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaArray other = (JavaArray) obj;
		if (arr == null) {
			if (other.arr != null)
				return false;
		} else if (!arr.equals(other.arr))
			return false;
		if (arrayLength != other.arrayLength)
			return false;
		if (arrayType == null) {
			if (other.arrayType != null)
				return false;
		} else if (!arrayType.equals(other.arrayType))
			return false;
		return true;
	}

	public JavaArray(VirtualMachine vm, Classfile arrayType) {
		this(vm, arrayType, 0);
	}

	@Override
	public JavaObject copy() {
		JavaArray copy = new JavaArray(vm, arrayType, arrayLength);
		Set<Entry<Integer, JavaObject>> entrySet = arr.entrySet();
		for (Entry<Integer, JavaObject> entry : entrySet) {
			copy.arr.put(entry.getKey(), entry.getValue().copy());
		}
		return copy;
	}

	@Override
	public String toString() {
		if (arrayType == vm.classes().primitives().charClass()) {
			StringBuilder stringValue = new StringBuilder();
			for(int i = 0; i < arrayLength;i++) {
				JavaObject javaObject = arr.get(i);
				if (javaObject instanceof JavaInteger) {
					JavaInteger jint = (JavaInteger) javaObject;
					char intValue = (char) jint.intValue();
					stringValue.append(intValue);
				}
			}
			return String.format("JavaArray@%d[string=%s]", getInstanceId(), stringValue.toString());
		}
		return String.format("JavaArray@%d[class=%s,length=%d]", getInstanceId(), arrayType, arrayLength);
	}

	public Classfile getComponentType() {
		return arrayType;
	}

	@Override
	public String asStringValue() {
		if (arrayType == vm.classes().primitives().charClass()
				|| arrayType == vm.classes().primitives().byteClass()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arrayLength; i++) {
				JavaObject jo = arr.get(i);
				sb.append(jo.asStringValue());
			}
			return sb.toString();
		}
		return toString();
	}

	public void set(int index, JavaObject value) {
		// TODO: This IOOBE should be thrown in the ExecutionEngine.
		// and put onto the operand stack
		if (index >= arrayLength)
			throw new ArrayIndexOutOfBoundsException("At index " + index + " on array: " + this + " for value: "
					+ value);
		arr.put(Integer.valueOf(index), value);
	}

	public int length() {
		return arrayLength;
	}

	public void addDimension(int intValue) {
		throw new UnsupportedOperationException();
	}

	public JavaObject get(int index) {
		if (index > arrayLength)
			throw new IndexOutOfBoundsException("Index: " + index + " but ArrayLength: " + arrayLength);
		Integer key = Integer.valueOf(index);
		if (arr.containsKey(key)) {
			return arr.get(key);
		}
		if (arrayType instanceof PrimitiveClassfile) {
			PrimitiveClassfile primitiveClassfile = (PrimitiveClassfile) arrayType;
			return primitiveClassfile.getDefaultValue();
		}
		return vm.objects().nullReference();
	}

	public static JavaArray str2char(VirtualMachine vm, String threadName) {
		JavaArray arr = new JavaArray(vm, vm.classes().primitives().charClass(), threadName.length());
		for (int i = 0; i < threadName.length(); i++) {
			arr.set(i, new JavaChar(threadName.charAt(i)));
		}
		return arr;
	}

}
