package com.smartwerkz.bytecode;

import com.smartwerkz.bytecode.classfile.MethodInfo;

public class CodeDumper {

	public static class Opcode implements Comparable<Opcode> {
		public final Byte opCode;
		public final int paramBytes;
		public final String opCodeName;
		public final String description;
		public final Integer intOpCode;

		public Opcode(int opCode, int paramBytes, String opCodeName, String description) {
			this.intOpCode = Integer.valueOf(opCode);
			this.opCode = Byte.valueOf((byte) opCode);
			this.paramBytes = paramBytes;
			this.opCodeName = opCodeName;
			this.description = description;
		}

		public int getIntOpCode() {
			return intOpCode.intValue();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((opCode == null) ? 0 : opCode.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Opcode other = (Opcode) obj;
			if (opCode == null) {
				if (other.opCode != null)
					return false;
			} else if (!opCode.equals(other.opCode))
				return false;
			return true;
		}

		@Override
		public int compareTo(Opcode o) {
			return intOpCode.compareTo(o.intOpCode);
		}

	}

	private Opcode[] opcodes = new Opcode[256];

	public CodeDumper() {
		add(new Opcode(0x00, 0, "nop", "performs no operation"));
		add(new Opcode(0x01, 0, "aconst_null", "pushes a null reference onto the stack"));
		add(new Opcode(0x02, 0, "iconst_m1", "loads the int value -1 onto the stack"));
		add(new Opcode(0x03, 0, "iconst_0", "loads the int value 0 onto the stack"));
		add(new Opcode(0x04, 0, "iconst_1", "loads the int value 1 onto the stack"));
		add(new Opcode(0x05, 0, "iconst_2", "loads the int value 2 onto the stack"));
		add(new Opcode(0x06, 0, "iconst_3", "loads the int value 3 onto the stack"));
		add(new Opcode(0x07, 0, "iconst_4", "loads the int value 4 onto the stack"));
		add(new Opcode(0x08, 0, "iconst_5", "loads the int value 5 onto the stack"));
		add(new Opcode(0x09, 0, "lconst_0", "pushes the long 0 onto the stack"));
		add(new Opcode(0x0a, 0, "lconst_1", "pushes the long 1 onto the stack"));
		add(new Opcode(0x0b, 0, "fconst_0", "pushes the float 0.0f onto the stack"));
		add(new Opcode(0x0c, 0, "fconst_1", "pushes the float 1.0f onto the stack"));
		add(new Opcode(0x0d, 0, "fconst_2", "pushes the float 2.0f onto the stack"));
		add(new Opcode(0x0e, 0, "dconst_0", "pushes the double 0.0f onto the stack"));
		add(new Opcode(0x0f, 0, "dconst_1", "pushes the double 1.0f onto the stack"));
		add(new Opcode(0x10, 1, "bipush", "pushes int value onto stack"));
		add(new Opcode(0x11, 2, "sipush", "pushes short value onto the stack"));
		add(new Opcode(0x12, 1, "ldc", "pushes a constant from constant pool (String, int or float) onto the stack"));
		add(new Opcode(0x13, 2, "ldc_w", "pushes a constant from a constant pool (String, int or float) onto the stack"));
		add(new Opcode(0x14, 2, "ldc2_w", "pushes a constant from a constant pool (double or long) onto the stack"));
		add(new Opcode(0x15, 1, "iload", "loads a int value from a local variable"));
		add(new Opcode(0x16, 1, "lload", "loads a long value from a local variable"));
		add(new Opcode(0x17, 1, "fload", "loads a float value from a local variable"));
		add(new Opcode(0x18, 1, "dload", "loads a double value from a local variable"));
		add(new Opcode(0x19, 1, "aload", "loads a reference onto the stack from a local variable"));
		add(new Opcode(0x1a, 0, "iload_0", "loads an int value from local variable 0"));
		add(new Opcode(0x1b, 0, "iload_1", "loads an int value from local variable 1"));
		add(new Opcode(0x1c, 0, "iload_2", "loads an int value from local variable 2"));
		add(new Opcode(0x1d, 0, "iload_3", "loads an int value from local variable 3"));
		add(new Opcode(0x1e, 0, "lload_0", "loads a long from local variable 0"));
		add(new Opcode(0x1f, 0, "lload_1", "loads a long from local variable 1"));
		add(new Opcode(0x20, 0, "lload_2", "loads a long from local variable 2"));
		add(new Opcode(0x21, 0, "lload_3", "loads a long from local variable 3"));
		add(new Opcode(0x22, 0, "fload_0", "loads a float from local variable 0"));
		add(new Opcode(0x23, 0, "fload_1", "loads a float from local variable 1"));
		add(new Opcode(0x24, 0, "fload_2", "loads a float from local variable 2"));
		add(new Opcode(0x25, 0, "fload_3", "loads a float from local variable 3"));
		add(new Opcode(0x26, 0, "dload_0", "loads a double from local variable 0"));
		add(new Opcode(0x27, 0, "dload_1", "loads a double from local variable 1"));
		add(new Opcode(0x28, 0, "dload_2", "loads a double from local variable 2"));
		add(new Opcode(0x29, 0, "dload_3", "loads a double from local variable 3"));
		add(new Opcode(0x2a, 0, "aload_0", "loads a reference onto the stack from local variable 0"));
		add(new Opcode(0x2b, 0, "aload_1", "loads a reference onto the stack from local variable 1"));
		add(new Opcode(0x2c, 0, "aload_2", "loads a reference onto the stack from local variable 2"));
		add(new Opcode(0x2d, 0, "aload_3", "loads a reference onto the stack from local variable 3"));
		add(new Opcode(0x2e, 0, "iaload", "loads an int from an array"));
		add(new Opcode(0x2f, 0, "laload", "loads a long from an array"));
		add(new Opcode(0x32, 0, "aaload", "loads onto the stack a reference from an array"));
		add(new Opcode(0x33, 0, "baload", "loads onto the stack a byte or boolean from an array"));
		add(new Opcode(0x34, 0, "caload", "loads a char into an array"));
		add(new Opcode(0x35, 0, "saload", "loads a short into an array"));
		add(new Opcode(0x36, 1, "istore", "store int value into local variable"));
		add(new Opcode(0x37, 1, "lstore", "store long value into local variable"));
		add(new Opcode(0x38, 1, "fstore", "store float value into local variable"));
		add(new Opcode(0x39, 1, "dstore", "store float value into local variable"));
		add(new Opcode(0x3a, 1, "astore", "store a reference into local variable"));
		add(new Opcode(0x3b, 0, "istore_0", "store int value into variable 0"));
		add(new Opcode(0x3c, 0, "istore_1", "store int value into variable 1"));
		add(new Opcode(0x3d, 0, "istore_2", "store int value into variable 2"));
		add(new Opcode(0x3e, 0, "istore_3", "store int value into variable 3"));
		add(new Opcode(0x3f, 0, "lstore_0", "stores a long value into local variable 0"));
		add(new Opcode(0x40, 0, "lstore_1", "stores a long value into local variable 1"));
		add(new Opcode(0x41, 0, "lstore_2", "stores a long value into local variable 2"));
		add(new Opcode(0x42, 0, "lstore_3", "stores a long value into local variable 3"));
		add(new Opcode(0x4b, 0, "astore_0", "stores a reference into local variable 0"));
		add(new Opcode(0x4c, 0, "astore_1", "stores a reference into local variable 1"));
		add(new Opcode(0x4d, 0, "astore_2", "stores a reference into local variable 2"));
		add(new Opcode(0x4e, 0, "astore_3", "stores a reference into local variable 3"));
		add(new Opcode(0x4f, 0, "iastore", "stores an int into an array"));
		add(new Opcode(0x50, 0, "lastore", "stores a long into an array"));
		add(new Opcode(0x53, 0, "aastore", "stores into a reference to an array"));
		add(new Opcode(0x54, 0, "bastore", "stores a byte or a Boolean into an array"));
		add(new Opcode(0x55, 0, "castore", "stores a char into an array"));
		add(new Opcode(0x56, 0, "sastore", "stores a short into an array"));
		add(new Opcode(0x57, 0, "pop", "discards the top value on the stack"));
		add(new Opcode(0x58, 0, "pop2", "discards the top two values on the stack (or one value for double/long)"));
		add(new Opcode(0x59, 0, "dup", "duplicates the value on top of the stack"));
		add(new Opcode(0x5a, 0, "dup_x1", "duplicates values"));
		add(new Opcode(0x5b, 0, "dup_x2", "duplicates values"));
		add(new Opcode(0x5c, 0, "dup2", "duplicates values"));
		add(new Opcode(0x5d, 0, "dup2_x1", "duplicates values"));
		add(new Opcode(0x5e, 0, "dup2_x2", "duplicates values"));
		add(new Opcode(0x5f, 0, "swap", "swaps two top words on the stack (no long and no double)"));
		add(new Opcode(0x60, 0, "iadd", "adds two ints together"));
		add(new Opcode(0x61, 0, "ladd", "adds two longs together"));
		add(new Opcode(0x62, 0, "fadd", "adds two floats together"));
		add(new Opcode(0x63, 0, "dadd", "adds two doubles together"));
		add(new Opcode(0x64, 0, "isub", "subtracts two ints"));
		add(new Opcode(0x65, 0, "lsub", "subtracts two longs"));
		add(new Opcode(0x66, 0, "fsub", "subtracts two floats"));
		add(new Opcode(0x67, 0, "dsub", "subtracts two doubles"));
		add(new Opcode(0x68, 0, "imul", "multiply two ints"));
		add(new Opcode(0x6a, 0, "fmul", "multiply two floats"));
		add(new Opcode(0x6b, 0, "dmul", "multiply two doubles"));
		add(new Opcode(0x6c, 0, "idiv", "divide integers"));
		add(new Opcode(0x6d, 0, "ldiv", "divide longs"));
		add(new Opcode(0x6e, 0, "fdiv", "divide floats"));
		add(new Opcode(0x6f, 0, "ddiv", "divide doubles"));
		add(new Opcode(0x70, 0, "irem", "logical int remainder"));
		add(new Opcode(0x74, 0, "ineg", "negate a int"));
		add(new Opcode(0x75, 0, "lneg", "negate a long"));
		add(new Opcode(0x76, 0, "fneg", "negate a float"));
		add(new Opcode(0x77, 0, "dneg", "negate a double"));
		add(new Opcode(0x78, 0, "ishl", "int shift left"));
		add(new Opcode(0x79, 0, "lshl", "long shift left"));
		add(new Opcode(0x7a, 0, "ishr", "int shift right"));
		add(new Opcode(0x7b, 0, "lshr", "long shift right"));
		add(new Opcode(0x7c, 0, "iushr", "int logical shift right"));
		add(new Opcode(0x7e, 0, "iand", "performs a bitwise and on two integers"));
		add(new Opcode(0x7f, 0, "land", "performs a bitwise and on two longs"));
		add(new Opcode(0x80, 0, "ior", "bitwise int or"));
		add(new Opcode(0x81, 0, "lor", "bitwise long or"));
		add(new Opcode(0x82, 0, "ixor", "int xor"));
		add(new Opcode(0x83, 0, "lxor", "long xor"));
		add(new Opcode(0x84, 2, "iinc", "increment local variable ... by signed byte ..."));
		add(new Opcode(0x85, 0, "i2l", "convert int to long"));
		add(new Opcode(0x86, 0, "i2f", "convert int to float"));
		add(new Opcode(0x87, 0, "i2d", "convert int to double"));
		add(new Opcode(0x88, 0, "l2i", "convert long to int"));
		add(new Opcode(0x8a, 0, "l2d", "convert long to double"));
		add(new Opcode(0x8b, 0, "f2i", "convert float to int"));
		add(new Opcode(0x8c, 0, "f2l", "convert float to long"));
		add(new Opcode(0x8d, 0, "f2d", "convert float to double"));
		add(new Opcode(0x8e, 0, "d2i", "convert double to int"));
		add(new Opcode(0x90, 0, "d2f", "convert double to float"));
		add(new Opcode(0x91, 0, "i2b", "convert int to byte"));
		add(new Opcode(0x92, 0, "i2c", "convert int to char"));
		add(new Opcode(0x93, 0, "i2s", "convert int to short"));
		add(new Opcode(0x94, 0, "lcmp", "compares two long values"));
		add(new Opcode(0x95, 0, "fcmpl", "compares two float values"));
		add(new Opcode(0x96, 0, "fcmpg", "compares two float values"));
		add(new Opcode(0x99, 2, "ifeq", "if value is 0, branch to instruction"));
		add(new Opcode(0x9a, 2, "ifne", "if value is not 0, branch to instruction at branchoffset"));
		add(new Opcode(0x9b, 2, "iflt", "if value is less than, branch to instruction"));
		add(new Opcode(0x9c, 2, "ifge", "if value is greater than or equal to 0, branch to instruction"));
		add(new Opcode(0x9d, 2, "ifgt", "if value is greater than, branch to instruction"));
		add(new Opcode(0x9e, 2, "ifle", "if value is less than or equal to 0, branch to instruction"));
		add(new Opcode(0x9f, 2, "if_icmpeq", "if ints are equal, branch to instruction"));
		add(new Opcode(0xa0, 2, "if_icmpne", "if ints are not equal, branch to instruction"));
		add(new Opcode(0xa1, 2, "if_icmplt", "if value1 is less than value2, branch to instruction"));
		add(new Opcode(0xa2, 2, "if_icmpge", "if value1 is greater than or equal to value2, branch to instruction"));
		add(new Opcode(0xa3, 2, "if_icmpgt", "if value1 is greater than value2, branch to instruction"));
		add(new Opcode(0xa4, 2, "if_icmple", "if value1 is less than or equal to value2, branch to instruction"));
		add(new Opcode(0xa5, 2, "if_acmpeq", "if references are equal, branch to instruction"));
		add(new Opcode(0xa6, 2, "if_acmpne", "if references are not equal, branch to instruction"));
		add(new Opcode(0xa7, 2, "goto", "goes to another instruction"));
		add(new Opcode(0xa8, 2, "jsr", "jump to subroutine"));
		add(new Opcode(0xa9, 1, "ret", "continue execution from address taken from a local variable"));
		add(new Opcode(0xaa, -1, "tableswitch", "a target address is looked up from a table"));
		add(new Opcode(0xab, -1, "lookupswitch", "a target address is looked up from a table"));
		add(new Opcode(0xac, 0, "ireturn", "returns an integer from a method"));
		add(new Opcode(0xad, 0, "lreturn", "returns a long from a method"));
		add(new Opcode(0xae, 0, "freturn", "returns a float from a method"));
		add(new Opcode(0xaf, 0, "dreturn", "returns a double from a method"));
		add(new Opcode(0xb0, 0, "areturn", "returns a reference from a method"));
		add(new Opcode(0xb1, 0, "return", "return void from method"));
		add(new Opcode(0xb2, 2, "getstatic", "get value of static field"));
		add(new Opcode(0xb3, 2, "putstatic", "set static field to a value"));
		add(new Opcode(0xb4, 2, "getfield", "gets a field value of an object objectref"));
		add(new Opcode(0xb5, 2, "putfield", "set field to value in an object objectref"));
		add(new Opcode(0xb6, 2, "invokevirtual", "invoke virtual method"));
		add(new Opcode(0xb7, 2, "invokespecial", "invoke instance method"));
		add(new Opcode(0xb8, 2, "invokestatic", "invoke static method"));
		add(new Opcode(0xb9, 4, "invokeinterface", "invoke interface method"));
		add(new Opcode(0xbb, 2, "new", "creates new object of type"));
		add(new Opcode(0xbc, 1, "newarray", "creates a new primitive array"));
		add(new Opcode(0xbd, 2, "anewarray", "creates a new array"));
		add(new Opcode(0xbe, 0, "arraylength", "gets the length of an array"));
		add(new Opcode(0xbf, 0, "athrow", "throws an error or exception (clears the stack, keeps exception)"));
		add(new Opcode(0xc0, 2, "checkcast", "checks whether an objectref is of a certain type"));
		add(new Opcode(0xc1, 2, "instanceof", "checks whether an objectref is of a certain type"));
		add(new Opcode(0xc2, 0, "monitorenter", "enter monitor for object"));
		add(new Opcode(0xc4, 0, "monitorexit", "exit monitor for object"));
		add(new Opcode(0xc3, 0, "monitorexit", "exit monitor for object"));
		add(new Opcode(0xc6, 2, "ifnull", "if value is null, branch to instruction"));
		add(new Opcode(0xc7, 2, "ifnonnull", "if value is not null, branch to instruction"));
		add(new Opcode(0xca, 0, "breakpoint", "**RESERVED FOR DEBUGGERS**"));
		add(new Opcode(0xfe, 0, "impdep1", "**RESERVED FOR DEBUGGERS**"));
		add(new Opcode(0xff, 0, "impdep2", "**RESERVED FOR DEBUGGERS**"));
	}

