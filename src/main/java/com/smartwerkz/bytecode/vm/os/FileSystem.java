package com.smartwerkz.bytecode.vm.os;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class FileSystem {

	private final AtomicLong ids = new AtomicLong(0);
	private final Map<Long,FileHandle> handles = new HashMap<Long,FileHandle>();
	
	public FileHandle createNewHandle() {
		long id = ids.incrementAndGet();
		FileHandle fileHandle = new FileHandle(id);
		handles.put(id, fileHandle);
		return fileHandle;
	}

}
