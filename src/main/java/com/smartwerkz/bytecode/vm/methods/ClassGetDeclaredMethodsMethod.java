package com.smartwerkz.bytecode.vm.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.Descriptor;
import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.BootstrapClassloader;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassGetDeclaredMethodsMethod implements NativeMethod {

	private final Map<String, JavaClassReference> mapping = new HashMap<String, JavaClassReference>();

	public ClassGetDeclaredMethodsMethod(VirtualMachine vm) {
		mapping.putAll(vm.classes().getMapping());
	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		VirtualMachine vm = frame.getVirtualMachine();

		// TODO: Only the public fields if this flag is true
		JavaInteger booleanPublicOnly = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		Classfile classField = rda.loadClass(frame.getVirtualThread(), "java/lang/reflect/Method");

		// a "java.lang.Class" object
		JavaClassReference thisObject = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);

		Classfile thisClass = thisObject.getClassFile();
		Methods methods = thisClass.getMethods(frame.getVirtualMachine());

		final List<MethodInfo> filtered = new ArrayList<MethodInfo>();
		for (MethodInfo methodInfo : methods) {
			if (booleanPublicOnly.intValue() == 1 && methodInfo.getAccessFlags().isPublic()) {
				filtered.add(methodInfo);
			} else if (booleanPublicOnly.intValue() == 0) {
				filtered.add(methodInfo);
			}
		}

		JavaArray javaArray = new JavaArray(vm, classField, methods.size());
		int i = 0;
		for (MethodInfo methodInfo : filtered) {
			JavaObjectReference field = new JavaObjectReference(classField);
			{
				String methodName = methodInfo.getMethodName();
				field.setValueOfField("name", rda.vm().getStringPool().intern(new JavaString(vm, methodName)));
				String descriptorName = methodInfo.getDescriptorName();

				// TODO: Auslagern, redundanz!
				if (mapping.containsKey(descriptorName)) {
					JavaClassReference primitiveClazzRef = mapping.get(descriptorName);
					field.setValueOfField("returnType", primitiveClazzRef);
				} else if (descriptorName.startsWith("L")) {
					Classfile classfile = rda.loadClass(frame.getVirtualThread(), descriptorName.substring(1)
							.replaceAll(";", ""));
					JavaClassReference clazzRef = new JavaClassReference(classfile);
					field.setValueOfField("returnType", clazzRef);
				} else if (descriptorName.startsWith("[L")) {
					// TODO: Type for arrays
					Classfile classfile = rda.loadClass(frame.getVirtualThread(), descriptorName.substring(2)
							.replaceAll(";", ""));
					JavaClassReference clazzRef = new JavaClassReference(classfile);
					field.setValueOfField("returnType", clazzRef);
				}

				JavaArray paramTypes = convertToArray(vm, rda.getBootstrapClassLoader(), thisClass,
						methodInfo.getDescriptorName());
				field.setValueOfField("parameterTypes", paramTypes);

				field.setValueOfField("modifiers", new JavaInteger(methodInfo.getAccessFlags().intValue()));
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

	private JavaArray convertToArray(VirtualMachine vm, BootstrapClassloader bcl, Classfile thisClass,
			String descriptorName) {
		final List<String> classNames = Descriptor.toClassNames(vm, descriptorName);
		final JavaArray javaArray = new JavaArray(vm, new LazyClassfile("[Ljava/lang/Class;"), classNames.size());
		int i = 0;
		for (String clazzName : classNames) {
			// TODO: Use the ClassLoader to actually load the classes here!
			Classfile cf = bcl.load(clazzName);
			JavaClassReference classObject = new JavaClassReference(cf);
			javaArray.set(0, classObject);
			i++;
		}
		return javaArray;
	}

}