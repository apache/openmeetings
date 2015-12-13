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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomBroadcaster.getClient;
import static org.apache.openmeetings.web.room.RoomPanel.PARAM_PORT;
import static org.apache.openmeetings.web.room.RoomPanel.PARAM_PROTOCOL;
import static org.apache.openmeetings.web.room.RoomPanel.PARAM_PUBLIC_SID;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.core.session.SessionManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class StartSharingEventBehavior extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(StartSharingEventBehavior.class, webAppRootKey);
	private final AjaxDownload download;
	private final long roomId;
	private enum Protocol {
		rtmp
		, rtmpe
		, rtmps
		, rtmpt
	}

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
		//TODO deny download in case other screen sharing is in progress
		String app = "";
		try (InputStream jnlp = getClass().getClassLoader().getResourceAsStream("APPLICATION.jnlp")) {
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			app = IOUtils.toString(jnlp, "UTF-8");
			String baseUrl = cfgDao.getBaseUrl();
			URL url = new URL(baseUrl);
			Room room = getBean(RoomDao.class).get(roomId);
			String publicSid = getParam(getComponent(), PARAM_PUBLIC_SID).toString();
			Client rc = getClient(publicSid);
			SessionManager sessionManager = getBean(SessionManager.class);
			String path = url.getPath();
			path = path.substring(1, path.indexOf('/', 2) + 1);
			String port = getParam(getComponent(), PARAM_PORT).toString();
			if (Strings.isEmpty(port)) {
				cfgDao.getConfValue(CONFIG_FLASH_PORT, String.class, "");
			}
			String _protocol = getParam(getComponent(), PARAM_PROTOCOL).toString();
			if (Strings.isEmpty(_protocol)) {
				_protocol = cfgDao.getConfValue(CONFIG_FLASH_PROTOCOL, String.class, "");
			}
			Protocol protocol = Protocol.valueOf(_protocol);
			app = addKeystore(app).replace("$codebase", baseUrl + "screenshare")
					.replace("$applicationName", cfgDao.getAppName())
					.replace("$protocol", protocol.name())
					.replace("$port", port)
					.replace("$host", url.getHost())
					.replace("$app", path + roomId)
					.replace("$userId", "" + getUserId())
					.replace("$publicSid", publicSid)
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
					.replace("$allowRemote", cfgDao.getConfValue(CONFIG_SCREENSHARING_ALLOW_REMOTE, String.class, "true"))
					.replace("$allowRecording", "" + (room.isAllowRecording() && rc.isAllowRecording() && (0 == sessionManager.getRecordingCount(roomId))))
					.replace("$allowPublishing", "" + (0 == sessionManager.getPublishingCount(roomId)))
					;
		} catch (Exception e) {
			log.error("Unexpected error while creating jnlp file", e);
		}
		download.setResourceStream(new StringResourceStream(app, "application/x-java-jnlp-file"));
		download.initiate(target);
	}

	private String getLabels(int ... ids) {
		StringBuilder result = new StringBuilder();
		boolean delim = false;
		LabelDao labelDao = getBean(LabelDao.class);
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(labelDao.getString(id, getLanguage()));
			delim = true;
		}
		return result.toString();
	}
	
	private String addKeystore(String app) {
		log.debug("RTMP Sharer Keystore :: start");
		String keystore = "--dummy--", password = "--dummy--";
		File conf = new File(OmFileHelper.getRootDir(), "conf");
		File keyStore = new File(conf, "keystore.screen");
		if (keyStore.exists()) {
			try (FileInputStream fis = new FileInputStream(keyStore); FileInputStream ris = new FileInputStream(new File(conf, "red5.properties"))) {
				Properties red5Props = new Properties();
				red5Props.load(ris);
				
				byte keyBytes[] = new byte[(int)keyStore.length()];
				fis.read(keyBytes);
				
				keystore = Hex.encodeHexString(keyBytes);
				password = red5Props.getProperty("rtmps.screen.keystorepass");
				
				/*
				KeyStore ksIn = KeyStore.getInstance(KeyStore.getDefaultType());
				ksIn.load(new FileInputStream(keyStore), red5Props.getProperty("rtmps.keystorepass").toCharArray());
				ByteArrayInputStream bin = new ByteArrayInputStream()
				
				byte fileContent[] = new byte[(int)file.length()];
				sb = addArgument(sb, Object arg)
				ctx.put("$KEYSTORE", users_id);
				/*
				KeyStore ksOut = KeyStore.getInstance(KeyStore.getDefaultType());
				for (Certificate cert : ksIn.getCertificateChain("red5")) {
					PublicKey pub = cert.getPublicKey();
					TrustedCertificateEntry tce = new TrustedCertificateEntry(cert);
					tce.
				}
				*/
			} catch (Exception e) {
			//no op
			}
		}
		return app.replace("$keystore", keystore).replace("$password", password);
	}
}
