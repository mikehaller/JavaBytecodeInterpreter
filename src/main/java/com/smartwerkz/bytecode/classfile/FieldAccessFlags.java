package com.smartwerkz.bytecode.classfile;

public class FieldAccessFlags {

	// ACC_PUBLIC 0x0001 Declared public; may be accessed from outside its
	// package.
	// ACC_PRIVATE 0x0002 Declared private; usable only within the defining
	// class.
	// ACC_PROTECTED 0x0004 Declared protected; may be accessed within
	// subclasses.
	// ACC_STATIC 0x0008 Declared static.
	// ACC_FINAL 0x0010 Declared final; no further assignment after
	// initialization.
	// ACC_VOLATILE 0x0040 Declared volatile; cannot be cached.
	// ACC_TRANSIENT 0x0080 Declared transient; not written or read by a
	// persistent object manager.
	private static final int ACC_PUBLIC = 0x0001;
	private static final int ACC_PRIVATE = 0x0002;
	private static final int ACC_PROTECTED = 0x0004;
	private static final int ACC_STATIC = 0x00008;
	private static final int ACC_FINAL = 0x0010;
	private static final int ACC_VOLATILE = 0x0040;
	private static final int ACC_TRANSIENT = 0x0080;
	private final int accessFlags;

	public FieldAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
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
	public boolean isVolatile() {
		return (accessFlags & ACC_VOLATILE) == ACC_VOLATILE;
	}
	public boolean isTransient() {
		return (accessFlags & ACC_TRANSIENT) == ACC_TRANSIENT;
	}
	public int intValue() {
		return accessFlags;
	}
}
