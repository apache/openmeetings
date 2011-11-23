package org.openlaszlo.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import javax.xml.parsers.SAXParserFactory;

import org.openlaszlo.generator.elements.ElementList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class SimpleGenerateDTD implements ContentHandler {
	
	private ElementList elementList = new ElementList();
	
	FilenameFilter folderFilter = new FilenameFilter(){
		 public boolean accept(File b, String name) {
			  //We do not scan folder that start with a "."
			  if (name.startsWith(".")) {
	    		  return false;
	    	  }
	    	  String absPath = b.getAbsolutePath()+File.separatorChar+name;
	    	  File f = new File (absPath);
	          return f.isDirectory();
	     }
	};
	
	FilenameFilter lzxFilter = new FilenameFilter(){
		 public boolean accept(File b, String name) {
	          return name.endsWith(".lzx");
	     }
	};
	
	private LinkedList<String> openNodes = new LinkedList<String>();
	
	public static void main(String... args) {
		new SimpleGenerateDTD("WebContent/src/");
	}
	
	public SimpleGenerateDTD(String basePath) {
		this.scanFolder(basePath);
		
		// elementList.filePrint();
		elementList.filePrint(true);
	}
	
	public void scanFolder(String filePath) {
		try {
			
			File baseFolder = new File(filePath);
			
			if (!baseFolder.exists()) {
				throw new Exception("Base path does not exist "+filePath);
			}
			
			if (baseFolder.isFile()) {
				scanFile(filePath);
			} else if (baseFolder.isDirectory()) {
				for (String folder : baseFolder.list(folderFilter)) {
					scanFolder(filePath + File.separatorChar + folder);
				}
				for (String file : baseFolder.list(lzxFilter)) {
					scanFile(filePath + File.separatorChar +file);
				}
			}
			
			
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		} 
	}
	
	public void scanFile(String filePath) {
		try {
			
			InputSource is = new InputSource(filePath);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			XMLReader parser = factory.newSAXParser().getXMLReader();
			
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) {
					return new InputSource(
					new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
				}
			});

			
			parser.setContentHandler(this);
            parser.parse(is);
            
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		}
	}

	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		openNodes = new LinkedList<String>();
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		//System.out.println("startElement "+qName);
		String currentParent = "";
		if (openNodes.size() > 0) {
			currentParent = openNodes.getLast();
		}
		
		elementList.addElement(qName, atts, currentParent);
		
		openNodes.add(qName);
		
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		
		//Removes last occurrence of the element
		openNodes.removeLastOccurrence(qName);
		
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
