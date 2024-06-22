package com.bok.iso.util.test;

import java.util.LinkedList;
import java.util.List;

public class ParsingTextThread extends Thread {
	
	private String input;
	private List<String> textList ;
	
	public ParsingTextThread(String input) {
		this.input = input;
		this.textList = new LinkedList<String>();
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer("--- TEXT PRINT : ");
		for ( String item : textList ) {
			out.append("["+item+"],");
		}
		return out.toString();
	}
	
	@Override
	public void run() {
		int[] list = { 5, 9, 3, 4, 8, 4, 4, 20, 14, 20, 4, 4, 1, 3, 65, 16, 16 };
		int indexPointer = 0;
		for (int pointer : list) {
			textList.add(input.substring(indexPointer, indexPointer + pointer));
			indexPointer = indexPointer + pointer;
		}
		this.toString();
	}

}
