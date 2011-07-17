package com.smartwerkz.bytecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.CodeDumper.Opcode;
import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.ConstantClassInfo;
import com.smartwerkz.bytecode.classfile.ConstantFieldrefInfo;
import com.smartwerkz.bytecode.classfile.ConstantNameAndTypeInfo;
import com.smartwerkz.bytecode.classfile.ConstantPool;
import com.smartwerkz.bytecode.classfile.ConstantUTF8Info;
import com.smartwerkz.bytecode.classfile.ExceptionHandler;
import com.smartwerkz.bytecode.classfile.ExceptionTable;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.controlflow.ControlFlowException;
import com.smartwerkz.bytecode.controlflow.FrameExit;
import com.smartwerkz.bytecode.primitives.JavaAddress;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaByte;
import com.smartwerkz.bytecode.primitives.JavaChar;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaNullReference;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaShort;
import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.MethodArea;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.Profiling;
import com.smartwerkz.bytecode.vm.ProgramCounter;
import com.smartwerkz.bytecode.vm.RuntimeConstantPool;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.SymbolicClassReference;
import com.smartwerkz.bytecode.vm.SymbolicFieldReference;
import com.smartwerkz.bytecode.vm.SymbolicMethodReference;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * Executes bytecode within the VM runtime.
 * 
 * @author mhaller
 */
public class ExecutionEngine {

	private static final Logger log = LoggerFactory.getLogger(ExecutionEngine.class);

	private final ExceptionFactory exceptionFactory = new ExceptionFactory();

	private final Frame frame;
	private final RuntimeDataArea rda;
	private final byte[] code;

	// private ByteArrayInputStream in;
	private final SeekableCode dis;
	private final OperandStack operandStack;
	private final MethodInfo methodInfo;
	private final ProgramCounter pc;
	private final VirtualMachine vm;

	private ExceptionTable exceptionTable;
	private Profiling profiling;

	private CodeDumper codeDumper;

	public ExecutionEngine(VirtualMachine vm, RuntimeDataArea rda, Frame frame, MethodInfo methodInfo) {
		if (rda == null)
			throw new IllegalArgumentException("RuntimeDataArea must not be null");
		this.code = methodInfo.getCode().getCode();
		if (code == null || code.length == 0)
			throw new IllegalArgumentException("Code must not be null or empty");
		this.vm = vm;
		this.codeDumper = vm.getCodeDumper();
		this.methodInfo = methodInfo;
		this.rda = rda;
		this.frame = frame;
		this.pc = frame.getVirtualThread().getProgramCounter();
		this.dis = new SeekableCode(code);
		this.profiling = vm.getProfiling();
		this.operandStack = frame.getOperandStack();
		this.exceptionTable = methodInfo.getCode().getExceptionTable();

		// if (methodInfo.getClassName().startsWith("java") ||
		// methodInfo.getClassName().startsWith("sun")) {
		// log.disable();
		// }

		if (log.isDebugEnabled()) {
			log.debug("New execution engine for frame: {}", frame.toString());
		}
		
		if (log.isTraceEnabled()) {
			log.trace("Exception Table:" + exceptionTable.print());
			CodeDumper codeDumper = new CodeDumper();
			log.trace("Dumping code {}", codeDumper.dumpDetailed(methodInfo));
		}
	}

	private void jumpRelative(int branchoffset) {
		int currentPos = pc.getCurrentAddress();
		int absolutePos = currentPos + branchoffset;
		dis.jumpTo(absolutePos);
	}

	public String interpret() {
		if (dis.available() == 0)
			return null;
		pc.setCurrentAddress(dis.getCurrentPosition());
		byte opCode = dis.readByte();

		Opcode opCodeDesc = codeDumper.getOpCode(opCode);
		if (opCodeDesc == null) {
			throw new IllegalStateException(String.format("No Opcode description found for: %02x", opCode));
		}

		// if (log.isDebugEnabled()) {
		// log.debug("{}: [{}] {} // {}", new Object[] { pc.getCurrentAddress(),
		// opCode, opCodeDesc.opCodeName,
		// opCodeDesc.description });
		// }
		// profiling.countInstruction(opCodeDesc);

		// vm.getListeners().notifyBeforeOpcodeExecution(frame, opCodeDesc);

		switch (opCode) {
		case (byte) 0x00:
			return nop();
		case (byte) 0x01:
			return aconst_null();
		case (byte) 0x02:
			return iconst_m1();
		case (byte) 0x03:
			return iconst_0();
		case (byte) 0x04:
			return iconst_1();
		case (byte) 0x05:
			return iconst_2();
		case (byte) 0x06:
			return iconst_3();
		case (byte) 0x07:
			return iconst_4();
		case (byte) 0x08:
			return iconst_5();
		case (byte) 0x09:
			return lconst_0();
		case (byte) 0x0a:
			return lconst_1();
		case (byte) 0x0b:
			return fconst_0();
		case (byte) 0x0c:
			return fconst_1();
		case (byte) 0x0d:
			return fconst_2();
		case (byte) 0x0e:
			return dconst_0();
		case (byte) 0x0f:
			return dconst_1();
		case (byte) 0x10:
			return bipush();
		case (byte) 0x11:
			return sipush();
		case (byte) 0x12:
			return ldc();
		case (byte) 0x13:
			return ldcw();
		case (byte) 0x14:
			return ldc2w();
		case (byte) 0x15:
			return iload();
		case (byte) 0x16:
			return lload();
		case (byte) 0x17:
			return fload();
		case (byte) 0x19:
			return aload();
		case (byte) 0x1a:
			return iload0();
		case (byte) 0x1b:
			return iload1();
		case (byte) 0x1c:
			return iload2();
		case (byte) 0x1d:
			return iload3();
		case (byte) 0x1e:
			return lload0();
		case (byte) 0x1f:
			return lload1();
		case (byte) 0x20:
			return lload2();
		case (byte) 0x21:
			return lload3();
		case (byte) 0x22:
			return fload0();
		case (byte) 0x23:
			return fload1();
		case (byte) 0x24:
			return fload2();
		case (byte) 0x25:
			return fload3();
		case (byte) 0x26:
			return dload0();
		case (byte) 0x2a:
			return aload0();
		case (byte) 0x2b:
			return aload1();
		case (byte) 0x2c:
			return aload2();
		case (byte) 0x2d:
			return aload3();
		case (byte) 0x2e:
			return iaload();
		case (byte) 0x2f:
			return laload();
		case (byte) 0x32:
			return aaload();
		case (byte) 0x33:
			return baload();
		case (byte) 0x34:
			return caload();
		case (byte) 0x35:
			return saload();
		case (byte) 0x36:
			return istore();
		case (byte) 0x37:
			return lstore();
		case (byte) 0x38:
			return fstore();
		case (byte) 0x39:
			return dstore();
		case (byte) 0x3a:
			return astore();
		case (byte) 0x3b:
			return istore_0();
		case (byte) 0x3c:
			return istore_1();
		case (byte) 0x3d:
			return istore_2();
		case (byte) 0x3e:
			return istore_3();
		case (byte) 0x3f:
			return lstore_0();
		case (byte) 0x40:
			return lstore_1();
		case (byte) 0x41:
			return lstore_2();
		case (byte) 0x42:
			return lstore_3();
		case (byte) 0x4b:
			return astore_0();
		case (byte) 0x4c:
			return astore_1();
		case (byte) 0x4d:
			return astore_2();
		case (byte) 0x4e:
			return astore_3();
		case (byte) 0x4f:
			return iastore();
		case (byte) 0x50:
			return lastore();
		case (byte) 0x53:
			return aastore();
		case (byte) 0x54:
			return bastore();
		case (byte) 0x55:
			return castore();
		case (byte) 0x56:
			return sastore();
		case (byte) 0x57:
			return pop();
		case (byte) 0x58:
			return pop2();
		case (byte) 0x59:
			return dup();
		case (byte) 0x5a:
			return dup_x1();
		case (byte) 0x5b:
			return dup_x2();
		case (byte) 0x5c:
			return dup2();
		case (byte) 0x5d:
			return dup2_x1();
		case (byte) 0x5e:
			return dup2_x2();
		case (byte) 0x5f:
			return swap();
		case (byte) 0x60:
			return iadd();
		case (byte) 0x61:
			return ladd();
		case (byte) 0x62:
			return fadd();
		case (byte) 0x63:
			return dadd();
		case (byte) 0x64:
			return isub();
		case (byte) 0x65:
			return lsub();
		case (byte) 0x66:
			return fsub();
		case (byte) 0x67:
			return dsub();
		case (byte) 0x68:
			return imul();
		case (byte) 0x6a:
			return fmul();
		case (byte) 0x6b:
			return dmul();
		case (byte) 0x6c:
			return idiv();
		case (byte) 0x6d:
			return ldiv();
		case (byte) 0x6e:
			return fdiv();
		case (byte) 0x6f:
			return ddiv();
		case (byte) 0x70:
			return irem();
		case (byte) 0x74:
			return ineg();
		case (byte) 0x78:
			return ishl();
		case (byte) 0x79:
			return lshl();
		case (byte) 0x7a:
			return ishr();
		case (byte) 0x7b:
			return lshr();
		case (byte) 0x7c:
			return iushr();
		case (byte) 0x7e:
			return iand();
		case (byte) 0x7f:
			return land();
		case (byte) 0x80:
			return ior();
		case (byte) 0x81:
			return lor();
		case (byte) 0x82:
			return ixor();
		case (byte) 0x83:
			return lxor();
		case (byte) 0x84:
			return iinc();
		case (byte) 0x85:
			return i2l();
		case (byte) 0x86:
			return i2f();
		case (byte) 0x87:
			return i2d();
		case (byte) 0x88:
			return l2i();
		case (byte) 0x8a:
			return l2d();
		case (byte) 0x8b:
			return f2i();
		case (byte) 0x8d:
			return f2d();
		case (byte) 0x8e:
			return d2i();
		case (byte) 0x91:
			return i2b();
		case (byte) 0x92:
			return i2c();
		case (byte) 0x94:
			return lcmp();
		case (byte) 0x95:
			return fcmpl();
		case (byte) 0x96:
			return fcmpg();
		case (byte) 0x99:
			return ifeq();
		case (byte) 0x9a:
			return ifne();
		case (byte) 0x9b:
			return iflt();
		case (byte) 0x9c:
			return ifge();
		case (byte) 0x9d:
			return ifgt();
		case (byte) 0x9e:
			return ifle();
		case (byte) 0x9f:
			return if_icmpeq();
		case (byte) 0xa0:
			return if_icmpne();
		case (byte) 0xa1:
			return if_icmplt();
		case (byte) 0xa2:
			return if_icmpge();
		case (byte) 0xa3:
			return if_icmpgt();
		case (byte) 0xa4:
			return if_icmple();
		case (byte) 0xa5:
			return if_acmpeq();
		case (byte) 0xa6:
			return if_acmpne();
		case (byte) 0xa7:
			return gotoOp();
		case (byte) 0xa8:
			return jsr();
		case (byte) 0xa9:
			return ret();
		case (byte) 0xaa:
			return tableSwitch();
		case (byte) 0xab:
			return lookupSwitch();
		case (byte) 0xac:
			return ireturn();
		case (byte) 0xad:
			return lreturn();
		case (byte) 0xae:
			return freturn();
		case (byte) 0xaf:
			return dreturn();
		case (byte) 0xb1:
			return returnOp();
		case (byte) 0xb3:
			return putstatic();
		case (byte) 0xb4:
			return getfield();
		case (byte) 0xb5:
			return putfield();
		case (byte) 0xb6:
			return invokevirtual();
		case (byte) 0xb7:
			return invokespecial();
		case (byte) 0xb8:
			return invokestatic();
		case (byte) 0xb9:
			return invokeinterface();
		case (byte) 0xbd:
			return anewarray();
		case (byte) 0xbe:
			return arraylength();
		case (byte) 0xbf:
			return athrow();
		case (byte) 0xb0:
			return areturn();
		case (byte) 0xb2:
			return getstatic();
		case (byte) 0xbb:
			return newOp();
		case (byte) 0xbc:
			return newArray();
		case (byte) 0xc0:
			return checkCast();
		case (byte) 0xc1:
			return instanceOf();
		case (byte) 0xc2:
			return monitorEnter();
		case (byte) 0xc3:
			return monitorExit();
		case (byte) 0xc6:
			return ifnull();
		case (byte) 0xc7:
			return ifnonnull();
		default:
			throw new IllegalStateException(String.format("Unknown opCode: %02x", opCode));
		}
	}

