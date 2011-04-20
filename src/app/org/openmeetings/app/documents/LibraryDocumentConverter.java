package org.openmeetings.app.documents;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.stringhandlers.StringComparer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;



public class LibraryDocumentConverter {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LibraryDocumentConverter.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static final String fileExt = ".wml";
	
	private static final String wmlFolderName = "stored/";

	private static LibraryDocumentConverter instance;

	private LibraryDocumentConverter() {}

	public static synchronized LibraryDocumentConverter getInstance() {
		if (instance == null) {
			instance = new LibraryDocumentConverter();
		}
		return instance;
	}
	
	public String writeToLocalFolder(String filePath, String fileName, ArrayList objList) {
		try {
			
			log.debug("filePath: "+filePath);
			
			String fileNameExtName = fileName.substring(fileName.length()-4,fileName.length());
			if (fileNameExtName.equals(fileExt)){
				fileName = StringComparer.getInstance().compareForRealPaths(fileName.substring(0, fileName.length()-4));
			} else {
				fileName = StringComparer.getInstance().compareForRealPaths(fileName.substring(0, fileName.length()));
			}
			
			if (fileName.length()<=0){
				//return new Long(-21);
				return "-20";
			}
			
			String filepathComplete = filePath+wmlFolderName+fileName+fileExt;
			
			//Add the Folder for the Room if it does not exist yet
			File localFolder = new File(filePath);
			if (!localFolder.exists()){
				localFolder.mkdir();
			}		
			//Add the Folder for the wmlFiles if it does not exist yet
			File localFolder2 = new File(filePath+wmlFolderName);
			if (!localFolder2.exists()){
				localFolder2.mkdir();
			}
			
			if (this.checkFileExist(filepathComplete)){
				//return new Long(-20);
				return "-20";
			}		
			
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			String xmlString = xStream.toXML(objList);		
			
			PrintWriter pw = new PrintWriter(new FileWriter(filepathComplete));
		    pw.println(xmlString);
		    pw.flush();
		    pw.close();
	    
		    //return new Long(1);
		    
		    return filepathComplete;
		    
		} catch (Exception err){
			log.error("writeToLocalFolder",err);
		}
		
		return null;
		
	}
	
	private boolean checkFileExist(String filepathComplete){
		try {
			File f = new File(filepathComplete);
			return f.exists();
		} catch (Exception err){
			log.error("checkFileExist",err);
		}
		return true;
	}	
	
