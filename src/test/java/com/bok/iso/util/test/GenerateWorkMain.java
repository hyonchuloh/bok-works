package com.bok.iso.util.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSInstance.SampleValueGenerator;
import jlibs.xml.xsd.XSParser;
import net.moznion.random.string.RandomStringGenerator;

/**
 * XSD 파일을 읽어들여 최대길이 샘플 XML을 생성하는 프로그램
 * @date	2023. 9. 7.
 * @author 	ohhyonchul
 *
 */
public class GenerateWorkMain {
	/*
	final static String INPUT_PATH = "C:\\Users\\ohhyonchul\\git\\bokwork\\myswift\\src\\main\\resources\\static\\xsds";
	final static String OUTPUT_PATH = "C:\\Users\\ohhyonchul\\git\\bokwork\\myswift\\src\\main\\resources\\static\\result";
	final static String CONVERT_PATH = "C:\\Users\\ohhyonchul\\git\\bokwork\\myswift\\src\\main\\resources\\static\\result\\convert2";
	*/
	final static String INPUT_PATH = "./src/main/resources/static/input/fedwire";
	final static String OUTPUT_PATH = "./src/main/resources/static/output/fedwire";
	final static String CONVERT_PATH = "./src/main/resources/static/output/fedwire/convert";
	
	public static void main(String[] args) throws TransformerConfigurationException, SAXException, IOException, ParserConfigurationException {
		
		generateFedWire();
		
	}
	
	/**
	 * 연방준비은행 ISO 20022 XML 생성 
	 * @throws TransformerConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void generateFedWire() throws TransformerConfigurationException, SAXException, IOException, ParserConfigurationException  {
		File inputDir = new File(INPUT_PATH);
		File outputDir = new File(OUTPUT_PATH);
		File convertDir = new File(CONVERT_PATH);
		
		File [] XsdLists = inputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if ( pathname.getName().endsWith(".xsd")) {
					if ( pathname.getName().startsWith("Fedwire_") )
						return true;
				}
				return false;
			}
		});
		
		XSInstance instance = new XSInstance();
		instance.minimumElementsGenerated = 1; // 0
		instance.maximumElementsGenerated = 1; // 1
		instance.generateAllChoices = true;
		instance.generateOptionalElements = true; // false
		instance.minimumListItemsGenerated = 1;
		instance.maximumRecursionDepth = 1;
		instance.generateDefaultAttributes = true;
		instance.generateFixedAttributes = true;
		instance.generateOptionalAttributes = true; // false
		instance.sampleValueGenerator = new SampleValueGeneratorImpl();
		
		for ( File xsd : XsdLists ) {
			QName root = new QName(getNamesapce(xsd),"Document");
			XSModel xsModel = new XSParser().parse(xsd.getAbsolutePath());
			XMLDocument sample = new XMLDocument(new StreamResult(new File(outputDir + "/" + xsd.getName() + ".txt")), true, 4, "utf-8");
			try {
				instance.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.err.println("오류파일 : " + xsd.getName());
				re.printStackTrace();
			}
		}
		
		/* 결과 컨버팅 bah와 document 병합 작업임 및 주석 삭제 작업임 */
		File [] resultList = outputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if ( pathname.getName().startsWith("Fedwire_") )
					return true;
				return false;
			}
		});
		for ( File result : resultList ) {
			
			System.out.println("--- CONVERT : " + result.getName());
			FileReader fr = null;
			BufferedReader br = null;
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fr = new FileReader(result);
				br = new BufferedReader(fr);
				fw = new FileWriter(new File(convertDir + "/" + result.getName()));
				bw = new BufferedWriter(fw);
				
				String temp = "";
				while ( ( temp = br.readLine() ) != null ) {
					if ( temp.trim().length() == 0 )
						continue;
					if ( temp.trim().startsWith("<!--") ) 
						continue;
					bw.write(temp);
					bw.newLine();
				}
				bw.flush();
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				try {
					if ( bw != null ) bw.close();
					if ( fw != null ) fw.close();
					if ( fr != null ) fr.close();
					if ( br != null ) br.close();
				} catch ( Exception e1 ) {}
			}
		}
	}
	
	/**
	 * 한국은행 ISO 20022 XML 생성
	 * @throws TransformerConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void generateBokWire() throws TransformerConfigurationException, SAXException, IOException, ParserConfigurationException {
		File rootDir = null;
		File resultDir = null;
		File convertDir = null;
		
		rootDir = new File(INPUT_PATH);
		resultDir = new File(OUTPUT_PATH);
		convertDir = new File(CONVERT_PATH);
		File [] XsdLists = rootDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if ( pathname.getName().endsWith(".xsd")) {
					if ( pathname.getName().startsWith("BOK_") )
						return true;
				}
				return false;
			}
		});
		
		XSInstance instance = new XSInstance();
		instance.minimumElementsGenerated = 1;
		instance.maximumElementsGenerated = 1;
		instance.generateAllChoices = true;
		instance.generateOptionalElements = false;
		instance.minimumListItemsGenerated = 1;
		instance.maximumRecursionDepth = 1;
		instance.generateDefaultAttributes = true;
		instance.generateFixedAttributes = true;
		instance.generateOptionalAttributes = false;
		instance.sampleValueGenerator = new SampleValueGeneratorImpl();
		
		for ( File xsd : XsdLists ) {
			QName root = new QName(getNamesapce(xsd),"Document");
			XSModel xsModel = new XSParser().parse(xsd.getAbsolutePath());
			XMLDocument sample = new XMLDocument(new StreamResult(new File(resultDir + "/" + xsd.getName() + ".txt")), true, 4, "utf-8");
			try {
				instance.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.out.println(xsd.getName());
				re.printStackTrace();
			}
			
			String bah = "bah_" + xsd.getName();
			File bahXsd = new File(rootDir + "/" + bah);
			
			root = new QName(getNamesapce(bahXsd),"AppHdr");
			xsModel = new XSParser().parse(bahXsd.getAbsolutePath());
			sample = new XMLDocument(new StreamResult(new File(resultDir + "/" + bahXsd.getName() + ".txt")), true, 4, "utf-8");
			try {
				instance.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.out.println(bahXsd.getName());
				re.printStackTrace();
			}
		}
		
		/* 결과 컨버팅 bah와 document 병합 작업임 */
		File [] resultList = resultDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if ( pathname.getName().startsWith("BOK_") )
					return true;
				return false;
			}
		});
		for ( File result : resultList ) {
			
			System.out.println("--- CONVERT : " + result.getName());
			FileReader fr = null;
			BufferedReader br = null;
			FileReader fr2 = null;
			BufferedReader br2 = null;
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fr2 = new FileReader(resultDir.getAbsolutePath() + "/bah_" + result.getName());
				br2 = new BufferedReader(fr2);
				fr = new FileReader(result);
				br = new BufferedReader(fr);
				fw = new FileWriter(new File(convertDir + "/" + result.getName()));
				bw = new BufferedWriter(fw);
				
				//     BOK_Phase1_CorePayment_v_1_1_BOK_acmt_023_001_03_IdentificationVerificationRequest_20230829_0121_iso15enriched.xsd.txt
				// bah_BOK_Phase1_CorePayment_v_1_1_BOK_acmt_023_001_03_IdentificationVerificationRequest_20230829_0121_iso15enriched.xsd.txt
				String temp = "";
				while ( ( temp = br2.readLine() ) != null ) {
					if ( temp.trim().length() == 0 )
						continue;
					if ( temp.trim().startsWith("<!--") ) 
						continue;
					bw.write(temp);
					bw.newLine();
				}
				while ( ( temp = br.readLine() ) != null ) {
					if ( temp.trim().length() == 0 )
						continue;
					if ( temp.trim().startsWith("<!--") ) 
						continue;
					bw.write(temp);
					bw.newLine();
				}
				bw.flush();
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				try {
					if ( bw != null ) bw.close();
					if ( fw != null ) fw.close();
					if ( fr != null ) fr.close();
					if ( br != null ) br.close();
					if ( fr2 != null ) fr2.close();
					if ( br2 != null ) br2.close();
				} catch ( Exception e1 ) {}
			}
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

