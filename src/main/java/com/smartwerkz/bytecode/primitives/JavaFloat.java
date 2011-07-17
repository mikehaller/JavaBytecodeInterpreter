package com.smartwerkz.bytecode.primitives;

public class JavaFloat implements JavaObject, Comparable<JavaFloat> {

	private float floatValue;

	public JavaFloat(float intValue) {
		super();
		this.floatValue = intValue;
	}

	public float floatValue() {
		return floatValue;
	}

	@Override
	public int compareTo(JavaFloat other) {
		if (floatValue < other.floatValue)
			return -1;
		if (floatValue > other.floatValue)
			return +1;
		return 0;
	}

	@Override
	public String asStringValue() {
		return Float.toString(floatValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(floatValue);
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
		JavaFloat other = (JavaFloat) obj;
		if (Float.floatToIntBits(floatValue) != Float.floatToIntBits(other.floatValue))
			return false;
		return true;
	}
	@Override
	public JavaObject copy() {
		return this;
	}

}
