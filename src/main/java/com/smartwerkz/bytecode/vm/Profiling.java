package com.smartwerkz.bytecode.vm;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import com.smartwerkz.bytecode.CodeDumper;
import com.smartwerkz.bytecode.CodeDumper.Opcode;

public class Profiling {

	private final static AtomicLong globalInstructionCounter = new AtomicLong();
	private final AtomicLong[] instructions = new AtomicLong[256];

	public void countInstruction(Opcode opCodeDesc) {
		globalInstructionCounter.incrementAndGet();
		AtomicLong counter = instructions[unsignedByteToInt(opCodeDesc.opCode.byteValue())];
		if (counter != null) {
			counter.incrementAndGet();
		} else {
			instructions[unsignedByteToInt(opCodeDesc.opCode.byteValue())] = new AtomicLong(1);
		}
	}

	public String printOpcodesHistogram() {
		StringBuilder sb = new StringBuilder();
		TreeMap<Opcode, AtomicLong> sorted = new TreeMap<Opcode, AtomicLong>();
		CodeDumper dumper = new CodeDumper();
		for (Opcode opcode : dumper.opcodes()) {
			if (opcode != null) {
				AtomicLong value = instructions[unsignedByteToInt(opcode.opCode.byteValue())];
				if (value != null) {
					sorted.put(opcode, value);
				}
			}
		}
		Set<Entry<Opcode, AtomicLong>> entries = sorted.entrySet();
		for (Entry<Opcode, AtomicLong> entry : entries) {
			Opcode opCode = entry.getKey();
			sb.append(String.format("\n[%02x] %-20s %10d invocations", opCode.opCode, opCode.opCodeName, entry
					.getValue().get()));
		}
		sb.append("\nTotal: " + globalInstructionCounter.get());
		return sb.toString();
	}

	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

	public long getTotalCount() {
		return globalInstructionCounter.get();
	}
}
