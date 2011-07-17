package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  RuntimeVisibleAnnotations_attribute {
 *         u2 attribute_name_index;
 *         u4 attribute_length;
 *         u2 num_annotations;
 *         annotation annotations[num_annotations];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class RuntimeVisibleAnnotations {

	private final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>();

	public RuntimeVisibleAnnotations(ConstantPool cp, AttributeInfo attribute) {
		if (!attribute.getAttributeName().equals("RuntimeVisibleAnnotations")) {
			throw new IllegalArgumentException(attribute.getAttributeName());
		}

		ByteArrayInputStream is = new ByteArrayInputStream(attribute.getValue());
		DataInputStream dis = new DataInputStream(is);
		try {
			int num = dis.readUnsignedShort();
			for (int i = 0; i < num; i++) {
				AnnotationInfo annotInfo = new AnnotationInfo(cp, dis);
				annotations.add(annotInfo);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return annotations.toString();
	}
	
}
