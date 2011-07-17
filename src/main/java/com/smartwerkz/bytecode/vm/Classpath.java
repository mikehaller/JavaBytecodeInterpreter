package com.smartwerkz.bytecode.vm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Classpath {

	private final List<ResourceLoader> cp = new ArrayList<ResourceLoader>();

	public void addFolder(String absolutePath) {
		File file = new File(absolutePath);
		cp.add(new FolderResourceLoader(file));
	}

	public void addLoader(ResourceLoader resourceLoader) {
		cp.add(resourceLoader);
	}

	public List<ResourceLoader> loaders() {
		return cp;
	}

}
