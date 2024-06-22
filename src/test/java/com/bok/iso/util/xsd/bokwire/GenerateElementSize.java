package com.bok.iso.util.xsd.bokwire;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.util.SystemOutLogger;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;

import jlibs.xml.xsd.XSParser;

public class GenerateElementSize {

	private File input;

	public GenerateElementSize(File input) {
		this.input = input;
	}

	public void doWork() {

		try {
//			QName root = new QName("", "AppHdr");
			XSModel xsModel = new XSParser().parse(input.getAbsolutePath());
			
			//String namesapce = "urn:iso:std:iso:20022:tech:xsd:head.001.001.02";
			//String name = "AppHdr";
			// process model group definitions
			XSNamedMap xsMap = xsModel.getComponents(XSConstants.MODEL_GROUP_DEFINITION);
			for (int i = 0; i < xsMap.getLength(); i++) {
				XSModelGroupDefinition xsGroupDef = (XSModelGroupDefinition) xsMap.item(i);
				XSModelGroup xsGroup = xsGroupDef.getModelGroup();
				System.out.println("xsGroup=" + xsGroup.getName());
			}
			
			xsMap = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
			for (int i=0;i<xsMap.getLength();i++) {
				XSElementDeclaration item=(XSElementDeclaration)xsMap.item(i);
					System.out.println(item.getName());
			}
			
			// process top-level type definitions
			xsMap = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
			for (int i = 0; i < xsMap.getLength(); i++) {
				XSTypeDefinition xsTDef = (XSTypeDefinition) xsMap.item(i);
				processXSTypeDef(xsTDef);
			}
			
			
		} catch (Exception e) {

		}

	}

	/**
	 * Process type definition
	 */
	private void processXSTypeDef(XSTypeDefinition xsTDef) {
		switch (xsTDef.getTypeCategory()) {
		case XSTypeDefinition.SIMPLE_TYPE:
			XSSimpleTypeDefinition xs = (XSSimpleTypeDefinition) xsTDef;
			System.out.println(xs.getName() + "\t" + xs.getLexicalPattern() + "\t" + xs.getOrdered());
			break;

		case XSTypeDefinition.COMPLEX_TYPE:

			XSComplexTypeDefinition xsCTDef = (XSComplexTypeDefinition) xsTDef;
			XSObjectList xsAttrList = xsCTDef.getAttributeUses();
			for (int i = 0; i < xsAttrList.getLength(); i++) {
				XSAttributeUse xsa = (XSAttributeUse) xsAttrList.item(i);
				System.out.println(xsa.getName());
			}

			// element content
			switch (xsCTDef.getContentType()) {
			
			case XSComplexTypeDefinition.CONTENTTYPE_EMPTY:
				break;

			case XSComplexTypeDefinition.CONTENTTYPE_SIMPLE:
				break;

			case XSComplexTypeDefinition.CONTENTTYPE_ELEMENT:
				processXSParticle(xsCTDef.getParticle());
				break;

			case XSComplexTypeDefinition.CONTENTTYPE_MIXED:
				processXSParticle(xsCTDef.getParticle());
				break;
			}
		}

	}

	/**
	 * Process particle
	 */
	private void processXSParticle(XSParticle xsParticle) {
		XSTerm xsTerm = xsParticle.getTerm();
		switch (xsTerm.getType()) {
		case XSConstants.ELEMENT_DECLARATION:
			XSElementDeclaration xs = (XSElementDeclaration) xsTerm;
			System.out.println(xs.getName());
			break;

		case XSConstants.MODEL_GROUP:

			XSModelGroup xsGroup = (XSModelGroup) xsTerm;

			XSObjectList xsParticleList = xsGroup.getParticles();
			for (int i = 0; i < xsParticleList.getLength(); i++) {
				processXSParticle((XSParticle) xsParticleList.item(i));
			}
			break;

		case XSConstants.WILDCARD:
			break;
		}
	}
	
	
}
