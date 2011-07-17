package com.smartwerkz.bytecode.vm;

import java.io.InputStream;

public class HostVMResourceLoader implements ResourceLoader {

	@Override
	public InputStream open(String resourceName) {
		return ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
	}

}
