package com.bok.iso.util.test;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParsingXmlThread extends Thread {
	
	private String input;
	private List<String> xmlList;
	
	public ParsingXmlThread(String input) {
		this.input = input;
		this.xmlList = new LinkedList<String>();
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer("--- XML PRINT : ");
		for ( String item : xmlList ) {
			out.append("["+item+"],");
		}
		return out.toString();
	}
	
	
	@Override
	public void run() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		try {
			// xml 파싱하기
			InputSource is = new InputSource(new StringReader(this.input));
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile("//Document/TechHdr");
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				NodeList child = nodeList.item(i).getChildNodes();
				for (int j = 0; j < child.getLength(); j++) {
					Node node = child.item(j);
					xmlList.add(node.getNodeValue());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		this.toString();
	}

}
