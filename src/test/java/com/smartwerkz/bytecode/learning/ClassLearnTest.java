package com.smartwerkz.bytecode.learning;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassLearnTest {

	@Test
	public void testLearnSuperClassnameOfPrimitiveClasses() throws Exception {
		assertNull(boolean.class.getSuperclass());
	}
	
	@Test
	public void testWhatIsClassOfPrimitiveArray() throws Exception {
		assertEquals("[B",Class.forName("[B").getName());
	}

	@Test
	public void testWhatIsSuperclassOfArrays() throws Exception {
		Class[] foo = new Class[0];
		assertEquals("[Ljava.lang.Class;",foo.getClass().getName());
		assertEquals("java.lang.Object",foo.getClass().getSuperclass().getName());
	}

	@Test
	public void testHowManyConstructorsHasJavaLangObject() throws Exception {
		assertEquals(1,java.lang.Object.class.getDeclaredConstructors().length);
	}
}
