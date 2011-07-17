package com.smartwerkz.bytecode.instructions;

public enum Instructions {
	/** Load an object reference from an array */
	AALOAD(new Aaload()),
	/** Store an object from the operand stack into an array */
	AASTORE(new Aastore()),
	/** Load a char from an array */
	CALOAD(new Caload()),
	/** Load NULL onto the stack */
	ACONST_NULL(new AconstNull()),
	/** Goto */
	GOTO(new Goto());

	private final BasicInstruction instruction;

	private Instructions(BasicInstruction instruction) {
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public static Instructions byOpCode(int opCode) {
		for (Instructions i : values()) {
			if (i.instruction.getOpCode() == opCode)
				return i;
		}
		return null;
	}

	public int getByteCode() {
		return instruction.getOpCode();
	}
}
