package com.smartwerkz.bytecode.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.vm.VMLog;

/**
 * <pre>
 *    method_info {
 *     	u2 access_flags;
 *     	u2 name_index;
 *     	u2 descriptor_index;
 *     	u2 attributes_count;
 *     	attribute_info attributes[attributes_count];
 *     }
 * </pre>
 * 
 * @author mhaller
 * 
 */
public class MethodInfo {

	private static final VMLog log = new VMLog(MethodInfo.class.getName(), false);

	private final List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();
	private final ConstantPool constantPool;
	private final String className;

	private final int accessFlags;
	private final int nameIndex;
	private final int descriptorIndex;
	private final int attributesCount;
	private final String methodName;
	private final String descriptorName;
	private final int parameterCount;
	private final MethodAccessFlags methodAccessFlags;
	private final String fullName;
	private CodeAttribute codeAttribute;

	public MethodInfo(String className, ConstantPool constantPool, DataInputStream dis) throws IOException {
		this.className = className;
		this.constantPool = constantPool;
		accessFlags = dis.readUnsignedShort();
		methodAccessFlags = new MethodAccessFlags(accessFlags);

		nameIndex = dis.readUnsignedShort();
		methodName = constantPool.getUTF8Info(nameIndex).getValue();
		// log.debug("Reading method '%s' with access flags: %s", methodName,
		// methodAccessFlags.toString());

		descriptorIndex = dis.readUnsignedShort();
		descriptorName = constantPool.getUTF8Info(descriptorIndex).getValue();

		parameterCount = Descriptor.countParameters(descriptorName);
		// log.debug("Reading method '%s' descriptor with %d parameters: %s",
		// methodName, parameterCount, descriptorName);

		attributesCount = dis.readUnsignedShort();
		// log.debug("Reading %d attributes of method '%s'", attributesCount,
		// methodName);
		for (int i = 0; i < attributesCount; i++) {
			AttributeInfo attributeInfo = new AttributeInfo(constantPool, dis);
			attributes.add(attributeInfo);
			if (constantPool.getUTF8Info(attributeInfo.getNameIndex()).getValue().equals("Code")) {
				codeAttribute = new CodeAttribute(constantPool, attributeInfo.getValue());
			}
		}
		if (!methodAccessFlags.isNative() && !methodAccessFlags.isAbstract()) {
			if (codeAttribute == null) {
				throw new IllegalStateException("Unable to find 'Code' attribute for method: " + getFullName());
			}
		}

		fullName = String.format("%s#%s(%s)", className, methodName, descriptorName);
	}

	public String getMethodName() {
		return methodName;
	}

	public String getDescriptorName() {
		return descriptorName;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public CodeAttribute getCode() {
		return codeAttribute;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return String.format("Method '%s' with signature '%s' in class '%s'", methodName, descriptorName, className);
	}

	public boolean isStaticInitializer() {
		return methodName.equals("<clinit>");
	}

	public boolean isNative() {
		return methodAccessFlags.isNative();
	}

	public boolean isAbstract() {
		return methodAccessFlags.isAbstract();
	}

	public int getParameterCount() {
		return parameterCount;
	}

	public String getFullName() {
		return fullName;
	}

	public MethodAccessFlags getAccessFlags() {
		return methodAccessFlags;
	}

	public int getLineNumber(int programCounter) {
		LineNumberTable table = getCode().getLineNumberTable();
		if (table == null) {
			return -1;
		}
		return table.lineNumberForProgramCounter(programCounter);
	}

}
