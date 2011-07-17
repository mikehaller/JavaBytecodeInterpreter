package com.smartwerkz.bytecode.primitives;


public class JavaLong implements JavaObject, Comparable<JavaLong> {

	private long longValue;

	public JavaLong(long intValue) {
		super();
		this.longValue = intValue;
	}

	public long longValue() {
		return longValue;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+longValue+"]";
	}

	@Override
	public int compareTo(JavaLong other) {
		if (longValue < other.longValue) return -1;
		if (longValue > other.longValue) return +1;
		return 0;
	}

	@Override
	public String asStringValue() {
		return Long.toString(longValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (longValue ^ (longValue >>> 32));
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
		JavaLong other = (JavaLong) obj;
		if (longValue != other.longValue)
			return false;
		return true;
	}

	@Override
	public JavaObject copy() {
		return this;
	}

}
