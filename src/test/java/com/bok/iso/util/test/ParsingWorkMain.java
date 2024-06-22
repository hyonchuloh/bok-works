package com.bok.iso.util.test;
import java.util.Date;
public class ParsingWorkMain {
	public static void main(String[] args) {
		String textInput = "55555999999999333444488888888444444442020202020202020202014141414141414202020202020202020204444444413336565656565656565656565656565656565656565656565656565656565656565616161616161616161616161616161616";
		String xmlInput = "<Document><TechHdr>" + "<column1>55555</column1>" + "<column2>999999999</column2>"
				+ "<column3>333</column3>" + "<column4>4444</column4>" + "<column5>88888888</column5>"
				+ "<column6>4444</column6>" + "<column7>4444</column7>" + "<column8>20202020202020202020</column8>"
				+ "<column9>14141414141414</column9>" + "<column10>20202020202020202020</column10>"
				+ "<column11>4444</column11>" + "<column12>4444</column12>" + "<column13>1</column13>"
				+ "<column14>333</column14>"
				+ "<column15>65656565656565656565656565656565656565656565656565656565656565656</column15>"
				+ "<column16>1616161616161616</column16>" + "<column17>1616161616161616</column17>"
				+ "</TechHdr></Document>";
		
		int threadCnt = 1000;
		
		Thread [] thread = new Thread[threadCnt];
		
		for ( int i=0; i<threadCnt; i++) {
//			thread[i] = new Thread(new ParsingTextThread(textInput));
			thread[i] = new Thread(new ParsingXmlThread(xmlInput));
		}
		long startTimestamp1 = new Date().getTime();
		for ( int i=0; i<threadCnt; i++)
			thread[i].start();
		
		while ( true ) {
			for (int i=0; i<threadCnt; i++) {
				if ( thread[i].isAlive() )
					continue;
			}
			break;
		}
		
		long endTimestamp1 = new Date().getTime();
		
		System.out.println("--- 걸린시간 : " + (endTimestamp1 - startTimestamp1) + " ms -----");
	}
}