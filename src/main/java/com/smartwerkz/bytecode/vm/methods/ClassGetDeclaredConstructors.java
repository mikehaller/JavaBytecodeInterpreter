package com.smartwerkz.bytecode.vm.methods;

import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.Descriptor;
import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.classfile.MethodInfo;
import com.smartwerkz.bytecode.classfile.Methods;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.BootstrapClassloader;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public class ClassGetDeclaredConstructors implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		VirtualMachine vm = frame.getVirtualMachine();
		
		// TODO: Only the public fields if this flag is true
		JavaInteger booleanPublicOnly = (JavaInteger) frame.getLocalVariables().getLocalVariable(1);
		Classfile classConstructor = rda.loadClass(frame.getVirtualThread(), "java/lang/reflect/Constructor");

		// a "java.lang.Class" object
		JavaClassReference thisObject = (JavaClassReference) frame.getLocalVariables().getLocalVariable(0);

		Classfile thisClass = thisObject.getClassFile();
		Methods methods = thisClass.getMethods(frame.getVirtualMachine());

		final List<MethodInfo> filtered = new ArrayList<MethodInfo>();
		for (MethodInfo methodInfo : methods.getAllMethods()) {
			if (booleanPublicOnly.intValue() == 1 && methodInfo.getAccessFlags().isPublic()) {
				filtered.add(methodInfo);
			} else if (booleanPublicOnly.intValue() == 0) {
				filtered.add(methodInfo);
			}
		}

		JavaArray javaArray = new JavaArray(vm,classConstructor, filtered.size());
		int i = 0;
		for (MethodInfo methodInfo : filtered) {
			JavaObjectReference ctor = new JavaObjectReference(classConstructor);
			{
				// this.clazz = declaringClass;
				// this.parameterTypes = parameterTypes;
				// this.exceptionTypes = checkedExceptions;
				// this.modifiers = modifiers;
				// this.slot = slot;
				// this.signature = signature;
				// this.annotations = annotations;
				// this.parameterAnnotations = parameterAnnotations;

				// TODO: Convert Descriptor to Class[]
				JavaArray paramTypes = convertToArray(vm, rda.getBootstrapClassLoader(), thisClass,
						methodInfo.getDescriptorName());

				ctor.setValueOfField("clazz", thisObject);
				ctor.setValueOfField("parameterTypes", paramTypes);
				ctor.setValueOfField("exceptionTypes", new JavaArray(vm,thisClass, 0));
				ctor.setValueOfField("modifiers", new JavaInteger(methodInfo.getAccessFlags().intValue()));
				ctor.setValueOfField("slot", new JavaInteger(0));
				ctor.setValueOfField("signature", vm.objects().nullReference());
				ctor.setValueOfField("annotations", vm.objects().nullReference());
				ctor.setValueOfField("parameterAnnotations", vm.objects().nullReference());

				javaArray.set(i++, ctor);
			}
		}

		// TODO: Create "Constructor" objects using
		// java.lang.reflect.ReflectAccess

		operandStack.push(javaArray);
	}

	private JavaArray convertToArray(VirtualMachine vm, BootstrapClassloader bcl, Classfile thisClass, String descriptorName) {
		final List<String> classNames = Descriptor.toClassNames(vm,descriptorName);
		final JavaArray javaArray = new JavaArray(vm,new LazyClassfile("[Ljava/lang/Class;"), classNames.size());
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