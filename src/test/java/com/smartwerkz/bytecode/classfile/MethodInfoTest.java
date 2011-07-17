package com.smartwerkz.bytecode.classfile;

import static org.junit.Assert.*;

import org.junit.Test;


public class MethodInfoTest {

	@Test
	public void testParameterCount() throws Exception {
		 assertEquals(0,count("()V"));
		 assertEquals(1,count("([Ljava/lang/String;)V"));
		 assertEquals(0,count("()Ljava/lang/String;"));
		 assertEquals(2,count("(IF)V"));
		 assertEquals(3,count("([Ljava/util/Hashtable$Entry;Ljava/lang/Object;Ljava/lang/Object;)V"));

	}

	private int count(String descriptor) {
		return Descriptor.countParameters(descriptor);
	}
	
}
