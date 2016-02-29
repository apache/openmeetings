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
package org.apache.openmeetings.core.servlet.outputhandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.openmeetings.core.batik.beans.PrintBean;
import org.apache.openmeetings.core.data.record.WhiteboardMapToSVG;
import org.apache.openmeetings.core.remote.PrintService;
import org.apache.openmeetings.core.servlet.BaseHttpServlet;
import org.apache.openmeetings.core.servlet.ServerNotInitializedException;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportToImage extends BaseHttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			ExportToImage.class, OpenmeetingsVariables.webAppRootKey);

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

			Long users_id = getBean(SessiondataDao.class).checkSession(sid);
			Set<Right> rights = getBean(UserDao.class).getRights(users_id);

			log.debug("users_id: " + users_id);

			if (rights != null && !rights.isEmpty() && hash != "") {

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

				svgGenerator = WhiteboardMapToSVG.getInstance().convertMapToSVG(svgGenerator, whiteBoardMap);

				// Finally, stream out SVG to the standard output using
				// UTF-8 encoding.
				boolean useCSS = true; // we want to use CSS style attributes

				File uploadTempDir = OmFileHelper.getUploadTempDir();
				log.debug("working_dir: " + uploadTempDir);
				String reqFilePrefix = fileName + "_" + CalendarPatterns.getTimeForStreamId(new Date());
				File svgFile = new File(uploadTempDir, reqFilePrefix + ".svg");
				log.debug("exported svg file: " + svgFile.getCanonicalPath());
				try (OutputStream os = new FileOutputStream(svgFile);
						Writer out = new OutputStreamWriter(os, StandardCharsets.UTF_8))
				{
					svgGenerator.stream(out, useCSS);
					out.flush();
				}
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
		} catch (ServerNotInitializedException e) {
			return;
		} catch (Exception er) {
			log.error("Error exporting to image ", er);
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
