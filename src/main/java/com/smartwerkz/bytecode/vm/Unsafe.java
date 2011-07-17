package com.smartwerkz.bytecode.vm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;

public class Unsafe {

	private static class Allocation {

		private final long start;
		private final long size;
		private ByteBuffer content;

		public Allocation(long start, long size) {
			this.start = start;
			this.size = size;
			this.content = ByteBuffer.allocate((int) size);
		}

	}

	private final AtomicLong position = new AtomicLong();
	private final List<Allocation> allocs = new ArrayList<Allocation>();

	// public native long getAddress(long address);
	// public native void putAddress(long address, long value);
	// public native long allocateMemory(long size);
	// public native long reallocateMemory(long l, long l1);
	// public native void setMemory(long l, long l1, byte b);
	// public native void copyMemory(long l, long l1, long l2);
	// public native byte getByte(long paramLong);
	// public native void putByte(long paramLong, byte paramByte);
	// public native short getShort(long paramLong);
	// public native void putShort(long paramLong, short paramShort);
	// public native char getChar(long paramLong);
	// public native void putChar(long paramLong, char paramChar);
	// public native int getInt(long paramLong);
	// public native void putInt(long paramLong, int paramInt);
	// public native long getLong(long paramLong);
	// public native void putLong(long paramLong1, long paramLong2);
	// public native float getFloat(long paramLong);
	// public native void putFloat(long paramLong, float paramFloat);
	// public native double getDouble(long paramLong);
	// public native void putDouble(long paramLong, double paramDouble);

	public long allocateMemory(long size) {
		long start = position.getAndAdd(size);
		Allocation allocation = new Allocation(start, size);
		allocs.add(allocation);
		return start;
	}

	public void putLong(long offset, long value) {
		Allocation allocation = find(offset);
		allocation.content.position((int) (offset - allocation.start));
		allocation.content.putLong(value);
	}

	public void putObject(long offset, JavaObject value) {
		Allocation allocation = find(offset);
		allocation.content.position((int) (offset - allocation.start));

		// TODO Write DIRECTLY TO THE MEMORY O_O
		// allocation.content.put(value);
	}

	public byte getByte(long offset) {
		Allocation allocation = find(offset);
		return allocation.content.get((int) (offset - allocation.start));
	}

	private Allocation find(long paramLong1) {
		for (Allocation alloc : allocs) {
			if (paramLong1 >= alloc.start && paramLong1 < alloc.start + alloc.size)
				return alloc;
		}
		return null;
	}

	public void freeMemory(long longValue) {
		Allocation alloc = find(longValue);
		alloc.content.clear();
		allocs.remove(alloc);
	}

	public boolean compareAndSwapInt(JavaObjectReference targetObject, long offset, int expect, int update) {
		Allocation allocation = find(offset);
//		int oldValue = allocation.content.getInt();
		// TODO: Implementation of the memory address lookup for targetObject
		return true;
	}

}
