package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Certain attributes are predefined as part of the class file specification.
 * The predefined attributes are the SourceFile (§4.7.7), ConstantValue
 * (§4.7.2), Code (§4.7.3), Exceptions (§4.7.4), InnerClasses (§4.7.5),
 * Synthetic (§4.7.6), LineNumberTable (§4.7.8), LocalVariableTable (§4.7.9),
 * and Deprecated (§4.7.10) attributes. Within the context of their use in this
 * specification, that is, in the attributes tables of the class file structures
 * in which they appear, the names of these predefined attributes are reserved.
 * 
 * @author mhaller
 */
public class Attributes {

	private final Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
	private final ConstantPool constantPool;

	public Attributes(ConstantPool constantPool, DataInputStream dis) throws IOException {
		this.constantPool = constantPool;
		int attributesCount = dis.readUnsignedShort();
		for (int i = 0; i < attributesCount; i++) {
			AttributeInfo attributeInfo = new AttributeInfo(constantPool, dis);
			ConstantUTF8Info strInfo = constantPool.getUTF8Info(attributeInfo.getNameIndex());
			attributes.put(strInfo.getValue(), attributeInfo);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (AttributeInfo info : attributes.values()) {
			if (sb.length() != 0)
				sb.append(",");
			sb.append(info.getAttributeName());
		}
		return sb.toString();
	}

	public int size() {
		return attributes.size();
	}

	// public AttributeInfo getAttributeInfo(int index) {
	// return attributes.get(index);
	// }

	public AttributeInfo getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public String getSourceFileAttribute(Classfile dc) {
		try {
			AttributeInfo attribute = getAttribute("SourceFile");
			int fileNameIndex = new DataInputStream(new ByteArrayInputStream(attribute.getValue())).readUnsignedShort();
			ConstantUTF8Info sourceFileName = dc.getConstantPool().getUTF8Info(fileNameIndex);
			return sourceFileName.getValue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RuntimeVisibleAnnotations getRuntimeVisibleAnnotations() {
		AttributeInfo attribute = getAttribute("RuntimeVisibleAnnotations");
		return new RuntimeVisibleAnnotations(constantPool, attribute);
	}

}
