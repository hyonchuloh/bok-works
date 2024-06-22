package com.bok.iso.util.xsd.bokwire;

import java.io.File;
import java.io.FilenameFilter;

public class ExportElementFromXsdMain {

	public static void main(String[] args) {
		
		String inputDirStr = "C:\\Users\\bok\\git\\bok\\bokworks-generate-xml\\src\\main\\resources\\static\\input\\bokwire\\20240404";
		String outputDirStr = "C:\\Users\\bok\\git\\bok\\bokworks-generate-xml\\src\\main\\resources\\static\\output\\bokwire\\20240404";
		
		try {
			
			File inputDir = new File(inputDirStr);
			File [] files = inputDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if ( name.contains("pacs_008") && name.contains("CORE") ) 
						return true;
					return false;
				}
			});
			
			ParseXsdToElement xsdToElement= null; 
			GenerateElementSize2 gen2 = null;
			for ( File f : files ) {
				System.out.println(f.getAbsolutePath());
				
				if ( f.getName().contains("bah")) {
					xsdToElement = new ParseXsdToElement(f, "AppHdr", "urn:iso:std:iso:20022:tech:xsd:head.001.001.02");
					xsdToElement.doWork();
					gen2 = new GenerateElementSize2();
					gen2.doWork(outputDirStr + "\\RESULT_AppHdr.txt", "ns:AppHdr");
				} else {
					xsdToElement = new ParseXsdToElement(f, "Document", "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08");
					xsdToElement.doWork();
					gen2 = new GenerateElementSize2();
					gen2.doWork(outputDirStr + "\\RESULT_Document.txt", "ns:Document");
				}
				
			}
			
		} catch ( Exception e ) {
			
		}

	}

}
