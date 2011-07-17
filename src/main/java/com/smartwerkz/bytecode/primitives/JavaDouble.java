package com.smartwerkz.bytecode.primitives;


public class JavaDouble implements JavaObject, Comparable<JavaDouble> {

	private double doubleValue;

	public JavaDouble(double intValue) {
		super();
		this.doubleValue = intValue;
	}

	public double doubleValue() {
		return doubleValue;
	}

	@Override
	public int compareTo(JavaDouble other) {
		if (doubleValue < other.doubleValue) return -1;
		if (doubleValue > other.doubleValue) return +1;
		return 0;
	}

	@Override
	public String asStringValue() {
		return Double.toString(doubleValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(doubleValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		JavaDouble other = (JavaDouble) obj;
		if (Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue))
			return false;
		return true;
	}
	@Override
	public JavaObject copy() {
		return this;
	}

}