	private String nop() {
		return "nop";
	}

	private String tableSwitch() {
		int currentPosition = dis.getCurrentPosition() - 1;

		while (dis.getCurrentPosition() % 4 != 0) {
			if (dis.readByte() != (byte) 0x00)
				throw new IllegalStateException();
		}

		int defaultbyte1 = dis.read();
		int defaultbyte2 = dis.read();
		int defaultbyte3 = dis.read();
		int defaultbyte4 = dis.read();
		int defaultbyte = (defaultbyte1 << 24) | (defaultbyte2 << 16) | (defaultbyte3 << 8) | defaultbyte4;

		byte lowbyte1 = dis.readByte();
		byte lowbyte2 = dis.readByte();
		byte lowbyte3 = dis.readByte();
		byte lowbyte4 = dis.readByte();
		int lowbyte = (lowbyte1 << 24) | (lowbyte2 << 16) | (lowbyte3 << 8) | lowbyte4;

		byte highbyte1 = dis.readByte();
		byte highbyte2 = dis.readByte();
		byte highbyte3 = dis.readByte();
		byte highbyte4 = dis.readByte();
		int highbyte = (highbyte1 << 24) | (highbyte2 << 16) | (highbyte3 << 8) | highbyte4;

		int jumpAddress = defaultbyte;
		int npairs = highbyte - lowbyte + 1;
		JavaInteger index = (JavaInteger) operandStack.pop();
		for (int i = lowbyte; i < highbyte; i++) {
			// read offset
			int offset1 = dis.read();
			int offset2 = dis.read();
			int offset3 = dis.read();
			int offset4 = dis.read();
			int offset = (offset1 << 24) | (offset2 << 16) | (offset3 << 8) | offset4;
			if (index.intValue() == i) {
				jumpAddress = offset;
			}
		}

		int absolutePos = currentPosition + jumpAddress;
		if (absolutePos <= 0) {
			throw new IllegalStateException("The absolute jump position in bytecode cannot be negative.");
		}
		// log.debug("Jumping to absolute {}", absolutePos);
		dis.jumpTo(absolutePos);
		return "tableSwitch";
	}

	private String lookupSwitch() {
		// base position is the position of the lookupswitch opcode itself,
		// hence we need to subtract it from the current position
		int currentPosition = dis.getCurrentPosition() - 1;

		// Padding
		while (dis.getCurrentPosition() % 4 != 0) {
			if (dis.readByte() != (byte) 0x00)
				throw new IllegalStateException();
			// log.debug("Lookupswitch read a padding byte");
		}

		// Default jump address
		int defaultbyte1 = dis.read();
		int defaultbyte2 = dis.read();
		int defaultbyte3 = dis.read();
		int defaultbyte4 = dis.read();
		int defaultbyte = (defaultbyte1 << 24) | (defaultbyte2 << 16) | (defaultbyte3 << 8) | defaultbyte4;

		// Number of key-address pairs
		byte npairs1 = dis.readByte();
		byte npairs2 = dis.readByte();
		byte npairs3 = dis.readByte();
		byte npairs4 = dis.readByte();
		int npairs = (npairs1 << 24) | (npairs2 << 16) | (npairs3 << 8) | npairs4;

		// Set default jump address
		int relativeJumpAddress = defaultbyte;

		// Find matching key
		JavaInteger key = (JavaInteger) operandStack.pop();
		for (int i = 0; i < npairs; i++) {
			// read match key
			byte match1 = dis.readByte();
			byte match2 = dis.readByte();
			byte match3 = dis.readByte();
			byte match4 = dis.readByte();
			int match = (match1 << 24) | (match2 << 16) | (match3 << 8) | match4;

			// read offset
			int offset1 = dis.read();
			int offset2 = dis.read();
			int offset3 = dis.read();
			int offset4 = dis.read();
			int offset = (offset1 << 24) | (offset2 << 16) | (offset3 << 8) | offset4;

			// log.debug("Lookupswitch: [%d] => 0x%02x%02x%02x%02x = [%d]", new
			// Object[] { match, offset1, offset2,
			// offset3, offset4, offset });

			// Is -126 but should be 144
			if (key.intValue() == match) {
				// log.debug("Lookupswitch found match for key %s at position %d",
				// key.intValue(), offset);
				relativeJumpAddress = offset;
			}
		}

		int absolutePos = currentPosition + relativeJumpAddress;
		if (absolutePos <= 0) {
			throw new IllegalStateException("The absolute jump position in bytecode cannot be negative.");
		}
		// log.debug("Jumping to absolute %d", absolutePos);
		dis.jumpTo(absolutePos);
		return "lookupswitch";
	}

	/**
	 * iinc 0x84 Parameters: index, const Operand Stack: [No change]
	 * Description: increment local variable #index by signed byte const
	 * 
	 * @return
	 */
	private String iinc() {
		int localVarIndex = dis.read();
		int incrementValue = dis.readByte();
		LocalVariables localVariables = frame.getLocalVariables();
		JavaObject localVariable = localVariables.getLocalVariable(localVarIndex);
		if (localVariable instanceof JavaInteger) {
			JavaInteger oldValue = (JavaInteger) localVariable;
			JavaInteger newValue = new JavaInteger(oldValue.intValue() + incrementValue);
			localVariables.setLocalVariable(localVarIndex, newValue);
		} else {
			throw new UnsupportedOperationException();
		}
		return "iinc";
	}

