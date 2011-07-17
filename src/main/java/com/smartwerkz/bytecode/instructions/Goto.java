package com.smartwerkz.bytecode.instructions;

public class Goto extends BasicInstruction {

	public Goto() {
		super(0x7a, "goto", "Jumps to another instruction");
	}

}
