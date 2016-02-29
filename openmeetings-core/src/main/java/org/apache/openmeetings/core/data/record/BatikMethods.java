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
package org.apache.openmeetings.core.data.record;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.geom.ArrowStyle;
import org.apache.openmeetings.util.geom.GeomPoint;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BatikMethods {

	private static final Logger log = Red5LoggerFactory.getLogger(BatikMethods.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private ConfigurationDao configurationDao;

	public void paintTextByWidthHeight(SVGGraphics2D g2d, int x1, int y1, int width, int height, String text, int style, int size,
			Color fontColor) throws Exception {

		this.drawTextByJTextArea(g2d, x1, y1, width, height, text, "Verdana", style, size, fontColor);
	}

	public void _paintTextByWidthHeight(SVGGraphics2D g2d, int x1, int y1, int width, int height, String text, int style, int size,
			Color fontColor) throws Exception {

		String default_export_font = configurationDao.getConfValue("default_export_font", String.class, "");

		this.drawTextByString(g2d, x1, y1, width, height, text, default_export_font, style, size, fontColor);

	}

	@SuppressWarnings("rawtypes")
	public void drawPointsObject(Graphics2D g2d, List pointsList, Color lineColor, int size, double xObj, double yObj, float alpha)
			throws Exception {

		for (Iterator iter = pointsList.iterator(); iter.hasNext();) {
			List point = (List) iter.next();

			// log.debug("POINT FROM TO :" +
			// Double.valueOf(point.get(1).toString()).doubleValue()+","+
			// Double.valueOf(point.get(2).toString()).doubleValue()+","+
			// Double.valueOf(point.get(3).toString()).doubleValue()+","+
			// Double.valueOf(point.get(4).toString()).doubleValue());

			this.drawThickLine2DPaint(g2d, Double.valueOf(point.get(1).toString()).doubleValue(),
					Double.valueOf(point.get(2).toString()).doubleValue(), Double.valueOf(point.get(3).toString()).doubleValue(),
					Double.valueOf(point.get(4).toString()).doubleValue(), size, lineColor, xObj, yObj, alpha);
		}

	}

	public void drawLine(Graphics2D g2d, Double x1, Double y1, Double x2, Double y2, Color lineColor, int size, double xObj, double yObj,
			float alpha) throws Exception {

		this.drawThickLine2D(g2d, x1, y1, x2, y2, size, lineColor, xObj, yObj, alpha);

	}

	public void paintEllipse2D(Graphics2D g2d, double x, double y, double width, double height, Color linecoler, int thickness,
			Color fillColor, float alpha) throws Exception {

		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int[] rules = new int[8];

		// all possible Compositing Rules:
		rules[0] = AlphaComposite.SRC_OVER;
		rules[1] = AlphaComposite.DST_OVER;
		rules[2] = AlphaComposite.CLEAR;
		rules[3] = AlphaComposite.SRC;
		rules[4] = AlphaComposite.SRC_IN;
		rules[5] = AlphaComposite.DST_IN;
		rules[6] = AlphaComposite.SRC_OUT;
		rules[7] = AlphaComposite.DST_OUT;

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));

		// int x, int y, int width, int height

		if (linecoler != null) {
			g2d.setPaint(linecoler);
			g2d.draw(new Ellipse2D.Double(x, y, width, height));
		}

		if (fillColor != null) {
			g2d.setPaint(fillColor);
			g2d.fill(new Ellipse2D.Double(x, y, width, height));
		}

	}

	public void paintRect2D(Graphics2D g2d, double x, double y, double width, double height, Color linecoler, int thickness,
			Color fillColor, float alpha) throws Exception {

		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int[] rules = new int[8];

		// all possible Compositing Rules:
		rules[0] = AlphaComposite.SRC_OVER;
		rules[1] = AlphaComposite.DST_OVER;
		rules[2] = AlphaComposite.CLEAR;
		rules[3] = AlphaComposite.SRC;
		rules[4] = AlphaComposite.SRC_IN;
		rules[5] = AlphaComposite.DST_IN;
		rules[6] = AlphaComposite.SRC_OUT;
		rules[7] = AlphaComposite.DST_OUT;

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));

		// int x, int y, int width, int height

		if (linecoler != null) {
			g2d.setPaint(linecoler);
			g2d.draw(new Rectangle2D.Double(x, y, width, height));
		}

		if (fillColor != null) {
			g2d.setPaint(fillColor);
			g2d.fill(new Rectangle2D.Double(x, y, width, height));
		}

	}

	public void paintRect(Graphics2D g2d, int x, int y, int width, int height, Color linecoler, int thickness, Color fillColor)
			throws Exception {

		// int x, int y, int width, int height
		// g2d.fill(new Rectangle(x,y,width,height));

		g2d.setStroke(new BasicStroke(thickness));

		if (linecoler != null) {
			g2d.setPaint(linecoler);
			g2d.drawRect(x, y, width, height);
		}

		if (fillColor != null) {
			g2d.setPaint(fillColor);
			g2d.fillRect(x, y, width, height);
		}

	}

	public void paintLine(Graphics2D g2d, int x1, int y1, int x2, int y2, Color col) throws Exception {
		g2d.setPaint(col);
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void drawThickLine2DPaint(Graphics2D g2d, double x1, double y1, double x2, double y2, int width, Color c, double xObj,
			double yObj, float alpha) throws Exception {
		g2d.setPaint(c);

		int[] rules = new int[8];

		// all possible Compositing Rules:
		rules[0] = AlphaComposite.SRC_OVER;
		rules[1] = AlphaComposite.DST_OVER;
		rules[2] = AlphaComposite.CLEAR;
		rules[3] = AlphaComposite.SRC;
		rules[4] = AlphaComposite.SRC_IN;
		rules[5] = AlphaComposite.DST_IN;
		rules[6] = AlphaComposite.SRC_OUT;
		rules[7] = AlphaComposite.DST_OUT;

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		g2d.draw(line);
	}

	public void drawThickLine2D(Graphics2D g2d, double x1, double y1, double x2, double y2, int width, Color c, double xObj, double yObj,
			float alpha) throws Exception {
		g2d.setPaint(c);

		int[] rules = new int[8];

		// all possible Compositing Rules:
		rules[0] = AlphaComposite.SRC_OVER;
		rules[1] = AlphaComposite.DST_OVER;
		rules[2] = AlphaComposite.CLEAR;
		rules[3] = AlphaComposite.SRC;
		rules[4] = AlphaComposite.SRC_IN;
		rules[5] = AlphaComposite.DST_IN;
		rules[6] = AlphaComposite.SRC_OUT;
		rules[7] = AlphaComposite.DST_OUT;

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		Line2D line = new Line2D.Double(x1 + xObj, y1 + yObj, x2 + xObj, y2 + yObj);
		g2d.draw(line);
	}

	public void drawThickLine(Graphics2D g2d, int x1, int y1, int x2, int y2, int width, Color c) throws Exception {
		g2d.setPaint(c);
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void paintTextByWidthHeightAndDocument(SVGGraphics2D g2d, int x1, int y1, int width, int height, String text, int style,
			int size, Color fontColor, Document document) throws Exception {

		this.drawText(g2d, x1, y1, width, height, text, "Verdana", style, size, fontColor, document);

	}

	public void drawText(SVGGraphics2D g2d, int x, int y, int width, int height, String text, String default_export_font, int style,
			int size, Color fontColor, Document document) throws Exception {

		// g2d.setClip(x, y, width, height);
		// g2d.setColor(Color.black);
		// g2d.drawString(text, x, y+20);

		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

		// Get the root element (the 'svg' element).
		Element svgRoot = document.getDocumentElement();

		log.debug("svgNS DEBUG: " + svgNS);

		// Element textElement = g2d.getDOMFactory().createElementNS(svgNS,"text");
		Element rectangle = document.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(svgNS, "x", "10");
		rectangle.setAttributeNS(svgNS, "y", "20");
		rectangle.setAttributeNS(svgNS, "width", "100");
		rectangle.setAttributeNS(svgNS, "height", "50");
		rectangle.setAttributeNS(svgNS, "fill", "red");

		// Attach the rectangle to the root 'svg' element.
		svgRoot.appendChild(rectangle);

	}

	public void drawTextByString(SVGGraphics2D g2d, int x, int y, int width, int height, String text, String default_export_font, int style,
			int size, Color fontColor) throws Exception {

		// g2d.setClip(x, y, width, height);
		g2d.setColor(Color.black);
		g2d.drawString(text, x, y);

	}

	public void drawTextByJTextArea(Graphics2D g2d, int x, int y, int width, int height, String text, String default_export_font, int style,
			int size, Color fontColor) throws Exception {

		// g2d.setClip(x, y, width, height);
		// g2d.setColor(Color.black);
		// g2d.drawString(text, x, y+20);

		// Font font = new Font("Verdana", Font.PLAIN, 11);
		Font font = new Font(default_export_font, style, size);

		String[] stringsText = text.split("\r");
		// log.debug("TEXT: "+stringsText);
		// log.debug("TEXT: "+stringsText.length);

		StringBuilder newText = new StringBuilder();

		for (int i = 0; i < stringsText.length; i++) {
			newText.append(stringsText[i]);
			if (i + 1 < stringsText.length) {
				newText.append("\n");
			}
		}

		JTextArea n = new JTextArea(newText.toString());
		n.setFont(font);
		n.setWrapStyleWord(true);
		n.setLineWrap(true);
		n.setForeground(fontColor);

		// log.debug("Text at: "+x+" "+y);
		// int x, int y, int width, int height
		n.setBounds(x, y, width, height);
		n.setOpaque(false);

		// Text
		SVGGraphics2D svgGenerator2 = (SVGGraphics2D) g2d.create(x, y, width, height);

		// svgGenerator2.create(x, y, width, height);
		// svgGenerator2.draw(.dra)
		n.paint(svgGenerator2);

	}

	public void drawArrow(SVGGraphics2D g2d, GeomPoint start, GeomPoint end, float thickness, float alpha, Color linecoler,
			Color fillColor) {

		if (start.equals(end))
			return;

		// (double edgeControlPosition, double edgeControlSize,
		// double headLength, double headWidth, double shaftControlPosition,
		// double shaftControlSize, float shaftPosition, double shaftThickness)
		ArrowStyle arrowStyle = new ArrowStyle(0.5, 0.5, thickness * 5, thickness * 5, 0.5, 0.5, 0, thickness);

		GeomPoint fullVect = end.subtract(start);
		double halfWidth = (arrowStyle.headWidth != -1) ? arrowStyle.headWidth / 2 : arrowStyle.headLength / 2;

		// Figure out the line start/end points
		GeomPoint startNorm = new GeomPoint();
		startNorm.setLocation(fullVect.getY(), -fullVect.getX());
		startNorm.normalize(arrowStyle.shaftThickness / 2);
		GeomPoint start1 = start.add(startNorm);
		GeomPoint start2 = start.subtract(startNorm);
		GeomPoint end1 = end.add(startNorm);
		GeomPoint end2 = end.subtract(startNorm);

		// log.debug("startNorm: "+startNorm.toString());
		// log.debug("start1: "+start1.toString());
		// log.debug("start2: "+start2.toString());
		// log.debug("end1: "+end1.toString());
		// log.debug("end2: "+end2.toString());

		// figure out where the arrow head starts
		GeomPoint headPnt = fullVect.clone();
		// log.debug("headPnt 1: "+headPnt.toString());
		// log.debug("headPnt.length 1: "+headPnt.length());
		// log.debug("arrowStyle.headLength 1: "+arrowStyle.headLength);
		headPnt.normalize(headPnt.length() - arrowStyle.headLength);
		// log.debug("headPnt 2: "+headPnt.toString());
		headPnt = headPnt.add(start);
		// log.debug("headPnt 3: "+headPnt.toString());

		// calculate the arrowhead corners
		GeomPoint headPntNorm = startNorm.clone();
		// log.debug("headPntNorm ^^: "+headPntNorm.toString());
		// log.debug("halfWidth ^^: "+halfWidth);
		headPntNorm.normalize(halfWidth);
		// log.debug("headPntNorm: "+headPntNorm.toString());
		GeomPoint edge1 = headPnt.add(headPntNorm);
		GeomPoint edge2 = headPnt.subtract(headPntNorm);

		// log.debug("edge1: "+edge1.toString());
		// log.debug("edge2: "+edge2.toString());

		// Figure out where the arrow connects the the shaft, then calc the 		// intersections
		GeomPoint shaftCenter = GeomPoint.interpolate(end, headPnt, arrowStyle.shaftPosition);
		// log.debug("end"+end.toString());
		// log.debug("headPnt: "+headPnt.toString());
		// log.debug("arrowStyle.shaftPosition: "+arrowStyle.shaftPosition);
		// log.debug("shaftCenter: "+shaftCenter);
		// log.debug("edge1: "+edge1);
		// shaftCenter.setLocation(185.857864376269, 185.857864376269);
		GeomPoint inter1 = GeomPoint.getLineIntersection(start1, end1, shaftCenter, edge1);
		GeomPoint inter2 = GeomPoint.getLineIntersection(start2, end2, shaftCenter, edge2);
		if (inter1 == null || inter2 == null) {
			log.warn("No intersection found");
			return;
		}

		// log.debug("inter1: "+inter1.toString());
		// log.debug("inter2: "+inter2.toString());

		// Figure out the control points
		GeomPoint edgeCenter = GeomPoint.interpolate(end, headPnt, (float) arrowStyle.edgeControlPosition);
		GeomPoint edgeNorm = startNorm.clone();
		edgeNorm.normalize(halfWidth * arrowStyle.edgeControlSize);
		// log.debug("halfWidth*arrowStyle.edgeControlSize:
		// "+(halfWidth*arrowStyle.edgeControlSize));
		// log.debug("edgeNorm: "+edgeNorm.toString());
		GeomPoint edgeCntrl1 = edgeCenter.add(edgeNorm);
		GeomPoint edgeCntrl2 = edgeCenter.subtract(edgeNorm);

		// log.debug("edgeCntrl1: "+edgeCntrl1.toString());
		// log.debug("edgeCntrl2: "+edgeCntrl2.toString());

		g2d.setPaint(new Color(255, 0, 0));

		// graphics.moveTo(start1.x,start1.y);
		// graphics.lineTo(inter1.x,inter1.y);
		// graphics.lineTo(edge1.x,edge1.y);
		// graphics.curveTo(edgeCntrl1.x,edgeCntrl1.y,end.x,end.y);
		// graphics.curveTo(edgeCntrl2.x,edgeCntrl2.y,edge2.x,edge2.y);
		// graphics.lineTo(inter2.x,inter2.y);
		// graphics.lineTo(start2.x,start2.y);
		// graphics.lineTo(start1.x,start1.y);

		// log.debug("moveTo"+start1.x+","+start1.y);
		// log.debug("lineTo"+inter1.x+","+inter1.y);
		// log.debug("lineTo"+edge1.x+","+edge1.y);
		// log.debug("quadTo"+edgeCntrl1.x+","+edgeCntrl1.y+","+end.x+","+end.y);
		// log.debug("quadTo"+edgeCntrl2.x+","+edgeCntrl2.y+","+edge2.x+","+edge2.y);
		// log.debug("lineTo"+inter2.x+","+inter2.y);
		// log.debug("lineTo"+start2.x+","+start2.y);
		// log.debug("lineTo"+start1.x+","+start1.y);

		GeneralPath graphics = new GeneralPath();
		graphics.moveTo(start1.x, start1.y);
		graphics.lineTo(inter1.x, inter1.y);
		graphics.lineTo(edge1.x, edge1.y);
		graphics.quadTo(edgeCntrl1.x, edgeCntrl1.y, end.x, end.y);
		graphics.quadTo(edgeCntrl2.x, edgeCntrl2.y, edge2.x, edge2.y);
		graphics.lineTo(inter2.x, inter2.y);
		graphics.lineTo(start2.x, start2.y);
		graphics.lineTo(start1.x, start1.y);
		graphics.closePath();

		int[] rules = new int[8];

		// all possible Compositing Rules:
		rules[0] = AlphaComposite.SRC_OVER;
		rules[1] = AlphaComposite.DST_OVER;
		rules[2] = AlphaComposite.CLEAR;
		rules[3] = AlphaComposite.SRC;
		rules[4] = AlphaComposite.SRC_IN;
		rules[5] = AlphaComposite.DST_IN;
		rules[6] = AlphaComposite.SRC_OUT;
		rules[7] = AlphaComposite.DST_OUT;

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

		if (linecoler != null) {
			g2d.setPaint(linecoler);
			g2d.draw(graphics);
		}

		if (fillColor != null) {
			g2d.setPaint(fillColor);
			g2d.fill(graphics);
		}
	}

	public String workAroundPixelErrorInSVG(String xmlDef) throws Exception {
		String resultingString = "";

		boolean byPassedFirst = false;

		while (xmlDef.indexOf("font-size:") != -1) {

			log.debug("xmlDef " + xmlDef);
			int i = xmlDef.indexOf("font-size:");

			log.debug("Found i " + i);
			if (byPassedFirst) {
				log.debug("byPassedFirst DO PROCESS ");
				String start = xmlDef.substring(0, i + 10);
				xmlDef = xmlDef.substring(i + 10, xmlDef.length());
				log.debug("byPassedFirst FIRST PART Process " + start);
				log.debug("byPassedFirst END PART Process " + xmlDef);

				int firstSemicolon = xmlDef.indexOf(";");
				log.debug("firstSemicolon " + firstSemicolon);
				String fontSize = xmlDef.substring(0, firstSemicolon);
				log.debug("fontSize " + fontSize);

				xmlDef = xmlDef.substring(firstSemicolon, xmlDef.length());

				resultingString += start + fontSize + "px";
			} else {
				log.debug("byPassedFirst DO NOT PROCESS ");
				String start = xmlDef.substring(0, i + 10);
				log.debug("byPassedFirst FIRST PART " + start);
				xmlDef = xmlDef.substring(i + 10, xmlDef.length());
				log.debug("byPassedFirst END PART " + xmlDef);
				resultingString += start;
				byPassedFirst = true;
			}
		}

		resultingString += xmlDef;

		log.debug("###################### ");
		log.debug("resultingString " + resultingString);

		return resultingString;
	}
}
