package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaLong;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.os.FileDescriptors;

/**
 * Gets an "int": 0 for stdin 1 for stdout 2 for stderr
 * 
 * Returns a "handle" for each of those
 * 
 * @author mhaller
 * 
 */
public class FileDescriptorSetMethod implements NativeMethod {

	private final FileDescriptors fileDescriptors;

	public FileDescriptorSetMethod(FileDescriptors fileDescriptors) {
		this.fileDescriptors = fileDescriptors;

	}

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		JavaInteger fd = (JavaInteger)frame.getLocalVariables().getLocalVariable(0);
		int fdNumber = fd.intValue();
		switch (fdNumber) {
		case 0:
			operandStack.push(new JavaLong(fileDescriptors.getStdInHandle()));
			break;
		case 1:
			operandStack.push(new JavaLong(fileDescriptors.getStdOutHandle()));
			break;
		case 2:
			operandStack.push(new JavaLong(fileDescriptors.getStdErrHandle()));
			break;
		default:
			throw new RuntimeException("Unknown file descriptor ID: " + fd);
		}
	}

}
