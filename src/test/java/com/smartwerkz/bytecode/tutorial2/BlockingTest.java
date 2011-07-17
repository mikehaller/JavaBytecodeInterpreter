package com.smartwerkz.bytecode.tutorial2;

import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Ignore;
import org.junit.Test;

public class BlockingTest {

	@Test
	@Ignore
	public void testBlocking() throws Exception {
		new ArrayBlockingQueue<String>(1).take();
	}

}
