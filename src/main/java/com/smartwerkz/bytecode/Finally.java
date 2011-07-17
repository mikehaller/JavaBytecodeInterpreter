package com.smartwerkz.bytecode;

public class Finally {
	public static void main(String[] args) {
		try {
			throw new RuntimeException();
		} finally {
			System.nanoTime();
		}
	}
}
