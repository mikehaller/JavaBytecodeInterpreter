package com.smartwerkz.bytecode.vm.methods;

import java.io.IOException;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaByte;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.LocalVariables;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;

public class FileOutputStreamWriteBytesMethod implements NativeMethod {
	private final VMLog log = new VMLog(FileOutputStreamWriteBytesMethod.class.getSimpleName());

	/**
	 * Writes a sub array as a sequence of bytes.
	 * 
	 * @param b
	 *            the data to be written
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes that are written
	 * @exception IOException
	 *                If an I/O error has occurred.
	 */
	// private native void writeBytes(byte b[], int off, int len) throws
	// IOException;

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		LocalVariables lv = frame.getLocalVariables();
		JavaObjectReference thisFileOutputStream = (JavaObjectReference) lv.getLocalVariable(0);
		JavaArray byteArray = (JavaArray) lv.getLocalVariable(1);
		JavaInteger offset = (JavaInteger) lv.getLocalVariable(2);
		JavaInteger length = (JavaInteger) lv.getLocalVariable(3);

		StringBuilder output = new StringBuilder();
		for (int i = offset.intValue(); i < offset.intValue() + length.intValue(); i++) {
			JavaByte javaObject = (JavaByte) byteArray.get(i);
			if (javaObject != null) {
				output.append((char) javaObject.byteValue());
			} else {
				output.append("•");
			}
		}

		frame.getVirtualMachine().getScreen().print(output.toString());

		log.debug(">>> Output: [%s]", output.toString());
	}

}
