package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public final class GetFileSystemMethod implements NativeMethod {
	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		// TODO: Give it our own?
		String fsName = "com/smartwerkz/bytecode/vm/os/JavaFileSystem";
		Classfile cf = rda.loadClass(frame.getVirtualThread(), fsName);
		operandStack.push(new JavaObjectReference(cf));
	}
}