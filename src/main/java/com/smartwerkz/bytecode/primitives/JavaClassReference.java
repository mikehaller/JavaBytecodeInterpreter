package com.smartwerkz.bytecode.primitives;

import com.smartwerkz.bytecode.classfile.Classfile;

/**
 * Represents a java.lang.CLass object in the virtual virtual machine.
 * 
 * @author mhaller
 */
public class JavaClassReference extends JavaObjectReference {

	private final Classfile classfile2;

	/**
	 * DO NOT CALL FROM ANY OTHER PLACE THAN FROM "Classfile" implementations!
	 * To ensure that there are only one instances of JavaClassReference.
	 * 
	 * @param classfile
	 */
	public JavaClassReference(Classfile classfile) {
		super(classfile, false);
		classfile2 = classfile;
	}

	@Override
	public String asStringValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("Dump of Class:\n");
		sb.append(String.format(" Class Id  :\t%d\n", getInstanceId()));
		sb.append(String.format(" Class Name:\t%s\n", getClassFile().getThisClassName()));
		return sb.toString();
	}

	@Override
	public JavaObject copy() {
		return new JavaClassReference(classfile2);
	}

}
