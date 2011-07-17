package com.smartwerkz.bytecode.vm.os;

public class FileHandle {

	private final long id;

	public FileHandle(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

}
