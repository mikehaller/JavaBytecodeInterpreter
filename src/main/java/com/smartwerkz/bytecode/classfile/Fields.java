package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.VirtualThread;

public class Fields implements Iterable<FieldInfo> {

	private final VMLog log = new VMLog(Fields.class.getName(), false);

	private final List<FieldInfo> fields = new ArrayList<FieldInfo>();

	public Fields(ConstantPool constantPool, DataInputStream dis) {
		try {
			int fieldsCount = dis.readUnsignedShort();
			for (int i = 0; i < fieldsCount; i++) {
				fields.add(new FieldInfo(constantPool, dis));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int size() {
		return fields.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (FieldInfo info : fields) {
			if (sb.length() != 0)
				sb.append(",");
			sb.append(info.getFieldName());
		}
		return sb.toString();
	}

	public void initialize(VirtualMachine vm, VirtualThread thread, RuntimeDataArea rda) throws IOException {
		if (fields.isEmpty()) {
			log.debug("No fields to initialize");
		}
		for (FieldInfo info : fields) {
			info.initialize(rda);
		}
	}

	@Override
	public Iterator<FieldInfo> iterator() {
		return fields.iterator();
	}

}
