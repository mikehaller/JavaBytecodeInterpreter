package com.smartwerkz.bytecode.vm.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.FieldInfo;
import com.smartwerkz.bytecode.classfile.Fields;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassGetDeclaredFieldsMethod implements NativeMethod {

	private final Map<String, JavaClassReference> mapping = new HashMap<String, JavaClassReference>();

	public ClassGetDeclaredFieldsMethod(VirtualMachine vm) {
		mapping.putAll(vm.classes().getMapping());
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		VirtualMachine vm = frame.getVirtualMachine();

		// TODO: Only the public fields if this flag is true
		JavaInteger booleanPublicOnly = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		Classfile classField = rda.loadClass(frame.getVirtualThread(), "java/lang/reflect/Field");

		// a "java.lang.Class" object
		JavaClassReference thisObject = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);

		Classfile thisClass = thisObject.getClassFile();
		Fields fields = thisClass.getFields();

		final List<FieldInfo> filtered = new ArrayList<FieldInfo>();
		for (FieldInfo fieldInfo : fields) {
			if (booleanPublicOnly.intValue() == 1 && fieldInfo.getAccessFlags().isPublic()) {
				filtered.add(fieldInfo);
			} else if (booleanPublicOnly.intValue() == 0) {
				filtered.add(fieldInfo);
			}
		}

		JavaArray javaArray = new JavaArray(vm, classField, fields.size());
		int i = 0;
		for (FieldInfo fieldInfo : filtered) {
			JavaObjectReference field = new JavaObjectReference(classField);
			{
				String fieldName = fieldInfo.getFieldName();
				field.setValueOfField("name",
						rda.vm().getStringPool().intern(new JavaString(vm, fieldName)));
				String descriptorName = fieldInfo.getDescriptorName();
				if (mapping.containsKey(descriptorName)) {
					JavaClassReference primitiveClazzRef = mapping.get(descriptorName);
					field.setValueOfField("type", primitiveClazzRef);
				} else if (descriptorName.startsWith("L")) {
					Classfile classfile = rda.loadClass(frame.getVirtualThread(), descriptorName.substring(1)
							.replaceAll(";", ""));
					JavaClassReference clazzRef = new JavaClassReference(classfile);
					field.setValueOfField("type", clazzRef);
				} else if (descriptorName.startsWith("[L")) {
					// TODO: Type for arrays
					Classfile classfile = rda.loadClass(frame.getVirtualThread(), descriptorName.substring(2)
							.replaceAll(";", ""));
					JavaClassReference clazzRef = new JavaClassReference(classfile);
					field.setValueOfField("type", clazzRef);
				}
				field.setValueOfField("modifiers", new JavaInteger(fieldInfo.getAccessFlags().intValue()));
				// TODO: More properties:
				// slot
				// clazz
				// signature
				// genericInfo
				// annotations
				// fieldAccessor
				// etc.
				javaArray.set(i++, field);
			}
		}

		// TODO: Create "Field" objects using
		// java.lang.reflect.ReflectAccess.newField(Class, String, Class, int,
		// int, String, byte[])

		operandStack.push(javaArray);
	}
}