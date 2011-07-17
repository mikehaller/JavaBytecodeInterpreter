package com.smartwerkz.bytecode.vm.methods;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.ClassArea;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VirtualMachine;

public final class SystemInitPropertiesMethod implements NativeMethod {
	/**
	 * System properties. The following properties are guaranteed to be defined:
	 * <dl>
	 * <dt>java.version
	 * <dd>Java version number
	 * <dt>java.vendor
	 * <dd>Java vendor specific string
	 * <dt>java.vendor.url
	 * <dd>Java vendor URL
	 * <dt>java.home
	 * <dd>Java installation directory
	 * <dt>java.class.version
	 * <dd>Java class version number
	 * <dt>java.class.path
	 * <dd>Java classpath
	 * <dt>os.name
	 * <dd>Operating System Name
	 * <dt>os.arch
	 * <dd>Operating System Architecture
	 * <dt>os.version
	 * <dd>Operating System Version
	 * <dt>file.separator
	 * <dd>File separator ("/" on Unix)
	 * <dt>path.separator
	 * <dd>Path separator (":" on Unix)
	 * <dt>line.separator
	 * <dd>Line separator ("\n" on Unix)
	 * <dt>user.name
	 * <dd>User account name
	 * <dt>user.home
	 * <dd>User home directory
	 * <dt>user.dir
	 * <dd>User's current working directory
	 * 
	 * file.encoding
	 * 
	 * </dl>
	 */

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack operandStack) {
		VirtualMachine vm = rda.vm();

		JavaObjectReference targetProps = (JavaObjectReference) frame.getLocalVariables().getLocalVariable(0);

		Properties sourceProps = new Properties();
		sourceProps.setProperty("file.encoding", "UTF-8");
		sourceProps.setProperty("file.separator", "/");
		sourceProps.setProperty("path.separator", ":");
		sourceProps.setProperty("line.separator", "\n");
		sourceProps.setProperty("user.name", "");
		sourceProps.setProperty("user.home", "/");
		sourceProps.setProperty("user.dir", "/");
		sourceProps.setProperty("java.home", "/");
		sourceProps.setProperty("java.version", "1.0");
		sourceProps.setProperty("java.vendor", "Mike Haller");
		sourceProps.setProperty("java.vendor.url", "http://www.mhaller.de/");
		sourceProps.setProperty("java.class.version", "50.0");
		sourceProps.setProperty("java.class.path", "rt.jar");
		sourceProps.setProperty("os.name", "Virtual Operating System");
		sourceProps.setProperty("os.arch", "Virtual Architecture");
		sourceProps.setProperty("os.version", "1.0");

		// Prevent Sun JDK DownloadManager from doing stuff
		sourceProps.setProperty("kernel.nomerge", "true");
		sourceProps.setProperty("kernel.download.enabled", "false");

		copyTo(vm, sourceProps, targetProps);

		// TODO: REMOVE IT: Copy the original system properties ..
		// copyTo(vm, System.getProperties(), props);

		ClassArea classArea = rda.getMethodArea().getClassArea(frame.getCurrentMethodInfo());
		if (!classArea.getClassfile().getThisClassName().equals("java/lang/System")) {
			throw new IllegalStateException("We should be in the java.lang.System class");
		}
		classArea.getRuntimeConstantPool().setValue("props", targetProps);

		operandStack.push(targetProps);
	}

	private void copyTo(VirtualMachine vm, Properties from, JavaObjectReference props) {
		Set<Entry<Object, Object>> entrySet = from.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			JavaObject key = new JavaString(vm, (String) entry.getKey());
			JavaObject value = new JavaString(vm, (String) entry.getValue());
			props.invoke(vm, "setProperty", key, value);
		}
	}
}