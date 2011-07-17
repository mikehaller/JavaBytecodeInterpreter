package com.smartwerkz.bytecode.vm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.smartwerkz.bytecode.classfile.DefaultClassfile;

public class ByteArrayResourceLoader implements ResourceLoader {

	private final byte[] clazzBytes;
	private String thisClassName;

	public ByteArrayResourceLoader(byte[] clazzBytes) {
		this.clazzBytes = clazzBytes;
		DefaultClassfile cf = new DefaultClassfile(new ByteArrayInputStream(clazzBytes));
		thisClassName = cf.getThisClassName();
	}

	@Override
	public InputStream open(String resourceName) {
		if (resourceName.equals(thisClassName+".class")) {
			return new ByteArrayInputStream(clazzBytes);
		}
		return null;
	}

}
