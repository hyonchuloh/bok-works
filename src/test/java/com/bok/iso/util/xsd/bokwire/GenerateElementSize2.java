package com.bok.iso.util.xsd.bokwire;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GenerateElementSize2 {
	
	public List<String> xpath = new LinkedList<String>();
	
	/**
	 * 
	 * @param filePath "C:\\Users\\bok\\git\\bok\\bokworks-generate-xml\\src\\main\\resources\\static\\output\\bokwire\\20240404\\RESULT_AppHdr.txt"
	 * @param rootTagName "ns:AppHdr"
	 */
	public void doWork(String filePath, String rootTagName) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(new File(filePath));

			// optional, but recommended
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName(rootTagName);
			for (int temp = 0; temp < list.getLength(); temp++) {
				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					xpath.add("/" + node.getNodeName().replaceAll("ns:", "") + "/");
					doParsing(node);
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}
	
	public void doParsing(Node element) {
		
		NodeList list = element.getChildNodes();
		for (int temp = 0; temp < list.getLength(); temp++) {
			Node node = list.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				xpath.add(node.getNodeName().replaceAll("ns:", "") + "/");
				doParsing(node);
				
			} else if ( node.getNodeType() == Node.TEXT_NODE ){
				if ( node.getNodeValue() !=null && node.getNodeValue().trim().length() > 0 ) {
					for ( String t : xpath ) {
						System.out.print(t);
					}
					System.out.println("\t" + node.getNodeValue() + "\t" + node.getNodeValue().getBytes().length + "\t" + (xpath.get(xpath.size()-1).getBytes().length-1 ));
				}
			} 
		}
		xpath.remove(xpath.size()-1);
	}

}
