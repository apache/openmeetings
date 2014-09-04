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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PROTOCOL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getSid;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.resource.StringResourceStream;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class StartSharingEventBehavior extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(StartSharingEventBehavior.class, webAppRootKey);
	private final AjaxDownload download;
	private final long roomId;

	public StartSharingEventBehavior(long _roomId) {
		this.roomId = _roomId;
		download = new AjaxDownload(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getFileName() {
				return "public_" + roomId + ".jnlp";
			}
		};
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		getComponent().add(download);
	}
	
	@Override
	protected void respond(AjaxRequestTarget target) {
		String app = "";
		InputStream jnlp = null;
		try {
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			jnlp = getClass().getClassLoader().getResourceAsStream("APPLICATION.jnlp");
			app = IOUtils.toString(jnlp, "UTF-8");
			String baseUrl = cfgDao.getBaseUrl();
			URL url = new URL(baseUrl);
			String path = url.getPath();
			path = path.substring(1, path.indexOf('/', 2) + 1);
			app = app.replace("$codebase", baseUrl + "screenshare")
					.replace("$applicationName", cfgDao.getAppName())
					.replace("$protocol", cfgDao.getConfValue(CONFIG_FLASH_PROTOCOL, String.class, ""))
					.replace("$port", cfgDao.getConfValue(CONFIG_FLASH_PORT, String.class, ""))
					.replace("$host", url.getHost())
					.replace("$app", path + roomId)
					.replace("$userId", "" + getUserId())
					.replace("$publicSid", getSid())
					.replace("$labels", "<![CDATA[" + getLabels(730,  731,  732,  733,  734
							,  735,  737,  738,  739,  740
							,  741,  742,  844,  869,  870
							,  871,  872,  878, 1089, 1090
							, 1091, 1092, 1093, 1465, 1466
							, 1467, 1468, 1469, 1470, 1471
							, 1472, 1473, 1474, 1475, 1476
							, 1477, 1589, 1598, 1078) + "]]>")
					.replace("$defaultQuality", cfgDao.getConfValue(CONFIG_SCREENSHARING_QUALITY, String.class, ""))
					.replace("$defaultFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS, String.class, ""))
					.replace("$showFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS_SHOW, String.class, "true"))
					.replace("$allowRecording", "true") //FIXME add/remove
					.replace("$allowPublishing", "true") //FIXME add/remove
					.replace("$keystore", "<![CDATA[]]>") //FIXME add/remove
					.replace("$password", "<![CDATA[]]>") //FIXME add/remove
					;
		} catch (Exception e) {
			log.error("Unexpected error while creating jnlp file", e);
		} finally {
			if (jnlp != null) {
				try {
					jnlp.close();
				} catch (IOException e) {}
			}
		}
		download.setResourceStream(new StringResourceStream(app, "application/x-java-jnlp-file"));
		download.initiate(target);
	}

	private String getLabels(int ... ids) {
		StringBuilder result = new StringBuilder();
		boolean delim = false;
		FieldLanguagesValuesDao labelDao = getBean(FieldLanguagesValuesDao.class);
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(labelDao.getString(id, getLanguage()));
			delim = true;
		}
		return result.toString();
	}
}
