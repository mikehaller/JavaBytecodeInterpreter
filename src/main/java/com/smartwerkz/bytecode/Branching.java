package com.smartwerkz.bytecode;

public class Branching {
	public static void main(String[] args) {
		doit();
	}

	private static int doit() {
		int a = 10;
		int b = 15;
		if (b <= a) {
			return 20;
		} else {
			return 25;
		}
	}
}
