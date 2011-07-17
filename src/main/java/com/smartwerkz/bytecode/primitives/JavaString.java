package com.smartwerkz.bytecode.primitives;

import com.smartwerkz.bytecode.vm.VirtualMachine;

public class JavaString extends JavaObjectReference {

	private final String value;
	private final VirtualMachine vm;
	private final String toString;

	public JavaString(VirtualMachine vm, String value) {
		super(vm.classes().stringClass());
		this.vm = vm;
		this.value = value;
		setValueOfField("count", new JavaInteger(value.length()));
		JavaArray charsArray = new JavaArray(vm, vm.classes().primitives().charClass(), value.length());
		for (int i = 0; i < value.length(); i++) {
			charsArray.set(i, new JavaInteger(value.charAt(i)));
		}
		setValueOfField("value", charsArray);
		toString = "JavaString[" + value + "]";
	}

	@Override
	public String toString() {
		return toString;
	}

	@Override
	public String asStringValue() {
		return value;
	}

	@Override
	public JavaObject copy() {
		return new JavaString(vm, value);
	}

	@Override
	public int hashCode() {
		return 31 + ((value == null) ? 0 : value.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaString other = (JavaString) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