	/**
	 * @deprecated
	 * @param filepathComplete
	 * @param wmlData
	 * @return
	 */
	private Long writeFileToLocation(String filepathComplete, String wmlData){
		try {

			FileWriter fileWriter = new FileWriter(filepathComplete);

			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			
			buffWriter.write(wmlData);
			
			buffWriter.flush();
			buffWriter.close();
			
			return new Long(1);
		} catch (Exception err){
			log.error("writeFileToLocation",err);
		}
		return null;
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param paint
	 */
	private void createNodesByPaint(Document document, Element baseElement, LinkedHashMap paint){
		try {
			
			Element pointsName = document.createElement("points");
			baseElement.appendChild(pointsName);
			LinkedHashMap pointsMap = (LinkedHashMap) paint.get(1);
			for (Iterator it = pointsMap.keySet().iterator();it.hasNext();){
				Integer key = (Integer) it.next();
				LinkedHashMap point = (LinkedHashMap) pointsMap.get(key);
				Element pointElement = document.createElement(point.get(0).toString());
				pointElement.setAttribute("val1", point.get(1).toString());
				pointElement.setAttribute("val2", point.get(2).toString());
				pointElement.setAttribute("val3", point.get(3).toString());
				pointElement.setAttribute("val4", point.get(4).toString());
				pointsName.appendChild(pointElement);
			}
			
			Element fillstyle = document.createElement("fillstyle");
			baseElement.appendChild(fillstyle);
			Text fillstyleT = document.createTextNode(paint.get(2).toString());
			fillstyle.appendChild(fillstyleT);
			
			Element linewidth = document.createElement("linewidth");
			baseElement.appendChild(linewidth);
			Text linewidthT = document.createTextNode(paint.get(3).toString());
			linewidth.appendChild(linewidthT);
			
			Element strokestyle = document.createElement("strokestyle");
			baseElement.appendChild(strokestyle);
			Text strokestyleT = document.createTextNode(paint.get(4).toString());
			strokestyle.appendChild(strokestyleT);
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(paint.get(5).toString());
			counter.appendChild(counterT);			
			
			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(paint.get(6).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(paint.get(7).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(paint.get(8).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(paint.get(9).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(paint.get(10).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByPaint",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param letter
	 */
	private void createNodesByLetter(Document document, Element baseElement, LinkedHashMap letter){
		try {
			
			Element textvalue = document.createElement("textforfield");
			baseElement.appendChild(textvalue);
			Text textvalueT = document.createTextNode(letter.get(1).toString());
			textvalue.appendChild(textvalueT);
			
			Element fgcolor = document.createElement("fgcolor");
			baseElement.appendChild(fgcolor);
			Text fgcolorT = document.createTextNode(letter.get(2).toString());
			fgcolor.appendChild(fgcolorT);
			
			Element fontsize = document.createElement("fontsize");
			baseElement.appendChild(fontsize);
			Text fontsizeT = document.createTextNode(letter.get(3).toString());
			fontsize.appendChild(fontsizeT);			
			
			Element fontstyle = document.createElement("fontstyle");
			baseElement.appendChild(fontstyle);
			Text fontstyleT = document.createTextNode(letter.get(4).toString());
			fontstyle.appendChild(fontstyleT);	
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(letter.get(6).toString());
			counter.appendChild(counterT);			
			
			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(letter.get(6).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(letter.get(7).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(letter.get(8).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(letter.get(9).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(letter.get(10).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByLetter",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param image
	 */
	private void createNodesByImage(Document document, Element baseElement, LinkedHashMap image){
		try {
			
			Element urlname = document.createElement("urlname");
			baseElement.appendChild(urlname);
			Text urlnameT = document.createTextNode(image.get(1).toString());
			urlname.appendChild(urlnameT);
			
			Element baseurl = document.createElement("baseurl");
			baseElement.appendChild(baseurl);
			Text baseurlT = document.createTextNode(image.get(2).toString());
			baseurl.appendChild(baseurlT);
			
			Element filename = document.createElement("filename");
			baseElement.appendChild(filename);
			Text filenameT = document.createTextNode(image.get(3).toString());
			filename.appendChild(filenameT);
			
			Element modulename = document.createElement("modulename");
			baseElement.appendChild(modulename);
			Text modulenameT = document.createTextNode(image.get(4).toString());
			modulename.appendChild(modulenameT);
			
			Element parentpath = document.createElement("parentpath");
			baseElement.appendChild(parentpath);
			Text parentpathT = document.createTextNode(image.get(5).toString());
			parentpath.appendChild(parentpathT);
			
			Element room = document.createElement("room");
			baseElement.appendChild(room);
			Text roomT = document.createTextNode(image.get(6).toString());
			room.appendChild(roomT);
			
			Element domain = document.createElement("domain");
			baseElement.appendChild(domain);
			Text domainT = document.createTextNode(image.get(7).toString());
			domain.appendChild(domainT);
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(image.get(8).toString());
			counter.appendChild(counterT);				
			
			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(image.get(9).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(image.get(10).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(image.get(11).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(image.get(12).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(image.get(13).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByImage",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param paintObject
	 */
	private void createNodesByObject(Document document, Element baseElement, LinkedHashMap paintObject){
		try {
			
			Element fillstyle = document.createElement("fillstyle");
			baseElement.appendChild(fillstyle);
			Text fillstyleT = document.createTextNode(paintObject.get(1).toString());
			fillstyle.appendChild(fillstyleT);
			
			Element linewidth = document.createElement("linewidth");
			baseElement.appendChild(linewidth);
			Text linewidthT = document.createTextNode(paintObject.get(2).toString());
			linewidth.appendChild(linewidthT);
			
			Element strokestyle = document.createElement("strokestyle");
			baseElement.appendChild(strokestyle);
			Text strokestyleT = document.createTextNode(paintObject.get(3).toString());
			strokestyle.appendChild(strokestyleT);
			
			Element startx = document.createElement("startx");
			baseElement.appendChild(startx);
			Text startxT = document.createTextNode(paintObject.get(4).toString());
			startx.appendChild(startxT);
			
			Element starty = document.createElement("starty");
			baseElement.appendChild(starty);
			Text startyT = document.createTextNode(paintObject.get(5).toString());
			starty.appendChild(startyT);

			Element endx = document.createElement("endx");
			baseElement.appendChild(endx);
			Text endxT = document.createTextNode(paintObject.get(6).toString());
			endx.appendChild(endxT);

			Element endy = document.createElement("endy");
			baseElement.appendChild(endy);
			Text endyT = document.createTextNode(paintObject.get(7).toString());
			endy.appendChild(endyT);
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(paintObject.get(8).toString());
			counter.appendChild(counterT);					
			
			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(paintObject.get(9).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(paintObject.get(10).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(paintObject.get(11).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(paintObject.get(12).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(paintObject.get(13).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByObject",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param paintObject
	 */
	private void createNodesByRectAndEllipse(Document document, Element baseElement, LinkedHashMap paintObject){
		try {

			Element stroke = document.createElement("stroke");
			baseElement.appendChild(stroke);
			Text strokeT = document.createTextNode(paintObject.get(1).toString());
			stroke.appendChild(strokeT);

			Element line = document.createElement("line");
			baseElement.appendChild(line);
			Text lineT = document.createTextNode(paintObject.get(2).toString());
			line.appendChild(lineT);
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(paintObject.get(3).toString());
			counter.appendChild(counterT);			

			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(paintObject.get(4).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(paintObject.get(5).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(paintObject.get(6).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(paintObject.get(7).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(paintObject.get(8).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByRect",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param document
	 * @param baseElement
	 * @param image
	 */
	private void createNodesBySWF(Document document, Element baseElement, LinkedHashMap image){
		try {
			
			Element urlname = document.createElement("urlname");
			baseElement.appendChild(urlname);
			Text urlnameT = document.createTextNode(image.get(1).toString());
			urlname.appendChild(urlnameT);
			
			Element baseurl = document.createElement("baseurl");
			baseElement.appendChild(baseurl);
			Text baseurlT = document.createTextNode(image.get(2).toString());
			baseurl.appendChild(baseurlT);
			
			Element filename = document.createElement("filename");
			baseElement.appendChild(filename);
			Text filenameT = document.createTextNode(image.get(3).toString());
			filename.appendChild(filenameT);
			
			Element modulename = document.createElement("modulename");
			baseElement.appendChild(modulename);
			Text modulenameT = document.createTextNode(image.get(4).toString());
			modulename.appendChild(modulenameT);
			
			Element parentpath = document.createElement("parentpath");
			baseElement.appendChild(parentpath);
			Text parentpathT = document.createTextNode(image.get(5).toString());
			parentpath.appendChild(parentpathT);
			
			Element room = document.createElement("room");
			baseElement.appendChild(room);
			Text roomT = document.createTextNode(image.get(6).toString());
			room.appendChild(roomT);
			
			Element domain = document.createElement("domain");
			baseElement.appendChild(domain);
			Text domainT = document.createTextNode(image.get(7).toString());
			domain.appendChild(domainT);
			
			Element slideNumber = document.createElement("slideNumber");
			baseElement.appendChild(slideNumber);
			Text slideNumberT = document.createTextNode(image.get(8).toString());
			slideNumber.appendChild(slideNumberT);			
			
			Element innerx = document.createElement("innerx");
			baseElement.appendChild(innerx);
			Text innerxT = document.createTextNode(image.get(9).toString());
			innerx.appendChild(innerxT);	
			
			Element innery = document.createElement("innery");
			baseElement.appendChild(innery);
			Text inneryT = document.createTextNode(image.get(10).toString());
			innery.appendChild(inneryT);	
			
			Element innerwidth = document.createElement("innerwidth");
			baseElement.appendChild(innerwidth);
			Text innerwidthT = document.createTextNode(image.get(11).toString());
			innerwidth.appendChild(innerwidthT);	
			
			Element innerheight = document.createElement("innerheight");
			baseElement.appendChild(innerheight);
			Text innerheightT = document.createTextNode(image.get(12).toString());
			innerheight.appendChild(innerheightT);
			
			Element zoomlevel = document.createElement("zoomlevel");
			baseElement.appendChild(zoomlevel);
			Text zoomlevelT = document.createTextNode(image.get(13).toString());
			zoomlevel.appendChild(zoomlevelT);
			
			Element initwidth = document.createElement("initwidth");
			baseElement.appendChild(initwidth);
			Text initwidthT = document.createTextNode(image.get(14).toString());
			initwidth.appendChild(initwidthT);
			
			Element initheight = document.createElement("initheight");
			baseElement.appendChild(initheight);
			Text initheightT = document.createTextNode(image.get(15).toString());
			initheight.appendChild(initheightT);
			
			Element currentzoom = document.createElement("currentzoom");
			baseElement.appendChild(currentzoom);
			Text currentzoomT = document.createTextNode(image.get(16).toString());
			currentzoom.appendChild(currentzoomT);
			
			Element counter = document.createElement("counter");
			baseElement.appendChild(counter);
			Text counterT = document.createTextNode(image.get(17).toString());
			counter.appendChild(counterT);				
			
			Element x = document.createElement("x");
			baseElement.appendChild(x);
			Text xT = document.createTextNode(image.get(18).toString());
			x.appendChild(xT);
			
			Element y = document.createElement("y");
			baseElement.appendChild(y);
			Text yT = document.createTextNode(image.get(19).toString());
			y.appendChild(yT);
			
			Element width = document.createElement("width");
			baseElement.appendChild(width);
			Text widthT = document.createTextNode(image.get(20).toString());
			width.appendChild(widthT);
			
			Element height = document.createElement("height");
			baseElement.appendChild(height);
			Text heightT = document.createTextNode(image.get(21).toString());
			height.appendChild(heightT);
			
			Element layername = document.createElement("layername");
			baseElement.appendChild(layername);
			Text layernameT = document.createTextNode(image.get(22).toString());
			layername.appendChild(layernameT);
			
		} catch (Exception err){
			log.error("createNodesByImage",err);
		}
	}
	
}