class SampleValueGeneratorImpl implements SampleValueGenerator {
	
	@Override
	public String generateSampleValue(XSAttributeDeclaration attribute, XSSimpleTypeDefinition simpleType) {
		return null;
	}
	
	@Override
	public String generateSampleValue(XSElementDeclaration element, XSSimpleTypeDefinition simpleType) {
		XSFacet lengthFacet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MAXLENGTH);
		StringBuffer result = null;
		if ( lengthFacet != null ) {
			result = new StringBuffer();
			int length = Integer.parseInt(lengthFacet.getLexicalFacetValue());
			for ( int i=0; i<length; i++) {
				result.append("0");
			}
		}
		if ( result != null ) {
			return result.toString();
		} else {
			String pattern = simpleType.getLexicalPattern().toString();
			pattern = pattern.substring(1);
			pattern = pattern.substring(0,pattern.length()-1);
			if ( simpleType.getPrimitiveType().getBuiltInKind() == 2 ) {
				if ( pattern.trim().length() > 2 ) {
					String retValue = null;
					try {
						retValue = new RandomStringGenerator().generateByRegex(pattern);
					} catch ( Exception e ) {
						
						if ( pattern.contains("{1,") ) {
							pattern = "[0-9a-zA-Z]{" + pattern.substring(pattern.indexOf("{1,") + 3);
						}
						System.out.println(element.getName() + "\t" + pattern);
						retValue = new RandomStringGenerator().generateByRegex(pattern);
					}
					return retValue;
				}
			}
			return null;
		}
	}
	
	public XSFacet getFacet(XSSimpleTypeDefinition simpleType, int kind) {
		XSObjectList facets = simpleType.getFacets();
		for ( int i=0; i<facets.getLength(); i++) {
			XSFacet facet = (XSFacet)facets.item(i);
			if ( facet.getFacetKind() == kind ) {
				return facet;
			}
		}
		return null;
	}
}
