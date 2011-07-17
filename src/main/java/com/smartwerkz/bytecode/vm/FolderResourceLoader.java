package com.smartwerkz.bytecode.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FolderResourceLoader implements ResourceLoader {

	private final File folder;

	public FolderResourceLoader(File folder) {
		this.folder = folder;
	}

	@Override
	public InputStream open(String resourceName) {
		try {
			return new FileInputStream(new File(folder,resourceName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
