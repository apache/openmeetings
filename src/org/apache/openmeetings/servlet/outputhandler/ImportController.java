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

import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.documents.beans.UploadCompleteMessage;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.xmlimport.LanguageImport;
import org.apache.openmeetings.xmlimport.UserImport;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ImportController extends AbstractUploadController {
	private static final Logger log = Red5LoggerFactory.getLogger(ImportController.class,
			OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private UserImport userImport;
	@Autowired
	private LanguageImport languageImport;
	
    @RequestMapping(value = "/import.upload", method = RequestMethod.POST)
	protected void service(HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws ServletException {

    	UploadInfo info = validate(request, true);
		try {
			String moduleName = request.getParameter("moduleName");
			if (moduleName == null) {
				moduleName = "moduleName";
			}
			log.debug("moduleName: " + moduleName);
			InputStream is = info.file.getInputStream();

			if (moduleName.equals("users")) {
				log.error("Import Users");
				userImport.addUsersByDocument(is);

			} else if (moduleName.equals("language")) {
				log.error("Import Language");
				String language = request
						.getParameter("secondid");
				if (language == null) {
					language = "0";
				}
				Long language_id = Long.valueOf(language).longValue();
				log.debug("language_id: " + language_id);

				languageImport.addLanguageByDocument(
						language_id, is);
			}

			log.debug("Return And Close");

			log.debug("moduleName.equals(userprofile) ? " + moduleName);

			log.debug("moduleName.equals(userprofile) ! ");
			
			UploadCompleteMessage uploadCompleteMessage = new UploadCompleteMessage(
					info.userId,
					"library", //message
					"import", //action
					"", //error
					info.filename);
		
			scopeApplicationAdapter.sendUploadCompletMessageByPublicSID(
					uploadCompleteMessage, info.publicSID);
			
		} catch (Exception er) {
			log.error("ERROR importing:", er);
			throw new ServletException(er);
		}
	}

}