	private void add(Opcode opcode) {
		opcodes[unsignedByteToInt(opcode.opCode.byteValue())] = opcode;
	}

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	public String dumpDetailed(MethodInfo methodInfo) {
		StringBuilder sb = new StringBuilder();

		byte[] code = methodInfo.getCode().getCode();
		sb.append("\nClass Name: " + methodInfo.getClassName());
		sb.append("\nMethod Name: " + methodInfo.getMethodName());
		sb.append("\nMethod Descriptor: " + methodInfo.getDescriptorName());
		sb.append("\nMethod Parameters: " + methodInfo.getParameterCount());
		sb.append("\nBytecode:");
		for (int i = 0; i < code.length; i++) {
			byte b = code[i];
			sb.append(String.format("%02x ", b));
		}
		SeekableCode dis = new SeekableCode(methodInfo.getCode().getCode());
		int position = 0;
		while (dis.available() > 0) {
			sb.append("\n");
			byte opCode = dis.readByte();
			Opcode opcode2 = opcodes[unsignedByteToInt(opCode)];
			int dynamicParametersCounter = 0;
			if (opcode2 == null) {
				throw new UnsupportedOperationException(String.format("%s: Unknown opCode %02x", position, opCode));
			} else {
				StringBuilder params = new StringBuilder();
				if (opcode2.paramBytes == 0) {
				} else if (opcode2.paramBytes == 1) {
					byte read = dis.readByte();
					params.append(String.format("- Hex:%02x Dec:%d", read, read));
				} else if (opcode2.paramBytes == 2) {
					byte readByte1 = dis.readByte();
					byte readByte2 = dis.readByte();
					int paras = (readByte1 << 8) | readByte2;
					params.append(String.format("- Byte1:0x%02x Byte2:0x%02x // as signed short:%d", readByte1,
							readByte2, paras));
				} else if (opcode2.paramBytes == 4) {
					byte readByte1 = dis.readByte();
					byte readByte2 = dis.readByte();
					byte readByte3 = dis.readByte();
					byte readByte4 = dis.readByte();
					int paras = (readByte1 << 24) | (readByte2 << 16) | (readByte3 << 8) | readByte4;
					params.append(String.format("- Byte1:0x%02x Byte2:0x%02x Byte3:0x%02x Byte4:0x%02x// as int:%d",
							readByte1, readByte2, readByte3, readByte4, paras));
				} else if (opcode2.paramBytes == -1) {
					// TODO: Dynamic lookup of parameter bytes for
					// 'lookupswitch' and 'tablesswitch'
					params.append("- Dynamic parameters: ");
					if (opcode2.opCode == Byte.valueOf((byte) 0xab)) {
						// "lookupswitch"
						while (dis.getCurrentPosition() % 4 != 0) {
							if (dis.readByte() != (byte) 0x00)
								throw new IllegalStateException();
							dynamicParametersCounter++;
						}
						int defaultbyte1 = dis.read();
						int defaultbyte2 = dis.read();
						int defaultbyte3 = dis.read();
						int defaultbyte4 = dis.read();
						dynamicParametersCounter += 4;
						int defaultbyte = (defaultbyte1 << 24) | (defaultbyte2 << 16) | (defaultbyte3 << 8)
								| defaultbyte4;
						params.append("Default:" + defaultbyte);
						byte npairs1 = dis.readByte();
						byte npairs2 = dis.readByte();
						byte npairs3 = dis.readByte();
						byte npairs4 = dis.readByte();
						dynamicParametersCounter += 4;
						int npairs = (npairs1 << 24) | (npairs2 << 16) | (npairs3 << 8) | npairs4;
						params.append(" Pairs:" + npairs);
						for (int i = 0; i < npairs; i++) {
							// read match
							byte match1 = dis.readByte();
							byte match2 = dis.readByte();
							byte match3 = dis.readByte();
							byte match4 = dis.readByte();
							dynamicParametersCounter += 4;
							int match = (match1 << 24) | (match2 << 16) | (match3 << 8) | match4;
							// read offset
							byte offset1 = dis.readByte();
							byte offset2 = dis.readByte();
							byte offset3 = dis.readByte();
							byte offset4 = dis.readByte();
							dynamicParametersCounter += 4;
							int offset = (offset1 << 24) | (offset2 << 16) | (offset3 << 8) | offset4;
							params.append(" Pair " + i);
							params.append(" Match:" + match);
							params.append(" Offset:" + offset);
						}
					} else

					if (opcode2.opCode == Byte.valueOf((byte) 0xaa)) {
						// "tableswitch"
						while (dis.getCurrentPosition() % 4 != 0) {
							if (dis.readByte() != (byte) 0x00)
								throw new IllegalStateException();
							dynamicParametersCounter++;
						}
						int defaultbyte1 = dis.read();
						int defaultbyte2 = dis.read();
						int defaultbyte3 = dis.read();
						int defaultbyte4 = dis.read();
						dynamicParametersCounter += 4;
						int defaultbyte = (defaultbyte1 << 24) | (defaultbyte2 << 16) | (defaultbyte3 << 8)
								| defaultbyte4;
						params.append(" Default:" + defaultbyte);
						
						byte lowbyte1 = dis.readByte();
						byte lowbyte2 = dis.readByte();
						byte lowbyte3 = dis.readByte();
						byte lowbyte4 = dis.readByte();
						dynamicParametersCounter += 4;
						int lowbyte = (lowbyte1 << 24) | (lowbyte2 << 16) | (lowbyte3 << 8) | lowbyte4;
						params.append(" Low:" + lowbyte);
						
						byte highbyte1 = dis.readByte();
						byte highbyte2 = dis.readByte();
						byte highbyte3 = dis.readByte();
						byte highbyte4 = dis.readByte();
						dynamicParametersCounter += 4;
						int highbyte = (highbyte1 << 24) | (highbyte2 << 16) | (highbyte3 << 8) | highbyte4;
						params.append(" High:" + highbyte);
						
						int npairs = highbyte-lowbyte+1;
						params.append(" Jump Offsets:" + npairs);
						for (int i = 0; i < npairs; i++) {
							byte offset1 = dis.readByte();
							byte offset2 = dis.readByte();
							byte offset3 = dis.readByte();
							byte offset4 = dis.readByte();
							dynamicParametersCounter += 4;
							int offset = (offset1 << 24) | (offset2 << 16) | (offset3 << 8) | offset4;
							params.append(" Offset:" + offset);
						}
					} else {
						throw new UnsupportedOperationException("Unknown bytecode instruction: " + opcode2.opCode);
					}
				} else {
					throw new UnsupportedOperationException("Unknown number of parameters: " + opcode2.paramBytes);
				}
				sb.append(String.format("%4d: [%02x] %-20s// %s %s", position, opCode, opcode2.opCodeName,
						opcode2.description, params.toString()));
			}
			position++;
			if (opcode2.paramBytes == -1) {
				position += dynamicParametersCounter; // For lookupswitch
			} else {
				position += opcode2.paramBytes;
			}
		}

		sb.append("\nException Table: " + methodInfo.getCode().getExceptionTable().print());

		return sb.toString();
	}

	public Opcode getOpCode(byte opCode) {
		return opcodes[unsignedByteToInt(opCode)];
	}

	public Opcode[] opcodes() {
		return opcodes;
	}
}
