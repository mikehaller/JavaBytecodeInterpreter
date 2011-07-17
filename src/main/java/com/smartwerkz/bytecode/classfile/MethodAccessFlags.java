package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
 * ACC_PRIVATE 	0x0002 	Declared private; accessible only within the defining class.
 * ACC_PROTECTED 	0x0004 	Declared protected; may be accessed within subclasses.
 * ACC_STATIC 	0x0008 	Declared static.
 * ACC_FINAL 	0x0010 	Declared final; may not be overridden.
 * ACC_SYNCHRONIZED 	0x0020 	Declared synchronized; invocation is wrapped in a monitor lock.
 * ACC_NATIVE 	0x0100 	Declared native; implemented in a language other than Java.
 * ACC_ABSTRACT 	0x0400 	Declared abstract; no implementation is provided.
 * ACC_STRICT 	0x0800 	Declared strictfp; floating-point mode is FP-strict
 * </pre>
 * 
 * @author mhaller
 */
public class MethodAccessFlags {

	private static final int ACC_PUBLIC = 0x0001;
	private static final int ACC_PRIVATE = 0x0002;
	private static final int ACC_PROTECTED = 0x0004;
	private static final int ACC_STATIC = 0x0008;
	private static final int ACC_FINAL = 0x0010;
	private static final int ACC_SYNCHRONIZED = 0x0020;
	private static final int ACC_NATIVE = 0x0100;
	private static final int ACC_ABSTRACT = 0x0400;
	private static final int ACC_STRICT = 0x0800;

	private int accessFlags;

	public MethodAccessFlags(DataInputStream dis) throws IOException {
		accessFlags = dis.readUnsignedShort();
	}

	public MethodAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[0x");
		sb.append(Integer.toHexString(accessFlags));
		sb.append("]");
		
		if (isPublic())
			sb.append(" public");
		if (isPrivate())
			sb.append(" private");
		if (isProtected())
			sb.append(" protected");
		if (isStatic())
			sb.append(" static");
		if (isFinal())
			sb.append(" final");
		if (isSynchronized())
			sb.append(" synchronized");
		if (isNative())
			sb.append(" native");
		if (isAbstract())
			sb.append(" abstract");
		if (isStrict())
			sb.append(" strict");
		return sb.toString().trim();
	}

	public boolean isPublic() {
		return (accessFlags & ACC_PUBLIC) == ACC_PUBLIC;
	}

	public boolean isPrivate() {
		return (accessFlags & ACC_PRIVATE) == ACC_PRIVATE;
	}

	public boolean isProtected() {
		return (accessFlags & ACC_PROTECTED) == ACC_PROTECTED;
	}

	public boolean isStatic() {
		return (accessFlags & ACC_STATIC) == ACC_STATIC;
	}

	public boolean isFinal() {
		return (accessFlags & ACC_FINAL) == ACC_FINAL;
	}

	public boolean isSynchronized() {
		return (accessFlags & ACC_SYNCHRONIZED) == ACC_SYNCHRONIZED;
	}

	public boolean isNative() {
		return (accessFlags & ACC_NATIVE) == ACC_NATIVE;
	}

	public boolean isAbstract() {
		return (accessFlags & ACC_ABSTRACT) == ACC_ABSTRACT;
	}

	public boolean isStrict() {
		return (accessFlags & ACC_STRICT) == ACC_STRICT;
	}

	public int intValue() {
		return accessFlags;
	}

}
