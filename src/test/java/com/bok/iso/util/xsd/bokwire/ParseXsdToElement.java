package com.bok.iso.util.xsd.bokwire;

import java.io.File;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSInstance.SampleValueGenerator;
import jlibs.xml.xsd.XSParser;
import net.moznion.random.string.RandomStringGenerator;

public class ParseXsdToElement {
	
	private File input;
	private String postFix;
	private String urn;
	
	/**
	 * 
	 * @param input
	 * @param postFix = "AppHdr"
	 */
	public ParseXsdToElement(File input, String postFix, String urn) {
		this.input = input;
		this.postFix = postFix;
		this.urn = urn;
	}
	
	public void doWork() {
		System.out.println("-----["+input.getName()+"]-----");
		
		// XML Document 객체 생성
		try {
	        XSInstance INSTANCE = new XSInstance();
			INSTANCE.minimumElementsGenerated 	= 0; 		// 최소 엘리먼트 생성 (MIN:0, MAX:1)
			INSTANCE.minimumListItemsGenerated 	= 0; 		// 최소 배열 생성 (MIN:0, MAX:1)
			INSTANCE.maximumRecursionDepth 		= 0; 		// 최대 재귀 깊이 (MIN:0, MAX:1)
			INSTANCE.maximumListItemsGenerated 	= 1; 		// 최대 배열 생성 고정
			INSTANCE.maximumElementsGenerated 	= 1; 		// 최대 엘리먼트 생성 고정
			INSTANCE.generateOptionalElements 	= false; 	// 옵셔널 엘리먼트 생성 (MIN:false, MAX:true)
			INSTANCE.generateOptionalAttributes = false; 	// 옵셔널 애트리뷰트 생성 (MIN:false, MAX:true)
			INSTANCE.generateAllChoices 		= true;		// 모든 선택 생성 고정
			INSTANCE.generateDefaultAttributes 	= false;	 // 옵셔널 디폴트 애트리뷰트 생성 (MIN:false)
			INSTANCE.generateFixedAttributes 	= true;		// 고정 애트리뷰트 생성
			INSTANCE.sampleValueGenerator = new SampleValueGeneratorImpl();
			
			QName root = new QName(urn, postFix);
			XSModel xsModel = new XSParser().parse(input.getAbsolutePath());
			
			XMLDocument sample = new XMLDocument(
					new StreamResult(
						new File("C:\\Users\\bok\\git\\bok\\bokworks-generate-xml\\src\\main\\resources\\static\\output\\bokwire\\20240509\\RESULT_"+postFix+".txt")), 
						true, 4, "utf-8");
			try {
				INSTANCE.generate(xsModel, root, sample);
			} catch (RuntimeException re ) {
				System.out.println("--- 오류난 파일 : " + input.getName());
				re.printStackTrace();
			}
	        
	        
		} catch (FactoryConfigurationError | TransformerConfigurationException e) {
			e.printStackTrace();
		} finally {
			
		}
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
						/*if ( pattern.contains("{1,") ) {
							pattern = "[0-9a-zA-Z]{" + pattern.substring(pattern.lastIndexOf("{1,") + 3);
						}*/
						//retValue = new RandomStringGenerator().generateByRegex(pattern);
						System.err.println(e);
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
