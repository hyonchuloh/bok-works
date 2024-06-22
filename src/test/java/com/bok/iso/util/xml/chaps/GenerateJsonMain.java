package com.bok.iso.util.xml.chaps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import org.json.JSONObject;
import org.json.XML;

public class GenerateJsonMain {
	
	final static String INPUT_DIR = "./src/main/resources/static/output/chaps/convert_MIN";
	final static String OUTPUT_DIR = INPUT_DIR + "/json";

	public static void main(String[] args) {
		
		doWork();

	}
	
	public static void doWork() {
		
		File [] listFiles = new File(INPUT_DIR).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if ( name.startsWith("CHAPS") && name.endsWith(".txt"))
					return true;
				return false;
			}
		});
		
		for ( File f : listFiles ) {
			
			BufferedReader br = null;
			BufferedWriter bw = null;
			FileReader fr = null;
			FileWriter fw = null;
			
			StringBuffer xml = null;
			JSONObject json = null;
			
			try {
				
				System.out.println("--- 작업중인 파일 [" + f.getName() + "]");
				
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				
				String temp = ""; 
				xml = new StringBuffer();
				
				while ( (temp = br.readLine() ) != null ) {
					xml.append(temp);
				}
				
				json = XML.toJSONObject(xml.toString());
				
				fw = new FileWriter(OUTPUT_DIR + "/" + f.getName());
				bw = new BufferedWriter(fw);
				
				bw.write(json.toString(4).replaceAll("ns:", ""));
				bw.flush();
				
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				try {
					if ( bw != null ) bw.close();
					if ( br != null ) br.close();
					if ( fr != null ) fr.close();
					if ( fw != null ) fw.close();
				} catch ( Exception e2 ) {
					//
				}
			}
		}
		
	}

}
