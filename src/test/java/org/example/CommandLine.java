package org.example;

public class CommandLine {

	public static void main(String[] args) {
		System.out.println("Command Line Arguments");
		System.out.println(args.length);
		for(int i = 0; i < args.length;i++) {
			System.out.print("Argument ");
			System.out.println(i);
			System.out.println(args[i]);
		}
		System.out.print("End of Command Line Arguments");
	}

}
