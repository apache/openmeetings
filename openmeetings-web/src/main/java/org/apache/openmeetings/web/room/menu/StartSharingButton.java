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
package org.apache.openmeetings.web.room.menu;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.core.remote.ScopeApplicationAdapter.FLASH_NATIVE_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.core.session.SessionManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmButton;
import org.apache.openmeetings.web.room.VideoSettings;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.util.resource.StringResourceStream;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class StartSharingButton extends OmButton {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(StartSharingButton.class, webAppRootKey);
	private static final String CDATA_BEGIN = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private final AjaxDownload download;
	private final Client c;

	public StartSharingButton(String id, Client c) {
		super(id);
		this.c = c;
		setOutputMarkupPlaceholderTag(true);
		setVisible(false);
		add(new AttributeAppender("title", Application.getString(1480)));
		add(download = new AjaxDownload(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getFileName() {
				return String.format("public_%s.jnlp", StartSharingButton.this.c.getRoomId());
			}
		});
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		//TODO deny download in case other screen sharing is in progress
		String app = "";
		try (InputStream jnlp = getClass().getClassLoader().getResourceAsStream("APPLICATION.jnlp")) {
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			app = IOUtils.toString(jnlp, UTF_8);
			String publicSid = c.getUid();
			JSONObject s = VideoSettings.getInitJson(WebSession.get().getExtendedProperties(), "" + c.getRoomId(), publicSid);
			String _url = s.getString(VideoSettings.URL);
			long roomId = c.getRoomId();
			Room room = getBean(RoomDao.class).get(roomId);
			SessionManager sessionManager = getBean(SessionManager.class);
			app = app.replace("$native", "" + s.getBoolean(FLASH_NATIVE_SSL))
					.replace("$codebase", WebSession.get().getExtendedProperties().getCodebase())
					.replace("$applicationName", cfgDao.getAppName())
					.replace("$url", _url)
					.replace("$fallback", s.getString(VideoSettings.FALLBACK))
					.replace("$publicSid", publicSid)
					.replace("$labels", getLabels(730,  731,  732,  733,  734
							,  735,  737,  738,  739,  740
							,  741,  742,  844,  869,  870
							,  871,  872,  878, 1089, 1090
							, 1091, 1092, 1093, 1465, 1466
							, 1467, 1468, 1469, 1470, 1471
							, 1472, 1473, 1474, 1475, 1476
							, 1477, 1589, 1598, 1078))
					.replace("$defaultQuality", cfgDao.getConfValue(CONFIG_SCREENSHARING_QUALITY, String.class, ""))
					.replace("$defaultFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS, String.class, ""))
					.replace("$showFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS_SHOW, String.class, "true"))
					.replace("$allowRemote", cfgDao.getConfValue(CONFIG_SCREENSHARING_ALLOW_REMOTE, String.class, "true"))
					.replace("$allowRecording", "" + (room.isAllowRecording() && (0 == sessionManager.getRecordingCount(roomId))))
					.replace("$allowPublishing", "" + (0 == sessionManager.getPublishingCount(roomId)))
					;

			StringResourceStream srs = new StringResourceStream(app, "application/x-java-jnlp-file");
			srs.setCharset(UTF_8);
			download.setResourceStream(srs);
			download.initiate(target);
		} catch (Exception e) {
			log.error("Unexpected error while creating jnlp file", e);
		}
	}

	private static String getLabels(int ... ids) {
		StringBuilder result = new StringBuilder(CDATA_BEGIN);
		boolean delim = false;
		LabelDao labelDao = getBean(LabelDao.class);
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(labelDao.getString(id, getLanguage()));
			delim = true;
		}
		result.append(CDATA_END);
		return result.toString();
	}
}
