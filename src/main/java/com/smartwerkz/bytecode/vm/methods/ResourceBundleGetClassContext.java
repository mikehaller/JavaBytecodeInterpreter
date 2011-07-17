package com.smartwerkz.bytecode.vm.methods;

import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

/**
 * ResourceBundle#getClassContext() is a special method which looks up the call
 * stack to find the caller of the method.
 * 
 * <p>
 * This may well create some strange problems in our virtual VM.
 * 
 * @author mhaller
 */
public class ResourceBundleGetClassContext implements NativeMethod {

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		final List<Frame> frames = frame.getVirtualThread().getStack().getFrames();
		final JavaArray javaArray = new JavaArray(rda.vm(), rda.vm().classes().classClass(), frames.size());
		int i = 0;
		for (Frame theFrame : frames) {
			javaArray.set(i++, theFrame.getThisClass().getAsJavaClassReference());
		}
		operandStack.push(javaArray);
	}

}
