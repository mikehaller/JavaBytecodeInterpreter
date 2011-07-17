package com.smartwerkz.bytecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaNullReference;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;
import com.smartwerkz.bytecode.vm.methods.FillInStackTraceMethod;

public class ExceptionFactory {
	private static final Logger log = LoggerFactory.getLogger(ExceptionFactory.class);

	protected JavaObjectReference newExceptionObjectWithFilledStacktrace(RuntimeDataArea rda, Frame frame, Classfile cf) {
		JavaObjectReference exception = new JavaObjectReference(cf);
		try {
			new FillInStackTraceMethod().populateException(rda, frame, exception);
		} catch (Exception e) {
			log.error("Unable to fill stack to exception", e);
		}
		return exception;
	}

	public JavaObjectReference convert(RuntimeDataArea rda, Frame frame, Exception originalException) {
		VirtualMachine vm = frame.getVirtualMachine();
		Classfile cf = rda
				.loadClass(frame.getVirtualThread(), originalException.getClass().getName().replace('.', '/'));
		JavaObjectReference exception = newExceptionObjectWithFilledStacktrace(rda, frame, cf);
		String detailMessage = String.valueOf(originalException.getMessage());
		exception.setValueOfField("detailMessage", new JavaString(vm, detailMessage));
		return exception;
	}

	public JavaObjectReference createClassCastException(RuntimeDataArea rda, Frame frame, String actualClass,
			String expectedClass) {
		VirtualMachine vm = frame.getVirtualMachine();
		Classfile cf = rda.loadClass(frame.getVirtualThread(), "java/lang/ClassCastException");
		JavaObjectReference exception = newExceptionObjectWithFilledStacktrace(rda, frame, cf);
		String detailMessage = String.format("Object of type '%s' cannot be cast to type '%s'", actualClass,
				expectedClass);
		exception.setValueOfField("detailMessage", new JavaString(vm, detailMessage));
		return exception;
	}

	public JavaObjectReference createClassNotFoundException(RuntimeDataArea rda, Frame frame, String message) {
		VirtualMachine vm = frame.getVirtualMachine();
		Classfile cf = rda.loadClass(frame.getVirtualThread(), "java/lang/ClassNotFoundException");
		JavaObjectReference exception = newExceptionObjectWithFilledStacktrace(rda, frame, cf);
		exception.setValueOfField("detailMessage", new JavaString(vm, message));
		return exception;
	}

	public JavaObjectReference createNullPointerException(RuntimeDataArea rda, Frame frame,
			JavaObjectReference nullReference) {
		VirtualMachine vm = frame.getVirtualMachine();
		Classfile cf = rda.loadClass(frame.getVirtualThread(), "java/lang/NullPointerException");
		JavaObjectReference exception = newExceptionObjectWithFilledStacktrace(rda, frame, cf);
		String detailMessage = String.format("Unable to invoke method on NULL reference '%s'", nullReference);
		exception.setValueOfField("detailMessage", new JavaString(vm, detailMessage));
		return exception;
	}

	public static String buildMessage(VirtualMachine vm, JavaObjectReference javaException) {
		StringBuilder sb = new StringBuilder();
		sb.append("**** JAVA EXCEPTION ****\n");
		printException(vm, sb, javaException);
		JavaObjectReference cause = (JavaObjectReference) javaException.getValueOfField("cause");
		if (cause != null && !(cause instanceof JavaNullReference)) {
			sb.append("*** CAUSED BY ***\n");
			printException(vm, sb, cause);
			cause = (JavaObjectReference) cause.getValueOfField("cause");
		}
		sb.append("**** JAVA EXCEPTION ****\n");
		return sb.toString();
	}

	private static void printException(VirtualMachine vm, StringBuilder sb, JavaObjectReference javaException) {
		// TODO: Should invoke exception.getMessage() here!
		String message = "";
		JavaObjectReference msg = (JavaObjectReference) javaException.invoke(vm, "getMessage");
		if (msg == null) {
			msg = (JavaObjectReference) javaException.getValueOfField("detailMessage");
		}
		if (msg != null) {
			message = msg.asStringValue();
		}
		Classfile classFile = javaException.getClassFile();
		sb.append(classFile.getThisClassName() + ": " + message);
		sb.append("\n");
		JavaArray stackTraceArray = (JavaArray) javaException.getValueOfField("stackTrace");
		if (stackTraceArray != null) {
			for (int i = 0; i < stackTraceArray.length(); i++) {
				JavaObjectReference javaObject = (JavaObjectReference) stackTraceArray.get(i);
				// private String declaringClass;
				// private String methodName;
				// private String fileName;
				// private int lineNumber;
				JavaString declaringClass = (JavaString) javaObject.getValueOfField("declaringClass");
				JavaString methodName = (JavaString) javaObject.getValueOfField("methodName");
				JavaString fileName = (JavaString) javaObject.getValueOfField("fileName");
				JavaInteger lineNumber = (JavaInteger) javaObject.getValueOfField("lineNumber");
				// TODO: Filename + Linenumber are currently not filled by
				// ourselves, so they're null
				String line = String.format(" [%d] at %s.%s(%s:%d)", i, declaringClass.asStringValue(),
						methodName.asStringValue(), fileName.asStringValue(), lineNumber.intValue());
				sb.append(line);
				sb.append("\n");
			}
		}
	}

}
