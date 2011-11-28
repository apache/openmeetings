package org.openlaszlo.generator;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.openlaszlo.generator.elements.ClassElement;
import org.openlaszlo.generator.elements.ClassElementList;
import org.openlaszlo.generator.elements.XsdUtil;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class GenerateSchema implements ContentHandler, LexicalHandler {
	
	private final ClassElementList elementList = new ClassElementList();
	
	FilenameFilter folderFilter = new FilenameFilter(){
		 public boolean accept(File b, String name) {
			  //We do not scan folder that start with a "."
			  if (name.startsWith(".")) {
	    		  return false;
	    	  }
//			  if (name.equals("incubator")) {
//				  return false;
//			  }s
	    	  String absPath = b.getAbsolutePath()+File.separatorChar+name;
	    	  File f = new File (absPath);
	    	  if (f.isDirectory()) {
	    		  
	    		  File checkForIgnore = new File(absPath+File.separatorChar+".ignore_dtd");
	    		  if (checkForIgnore.exists()) {
	    			  return false;
	    		  }
	    		  
	    		  return true;
	    	  }
	    	  
	          return false;
	     }
	};
	
	FilenameFilter lzxFilter = new FilenameFilter(){
		 public boolean accept(File b, String name) {
	          return name.endsWith(".lzx");
	     }
	};
	
	FilenameFilter hardCopyFileFilter = new FilenameFilter() {
		public boolean accept(File b, String name) {
			if (name.startsWith(".")) {
				return false;
			}
			if (name.endsWith(".lzx")){
				return false;
			}
			return true;
		}
	};
	
	private String currentClassName = "";

	private String currentFile;
	
	private String currentComment = "";
	
	//private File currentOutPutFile;

	/**
	 * The hook can also rewrite complete directories, so 
	 * we need a variable to disable to schema generation
	 * while running the rewrite hook
	 */
	private boolean generateSchema = true;

	private String baseInputPath;

	private String baseOutputPath;

	private ArrayList<String> baseOutputPathSegments;

	private ArrayList<String> baseInputPathSegments;

	private ArrayList<String> replaceClassElementList;
	
	public void printXsd(String fileName, String staticFileSectionFilepath) {
		elementList.xsdPrint(false, fileName, staticFileSectionFilepath);
		System.out.println("written file to "+fileName);
	}
	
	public XsdUtil getXsdUtil() {
		return elementList.getXsdUtil();
	}
	
	public void scanFolder(String filePath) {
		generateSchema  = true;
		runFolder(filePath);
	}
	
	public void rewriteFolder(String inputPath, String outputPath) {
		try {
			generateSchema = false;	
			baseInputPath = inputPath;
			baseOutputPath = outputPath;
			
			File baseInputFolder = new File(baseInputPath);
			
			if (!baseInputFolder.isDirectory()) {
				new Exception("Input dir is no directory");
			}
			
			baseInputPathSegments = getPathSegmentsByString(baseInputPath);
			
			System.out.println("IN: "+baseInputPathSegments.toString());
			
			File baseOutputFolder = new File(baseOutputPath);
			
			if (!baseOutputFolder.exists()) {
				baseOutputFolder.mkdir();
			} else {
				FileUtils.deleteDirectory(baseOutputFolder);
				baseOutputFolder.delete();
				
				baseOutputFolder = new File(baseOutputPath);
				baseOutputFolder.mkdir();
			}
			
			if (!baseOutputFolder.isDirectory()) {
				new Exception("Input dir is no directory");
			}
			baseOutputPathSegments = getPathSegmentsByString(baseOutputPath);
			
			System.out.println("Out "+baseOutputPathSegments.toString());
			
			runFolder(inputPath);
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		}
	}
	
	private ArrayList<String> getPathSegmentsByString(String baseInputPath) {
		
		ArrayList<String> pathSegments = loopPathSegements(new File(baseInputPath));
		
		Collections.reverse(pathSegments);
		return pathSegments;
	}
	
	private ArrayList<String> loopPathSegements(File f) {
		ArrayList<String> pathSegments = new ArrayList<String>();
		
		if (f.isDirectory()) {
			pathSegments.add(f.getName());
		}
		
		if (f.getParentFile() != null) {
			pathSegments.addAll(loopPathSegements(f.getParentFile()));
		}
		
		return pathSegments;
	}
	
	public void runFolder(String filePath) {
		try {
			
			File baseFolder = new File(filePath);
			
			if (!baseFolder.exists()) {
				throw new Exception("Base path does not exist "+filePath);
			}
			
			if (baseFolder.isFile()) {
				scanFile(filePath);
			} else if (baseFolder.isDirectory()) {
				
				System.err.println("SCAN "+baseFolder.getPath());
				
				if (!generateSchema) {
					
					for (File hardcopy : baseFolder.listFiles(hardCopyFileFilter)) {
						simpleCopyFileToOutput(hardcopy);
					}
				}
				
				for (String folder : baseFolder.list(folderFilter)) {
					runFolder(filePath + File.separatorChar + folder);
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
	
	private void simpleCopyFileToOutput(File file) throws Exception {
		
		//Dirs are in the file iteration cause they can contain
		//subfiles that need to be moved
		if (file.isDirectory()) {
			return;
		}
		
		String outputPath = generateOutPutPath(file);
		
		File outputFolder = new File(outputPath);
		outputFolder.mkdirs();
		
		FileUtils.copyFile(file, new File(outputPath + file.getName()));
		
	}
	
	/**
	 * This helper method does calculate the folder path to the output directory
	 * @param in
	 * @return
	 */
	private String generateOutPutPath(File in) throws Exception {
		ArrayList<String> fileSegments = getPathSegmentsByString(in.getPath());
		
		for (int i=0;i<baseInputPathSegments.size();i++) {
			
			fileSegments.remove(0);
			
		}
		
		String outPutPath = "";
		
		for (int i=0;i<baseOutputPathSegments.size();i++) {
			outPutPath += baseOutputPathSegments.get(i) + File.separatorChar;
		}
		for (int i=0;i<fileSegments.size();i++) {
			outPutPath += fileSegments.get(i) + File.separatorChar;
		}
		
		System.err.println("P1 "+outPutPath);
		
		return outPutPath;
	}

	public void scanFile(String filePath) {
		try {
			
			this.currentFile = filePath;
			
			InputSource is = new InputSource(filePath);
			is.setEncoding("UTF-8");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			
			//factory.setValidating(false);
			XMLReader parser = factory.newSAXParser().getXMLReader();
			
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) {
					return new InputSource(
					new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
				}
			});

			parser.setProperty(
				      "http://xml.org/sax/properties/lexical-handler",
				      this
				      ); 
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
		currentClassName = "";
		replaceClassElementList = new ArrayList<String>();
	}
	
	private String projectXsdReplacement = "<canvas xmlns=\"http://localhost/openlaszlo/lzx\" "
			+ "xmlns:project=\"http://localhost/project/lzx\" "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			+ "xsi:schemaLocation=\"http://localhost/openlaszlo/lzx ../../my.xsd "
			+ "http://localhost/project/lzx ../../project.xsd \"";
	
	private String libraryXsdReplacement = "<library xmlns=\"http://localhost/openlaszlo/lzx\" "
			+ "xmlns:project=\"http://localhost/project/lzx\" "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
			+ "xsi:schemaLocation=\"http://localhost/openlaszlo/lzx ../../my.xsd "
			+ "http://localhost/project/lzx ../../project.xsd \"";

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		try {
			if (!generateSchema) {
				
				File fInput = new File(currentFile);
				
				@SuppressWarnings("unchecked")
				List<String> lines = FileUtils.readLines(fInput, "UTF-8");
				
				List<String> encodedLines = new LinkedList<String>();
				
				for (String line : lines) {
					
					String resultingLine = line;
					
					resultingLine.replaceAll("<canvas", projectXsdReplacement);
					
					for (String classElementName : replaceClassElementList) {
						
						resultingLine = resultingLine.replaceAll("<"+classElementName,"<"+getXsdUtil().getProjectXsdPrefix()+":"+classElementName);
						resultingLine = resultingLine.replaceAll("</"+classElementName,"</"+getXsdUtil().getProjectXsdPrefix()+":"+classElementName);
						
					}
					
					//resultingLine.replaceAll("canvas", projectXsdReplacement);
					resultingLine.replaceAll("\\<library", libraryXsdReplacement);
					
					encodedLines.add(resultingLine);
					
				}
				
				String outputPath = generateOutPutPath(fInput);
				
				File outputFolder = new File(outputPath);
				outputFolder.mkdirs();
				
				File fOut = new File(outputPath + fInput.getName());
				
				FileUtils.writeLines(fOut, "UTF-8", encodedLines);
				
			}
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		}
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
		try {
			
			//no need to remember the nodes here if there is a rewrite running
			if (!generateSchema) {
				
				ClassElement cElement = elementList.checkClassAttribute(qName);
				if (cElement != null) {
					replaceClassElementList.add(cElement.getName());
				} 
				return;
			}
			
			if (qName.equals("class") || qName.equals("interface") ) {
				
				String className = atts.getValue("name");
				
				if (className == null) {
					return;
				}
				
				String extendsName = atts.getValue("extends");
				
				if (extendsName == null) {
					if (className.equals("node")) {
						extendsName = "";
					} else {
						extendsName = "node";
					}
				} else if (extendsName.equals("false")) {
					extendsName = "";
				}
				
				String isRootAsStr = atts.getValue("isRoot");
				boolean isRoot = false;
				if (isRootAsStr != null && isRootAsStr.equals("true")) {
					isRoot = true;
				}
				
				currentClassName = className;
				elementList.addClassElement(className, extendsName, isRoot, currentComment);
				
			} else if (qName.equals("attribute") || qName.equals("event")) {
				
				if (currentClassName.length() == 0) {
					return;
				}
				
				String attrName = atts.getValue("name");
				
				if (attrName == null 
						|| attrName.startsWith("_")
						|| currentComment.indexOf("@keywords private") >= 0) {
					return;
				}
				
				String requiredAsStr = atts.getValue("required");
				boolean required = false;
				if (requiredAsStr != null && requiredAsStr.equals("true")) {
					required = true;
				}
				
				String type = atts.getValue("type");
				if (type == null) {
					type = "string";
				} 

				String defaultValue = atts.getValue("value");
				
				elementList.addClassAttribute(attrName, required, currentClassName, type, defaultValue, currentComment);
				
			}
		} catch (Exception err) {
			System.err.println("Error in File "+currentFile);
			err.printStackTrace();
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// Clear comments
		currentComment = "";
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
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

	public void comment(char[] ch, int start, int length) throws SAXException {
		//no need to remember the comment here if there is a rewrite running
		if (!generateSchema) {
			return;
		}
		String text = new String(ch, start, length);
		if (text.startsWith("-")) {
			currentComment = text.substring(1, length);
		}
	}

	public void endCDATA() throws SAXException {
		// TODO Auto-generated method stub
	}

	public void endDTD() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startCDATA() throws SAXException {
		// TODO Auto-generated method stub
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
