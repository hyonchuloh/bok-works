package com.bok.iso.util.test;

import java.io.File;

public class FileInfoMain {
	
	final static String []  ROOT_DIR = {
			"./src/main/resources/static/output/bokwire/convert_MIN", 
			"./src/main/resources/static/output/bokwire/convert_MAX",
			"./src/main/resources/static/output/chaps/convert_MIN", 
			"./src/main/resources/static/output/chaps/convert_MAX",
			"./src/main/resources/static/output/fedwire/convert_MIN", 
			"./src/main/resources/static/output/fedwire/convert_MAX"
	};
//	final static String ROOT_DIR = "./src/main/resources/static/output/fedwire/convert_MIN";

	public static void main(String[] args) {
		
		for ( String d : ROOT_DIR ) {
			File dir = new File(d);
			File [] xmls = dir.listFiles(); 
			String fileName = "";
			for ( File xml : xmls ) {
				fileName = xml.getName();
//				System.out.println("--- DEBUG : " + fileName);
				fileName = fileName.replaceAll("Fedwire_Funds_Service_Release_2025_", "Fedwire_");
				fileName = fileName.replaceAll("_20230831_2310_iso15enriched.xsd.txt", ".xml");
				fileName = fileName.replaceAll("BOK_Phase1_CorePayment_v_1_1_BOK_", "BOK_");
				fileName = fileName.replaceAll("_20230829_0121_iso15enriched.xsd.txt", ".xml");
				fileName = fileName.replaceAll("_20230830_0857.xsd.txt", ".xml");
				fileName = fileName.replaceAll("_20230815_1012.xsd.txt", ".xml");
				fileName = fileName.replaceAll("_20230814_1251.xsd.txt", ".xml");
				fileName = fileName.replaceAll("_20230829_0119_iso15enriched.xsd.txt", ".xml");
				System.out.println(fileName + "\t" + xml.length());
			}
		}
		

	}

}
