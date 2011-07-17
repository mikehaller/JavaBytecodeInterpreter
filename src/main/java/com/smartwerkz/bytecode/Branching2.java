package com.smartwerkz.bytecode;

public class Branching2 {

	public static void main(String[] args) {
		outer: for (int i = 2; i < 10; i++) {
			for (int j = 2; j < i; j++) {
				if (i % j == 0)
					continue outer;
			}
			System.out.println(i);
		}

	}

}
