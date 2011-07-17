package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObjectReference;
import com.smartwerkz.bytecode.primitives.JavaString;

/**
 * Maintains the list of interned strings.
 * 
 * @author mhaller
 */
public class StringPool {

	private final Map<String, JavaString> internedStrings = new HashMap<String, JavaString>();
	private final VirtualMachine vm;

	public StringPool(VirtualMachine vm) {
		this.vm = vm;
	}
	
	public JavaString intern(JavaString newString) {
		if (internedStrings.containsKey(newString.asStringValue()))
			return internedStrings.get(newString.asStringValue());
		internedStrings.put(newString.asStringValue(), newString);
		return newString;
	}

	public JavaString intern(JavaObjectReference javaObjectReference) {
		JavaArray javaObject = (JavaArray) javaObjectReference.getValueOfField("value");
		if (javaObject==null)
		{
			throw new IllegalStateException("The 'value' char array is null on JavaString: " + javaObjectReference);
		}
		StringBuilder strValue = new StringBuilder();
		for (int i = 0; i < javaObject.length(); i++) {
			JavaInteger c = (JavaInteger) javaObject.get(i);
			strValue.append((char) c.intValue());
		}
		return intern(new JavaString(vm, strValue.toString()));
	}

}
