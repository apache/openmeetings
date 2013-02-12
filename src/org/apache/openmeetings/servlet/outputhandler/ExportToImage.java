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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.batik.beans.PrintBean;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.record.WhiteboardMapToSVG;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.documents.GenerateImage;
import org.apache.openmeetings.remote.PrintService;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportToImage extends HttpServlet {
	private static final long serialVersionUID = -3535998254746084297L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			ExportToImage.class, OpenmeetingsVariables.webAppRootKey);

	public SessiondataDao getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (SessiondataDao) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Usermanagement) context.getBean("userManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	public GenerateImage getGenerateImage() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (GenerateImage) context.getBean("generateImage");
			}
		} catch (Exception err) {
			log.error("[getGenerateImage]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		try {
			if (getUserManagement() == null || getSessionManagement() == null
					|| getGenerateImage() == null) {
				return;
			}

			String sid = request.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			String hash = request.getParameter("hash");
			if (hash == null) {
				hash = "";
			}
			log.debug("hash: " + hash);

			String fileName = request.getParameter("fileName");
			if (fileName == null) {
				fileName = "file_xyz";
			}

			String exportType = request.getParameter("exportType");
			if (exportType == null) {
				exportType = "svg";
			}

			Long users_id = getSessionManagement().checkSession(sid);
			Long user_level = getUserManagement().getUserLevelByID(users_id);

			log.debug("users_id: " + users_id);
			log.debug("user_level: " + user_level);

			if (user_level != null && user_level > 0 && hash != "") {

				PrintBean pBean = PrintService.getPrintItemByHash(hash);

				// Whiteboard Objects
				@SuppressWarnings("rawtypes")
				List whiteBoardMap = pBean.getMap();

				// Get a DOMImplementation.
				DOMImplementation domImpl = GenericDOMImplementation
						.getDOMImplementation();

				// Create an instance of org.w3c.dom.Document.
				// String svgNS = "http://www.w3.org/2000/svg";
				String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

				Document document = domImpl.createDocument(svgNS, "svg", null);

				// Get the root element (the 'svg' element).
				Element svgRoot = document.getDocumentElement();

				// Set the width and height attributes on the root 'svg'
				// element.
				svgRoot.setAttributeNS(null, "width", "" + pBean.getWidth());
				svgRoot.setAttributeNS(null, "height", "" + pBean.getHeight());

				log.debug("pBean.getWidth(),pBean.getHeight()"
						+ pBean.getWidth() + "," + pBean.getHeight());

				// Create an instance of the SVG Generator.
				SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

				svgGenerator = WhiteboardMapToSVG.getInstance()
						.convertMapToSVG(svgGenerator, whiteBoardMap);

				// Finally, stream out SVG to the standard output using
				// UTF-8 encoding.
				boolean useCSS = true; // we want to use CSS style attributes

				File uploadTempDir = OmFileHelper.getUploadTempDir();
				log.debug("working_dir: " + uploadTempDir);
				String reqFilePrefix = fileName + "_" + CalendarPatterns.getTimeForStreamId(new Date());
				File svgFile = new File(uploadTempDir, reqFilePrefix + ".svg");
				log.debug("exported svg file: " + svgFile.getCanonicalPath());
				FileWriter out = new FileWriter(svgFile);
				svgGenerator.stream(out, useCSS);
				out.flush();
				out.close();
				File expFile = new File(uploadTempDir, reqFilePrefix + "." + exportType);
				log.debug("exported file: " + expFile.getCanonicalPath());
				if ("svg".equals(exportType)) {
					outFile(response, expFile);
				} else if ("jpg".equals(exportType)) {
					JPEGTranscoder t = new JPEGTranscoder();
			        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 1f);
			        
			        OutputStream ostream = new FileOutputStream(expFile);
			        TranscoderOutput output = new TranscoderOutput(new FileOutputStream(expFile));

			        // Perform the transcoding.
			        t.transcode(new TranscoderInput(svgFile.toURI().toString()), output);
			        ostream.flush();
			        ostream.close();
			        
					outFile(response, expFile);
				} else if (exportType.equals("png")
						|| exportType.equals("gif") || exportType.equals("tif")
						|| exportType.equals("pdf")) {
						//TODO not implemented yet
				}
			}

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}
	
	private void outFile(HttpServletResponse response, File f) throws IOException {
		response.reset();
		response.resetBuffer();
		OutputStream outStream = response.getOutputStream();
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
		response.setHeader("Content-Length", "" + f.length());

		OmFileHelper.copyFile(f, outStream);
		outStream.close();
	}
	
}
