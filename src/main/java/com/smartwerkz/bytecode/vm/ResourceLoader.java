package com.smartwerkz.bytecode.vm;

import java.io.InputStream;

public interface ResourceLoader {

	InputStream open(String resourceName);

}