	/**
	 * Int logical shift right.
	 * 
	 * value1, value2 > result
	 * 
	 * @return
	 */
	private String iushr() {
		JavaInteger value2 = (JavaInteger) operandStack.pop();
		JavaInteger value1 = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(value1.intValue() >>> value2.intValue()));
		return "iushr";
	}

	private String ishl() {
		JavaInteger value2 = (JavaInteger) operandStack.pop();
		JavaInteger value1 = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(value1.intValue() << value2.intValue()));
		return "ishl";
	}

	private String lshl() {
		JavaObject pop1 = operandStack.pop();
		JavaObject pop2 = operandStack.pop();
		if (pop1 instanceof JavaLong && pop2 instanceof JavaLong) {
			JavaLong value2 = (JavaLong) pop1;
			JavaLong value1 = (JavaLong) pop2;
			operandStack.push(new JavaLong(value1.longValue() << value2.longValue()));
		} else if (pop1 instanceof JavaInteger && pop2 instanceof JavaLong) {
			JavaInteger value2 = (JavaInteger) pop1;
			JavaLong value1 = (JavaLong) pop2;
			operandStack.push(new JavaLong(value1.longValue() << value2.intValue()));
		} else {
			throw new IllegalStateException();
		}
		return "lshl";
	}

	private String ishr() {
		JavaInteger value2 = (JavaInteger) operandStack.pop();
		JavaInteger value1 = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(value1.intValue() >> value2.intValue()));
		return "ishr";
	}

	private String lshr() {
		JavaLong value2 = (JavaLong) operandStack.pop();
		JavaLong value1 = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(value1.longValue() >> value2.longValue()));
		return "lshr";
	}

	private String irem() {
		JavaInteger first = (JavaInteger) operandStack.pop();
		JavaInteger second = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() / second.intValue()));
		return "irem";
	}

	private String ineg() {
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() * -1));
		return "ineg";
	}

	private String iand() {
		JavaInteger second = (JavaInteger) operandStack.pop();
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() & second.intValue()));
		return "iand";
	}

	private String ior() {
		JavaInteger second = (JavaInteger) operandStack.pop();
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() | second.intValue()));
		return "ior";
	}

	private String lor() {
		JavaLong second = (JavaLong) operandStack.pop();
		JavaLong first = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(first.longValue() | second.longValue()));
		return "lor";
	}

	private String ixor() {
		JavaInteger second = (JavaInteger) operandStack.pop();
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() ^ second.intValue()));
		return "ixor";
	}

	private String lxor() {
		JavaLong second = (JavaLong) operandStack.pop();
		JavaLong first = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(first.longValue() ^ second.longValue()));
		return "lxor";
	}

	private String land() {
		JavaLong second = (JavaLong) operandStack.pop();
		JavaLong first = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(first.longValue() & second.longValue()));
		return "iand";
	}

	private String caload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObject value = arrayref.get(index.intValue());
		operandStack.push(value);
		return "caload";
	}

	private String saload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObject value = arrayref.get(index.intValue());
		operandStack.push(value);
		return "saload";
	}

	private String baload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObject value = arrayref.get(index.intValue());
		operandStack.push(value);
		return "baload";
	}

	/**
	 * checks whether an objectref is of a certain type, the class reference of
	 * which
	 * 
	 * TODO: Shouldn't this include the type checking, instead of only "equals"
	 * ??
	 * 
	 * @return
	 */
	private String checkCast() {
		int index = readConstantPoolIndex();
		SymbolicClassReference classReference = frame.getRuntimeConstantPool().getSymbolicClassReference(index);
		String expectedClassName = classReference.getClassName();
		// TODO Auto-generated method stub
		JavaObjectReference toBeChecked = (JavaObjectReference) operandStack.peekFirst();
		if (toBeChecked instanceof JavaArray) {
			JavaArray javaArray = (JavaArray) toBeChecked;
			String arrayComponentClass = javaArray.getComponentType().getThisClassName();
			if (!arrayComponentClass.equals(expectedClassName)) {
				if (expectedClassName.startsWith("[L")) {
					String expectedComponentClass = expectedClassName.substring(2,expectedClassName.length()-1);
					if (!javaArray.getComponentType().isInstanceOf(rda, frame, expectedComponentClass)) {
						throw new ControlFlowException(exceptionFactory.createClassCastException(rda, frame, arrayComponentClass,
								expectedClassName));
					}
				}
				else if (!javaArray.getComponentType().isInstanceOf(rda, frame, expectedClassName)) {
					throw new ControlFlowException(exceptionFactory.createClassCastException(rda, frame, arrayComponentClass,
							expectedClassName));
				}
			}
		} else if (toBeChecked instanceof JavaNullReference) {
			// NULL can be cast to anything ...
		} else {
			if (!toBeChecked.getClassFile().isInstanceOf(rda, frame, expectedClassName)) {
				throw new ControlFlowException(exceptionFactory.createClassCastException(rda, frame, toBeChecked
						.getClassFile().getThisClassName(), expectedClassName));
			}
		}
		return "checkCast";
	}

	private String arraylength() {
		JavaArray arrayre = (JavaArray) operandStack.pop();
		operandStack.push(new JavaInteger(arrayre.length()));
		return "arraylength";
	}

	private String iastore() {
		JavaObject value = operandStack.pop();
		// TODO: 'value' can be JavaInteger, JavaShort etc.
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		arrayref.set(index.intValue(), value);
		return "iastore";
	}

	private String lastore() {
		JavaObject value = operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		if (value instanceof JavaInteger) {
			JavaInteger javaInteger = (JavaInteger) value;
			value = new JavaLong(javaInteger.intValue());
		}
		arrayref.set(index.intValue(), value);
		return "iastore";
	}

	private String isub() {
		JavaInteger second = (JavaInteger) operandStack.pop();
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() - second.intValue()));
		return "isub";
	}

	private String lsub() {
		JavaLong second = (JavaLong) operandStack.pop();
		JavaLong first = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(first.longValue() - second.longValue()));
		return "lsub";
	}

	private String fsub() {
		JavaFloat second = (JavaFloat) operandStack.pop();
		JavaFloat first = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaFloat(first.floatValue() - second.floatValue()));
		return "fsub";
	}

	private String dsub() {
		JavaDouble second = (JavaDouble) operandStack.pop();
		JavaDouble first = (JavaDouble) operandStack.pop();
		operandStack.push(new JavaDouble(first.doubleValue() - second.doubleValue()));
		return "dsub";
	}

	private String iadd() {
		JavaInteger second = (JavaInteger) operandStack.pop();
		JavaInteger first = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(first.intValue() + second.intValue()));
		return "iadd";
	}

	private String ladd() {
		JavaLong second = (JavaLong) operandStack.pop();
		JavaLong first = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(first.longValue() + second.longValue()));
		return "ladd";
	}

	private String fadd() {
		JavaFloat second = (JavaFloat) operandStack.pop();
		JavaFloat first = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaFloat(first.floatValue() + second.floatValue()));
		return "fadd";
	}

	private String dadd() {
		JavaDouble second = (JavaDouble) operandStack.pop();
		JavaDouble first = (JavaDouble) operandStack.pop();
		operandStack.push(new JavaDouble(first.doubleValue() + second.doubleValue()));
		return "dadd";
	}

	private String fmul() {
		JavaFloat left = (JavaFloat) operandStack.pop();
		JavaFloat right = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaFloat(right.floatValue() * left.floatValue()));
		return "fmul";
	}

	private String dmul() {
		JavaDouble left = (JavaDouble) operandStack.pop();
		JavaDouble right = (JavaDouble) operandStack.pop();
		operandStack.push(new JavaDouble(right.doubleValue() * left.doubleValue()));
		return "dmul";
	}

	private String imul() {
		JavaInteger left = (JavaInteger) operandStack.pop();
		JavaInteger right = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(right.intValue() * left.intValue()));
		// TODO: Overflow stuff?
		return "imul";
	}

	private String idiv() {
		JavaInteger left = (JavaInteger) operandStack.pop();
		JavaInteger right = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaInteger(right.intValue() / left.intValue()));
		return "idiv";
	}

	private String ldiv() {
		JavaLong left = (JavaLong) operandStack.pop();
		JavaLong right = (JavaLong) operandStack.pop();
		operandStack.push(new JavaLong(right.longValue() / left.longValue()));
		return "ldiv";
	}

	private String fdiv() {
		JavaFloat left = (JavaFloat) operandStack.pop();
		JavaFloat right = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaFloat(right.floatValue() / left.floatValue()));
		return "fdiv";
	}

	private String ddiv() {
		JavaDouble left = (JavaDouble) operandStack.pop();
		JavaDouble right = (JavaDouble) operandStack.pop();
		operandStack.push(new JavaDouble(right.doubleValue() / left.doubleValue()));
		return "ddiv";
	}

	private String i2f() {
		JavaInteger source = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaFloat(source.intValue()));
		return "i2f";
	}

	private String i2d() {
		JavaInteger source = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaDouble(source.intValue()));
		return "i2d";
	}

	private String l2i() {
		JavaLong source = (JavaLong) operandStack.pop();
		operandStack.push(new JavaInteger((int) source.longValue()));
		return "l2i";
	}

	private String i2c() {
		JavaInteger source = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaChar(source.intValue()));
		return "i2c";
	}

	private String i2b() {
		JavaInteger source = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaByte(source.intValue()));
		return "i2b";
	}

	private String i2l() {
		JavaInteger source = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaLong(source.intValue()));
		return "i2l";
	}

	private String f2i() {
		JavaFloat source = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaInteger((int) source.floatValue()));
		return "f2i";
	}

	private String f2d() {
		JavaFloat source = (JavaFloat) operandStack.pop();
		operandStack.push(new JavaDouble(source.floatValue()));
		return "f2d";
	}

	private String d2i() {
		JavaDouble source = (JavaDouble) operandStack.pop();
		operandStack.push(new JavaInteger((int) source.doubleValue()));
		return "d2i";
	}

	private String l2d() {
		JavaLong source = (JavaLong) operandStack.pop();
		operandStack.push(new JavaDouble((double) source.longValue()));
		return "l2d";
	}

	private String fcmpg() {
		JavaFloat right = (JavaFloat) operandStack.pop();
		JavaFloat left = (JavaFloat) operandStack.pop();
		int compareTo = left.compareTo(right);
		operandStack.push(new JavaInteger(compareTo));
		return "fcmpg\t\tleft=" + left + "\tright=" + right;
	}

	private String fcmpl() {
		JavaFloat right = (JavaFloat) operandStack.pop();
		JavaFloat left = (JavaFloat) operandStack.pop();
		int compareTo = left.compareTo(right);
		operandStack.push(new JavaInteger(compareTo));
		return "fcmpl\t\tleft=" + left + "\tright=" + right;
	}

	private String instanceOf() {
		int index = readConstantPoolIndex();
		SymbolicClassReference classReference = frame.getRuntimeConstantPool().getSymbolicClassReference(index);
		String className = classReference.getClassName();
		Classfile classfile = rda.loadClass(frame.getVirtualThread(), className);
		JavaObjectReference pop = (JavaObjectReference) operandStack.pop();
		if (pop.getClassFile().equals(classfile)) {
			operandStack.push(new JavaInteger(1));
		} else {
			operandStack.push(new JavaInteger(0));
		}
		return "instanceof";
	}

	/**
	 * Array Type atype T_BOOLEAN 4 T_CHAR 5 T_FLOAT 6 T_DOUBLE 7 T_BYTE 8
	 * T_SHORT 9 T_INT 10 T_LONG 11
	 * 
	 * @return
	 */
	private String newArray() {
		int primitiveType = dis.read();
		Classfile cf = null;
		switch (primitiveType) {
		case 4:
			cf = vm.classes().primitives().booleanClass();
			break;
		case 5:
			cf = vm.classes().primitives().charClass();
			break;
		case 6:
			cf = vm.classes().primitives().floatClass();
			break;
		case 7:
			cf = vm.classes().primitives().doubleClass();
			break;
		case 8:
			cf = vm.classes().primitives().byteClass();
			break;
		case 9:
			cf = vm.classes().primitives().shortClass();
			break;
		case 10:
			cf = vm.classes().primitives().intClass();
			break;
		case 11:
			cf = vm.classes().primitives().longClass();
			break;
		default:
			throw new UnsupportedOperationException("Unknown primitive type: " + primitiveType);
		}
		// TODO: Map primitiveType to a Java Primitive Type fake-classfile?
		JavaInteger size = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaArray(vm, cf, size.intValue()));
		return "newarray";
	}

	private String castore() {
		JavaInteger charValue = (JavaInteger) operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		arrayref.set(index.intValue(), charValue);
		return "castore";
	}

	private String sastore() {
		JavaInteger shortValue = (JavaInteger) operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		arrayref.set(index.intValue(), shortValue);
		return "sastore";
	}

	private String bastore() {
		JavaInteger byteValue = (JavaInteger) operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		arrayref.set(index.intValue(), byteValue);
		return "bastore";
	}

	private String aastore() {
		JavaObject value = operandStack.pop();
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		arrayref.set(index.intValue(), value);
		return "castore";
	}

	private String athrow() {
		return "athrow";
	}

	private String anewarray() {
		int classtypeIndex = readConstantPoolIndex();
		SymbolicClassReference symbolicClassReference = frame.getRuntimeConstantPool().getSymbolicClassReference(
				classtypeIndex);
		String className = symbolicClassReference.getClassName();
		Classfile arrayType = rda.loadClass(frame.getVirtualThread(), className);
		JavaInteger length = (JavaInteger) operandStack.pop();
		operandStack.push(new JavaArray(vm, arrayType, length.intValue()));
		return "anewarray";
	}

	private String multianewarray() {
		int classtypeIndex = readConstantPoolIndex();

		SymbolicClassReference symbolicClassReference = frame.getRuntimeConstantPool().getSymbolicClassReference(
				classtypeIndex);
		String className = symbolicClassReference.getClassName();
		Classfile arrayType = rda.loadClass(frame.getVirtualThread(), className);

		JavaArray javaArray = new JavaArray(vm, arrayType);
		int dimensions = dis.read();
		for (int i = 0; i < dimensions; i++) {
			JavaInteger dimCnt = (JavaInteger) operandStack.pop();
			javaArray.addDimension(dimCnt.intValue());
		}
		operandStack.push(javaArray);

		return "anewarray";
	}

	private String pop() {
		operandStack.pop();
		return "pop";
	}

	private String pop2() {
		JavaObject first = operandStack.pop();
		if (first instanceof JavaLong || first instanceof JavaDouble) {
			// TODO: Long and Double, as usual, take up two units on the stack
			// which totally sucks, so we have to implement a workaround here.
			return "pop2";
		}
		operandStack.pop();
		return "pop2";
	}

	private String sipush() {
		short readShort = dis.readShort();
		operandStack.push(new JavaShort(readShort));
		return "sipush\t" + readShort;
	}

	private String gotoOp() {
		int branchoffset = readBranchOffset();
		jumpRelative(branchoffset);
		return "goto\t" + branchoffset;
	}

	private String jsr() {
		int branchOffset = readBranchOffset();
		operandStack.push(new JavaAddress(dis.getCurrentPosition()));
		jumpRelative(branchOffset);
		return "jsr";
	}

	private String ret() {
		int localVar = dis.read();
		JavaAddress address = (JavaAddress) frame.getLocalVariables().getLocalVariable(localVar);
		dis.jumpTo(address.getAddress());
		return "ret";
	}

	private String iconst_2() {
		operandStack.push(new JavaInteger(2));
		return "iconst_2";
	}

	private String if_icmple() {
		int branchoffset = readBranchOffset();
		JavaInteger value2 = (JavaInteger) operandStack.pop();
		JavaInteger value1 = (JavaInteger) operandStack.pop();
		if (value1.compareTo(value2) <= 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmple";
	}

	private String if_acmpne() {
		int branchoffset = readBranchOffset();
		JavaObjectReference value1 = (JavaObjectReference) operandStack.pop();
		JavaObjectReference value2 = (JavaObjectReference) operandStack.pop();
		if (!value1.equals(value2)) {
			jumpRelative(branchoffset);
		}
		return "if_acmpne";
	}

	private String if_acmpeq() {
		int branchoffset = readBranchOffset();
		JavaObjectReference value1 = (JavaObjectReference) operandStack.pop();
		JavaObjectReference value2 = (JavaObjectReference) operandStack.pop();
		if (value1.equals(value2)) {
			jumpRelative(branchoffset);
		}
		return "if_acmpeq";
	}

	private String if_icmplt() {
		int branchoffset = readBranchOffset();
		Comparable value2 = (Comparable) operandStack.pop();
		Comparable value1 = (Comparable) operandStack.pop();
		if (value1.compareTo(value2) < 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmplt";
	}

	private String if_icmpne() {
		int branchoffset = readBranchOffset();
		Comparable value2 = (Comparable) operandStack.pop();
		Comparable value1 = (Comparable) operandStack.pop();
		if (value1.compareTo(value2) != 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmpne";
	}

	private static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	private String if_icmpeq() {
		int branchoffset = readBranchOffset();
		Comparable value2 = (Comparable) operandStack.pop();
		Comparable value1 = (Comparable) operandStack.pop();
		if (value1.compareTo(value2) == 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmpeq";
	}

	private int readBranchOffset() {
		int branchbyte1 = unsignedByteToInt(dis.readByte());
		int branchbyte2 = unsignedByteToInt(dis.readByte());
		int branchoffset = ((branchbyte1 << 8) + (branchbyte2 << 0));
		if (branchoffset == 0)
			throw new IllegalStateException("The branchoffset must not be 0");
		return (short) branchoffset;
	}

	private String if_icmpgt() {
		int branchoffset = readBranchOffset();
		Comparable value2 = (Comparable) operandStack.pop();
		Comparable value1 = (Comparable) operandStack.pop();
		if (value1.compareTo(value2) > 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmpgt";
	}

	private String if_icmpge() {
		int branchoffset = readBranchOffset();
		Comparable value2 = (Comparable) operandStack.pop();
		Comparable value1 = (Comparable) operandStack.pop();
		if (value1.compareTo(value2) >= 0) {
			jumpRelative(branchoffset);
		}
		return "if_icmple";
	}

	private String ireturn() {
		return "ireturn";
	}

	private String lreturn() {
		return "lreturn";
	}

	private String freturn() {
		return "freturn";
	}

	private String dreturn() {
		return "dreturn";
	}

	private String bipush() {
		int value = dis.read();
		operandStack.push(new JavaInteger(value));
		return "bipush";
	}

	/**
	 * getfield b4 index1, index2 objectref - value gets a field value of an
	 * object objectref, where the field is identified by field reference in the
	 * constant pool index (index1 << 8 + index2)
	 * 
	 * @return @
	 */
	private String getfield() {
		int index = readConstantPoolIndex();
		SymbolicFieldReference fieldRef = frame.getRuntimeConstantPool().getSymbolicFieldReference(index);

		Object pop = operandStack.pop();
		if (pop instanceof JavaObjectReference) {
			JavaObjectReference object = (JavaObjectReference) pop;
			JavaObject value = object.getValueOfField(fieldRef);
			// log.debug("getfield() '%s' of type %s in class %s with value '%s' on object '%s'",
			// fieldRef.getFieldName(),
			// fieldRef.getFieldType(), fieldRef.getClassName(), value, pop);
			if (value == null) {
				if (fieldRef.getFieldType().equals("I")) {
					operandStack.push(new JavaInteger(0));
				} else if (fieldRef.getFieldType().equals("Z")) {
					operandStack.push(new JavaInteger(0));
				} else if (fieldRef.getFieldType().equals("F")) {
					operandStack.push(new JavaFloat(0.0f));
				} else if (fieldRef.getFieldType().equals("D")) {
					operandStack.push(new JavaDouble(0.0f));
				} else if (fieldRef.getFieldType().equals("C")) {
					operandStack.push(new JavaInteger(0));
				} else if (fieldRef.getFieldType().startsWith("L")) {
					operandStack.push(vm.objects().nullReference());
				} else if (fieldRef.getFieldType().startsWith("[L")) {
					// NULL object array
					operandStack.push(vm.objects().nullReference());
				} else if (fieldRef.getFieldType().startsWith("[C")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().charClass()));
				} else if (fieldRef.getFieldType().startsWith("[B")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().byteClass()));
				} else if (fieldRef.getFieldType().startsWith("[I")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().intClass()));
				} else if (fieldRef.getFieldType().startsWith("[Z")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().booleanClass()));
				} else if (fieldRef.getFieldType().startsWith("[F")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().floatClass()));
				} else if (fieldRef.getFieldType().startsWith("[D")) {
					operandStack.push(new JavaArray(vm, vm.classes().primitives().doubleClass()));
				} else {
					throw new UnsupportedOperationException("Unknown default value for field type: "
							+ fieldRef.getFieldType());
				}
			} else {
				operandStack.push(value);
			}
		} else {
			throw new IllegalStateException(
					"Unable to get a field value from an object which is not stored as a JavaObjectReference in the operand stack: "
							+ pop);
		}
		return "getfield";
	}

	private String putfield() {
		int index = readConstantPoolIndex();

		RuntimeConstantPool runtimeConstantPool = frame.getRuntimeConstantPool();
		ConstantPool constantPool = runtimeConstantPool.getConstantPool();
		Object entry = constantPool.getEntry(index);
		String fieldName = null;
		if (entry instanceof ConstantUTF8Info) {
			ConstantUTF8Info utf8Info = constantPool.getUTF8Info(index);
			fieldName = utf8Info.getValue();
		} else if (entry instanceof ConstantFieldrefInfo) {
			ConstantFieldrefInfo constantFieldrefInfo = (ConstantFieldrefInfo) entry;
			int clazzIndex = constantFieldrefInfo.getClassIndex();
			int nameAndTypeIndex = constantFieldrefInfo.getNameAndTypeIndex();
			ConstantNameAndTypeInfo nameAndTypeInfo = constantPool.getNameAndTypeInfo(nameAndTypeIndex);
			int nameIndex = nameAndTypeInfo.getNameIndex();
			ConstantUTF8Info utf8 = constantPool.getUTF8Info(nameIndex);
			fieldName = utf8.getValue();
		} else {
			throw new IllegalStateException("Dont know how to get field name");
		}
		JavaObject value = operandStack.pop();
		JavaObject targetObject = operandStack.pop();
		log.debug("putfield() on field '{}' to value '{}' on object '{}'", new Object[] { fieldName, value,
				targetObject });
		JavaObjectReference targetObjectReference = (JavaObjectReference) targetObject;
		targetObjectReference.setValueOfField(fieldName, value);
		return "putfield";
	}

	/**
	 * ifnonnull c7 branchbyte1, branchbyte2 value if value is not null, branch
	 * to instruction at branchoffset (signed short constructed from unsigned
	 * bytes branchbyte1 << 8 + branchbyte2)
	 * 
	 * @return @
	 */
	private String ifnonnull() {
		int branchoffset = readBranchOffset();
		JavaObject value = operandStack.pop();
		if (!(value instanceof JavaNullReference)) {
			jumpRelative(branchoffset);
		}
		return "ifnonnull";
	}

	private String ifnull() {
		int branchoffset = readBranchOffset();
		JavaObject value = operandStack.pop();
		if (value instanceof JavaNullReference) {
			jumpRelative(branchoffset);
		}
		return "ifnull";
	}

	// TODO: Monitor
	private String monitorEnter() {
		Object monitor = operandStack.pop();
		return "monitorEnter";
	}

	// TODO: Monitor
	private String monitorExit() {
		Object monitor = operandStack.pop();
		return "monitorExit";
	}

	/**
	 * astore 3a objectref - stores a reference into local variable at index
	 * 
	 * @return @
	 */
	private String astore() {
		int idx = dis.read();
		JavaObject obj = operandStack.pop();
		frame.getLocalVariables().setLocalVariable(idx, obj);
		return "astore";
	}

	private String istore() {
		JavaInteger obj = (JavaInteger) operandStack.pop();
		int idx = dis.read();
		frame.getLocalVariables().setLocalVariable(idx, obj);
		return "istore";
	}

	private String lstore() {
		JavaLong obj = (JavaLong) operandStack.pop();
		int idx = dis.read();
		frame.getLocalVariables().setLocalVariable(idx, obj);
		return "lstore";
	}

	private String fstore() {
		JavaFloat obj = (JavaFloat) operandStack.pop();
		int idx = dis.read();
		frame.getLocalVariables().setLocalVariable(idx, obj);
		return "fstore";
	}

	private String dstore() {
		JavaDouble obj = (JavaDouble) operandStack.pop();
		int idx = dis.read();
		frame.getLocalVariables().setLocalVariable(idx, obj);
		return "dstore";
	}

	private String istore_0() {
		JavaInteger obj = (JavaInteger) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(0, obj);
		return "istore_0";
	}

	private String istore_1() {
		JavaInteger obj = (JavaInteger) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(1, obj);
		return "istore_1";
	}

	private String istore_2() {
		JavaInteger obj = (JavaInteger) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(2, obj);
		return "istore_2";
	}

	private String istore_3() {
		JavaInteger obj = (JavaInteger) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(3, obj);
		return "istore_3";
	}

	private String lstore_0() {
		JavaLong obj = (JavaLong) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(0, obj);
		return "lstore_0";
	}

	private String lstore_1() {
		JavaLong obj = (JavaLong) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(1, obj);
		return "lstore_1";
	}

	private String lstore_2() {
		JavaLong obj = (JavaLong) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(2, obj);
		return "lstore_2";
	}

	private String lstore_3() {
		JavaLong obj = (JavaLong) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(3, obj);
		return "lstore_3";
	}

	/**
	 * astore_0 4b objectref - stores a reference into local variable 0
	 * 
	 * @return
	 */
	private String astore_0() {
		JavaObjectReference obj = (JavaObjectReference) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(0, obj);
		return "astore_0";
	}

	/**
	 * astore_1 4c objectref - stores a reference into local variable 1
	 * 
	 * @return
	 */
	private String astore_1() {
		JavaObjectReference obj = (JavaObjectReference) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(1, obj);
		return "astore_1";
	}

	/**
	 * astore_2 4d objectref - stores a reference into local variable 2
	 * 
	 * @return
	 */
	private String astore_2() {
		JavaObjectReference obj = (JavaObjectReference) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(2, obj);
		return "astore_2";
	}

	/**
	 * astore_3 4e objectref - stores a reference into local variable 3
	 * 
	 * @return
	 */
	private String astore_3() {
		JavaObjectReference obj = (JavaObjectReference) operandStack.pop();
		frame.getLocalVariables().setLocalVariable(3, obj);
		return "astore_3";
	}

	/**
	 * dup 59 value - value, value duplicates the value on top of the stack
	 * 
	 * @return
	 */
	private String dup() {
		JavaObject value1 = operandStack.pop();
		operandStack.push(value1);
		operandStack.push(value1);
		return "dup";
	}

	/**
	 * Inserts a copy of the top value into the stack two values from the top.
	 * 
	 * value2, value1 > value1, value2, value1
	 * 
	 * @return
	 */
	private String dup_x1() {
		JavaObject value1 = operandStack.pop();
		JavaObject value2 = operandStack.pop();
		operandStack.push(value1);
		operandStack.push(value2);
		operandStack.push(value1);
		return "dup_x1";
	}

	private String dup_x2() {
		JavaObject value1 = operandStack.pop();
		JavaObject value2 = operandStack.pop();
		JavaObject value3 = operandStack.pop();
		operandStack.push(value1);
		operandStack.push(value2);
		operandStack.push(value3);
		operandStack.push(value1);
		return "dup_x2";
	}

	private String dup2() {
		JavaObject value1 = operandStack.pop();
		if (value1 instanceof JavaLong || value1 instanceof JavaDouble) {
			operandStack.push(value1);
			operandStack.push(value1);
			return "dup2";
		}
		JavaObject value2 = operandStack.pop();
		operandStack.push(value2);
		operandStack.push(value1);
		operandStack.push(value2);
		operandStack.push(value1);
		return "dup2";
	}

	private String dup2_x1() {
		// value3, {value2, value1} > {value2, value1}, value3, {value2, value1}
		JavaObject value1 = operandStack.pop();
		// TODO: Other behavior if value1 ist JavaLong or JavaDouble
		if (value1 instanceof JavaLong || value1 instanceof JavaDouble) {
			// value2 is ignored because it's 'the other half'
			JavaObject value3 = operandStack.pop();
			operandStack.push(value1);
			operandStack.push(value3);
			operandStack.push(value1);
			return "dup2_x1";
		}

		JavaObject value2 = operandStack.pop();
		JavaObject value3 = operandStack.pop();
		operandStack.push(value2);
		operandStack.push(value1);
		operandStack.push(value3);
		operandStack.push(value2);
		operandStack.push(value1);
		return "dup2_x1";
	}

	private String dup2_x2() {
		// {value4, value3}, {value2, value1}
		// => {value2, value1}, {value4, value3}, {value2, value1}
		JavaObject value1 = operandStack.pop();
		JavaObject value2 = operandStack.pop();
		JavaObject value3 = operandStack.pop();
		JavaObject value4 = operandStack.pop();
		operandStack.push(value2);
		operandStack.push(value1);
		operandStack.push(value4);
		operandStack.push(value3);
		operandStack.push(value2);
		operandStack.push(value1);
		return "dup2_x2";
	}

	private String swap() {
		JavaObject value1 = operandStack.pop();
		JavaObject value2 = operandStack.pop();
		operandStack.push(value1);
		operandStack.push(value2);
		return "swap";
	}

	/**
	 * new bb indexbyte1, indexbyte2
	 * 
	 * objectref creates new object of type identified by class reference in
	 * constant pool index (indexbyte1 << 8 + indexbyte2)
	 * 
	 * @return @
	 */
	private String newOp() {
		int index = readConstantPoolIndex();
		RuntimeConstantPool rcp = frame.getRuntimeConstantPool();
		ConstantPool constantPool = rcp.getConstantPool();
		Object entry = constantPool.getEntry(index);
		String className = null;
		if (entry instanceof SymbolicClassReference) {
			SymbolicClassReference classReference = rcp.getSymbolicClassReference(index);
			className = classReference.getClassName();
		} else if (entry instanceof ConstantUTF8Info) {
			ConstantUTF8Info constantUTF8Info = (ConstantUTF8Info) entry;
			className = constantUTF8Info.getValue();
		} else if (entry instanceof ConstantClassInfo) {
			ConstantClassInfo clazzInfo = (ConstantClassInfo) entry;
			ConstantUTF8Info utf8 = constantPool.getUTF8Info(clazzInfo.getIndex());
			className = utf8.getValue();
		} else {
			throw new UnsupportedOperationException("Dont know how to get classname ..." + entry.getClass().getName());
		}
		Classfile classfile = rda.loadClass(frame.getVirtualThread(), className);
		JavaObjectReference newInstance = new JavaObjectReference(classfile);
		operandStack.push(newInstance);
		return "new";
	}

	/**
	 * putstatic b3 indexbyte1, indexbyte2 value
	 * 
	 * set static field to value in a class, where the field is identified by a
	 * field reference index in constant pool (indexbyte1 << 8 + indexbyte2)
	 * 
	 * @return @
	 */
	private String putstatic() {
		int index = readConstantPoolIndex();
		SymbolicFieldReference staticFieldRef = frame.getRuntimeConstantPool().getSymbolicFieldReference(index);
		String className = staticFieldRef.getClassName();
		rda.loadClass(frame.getVirtualThread(), className);
		JavaObject newValue = operandStack.pop();
		frame.getRuntimeConstantPool().setValue(staticFieldRef.getFieldName(), newValue);
		return "putstatic\t" + staticFieldRef;
	}

	private String iflt() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() < 0) {
			jumpRelative(branchoffset);
		}
		return "iflt";
	}

	private String ifge() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() >= 0) {
			jumpRelative(branchoffset);
		}
		return "ifge";
	}

	private String ifgt() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() > 0) {
			jumpRelative(branchoffset);
		}
		return "ifgt";
	}

	/**
	 * ifle 9e branchbyte1, branchbyte2 value
	 * 
	 * if value is less than or equal to 0, branch to instruction at
	 * branchoffset (signed short constructed from unsigned bytes branchbyte1 <<
	 * 8 + branchbyte2)
	 * 
	 * @return @
	 */
	private String ifle() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() <= 0) {
			jumpRelative(branchoffset);
		}
		return "ifle";
	}

	private String ifeq() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() == 0) {
			jumpRelative(branchoffset);
		}
		return "ifeq";
	}

	private String ifne() {
		int branchoffset = readBranchOffset();
		JavaInteger value = (JavaInteger) operandStack.pop();
		if (value.intValue() != 0) {
			jumpRelative(branchoffset);
		}
		return "ifne";
	}

	/**
	 * compares two longs values
	 * 
	 * +1 if lower > top 0 if lower = top -1 if lower < top.
	 * 
	 * @return
	 */
	private String lcmp() {
		// x > 0 .. wenn das 2. pop() die 0 gibt
		JavaLong right = (JavaLong) operandStack.pop();
		JavaLong left = (JavaLong) operandStack.pop();
		int compareTo = left.compareTo(right);
		operandStack.push(new JavaInteger(compareTo));
		return "lcmp";
	}

	/**
	 * lconst_0 09 0L pushes the long 0 onto the stack
	 * 
	 * @return
	 */
	private String lconst_0() {
		operandStack.push(new JavaLong(0));
		return "lconst_0";
	}

	private String lconst_1() {
		operandStack.push(new JavaLong(1));
		return "lconst_1";
	}

	private String fconst_0() {
		operandStack.push(new JavaFloat(0.0f));
		return "fconst_0";
	}

	private String fconst_1() {
		operandStack.push(new JavaFloat(1.0f));
		return "fconst_1";
	}

	private String fconst_2() {
		operandStack.push(new JavaFloat(2.0f));
		return "fconst_2";
	}

	private String dconst_0() {
		operandStack.push(new JavaDouble(0.0d));
		return "dconst_0";
	}

	private String dconst_1() {
		operandStack.push(new JavaDouble(1.0d));
		return "dconst_1";
	}

	private String iconst_0() {
		operandStack.push(new JavaInteger(0));
		return "iconst_0";
	}

	private String iconst_1() {
		operandStack.push(new JavaInteger(1));
		return "iconst_1";
	}

	private String iconst_m1() {
		operandStack.push(new JavaInteger(-1));
		return "iconst_m1";
	}

	private String iconst_3() {
		operandStack.push(new JavaInteger(3));
		return "iconst_3";
	}

	private String iconst_4() {
		operandStack.push(new JavaInteger(4));
		return "iconst_4";
	}

	private String iconst_5() {
		operandStack.push(new JavaInteger(5));
		return "iconst_5";
	}

	private String invokeinterface() {
		int index = readConstantPoolIndex();

		int parameterCount = dis.read();
		int mustBeZero = dis.read();
		if (mustBeZero != 0)
			throw new IllegalStateException("JLS: The value of the fourth operand byte must always be zero");

		// A reference to "System.out#println()"
		RuntimeConstantPool runtimeConstantPool = frame.getRuntimeConstantPool();
		SymbolicMethodReference methodRef = runtimeConstantPool.getSymbolicMethodReference(index);
		// System.out - the one in which we invoke "printLn"

		if (log.isDebugEnabled()) {
			log.debug("Invoking interface method '{}' with {} parameters", methodRef.getFullName(), parameterCount);
		}

		LocalVariables variables = new LocalVariables(parameterCount);
		variables.populate(parameterCount, operandStack);

		JavaObjectReference targetObject = (JavaObjectReference) variables.getLocalVariable(0);

		if (targetObject instanceof JavaNullReference) {
			throw new ControlFlowException(exceptionFactory.createNullPointerException(rda, frame, targetObject));
		}

		Classfile classFile = targetObject.getClassFile();
		String className = classFile.getThisClassName();
		MethodInfo invokeMethodInfo;
		ClassArea classArea;
		while (true) {
			// log.debug("Looking up method in class '{}'", className);
			rda.loadClass(frame.getVirtualThread(), className);
			classArea = rda.getMethodArea().getClassArea(className);
			Classfile classfile = classArea.getClassfile();
			Methods methods = classfile.getMethods(vm);
			invokeMethodInfo = methods.getMethodInfo(methodRef);
			if (invokeMethodInfo == null) {
				// log.debug("Did not find method '{}' in '{}'",
				// methodRef.getFullName(), className);
			} else if (invokeMethodInfo.isAbstract()) {
				// log.debug("Found abstract method '{}' in '{}'",
				// invokeMethodInfo.getFullName(), className);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Found method '{}' in '{}'", invokeMethodInfo.getFullName(), className);
				}
				break;
			}
			className = classfile.getSuperClassName();
			if (className == null) {
				JavaObject peekFirst = operandStack.peekFirst();
				if (peekFirst instanceof JavaObjectReference) {
					JavaObjectReference javaObjectReference = (JavaObjectReference) peekFirst;
					className = javaObjectReference.getClassFile().getThisClassName();
				}
			}
			// log.debug("Trying super class '{}'",
			// classfile.getSuperClassName());
		}
		if (invokeMethodInfo == null || classArea == null) {
			throw new NoSuchMethodError(methodRef.getMethodName());
		}

		if (invokeMethodInfo.getParameterCount() != parameterCount - 1) {
			// -1 ==> do not count "this" object, which is counted in
			// 'parameterCount'
			// but not on methodInfo.getParameterCount() ...
			throw new IllegalStateException("Method parameter count does not match");
		}

		if (log.isTraceEnabled()) {
			log.trace("Invoking interface method '{}' with {} params", invokeMethodInfo.getFullName(),
					invokeMethodInfo.getParameterCount());
		}

		Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, classArea.getRuntimeConstantPool(),
				invokeMethodInfo);
		newFrame.getLocalVariables().populate(variables);
		invokeWithExceptionHandling(rda, invokeMethodInfo, newFrame);

		return "invokeinterface";
	}

	/**
	 * invokevirtual b6 indexbyte1, indexbyte2 objectref, [arg1, arg2, ...]
	 * invoke virtual method on object objectref, where the method is identified
	 * by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
	 * 
	 * @return @
	 */
	private String invokevirtual() {
		int index = readConstantPoolIndex();
		if (index < 0) {
			throw new IllegalStateException("The index in the constant pool must be positive, but is: " + index);
		}

		RuntimeConstantPool runtimeConstantPool = frame.getRuntimeConstantPool();
		SymbolicMethodReference methodRef = runtimeConstantPool.getSymbolicMethodReference(index);
		// System.out - the one in which we invoke "printLn"
		log.debug("Invoking virtual method '{}'", methodRef.getFullName());

		int parameterCount = methodRef.getParameterCount();
		LocalVariables variables = new LocalVariables(parameterCount);
		variables.populate(parameterCount + 1, operandStack);
		Classfile classfile = null;
		JavaObject targetObject = variables.getLocalVariable(0);
		JavaObjectReference targetObjectReference = null;
		if (targetObject instanceof JavaClassReference) {
			classfile = rda.loadClass(frame.getVirtualThread(), "java/lang/Class");
		} else if (targetObject instanceof JavaObjectReference) {
			targetObjectReference = (JavaObjectReference) targetObject;
			classfile = targetObjectReference.getClassFile();
		}

		if (targetObject instanceof JavaNullReference) {
			throw new ControlFlowException(exceptionFactory.createNullPointerException(rda, frame,
					targetObjectReference));
		}

		MethodInfo invokeMethodInfo = findMethodToInvoke(methodRef, classfile);

		log.debug("Invoking virtual method '{}' with {} params", invokeMethodInfo.getFullName(),
				invokeMethodInfo.getParameterCount());

		rda.loadClass(frame.getVirtualThread(), invokeMethodInfo.getClassName());
		ClassArea classArea = rda.getMethodArea().getClassArea(invokeMethodInfo);
		Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, classArea.getRuntimeConstantPool(),
				invokeMethodInfo);
		newFrame.getLocalVariables().populate(variables);

		invokeWithExceptionHandling(rda, invokeMethodInfo, newFrame);
		return "invokevirtual";
	}

	protected MethodInfo findMethodToInvoke(final SymbolicMethodReference methodRef, final Classfile originalClassfile)
			throws NoSuchMethodError {
		MethodInfo invokeMethodInfo;
		Classfile classfile = originalClassfile;
		while (true) {
			String className = classfile.getThisClassName();
			log.debug("Looking up method in class '{}'", className);
			Methods methods = classfile.getMethods(vm);
			invokeMethodInfo = methods.getMethodInfo(methodRef);
			if (invokeMethodInfo == null) {
				log.debug("Did not find method '{}' in '{}'", methodRef.getFullName(), className);
			} else if (invokeMethodInfo.isAbstract()) {
				log.debug("Found abstract method '{}' in '{}'", invokeMethodInfo.getFullName(), className);
			} else {
				log.debug("Found method '{}' in '{}'", invokeMethodInfo.getFullName(), className);
				break;
			}
			if (classfile.getSuperClassName() != null) {
				classfile = rda.loadClass(frame.getVirtualThread(), classfile.getSuperClassName());
				className = classfile.getThisClassName();
				log.debug("Trying super class '{}'", classfile.getThisClassName());
			} else {
				throw new NoSuchMethodError(methodRef.getMethodName());
			}
		}
		if (invokeMethodInfo == null) {
			throw new NoSuchMethodError(methodRef.getMethodName());
		}
		return invokeMethodInfo;
	}

	protected int readConstantPoolIndex() {
		int indexbyte1 = dis.read();
		int indexbyte2 = dis.read();
		int index = (indexbyte1 << 8) | indexbyte2;
		return index;
	}

	/**
	 * invokestatic b8 indexbyte1, indexbyte2 [arg1, arg2, ...] invoke a static
	 * method, where the method is identified by method reference index in
	 * constant pool (indexbyte1 << 8 + indexbyte2)
	 * 
	 * @return @
	 */
	private String invokestatic() {
		int index = readConstantPoolIndex();
		RuntimeConstantPool runtimeConstantPool = frame.getRuntimeConstantPool();
		SymbolicMethodReference methodRef = runtimeConstantPool.getSymbolicMethodReference(index);
		// System.out - the one in which we invoke "printLn"
		log.debug("Invoking static method '{}' on class '{}'", methodRef.getMethodName(), methodRef.getClassName());

		// if (methodRef.getClassName().equals("java/lang/System") &&
		// methodRef.getMethodName().equals("nullInputStream")) {
		// log.debug("Pushing NULL to special VM-internal methods for stdin null inputstream");
		// operandStack.push(new JavaNullReference());
		// return "invokestatic-ignored";
		// }

		Classfile classfile = rda.loadClass(frame.getVirtualThread(), methodRef.getClassName());

		int parameterCount = methodRef.getParameterCount();
		LocalVariables variables = new LocalVariables(parameterCount);
		variables.populate(parameterCount, operandStack);

		RuntimeDataArea rda = frame.getVirtualThread().getRuntimeDataArea();
		MethodArea globalMethodArea = rda.getMethodArea();
		ClassArea classArea = globalMethodArea.getClassArea(methodRef.getClassName());
		Methods methods = classfile.getMethods(vm);
		MethodInfo invokeMethodInfo = methods.getMethodInfo(methodRef);
		Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, classArea.getRuntimeConstantPool(),
				invokeMethodInfo);
		newFrame.getLocalVariables().populate(variables);
		invokeWithExceptionHandling(rda, invokeMethodInfo, newFrame);
		return "invokestatic\t" + methodRef.getMethodName();
	}

	public void invokeWithExceptionHandling(RuntimeDataArea rda, MethodInfo invokeMethodInfo, Frame newFrame) {

		try {

			FrameExit result;
			if (invokeMethodInfo.isNative()) {
				if (vm.hasReplacement(invokeMethodInfo)) {
					result = vm.executeReplacement(rda, newFrame, invokeMethodInfo);
				} else {
					log.debug("MethodInfo Fullname: {}", invokeMethodInfo.getFullName());
					if (invokeMethodInfo.getMethodName().equals("registerNatives")) {
						return;
					}
					String message = "Native Method: " + invokeMethodInfo.getFullName();
					log.error(message);
					throw new UnsupportedOperationException(message);
				}
			} else {
				result = newFrame.execute();
			}
			if (result.hasReturnValue()) {
				operandStack.push(result.getResult());
			}

		} catch (ControlFlowException cfe) {
			int currentAddress = dis.getCurrentPosition();
			log.warn("Catched exception in method '{}' at position {}", methodInfo.getFullName(), currentAddress);
			ExceptionHandler handler = exceptionTable.findHandler(rda, frame, currentAddress,
					frame.getRuntimeConstantPool(), cfe.getJavaException());
			if (handler != null) {
				log.debug("Exception Handler found: jumping to {}", handler.getHandlerProgramCounter());
				operandStack.push(cfe.getJavaException());
				dis.jumpTo(handler.getHandlerProgramCounter());
			} else {
				log.debug("Control flow exception, but no exception handler. Throwing up the stack...");
				throw cfe;
			}
		}
	}

	/**
	 * The index is an unsigned byte that must be a valid index into the runtime
	 * constant pool of the current class (§3.6). The runtime constant pool
	 * entry at index either must be a runtime constant of type int or float, or
	 * must be a symbolic reference to a string literal (§5.1).
	 * 
	 * If the runtime constant pool entry is a runtime constant of type int or
	 * float, the numeric value of that runtime constant is pushed onto the
	 * operand stack as an int or float, respectively.
	 * 
	 * Otherwise, the runtime constant pool entry must be a reference to an
	 * instance of class String representing a string literal (§5.1). A
	 * reference to that instance, value, is pushed onto the operand stack.
	 */
	private String ldc() {
		int index = dis.read();
		JavaObject object = frame.getRuntimeConstantPool().getValue(index);
		if (object instanceof JavaObjectReference || object instanceof JavaInteger || object instanceof JavaFloat) {
			operandStack.push(object);
			return "ldc";
		}
		throw new IllegalStateException(object.toString());
	}

	private String ldcw() {
		int index = readConstantPoolIndex();

		JavaObject object = frame.getRuntimeConstantPool().getValue(index);
		if (object instanceof JavaObjectReference || object instanceof JavaInteger || object instanceof JavaFloat) {
			operandStack.push(object);
			return "ldcw";
		}
		throw new IllegalStateException(object.toString());
	}

	private String ldc2w() {
		int index = readConstantPoolIndex();

		JavaObject object = frame.getRuntimeConstantPool().getValue(index);
		if (object instanceof JavaDouble || object instanceof JavaLong) {
			operandStack.push(object);
			return "ldc2w";
		}
		throw new IllegalStateException(object.toString());
	}

	/**
	 * The unsigned indexbyte1 and indexbyte2 are used to construct an index
	 * into the runtime constant pool of the current class (§3.6), where the
	 * value of the index is (indexbyte1 << 8) | indexbyte2. The runtime
	 * constant pool item at that index must be a symbolic reference to a field
	 * (§5.1), which gives the name and descriptor of the field as well as a
	 * symbolic reference to the class or interface in which the field is to be
	 * found. The referenced field is resolved (§5.4.3.2).
	 * 
	 * On successful resolution of the field, the class or interface that
	 * declared the resolved field is initialized (§5.5) if that class or
	 * interface has not already been initialized.
	 * 
	 * The value of the class or interface field is fetched and pushed onto the
	 * operand stack.
	 */
	private String getstatic() {
		int index = readConstantPoolIndex();

		ConstantPool thisConstantPool = frame.getRuntimeConstantPool().getConstantPool();
		ConstantFieldrefInfo constant = (ConstantFieldrefInfo) thisConstantPool.getFieldRef(index);
		ConstantClassInfo classInfoEntry = thisConstantPool.getClassInfoEntry(constant.getClassIndex());
		ConstantNameAndTypeInfo nameAndType = thisConstantPool.getNameAndTypeInfo(constant.getNameAndTypeIndex());
		String className = thisConstantPool.getUTF8Info(classInfoEntry.getIndex()).getValue();
		String fieldName = thisConstantPool.getUTF8Info(nameAndType.getNameIndex()).getValue();
		String fieldDescriptor = thisConstantPool.getUTF8Info(nameAndType.getDescriptorIndex()).getValue();

		if (fieldDescriptor.length() == 0) {
			throw new IllegalStateException();
		}

		String fieldType = fieldDescriptor;
		if (fieldDescriptor.endsWith(";")) {
			fieldType = fieldDescriptor.substring(1, fieldDescriptor.length() - 1);
		} else {
			// Primitive
		}

		rda.loadClass(frame.getVirtualThread(), className);

		RuntimeDataArea rda = frame.getVirtualThread().getRuntimeDataArea();
		MethodArea globalMethodArea = rda.getMethodArea();
		ClassArea otherClassArea = globalMethodArea.getClassArea(className);
		if (otherClassArea == null) {
			throw new IllegalStateException("Unable to load class area for class: " + className);
		}
		RuntimeConstantPool otherRCP = otherClassArea.getRuntimeConstantPool();

		JavaObject value = otherRCP.getValue(frame, fieldName, fieldDescriptor);

		log.debug("Retrieving static field '{}' with value '{}' of type '{}' in class '{}'", new Object[] { fieldName,
				value, fieldDescriptor, className });

		operandStack.push(value);
		return "getstatic";
	}

	private String areturn() {
		return "areturn";
	}

	private String aconst_null() {
		operandStack.push(vm.objects().nullReference());
		return "aconst_null";
	}

	private String returnOp() {
		return "return";
	}

	private String invokespecial() {
		int index = readConstantPoolIndex();

		RuntimeConstantPool runtimeConstantPool = frame.getRuntimeConstantPool();
		SymbolicMethodReference methodRef = runtimeConstantPool.getSymbolicMethodReference(index);

		int parameterCount = methodRef.getParameterCount();
		LocalVariables variables = new LocalVariables(parameterCount);
		variables.populate(parameterCount + 1, operandStack);

		String className = methodRef.getClassName();
		JavaObjectReference targetObject = (JavaObjectReference) variables.getLocalVariable(0);
		log.debug("Invoking special method '{}' in class '{}' on object '{}'", new Object[] { methodRef.getFullName(),
				className, targetObject });
		if (targetObject instanceof JavaNullReference) {
			throw new ControlFlowException(exceptionFactory.createNullPointerException(rda, frame, targetObject));
		}
		MethodArea methodArea = rda.getMethodArea();
		MethodInfo invokeMethodInfo = methodArea.getMethodInfo(vm, methodRef);
		Classfile superClassfile = targetObject.getClassFile();
		while (invokeMethodInfo == null) {
			// super.foo()
			superClassfile = rda.loadClass(frame.getVirtualThread(), superClassfile.getSuperClassName());
			invokeMethodInfo = superClassfile.getMethods(vm).getMethodInfo(methodRef);
		}
		ClassArea classArea = rda.getMethodArea().getClassArea(invokeMethodInfo);
		RuntimeConstantPool otherRCP = classArea.getRuntimeConstantPool();
		Frame newFrame = new Frame(vm, frame.getVirtualThread(), rda, otherRCP, invokeMethodInfo);
		newFrame.getLocalVariables().populate(variables);
		invokeWithExceptionHandling(rda, invokeMethodInfo, newFrame);
		return "invokespecial";
	}

	private String aload() {
		int indexbyte1 = dis.read();
		JavaObjectReference object = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(indexbyte1);
		operandStack.push(object);
		return "aload";
	}

	private String iload() {
		int indexbyte1 = dis.read();
		JavaInteger object = (JavaInteger) frame.getLocalVariables().getLocalVariable(indexbyte1);
		operandStack.push(object);
		return "iload";
	}

	private String iload0() {
		JavaInteger object = (JavaInteger) frame.getLocalVariables().getLocalVariable(0);
		operandStack.push(object);
		return "iload_0";
	}

	private String iload1() {
		JavaObject localVariable = frame.getLocalVariables().getLocalVariable(1);
		JavaInteger object = (JavaInteger) localVariable;
		operandStack.push(object);
		return "iload_1";
	}

	private String iload2() {
		JavaInteger object = (JavaInteger) frame.getLocalVariables().getLocalVariable(2);
		operandStack.push(object);
		return "iload_2";
	}

	private String iload3() {
		JavaInteger object = (JavaInteger) frame.getLocalVariables().getLocalVariable(3);
		operandStack.push(object);
		return "fload_3";
	}

	private String lload() {
		int indexbyte1 = dis.read();
		JavaObject localVariable = frame.getLocalVariables().getLocalVariable(indexbyte1);
		JavaLong longObject = (JavaLong) localVariable;
		operandStack.push(longObject);
		return "lload";
	}

	private String lload0() {
		JavaLong object = (JavaLong) frame.getLocalVariables().getLocalVariable(0);
		operandStack.push(object);
		return "lload_0";
	}

	private String lload1() {
		JavaLong object = (JavaLong) frame.getLocalVariables().getLocalVariable(1);
		operandStack.push(object);
		return "lload_1";
	}

	private String lload2() {
		JavaLong object = (JavaLong) frame.getLocalVariables().getLocalVariable(2);
		operandStack.push(object);
		return "lload_2";
	}

	private String lload3() {
		JavaLong object = (JavaLong) frame.getLocalVariables().getLocalVariable(3);
		operandStack.push(object);
		return "lload_3";
	}

	private String fload() {
		int indexbyte1 = dis.read();
		JavaFloat object = (JavaFloat) frame.getLocalVariables().getLocalVariable(indexbyte1);
		operandStack.push(object);
		return "fload";
	}

	private String fload0() {
		JavaFloat object = (JavaFloat) frame.getLocalVariables().getLocalVariable(0);
		operandStack.push(object);
		return "fload_0";
	}

	private String fload1() {
		JavaFloat object = (JavaFloat) frame.getLocalVariables().getLocalVariable(1);
		operandStack.push(object);
		return "fload_1";
	}

	private String fload2() {
		JavaFloat object = (JavaFloat) frame.getLocalVariables().getLocalVariable(2);
		operandStack.push(object);
		return "fload_2";
	}

	private String fload3() {
		JavaFloat object = (JavaFloat) frame.getLocalVariables().getLocalVariable(3);
		operandStack.push(object);
		return "fload_3";
	}

	private String dload0() {
		JavaDouble object = (JavaDouble) frame.getLocalVariables().getLocalVariable(0);
		operandStack.push(object);
		return "dload_0";
	}

	private String dload1() {
		JavaDouble object = (JavaDouble) frame.getLocalVariables().getLocalVariable(1);
		operandStack.push(object);
		return "dload_1";
	}

	private String dload2() {
		JavaDouble object = (JavaDouble) frame.getLocalVariables().getLocalVariable(2);
		operandStack.push(object);
		return "dload_2";
	}

	private String dload3() {
		JavaDouble object = (JavaDouble) frame.getLocalVariables().getLocalVariable(3);
		operandStack.push(object);
		return "dload_3";
	}

	/**
	 * loads a reference onto the stack from local variable 0
	 * 
	 * @return
	 */
	private String aload0() {
		JavaObject object = frame.getLocalVariables().getLocalVariable(0);
		operandStack.push(object);
		return "aload_0";
	}

	private String aload1() {
		JavaObjectReference object = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(1);
		operandStack.push(object);
		return "aload_1";
	}

	private String aload2() {
		JavaObjectReference object = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(2);
		operandStack.push(object);
		return "aload_2";
	}

	private String aload3() {
		JavaObjectReference object = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(3);
		operandStack.push(object);
		return "aload3";
	}

	private String iaload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray object = (JavaArray) operandStack.pop();
		JavaInteger intValue = (JavaInteger) object.get(index.intValue());
		operandStack.push(intValue);
		return "iaload";
	}

	private String laload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray object = (JavaArray) operandStack.pop();
		JavaObject javaObject = object.get(index.intValue());
		if (javaObject instanceof JavaLong) {
			JavaLong javaLong = (JavaLong) javaObject;
			operandStack.push(javaLong);
		} else if (javaObject instanceof JavaInteger) {
			// TODO: Sollte das überhaupt möglich sein?!
			JavaInteger javaInteger = (JavaInteger) javaObject;
			operandStack.push(new JavaLong(javaInteger.intValue()));
		} else {
			throw new IllegalStateException();
		}
		return "iaload";
	}

	private String aaload() {
		JavaInteger index = (JavaInteger) operandStack.pop();
		JavaArray arrayref = (JavaArray) operandStack.pop();
		JavaObject javaObject = arrayref.get(index.intValue());
		operandStack.push(javaObject);
		return "aaload";
	}

}
