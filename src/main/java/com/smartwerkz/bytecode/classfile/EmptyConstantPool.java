package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EmptyConstantPool extends ConstantPool {

	public EmptyConstantPool() {
		super(createEmptyConstantPoolStream());
	}

	private static DataInputStream createEmptyConstantPoolStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		try {
			dos.writeShort(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
	}

}
