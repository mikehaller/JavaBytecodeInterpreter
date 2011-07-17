package com.smartwerkz.bytecode;

public class SeekableCode {

	private final byte[] code;
	private int currentPosition;

	public SeekableCode(byte[] code) {
		this.code = code;
		this.currentPosition = 0;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public int available() {
		return code.length - currentPosition;
	}

	public byte readByte() {
		return code[currentPosition++];
	}

	public void jumpTo(int absolutePos) {
		if (absolutePos < 0 || absolutePos > code.length) {
			throw new IllegalArgumentException("Jumping to absolute code position invalid: " + absolutePos
					+ ". Actual code length is: " + code.length + ". Current position is: " + currentPosition);
		}
		currentPosition = absolutePos;
	}

	public int read() {
		return unsignedByteToInt(code[currentPosition++]);
	}

	public short readShort() {
		int ch1 = read();
		int ch2 = read();
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

}
