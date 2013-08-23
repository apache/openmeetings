/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.data.record;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.geom.GeomPoint;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class WhiteboardMapToSVG extends BatikMethods {

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteboardMapToSVG.class, OpenmeetingsVariables.webAppRootKey);

	private WhiteboardMapToSVG() {
	}

	private static WhiteboardMapToSVG instance = null;

	public static synchronized WhiteboardMapToSVG getInstance() {
		if (instance == null) {
			instance = new WhiteboardMapToSVG();
		}

		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public SVGGraphics2D convertMapToSVG(SVGGraphics2D svgGenerator, List whiteBoardMap) throws Exception {
		
		//log.debug("convertMapToSVG: "+whiteBoardMap.size());
		
        for (Iterator iter = whiteBoardMap.iterator();iter.hasNext();) {
        	
        	List graphObject = (List) iter.next();
        	
        	String graphType = graphObject.get(0).toString();
        	
        	//log.debug("graphType: "+graphType);
        	
        	if (graphType.equals("paint")) {
        		
        		List pointsList = (List) graphObject.get(1);
        		
        		Integer lineWidth = Integer.valueOf(graphObject.get(3).toString()).intValue();
        		Integer col = Integer.valueOf(graphObject.get(4).toString()).intValue();
        		
        		Float alpha = Float.valueOf(graphObject.get(5).toString()).floatValue();
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		
        		//log.debug("x,y,width,height: "+x+","+y+","+width+","+height);
        		
        		//Draw a Painting
    	        SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
    	        //SVGGraphics2D svgGenerator2 = new SVGGraphics2D(document);
    	         
    	        log.debug("pointsList: "+pointsList);
    	        this.drawPointsObject(svgGenerator_temp, pointsList, new Color(col), lineWidth, x, y, alpha);
    	        
        	} else if (graphType.equals("rectangle")) {
        		
        		/*actionObject[0] = 'rectangle';
		        actionObject[1] = stroke;
		        actionObject[2] = line;
		        actionObject[3] = fill;
		        actionObject[4] = strokeDis;
		        actionObject[5] = fillDis;
		        actionObject[6] = this.currentrectangleOpacity;*/
        		
        		Integer lineWidth = Integer.valueOf(graphObject.get(2).toString()).intValue();
        		
        		Integer stroke = Integer.valueOf(graphObject.get(1).toString()).intValue();
        		Integer strokeDis= Integer.valueOf(graphObject.get(4).toString()).intValue();
        		
        		Color strokeColor = null;
        		if (strokeDis != -1) {
        			strokeColor = new Color(stroke);
        		}
        		
        		Integer fill = Integer.valueOf(graphObject.get(3).toString()).intValue();
        		Integer fillDis= Integer.valueOf(graphObject.get(5).toString()).intValue();
        		
        		Color fillColor = null;
        		if (fillDis != -1) {
        			fillColor = new Color(fill);
        		}
        		
        		Float alpha = Float.valueOf(graphObject.get(6).toString()).floatValue();
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		Double width = Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue();
        		Double height = Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue();
        	
        		SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
        		this.paintRect2D(svgGenerator_temp, x, y, width, height, strokeColor, lineWidth, fillColor, alpha);
        		
        	} else if (graphType.equals("ellipse")) {
        		
        		Integer lineWidth = Integer.valueOf(graphObject.get(2).toString()).intValue();
        		
        		Integer stroke = Integer.valueOf(graphObject.get(1).toString()).intValue();
        		Integer strokeDis= Integer.valueOf(graphObject.get(4).toString()).intValue();
        		
        		Color strokeColor = null;
        		if (strokeDis != -1) {
        			strokeColor = new Color(stroke);
        		}
        		
        		Integer fill = Integer.valueOf(graphObject.get(3).toString()).intValue();
        		Integer fillDis= Integer.valueOf(graphObject.get(5).toString()).intValue();
        		
        		Color fillColor = null;
        		if (fillDis != -1) {
        			fillColor = new Color(fill);
        		}
        		
        		Float alpha = Float.valueOf(graphObject.get(6).toString()).floatValue();
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		Double width = Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue();
        		Double height = Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue();
        	
        		SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
        		this.paintEllipse2D(svgGenerator_temp, x, y, width, height, strokeColor, lineWidth, fillColor, alpha);
        		
        	} else if (graphType.equals("letter")) {
        		
        		String text = graphObject.get(1).toString();
        		Color fontColor = new Color (Integer.valueOf(graphObject.get(2).toString()).intValue());
        		Integer fontSize = Integer.valueOf(graphObject.get(3).toString()).intValue();
        		
        		String fontStyle = graphObject.get(4).toString();
        		Integer style = null;
        		if (fontStyle.equals("plain")) {
        			style = Font.PLAIN;
        		} else if (fontStyle.equals("bold")) {
        			style = Font.BOLD;
        		} else if (fontStyle.equals("italic")) {
        			style = Font.ITALIC;
        		} else if (fontStyle.equals("bolditalic")) {
        			style = Font.ITALIC+Font.BOLD;
        		}
        		
        		log.debug("fontStyle,style "+style+" fs: "+fontStyle);
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		Double width = Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue();
        		Double height = Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue();
        		
        		SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
        		this.paintTextByWidthHeight(svgGenerator_temp, (int) Math.round(x), (int) Math.round(y), (int) Math.round(width), 
        					(int) Math.round(height), text, style, fontSize, fontColor);
        		
        	} else if (graphType.equals("drawarrow")) {
        		
        		Integer thickness = Integer.valueOf(graphObject.get(2).toString()).intValue();
        		
        		Integer stroke = Integer.valueOf(graphObject.get(1).toString()).intValue();
        		Integer strokeDis= Integer.valueOf(graphObject.get(4).toString()).intValue();
        		
        		Color strokeColor = null;
        		if (strokeDis != -1) {
        			strokeColor = new Color(stroke);
        		}
        		
        		Integer fill = Integer.valueOf(graphObject.get(3).toString()).intValue();
        		Integer fillDis= Integer.valueOf(graphObject.get(5).toString()).intValue();
        		
        		Color fillColor = null;
        		if (fillDis != -1) {
        			fillColor = new Color(fill);
        		}
        		
        		Float alpha = Float.valueOf(graphObject.get(6).toString()).floatValue();
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		//Double width = Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue();
        		//Double height = Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue();
        		
        		Double x1 = Double.valueOf(graphObject.get(7).toString()).doubleValue();
        		Double y1 = Double.valueOf(graphObject.get(8).toString()).doubleValue();
        		Double x2 = Double.valueOf(graphObject.get(9).toString()).doubleValue();
        		Double y2 = Double.valueOf(graphObject.get(10).toString()).doubleValue();
        		
        		GeomPoint start = new GeomPoint();
    	        start.setLocation(x+x1,y+y1);
    	        GeomPoint end = new GeomPoint();
    	        end.setLocation(x+x2,y+y2);
    	        
    	        SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
    	        this.drawArrow(svgGenerator_temp, start, end, thickness, alpha, strokeColor,fillColor);
        	} else if (graphType.equals("image")) {
        		
        		//log.debug("graphObject image "+graphObject);
        		//log.debug("",graphObject);
        		
        		String room = graphObject.get(6).toString();
        		String parentPath = graphObject.get(5).toString();
        		String fileItemName = graphObject.get(3).toString();
        		
        		File imageFile = new File(OmFileHelper.getUploadDir(), room);
        		
        		if (parentPath.length() > 1) {
        			imageFile = new File(imageFile, parentPath);
        		}
        		
        		//log.debug("fileItemName: "+fileItemName);
        		
        		File myFile = new File(imageFile, fileItemName);
        		
        		if (myFile.exists() && myFile.canRead()) {
        			
        			Image myImage = ImageIO.read(myFile);
        			
        			int x = (int) Math.round(Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue());
        			int y = (int) Math.round(Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue());
        			int width = (int) Math.round(Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue());
        			int height = (int) Math.round(Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue());
	        		
	        		SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
	        		svgGenerator_temp.drawImage(myImage, x, y, width, height, null);
        			
        		} else {
        			log.error("tried to inculde a non existing File into SVG/Image Export Path: " + myFile);
        		}
        		
        	} else if(graphType.equals("line") || graphType.equals("uline")) {
        		
//        		actionObject[0] = 'line';
//                actionObject[1] = stroke;
//                actionObject[2] = line;
//                actionObject[3] = opacity;
//                actionObject[4] = x1  
//                actionObject[5] = y1;  
//                actionObject[6] = x2;    
//                actionObject[7] = y2;    
//                actionObject[8] = this.counter; 
//                actionObject[9] = x;
//                actionObject[10] = y;
//                actionObject[11] = width;
//                actionObject[12] = height;  
//                actionObject[13] = newName;
        		
    			Integer lineWidth = Integer.valueOf(graphObject.get(2).toString()).intValue();
        		
        		Integer stroke = Integer.valueOf(graphObject.get(1).toString()).intValue();
        		Color strokeColor = new Color(stroke);
        		
        		Float alpha = Float.valueOf(graphObject.get(3).toString()).floatValue();
        		
        		Double x = Double.valueOf(graphObject.get(graphObject.size()-5).toString()).doubleValue();
        		Double y = Double.valueOf(graphObject.get(graphObject.size()-4).toString()).doubleValue();
        		//Double width = Double.valueOf(graphObject.get(graphObject.size()-3).toString()).doubleValue();
        		//Double height = Double.valueOf(graphObject.get(graphObject.size()-2).toString()).doubleValue();

        		Double x1 = Double.valueOf(graphObject.get(4).toString()).doubleValue();
        		Double y1 = Double.valueOf(graphObject.get(5).toString()).doubleValue();
        		Double x2 = Double.valueOf(graphObject.get(6).toString()).doubleValue();
        		Double y2 = Double.valueOf(graphObject.get(7).toString()).doubleValue();
        		
        		SVGGraphics2D svgGenerator_temp = new SVGGraphics2D(svgGenerator);
        		this.drawLine(svgGenerator_temp, x1, y1, x2, y2, strokeColor, lineWidth, x, y, alpha);
        		
        	} else {
        		log.error("tried to include a non supported Graph-Object graphType: "+graphType);
        	}
        	
        }
        
        return svgGenerator;
	}

}
