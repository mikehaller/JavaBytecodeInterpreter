package com.smartwerkz.bytecode.primitives;

public class JavaInteger implements JavaObject, Comparable<JavaObject> {

	private int intValue;
	private String toString;

	public JavaInteger(int intValue) {
		super();
		this.intValue = intValue;
		toString = "JavaInteger["+intValue+"]";
	}

	public int intValue() {
		return intValue;
	}
	
	@Override
	public String toString() {
		return toString;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + intValue;
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
		JavaInteger other = (JavaInteger) obj;
		if (intValue != other.intValue)
			return false;
		return true;
	}

	@Override
	public int compareTo(JavaObject other) {
		if (other instanceof JavaInteger) {
			JavaInteger otherInt = (JavaInteger) other;
			if (intValue < otherInt.intValue)
				return -1;
			if (intValue > otherInt.intValue)
				return +1;
			return 0;
		} else if (other instanceof JavaShort) {
			JavaShort otherShort = (JavaShort) other;
			if (intValue < otherShort.shortValue())
				return -1;
			if (intValue > otherShort.shortValue())
				return +1;
			return 0;
		}
		throw new IllegalArgumentException(String.valueOf(other));
	}

	@Override
	public String asStringValue() {
		return Integer.toString(intValue);
	}
	@Override
	public JavaObject copy() {
		return this;
	}

}
