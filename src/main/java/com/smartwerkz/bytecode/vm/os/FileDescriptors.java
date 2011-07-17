package com.smartwerkz.bytecode.vm.os;

public class FileDescriptors {

	private final FileHandle stdin;
	private final FileHandle stdout;
	private final FileHandle stderr;

	public FileDescriptors(FileSystem fs) {
		stdin = fs.createNewHandle();
		stdout = fs.createNewHandle();
		stderr = fs.createNewHandle();
	}

	public long getStdInHandle() {
		return stdin.getId();
	}

	public long getStdOutHandle() {
		return stdout.getId();
	}

	public long getStdErrHandle() {
		return stderr.getId();
	}

}
