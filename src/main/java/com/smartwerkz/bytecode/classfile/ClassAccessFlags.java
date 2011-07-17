package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <pre>
 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
 * ACC_FINAL 	0x0010 	Declared final; no subclasses allowed.
 * ACC_SUPER 	0x0020 	Treat superclass methods specially when invoked by the invokespecial instruction.
 * ACC_INTERFACE 	0x0200 	Is an interface, not a class.
 * ACC_ABSTRACT 	0x0400 	Declared abstract; may not be instantiated.
 * </pre>
 * 
 * @author mhaller
 */
public class ClassAccessFlags {

	private static final int ACC_PUBLIC = 0x0001;
	private static final int ACC_FINAL = 0x0010;
	private static final int ACC_SUPER = 0x0020;
	private static final int ACC_INTERFACE = 0x0200;
	private static final int ACC_ABSTRACT = 0x0400;

	private int accessFlags;

	public ClassAccessFlags(DataInputStream dis) throws IOException {
		accessFlags = dis.readUnsignedShort();
	}

	public ClassAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isPublic()) sb.append(" public");
		if (isAbstract()) sb.append(" abstract");
		if (isFinal()) sb.append(" final");
		if (isSuper()) sb.append(" super");
		if (isInterface()) sb.append(" interface");
		return sb.toString().trim();
	}
	
	public boolean isPublic() {
		return (accessFlags & ACC_PUBLIC) == ACC_PUBLIC;
	}

	public boolean isFinal() {
		return (accessFlags & ACC_FINAL) == ACC_FINAL;
	}

	public boolean isSuper() {
		return (accessFlags & ACC_SUPER) == ACC_SUPER;
	}

	public boolean isInterface() {
		return (accessFlags & ACC_INTERFACE) == ACC_INTERFACE;
	}

	public boolean isAbstract() {
		return (accessFlags & ACC_ABSTRACT) == ACC_ABSTRACT;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

}
