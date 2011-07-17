package com.smartwerkz.bytecode.vm;

public class ProgramCounter {

	private int currentAddress;

	public int getCurrentAddress() {
		return currentAddress;
	}
	
	public void setCurrentAddress(int currentAddress) {
		this.currentAddress = currentAddress;
	}

}
