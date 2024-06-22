package com.bok.iso.util.xml.fedwire;

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
	
	final static String INPUT_PATH = "./src/main/resources/static/input/fedwire";
	final static String OUTPUT_PATH = "./src/main/resources/static/output/fedwire";
//	final static String CONVERT_PATH = "./src/main/resources/static/output/fedwire/convert_MIN";
	final static String CONVERT_PATH = "./src/main/resources/static/output/fedwire/convert_MAX";
	
	final static String PRE_FIX = "Fedwire_";
	final static String POST_FIX = ".xsd";
	final static String ROOT_ELEMENT_DUCMENT = "Document";
	final static String ROOT_ELEMENT_APPHDR = "AppHdr";
	
	final static int	TAP_SPACE_SIZE = 4; 
	public static XSInstance INSTANCE = new XSInstance();
	
	public static void main(String[] args) throws TransformerConfigurationException, SAXException, IOException, ParserConfigurationException {
		
		if ( CONVERT_PATH.endsWith("MIN")) {
			INSTANCE.minimumElementsGenerated 	= 0; 		// 최소 엘리먼트 생성 (MIN:0, MAX:1)
			INSTANCE.minimumListItemsGenerated 	= 0; 		// 최소 배열 생성 (MIN:0, MAX:1)
			INSTANCE.maximumRecursionDepth 		= 0; 		// 최대 재귀 깊이 생략
			INSTANCE.maximumListItemsGenerated 	= 0; 		// 최대 배열 생성 고정
			INSTANCE.maximumElementsGenerated 	= 0; 		// 최대 엘리먼트 생성 고정
			INSTANCE.generateOptionalElements 	= false; 	// 옵셔널 엘리먼트 생성 (MIN:false, MAX:true)
			INSTANCE.generateOptionalAttributes = false; 	// 옵셔널 애트리뷰트 생성 (MIN:false, MAX:true)
		} else if ( CONVERT_PATH.endsWith("MAX")) {
			INSTANCE.minimumElementsGenerated 	= 1; 		// 최소 엘리먼트 생성 (MIN:0, MAX:1)
			INSTANCE.minimumListItemsGenerated 	= 1; 		// 최소 배열 생성 (MIN:0, MAX:1)
			INSTANCE.maximumRecursionDepth 		= 1; 		// 최대 재귀 깊이 (MIN:0, MAX:1)
			INSTANCE.maximumListItemsGenerated 	= 1; 		// 최대 배열 생성 고정
			INSTANCE.maximumElementsGenerated 	= 1; 		// 최대 엘리먼트 생성 고정
			INSTANCE.generateOptionalElements 	= true; 	// 옵셔널 엘리먼트 생성 (MIN:false, MAX:true)
			INSTANCE.generateOptionalAttributes = true; 	// 옵셔널 애트리뷰트 생성 (MIN:false, MAX:true)
		}
		INSTANCE.generateAllChoices 		= true;		// 모든 선택 생성 고정
		INSTANCE.generateDefaultAttributes 	= true;	 	// 옵셔널 디폴트 애트리뷰트 생성
		INSTANCE.generateFixedAttributes 	= true;		// 고정 애트리뷰트 생성
		INSTANCE.sampleValueGenerator = new SampleValueGeneratorImpl();
		
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
				if ( pathname.getName().endsWith(POST_FIX)) {
					if ( pathname.getName().startsWith(PRE_FIX) )
						return true;
				}
				return false;
			}
		});
		
		for ( File xsd : XsdLists ) {
			
			if ( xsd.getName().contains("BusinessApplicationHeader"))
				continue;
			
			QName root = new QName(getNamesapce(xsd),ROOT_ELEMENT_DUCMENT);
			XSModel xsModel = new XSParser().parse(xsd.getAbsolutePath());
			XMLDocument sample = new XMLDocument(new StreamResult(new File(outputDir + "/" + xsd.getName() + ".txt")), true, 4, "utf-8");
			try {
				INSTANCE.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.out.println("--- 오류난 파일 : " + xsd.getName());
				re.printStackTrace();
			}
			
			// AppHdr
			File bahXsd = new File(INPUT_PATH + "/Fedwire_Funds_Service_Release_2025_BusinessApplicationHeader_head_001_001_03_20230831_2310_iso15enriched.xsd");
			root = new QName(getNamesapce(bahXsd),ROOT_ELEMENT_APPHDR);
			xsModel = new XSParser().parse(bahXsd.getAbsolutePath());
			sample = new XMLDocument(new StreamResult(new File(outputDir + "/bah_" + xsd.getName() + ".txt")), true, TAP_SPACE_SIZE, "utf-8");
			try {
				INSTANCE.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.out.println(bahXsd.getName());
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
			FileReader fr2 = null;
			BufferedReader br2 = null;
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fr2 = new FileReader(outputDir.getAbsolutePath() + "/bah_" + result.getName());
				br2 = new BufferedReader(fr2);
				fr = new FileReader(result);
				br = new BufferedReader(fr);
				fw = new FileWriter(new File(convertDir + "/" + result.getName()));
				bw = new BufferedWriter(fw);
				
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
							pattern = "[0-9a-zA-Z]{" + pattern.substring(pattern.lastIndexOf("{1,") + 3);
						}
//						System.out.println(element.getName() + "\t" + pattern);
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
