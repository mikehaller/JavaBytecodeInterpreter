package com.smartwerkz.bytecode.classfile;

import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public interface Classfile {

	String getThisClassName();
	Methods getMethods(VirtualMachine vm);
	Fields getFields();
	ConstantPool getConstantPool();
	String getSuperClassName();
	ClassAccessFlags getAccessFlags();
	JavaClassReference getAsJavaClassReference();
	boolean isInstanceOf(RuntimeDataArea rda, Frame frame, String className);
	List<Classfile> getParentClasses(RuntimeDataArea rda, Frame frame);
	Attributes getAttributes();
}
