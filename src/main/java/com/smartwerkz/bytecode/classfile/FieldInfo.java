package com.smartwerkz.bytecode.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaDouble;
import com.smartwerkz.bytecode.primitives.JavaFloat;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * <pre>
 *    field_info {
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
public class FieldInfo {

	private final VMLog log = new VMLog(FieldInfo.class.getName(), false);

	private int accessFlags;
	private int nameIndex;
	private int descriptorIndex;
	private int attributesCount;
	private final List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();
	private String fieldName;
	private String descriptorName;

	private JavaObject value;

	private final ConstantPool constantPool;

	private String signatureWithGenerics;

	private FieldAccessFlags fieldAccessFlags;

	public FieldInfo(ConstantPool constantPool, DataInputStream dis) throws IOException {
		this.constantPool = constantPool;
		accessFlags = dis.readUnsignedShort();
		fieldAccessFlags = new FieldAccessFlags(accessFlags);
		nameIndex = dis.readUnsignedShort();
		descriptorIndex = dis.readUnsignedShort();
		attributesCount = dis.readUnsignedShort();
		for (int i = 0; i < attributesCount; i++) {
			AttributeInfo info = new AttributeInfo(constantPool, dis);
			attributes.add(info);
		}

		fieldName = constantPool.getUTF8Info(nameIndex).getValue();
		descriptorName = constantPool.getUTF8Info(descriptorIndex).getValue();
	}

	public String getFieldName() {
		return fieldName;
	}

	public FieldAccessFlags getAccessFlags() {
		return fieldAccessFlags;
	}

	public String getDescriptorName() {
		return descriptorName;
	}

	public void initialize(RuntimeDataArea rda) throws IOException {
		log.debug("Initializing field '%s' of type '%s' with %d attributes", fieldName, descriptorName, attributesCount);
		VirtualMachine vm = rda.vm();
		for (AttributeInfo info : attributes) {
			if (info.getAttributeName().equals("ConstantValue")) {
				int index = new DataInputStream(new ByteArrayInputStream(info.getValue())).readUnsignedShort();
				log.debug("Looking up value from constant pool at index %d", index);
				Object valueInfo = constantPool.getEntry(index);
				if (valueInfo instanceof ConstantDoubleInfo) {
					ConstantDoubleInfo constantDoubleInfo = (ConstantDoubleInfo) valueInfo;
					value = new JavaDouble(constantDoubleInfo.getValue());
				} else if (valueInfo instanceof ConstantLongInfo) {
					ConstantLongInfo constant = (ConstantLongInfo) valueInfo;
					value = new JavaLong(constant.getValue());
				} else if (valueInfo instanceof ConstantFloatInfo) {
					ConstantFloatInfo constant = (ConstantFloatInfo) valueInfo;
					value = new JavaFloat(constant.getValue());
				} else if (valueInfo instanceof ConstantIntegerInfo) {
					ConstantIntegerInfo constant = (ConstantIntegerInfo) valueInfo;
					value = new JavaInteger(constant.getValue());
				} else if (valueInfo instanceof ConstantStringInfo) {
					ConstantStringInfo constant = (ConstantStringInfo) valueInfo;
					ConstantUTF8Info utf8Info = constantPool.getUTF8Info(constant.getStringIndex());
					value = new JavaString(vm, utf8Info.getValue());
				} else {
					throw new IllegalStateException("Dont know how to initialize: " + valueInfo.getClass().getName());
				}
			} else if (info.getAttributeName().equals("Signature")) {
				int index = new DataInputStream(new ByteArrayInputStream(info.getValue())).readUnsignedShort();
				log.debug("Looking up value from constant pool at index %d", index);
				Object valueInfo = constantPool.getEntry(index);
				ConstantUTF8Info c = (ConstantUTF8Info) valueInfo;
				// http://www.fh-wedel.de/~si/seminare/ws07/Ausarbeitung/05.jvm/jvm3.htm#signatures
				signatureWithGenerics = c.getValue();
				log.debug("Signature of field is '%s'", signatureWithGenerics);
			} else if (info.getAttributeName().equals("Synthetic")) {
				log.debug("Unknown field-info attribute '%s'", info.getAttributeName());
			} else if (info.getAttributeName().equals("Deprecated")) {
				log.debug("Unknown field-info attribute '%s'", info.getAttributeName());
			} else if (info.getAttributeName().equals("RuntimeVisibleAnnotations")) {
				log.debug("Unknown field-info attribute '%s'", info.getAttributeName());
			} else if (info.getAttributeName().equals("RuntimeInvisibleAnnotations")) {
				log.debug("Unknown field-info attribute '%s'", info.getAttributeName());
			} else {
				log.debug("Unknown field-info attribute '%s'", info.getAttributeName());
			}
		}

		// TODO Auto-generated method stub
		// Set value to Default Value for its type.
		// e.g. "boolean" to "false" etc.
		// These are the "static" fields!!
	}

	public void setValue(JavaObject newValue) {
		value = newValue;
	}

	@Override
	public String toString() {
		return "Field[" + fieldName + "][" + descriptorName + "]";
	}
}
