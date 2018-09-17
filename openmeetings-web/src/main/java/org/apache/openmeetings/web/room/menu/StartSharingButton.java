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
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_NATIVE_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.wicket.util.time.Duration.NONE;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.StreamClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmButton;
import org.apache.openmeetings.web.room.VideoSettings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class StartSharingButton extends OmButton {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(StartSharingButton.class, getWebAppRootKey());
	private static final String CDATA_BEGIN = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private final AjaxDownloadBehavior download;
	private final String uid;
	private String app = "";

	public StartSharingButton(String id, String uid) {
		super(id);
		this.uid = uid;
		setOutputMarkupPlaceholderTag(true);
		setVisible(false);
		add(download = new AjaxDownloadBehavior(new ResourceStreamResource() {
			private static final long serialVersionUID = 1L;

			{
				setCacheDuration(NONE);
			}

			@Override
			protected IResourceStream getResourceStream(Attributes attributes) {
				setFileName(String.format("public_%s.jnlp", getBean(ClientManager.class).get(uid).getRoom().getId()));
				StringResourceStream srs = new StringResourceStream(app, "application/x-java-jnlp-file");
				srs.setCharset(UTF_8);
				return srs;
			}
		}));
	}

	@Override
	protected void onInitialize() {
		add(AttributeModifier.replace(ATTR_TITLE, getString("1480")));
		super.onInitialize();
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		try (InputStream jnlp = getClass().getClassLoader().getResourceAsStream("APPLICATION.jnlp")) {
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			app = IOUtils.toString(jnlp, UTF_8);
			Client c = getBean(ClientManager.class).get(uid);
			String sid = c.getSid();
			Long roomId = c.getRoom().getId();
			JSONObject s = VideoSettings.getInitJson(WebSession.get().getExtendedProperties(), roomId, sid);
			String _url = s.getString(VideoSettings.URL);
			Room room = getBean(RoomDao.class).get(roomId);
			StreamClientManager streamClientManager = getBean(StreamClientManager.class);
			app = app.replace("$native", String.valueOf(s.getBoolean(FLASH_NATIVE_SSL)))
					.replace("$codebase", WebSession.get().getExtendedProperties().getCodebase())
					.replace("$applicationName", getApplicationName())
					.replace("$url", _url)
					.replace("$fallback", s.getString(VideoSettings.FALLBACK))
					.replace("$sid", sid)
					.replace("$labels", getLabels("730",  "731",  "732",  "733",  "734"
							,  "735",  "737",  "738",  "739",  "740"
							,  "741",  "742",  "844",  "869",  "870"
							,  "871",  "872",  "878", "1089", "1090"
							, "1091", "1092", "1093", "1465", "1466"
							, "1467", "1468", "1469", "1470", "1471"
							, "1472", "1473", "1474", "1475", "1476"
							, "1477", "1589", "1598", "ulist.right.remote.grant"))
					.replace("$defaultQuality", String.valueOf(cfgDao.getLong(CONFIG_SCREENSHARING_QUALITY, 1L)))
					.replace("$defaultFps", String.valueOf(cfgDao.getLong(CONFIG_SCREENSHARING_FPS, 10L)))
					.replace("$showFps", String.valueOf(cfgDao.getBool(CONFIG_SCREENSHARING_FPS_SHOW, true)))
					.replace("$allowRemote", String.valueOf(cfgDao.getBool(CONFIG_SCREENSHARING_ALLOW_REMOTE, true)))
					.replace("$allowRecording", String.valueOf(room.isAllowRecording() && (0 == streamClientManager.getRecordingCount(roomId))))
					.replace("$allowPublishing", String.valueOf(0 == streamClientManager.getPublishingCount(roomId)))
					;
			download.initiate(target);
		} catch (Exception e) {
			log.error("Unexpected error while creating jnlp file", e);
		}
	}

	private static String getLabels(String ... ids) {
		StringBuilder result = new StringBuilder(CDATA_BEGIN);
		boolean delim = false;
		for (String id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(LabelDao.getString(id, getLanguage()));
			delim = true;
		}
		result.append(CDATA_END);
		return result.toString();
	}
}
