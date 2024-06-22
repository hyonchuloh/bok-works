package com.bok.iso.util.xml.fedwire;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;

public class GenerateWorkMain2 {
	
	public static XSInstance INSTANCE = new XSInstance();
	final static String INPUT_PATH = "./src/main/resources/static/input/fedwire/iso/fedwirefunds-incoming.xsd";
	final static String OUTPUT_PATH = "./src/main/resources/static/output/fedwire";

	public static void main(String[] args) throws TransformerConfigurationException, SAXException, IOException, ParserConfigurationException {
		
		INSTANCE.minimumElementsGenerated 	= 1; 		// 최소 엘리먼트 생성 (MIN:0, MAX:1)
		INSTANCE.minimumListItemsGenerated 	= 1; 		// 최소 배열 생성 (MIN:0, MAX:1)
		INSTANCE.maximumRecursionDepth 		= 1; 		// 최대 재귀 깊이 생략
		INSTANCE.maximumListItemsGenerated 	= 1; 		// 최대 배열 생성 고정
		INSTANCE.maximumElementsGenerated 	= 1; 		// 최대 엘리먼트 생성 고정
		INSTANCE.generateOptionalElements 	= false; 	// 옵셔널 엘리먼트 생성 (MIN:false, MAX:true)
		INSTANCE.generateOptionalAttributes = false; 	// 옵셔널 애트리뷰트 생성 (MIN:false, MAX:true)
		INSTANCE.generateAllChoices 		= true;		// 모든 선택분 생성
		INSTANCE.generateDefaultAttributes 	= true;	 	// 옵셔널 디폴트 애트리뷰트 생성
		INSTANCE.generateFixedAttributes 	= true;		// 고정 애트리뷰트 생성
		INSTANCE.sampleValueGenerator = new SampleValueGeneratorImpl();
		
		File xsd = new File(INPUT_PATH);
		File outputDir = new File(OUTPUT_PATH);
		
		QName root = new QName(getNamesapce(xsd),"FedwireFundsIncoming");
		XSModel xsModel = new XSParser().parse(xsd.getAbsolutePath());
		XMLDocument sample = new XMLDocument(new StreamResult(new File(outputDir + "/" + xsd.getName() + ".txt")), true, 4, "utf-8");
		try {
			INSTANCE.generate(xsModel, root, sample);
		} catch (RuntimeException re ) {
			System.out.println("--- 오류난 파일 : " + xsd.getName());
			re.printStackTrace();
		}

	}
	
	public static String getNamesapce(File xsd) throws SAXException, IOException, ParserConfigurationException {
		// XML Document 객체 생성
        InputSource is = new InputSource(new FileInputStream(xsd));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        Element root = document.getDocumentElement();
        String namespace = root.getAttribute("xmlns");
        System.out.println(namespace);
		return namespace;
	}

}
