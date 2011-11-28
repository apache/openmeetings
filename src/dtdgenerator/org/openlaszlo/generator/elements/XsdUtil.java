package org.openlaszlo.generator.elements;

import java.util.SortedSet;
import java.util.TreeSet;

public class XsdUtil {

	private String xsdPrefix = "xs";

	private String xsdProjectPrefix = "lzx";

	private String nameSpace = "http://localhost/openlaszlo/lzx";

	private String tabSpace = "  ";
	
	private String topLevelElementName = "topLevelElements";
	
	public void setNameSpace(String nameSpace2) {
		this.nameSpace = nameSpace2;
	}
	
	public void setXsdProjectPrefix(String xsdProjectPrefix2) {
		this.xsdProjectPrefix = xsdProjectPrefix2;
	}

	private final SortedSet<ClassElement> allowedElements = new TreeSet<ClassElement>();

	public void registerAllowedSubElement(ClassElement classElement) {
		allowedElements.add(classElement);
	}
	
	public void writeBaseAllowedSubElements(StringBuilder sb) throws Exception {

		sb.append("<" + xsdPrefix
				+ ":group name=\""+ topLevelElementName +"\">\n");
		sb.append("<" + xsdPrefix +":sequence>\n");
		sb.append("<" + xsdPrefix +":choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		
		sb.append("<" + xsdPrefix +":any />\n");
		
		sb.append("</" + xsdPrefix +":choice>\n");
		sb.append("</" + xsdPrefix +":sequence>\n");
		sb.append("</" + xsdPrefix +":group>\n");
		

	}

	public void writeAllowedSubElements(StringBuilder sb) throws Exception {

		sb.append("<" + xsdPrefix
				+ ":group name=\""+ topLevelElementName +"\">\n");
		sb.append("<" + xsdPrefix +":sequence>\n");
		sb.append("<" + xsdPrefix +":choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		
		for(ClassElement cl : allowedElements) {
			
			sb.append("<" + xsdPrefix +":element ref=\""+xsdProjectPrefix+":"+cl.getName()+"\"/>\n");
			
		}
		
		sb.append("</" + xsdPrefix +":choice>\n");
		sb.append("</" + xsdPrefix +":sequence>\n");
		sb.append("</" + xsdPrefix +":group>\n");
		

	}

	public void writeXsdHeader(StringBuilder sb) throws Exception {

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		sb.append("<" + xsdPrefix + ":schema ");
		sb.append("xmlns:" + xsdPrefix + "=\"http://www.w3.org/2001/XMLSchema\" ");
		sb.append("elementFormDefault=\"qualified\" \n");
		sb.append(tabSpace + "targetNamespace=\"" + nameSpace + "\" \n");
		sb.append(tabSpace + "xmlns:" + xsdProjectPrefix + "=\"" + nameSpace + "\">\n");

	}

	public void generateXsdFooter(StringBuilder sb) throws Exception {
		sb.append("</" + xsdPrefix + ":schema>\n");
	}

	public void writeComplexType(StringBuilder sb, String className,
			ClassElement classElement) throws Exception {

		sb.append("<" + xsdPrefix + ":element name=\"" + className + "\" ");
		sb.append("type=\"" + xsdProjectPrefix + ":" + className + "\" >\n");
		//Write documentation into Element
		writeDocumentation(sb, classElement.getComment());
		sb.append("</" + xsdPrefix + ":element>\n");
		
		sb.append(tabSpace + "<" + xsdPrefix
				+ ":complexType name=\"" + className + "\" mixed=\"true\">\n");

		// Write Attributes to Element
		writeAttributes(sb, classElement);

		sb.append(tabSpace + "</" + xsdPrefix
				+ ":complexType>\n");

	}
	
	private void writeAttributes(StringBuilder sb, ClassElement classElement)
			throws Exception {
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(tabSpace + "<" + xsdPrefix +":complexContent>\n");
	
			sb.append(tabSpace + "<" + xsdPrefix
					+ ":extension base=\"" + xsdProjectPrefix + ":" + classElement.getParentAsString()
					+ "\">\n");
		}
		
		if (classElement.getName().equals("node")) {
			sb.append(tabSpace + "<" + xsdPrefix +":group ref=\"" + xsdProjectPrefix + ":"+ topLevelElementName +"\" />\n");
		} else if (!classElement.getClassRoot().equals("node")) {
			sb.append(tabSpace + "<" + xsdPrefix +":group ref=\"" + xsdProjectPrefix + ":"+ topLevelElementName +"\" />\n");
		}
		
		for (ClassAttribute classAttribute : classElement.getAttributes()) {

			sb.append(tabSpace + tabSpace + tabSpace);

			sb.append("<" + xsdPrefix + ":attribute name=\"" + classAttribute.getName() + "\" ");
			
			if (false && classAttribute.getType() != null && classAttribute.getType() != "") {
				sb.append("type=\"" + xsdPrefix + ":"+classAttribute.getType()+"\" ");
			} else {
				sb.append("type=\"" + xsdPrefix + ":string\" ");
			}
			
			if (classAttribute.isRequired()) {
				sb.append("use=\"required\" ");
			}
			
			if (classAttribute.getDefaultValue() != null) {
				sb.append("default=\""+classAttribute.getDefaultValue()
						.replaceAll("&", "&amp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						+"\" ");
			}
			
			sb.append(">\n");
			
			//Write documentation into Element
			writeDocumentation(sb, classAttribute.getComment());
			
			sb.append("</" + xsdPrefix + ":attribute>\n");

		}
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(tabSpace + "</" + xsdPrefix + ":extension>\n");
			
			sb.append(tabSpace + "</" + xsdPrefix +":complexContent>\n");
		}
		
	}
	
	private void writeDocumentation(StringBuilder sb, String comment) {
		if (comment == null || comment.equals("")) {
			return;
		}
		
		sb.append("<" + xsdPrefix + ":annotation>\n");
		sb.append("<" + xsdPrefix + ":documentation xml:lang=\"en\">\n");
        sb.append(comment.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;"));
        sb.append("</" + xsdPrefix + ":documentation>\n");
        sb.append("</" + xsdPrefix + ":annotation>\n");
	}

}
