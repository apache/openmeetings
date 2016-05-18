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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.room.RoomBroadcaster.getClient;
import static org.apache.openmeetings.web.room.RoomPanel.PARAM_PUBLIC_SID;
import static org.apache.openmeetings.web.room.RoomPanel.PARAM_URL;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
	private static final String CDATA_BEGIN = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private final AjaxDownload download;
	private final Long roomId;
	private enum Protocol {
		rtmp
		, rtmpe
		, rtmps
		, rtmpt
	}

	public StartSharingEventBehavior(Long _roomId) {
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
			app = IOUtils.toString(jnlp, StandardCharsets.UTF_8);
			String baseUrl = cfgDao.getBaseUrl();
			String _url = getParam(getComponent(), PARAM_URL).toString();
			URI url = new URI(_url);
			Room room = getBean(RoomDao.class).get(roomId);
			String publicSid = getParam(getComponent(), PARAM_PUBLIC_SID).toString();
			SessionManager sessionManager = getBean(SessionManager.class);
			Client rc = getClient(publicSid);
			if (rc == null) {
				throw new RuntimeException(String.format("Unable to find client by publicSID '%s'", publicSid));
			}
			String path = url.getPath();
			path = path.substring(path.lastIndexOf('/') + 1);
			if (Strings.isEmpty(path) || rc.getRoomId() == null || !path.equals(rc.getRoomId().toString()) || !rc.getRoomId().equals(roomId)) {
				throw new RuntimeException(String.format("Invalid room id passed %s, expected, %s", path, roomId));
			}
			Protocol protocol = Protocol.valueOf(url.getScheme());
			app = addKeystore(app, protocol).replace("$codebase", baseUrl + "screenshare")
					.replace("$applicationName", cfgDao.getAppName())
					.replace("$url", _url)
					.replace("$publicSid", publicSid)
					.replace("$labels", CDATA_BEGIN + getLabels(730,  731,  732,  733,  734
							,  735,  737,  738,  739,  740
							,  741,  742,  844,  869,  870
							,  871,  872,  878, 1089, 1090
							, 1091, 1092, 1093, 1465, 1466
							, 1467, 1468, 1469, 1470, 1471
							, 1472, 1473, 1474, 1475, 1476
							, 1477, 1589, 1598, 1078) + CDATA_END)
					.replace("$defaultQuality", cfgDao.getConfValue(CONFIG_SCREENSHARING_QUALITY, String.class, ""))
					.replace("$defaultFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS, String.class, ""))
					.replace("$showFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS_SHOW, String.class, "true"))
					.replace("$allowRemote", cfgDao.getConfValue(CONFIG_SCREENSHARING_ALLOW_REMOTE, String.class, "true"))
					.replace("$allowRecording", "" + (rc.getUserId() > 0 && room.isAllowRecording() && rc.isAllowRecording() && (0 == sessionManager.getRecordingCount(roomId))))
					.replace("$allowPublishing", "" + (0 == sessionManager.getPublishingCount(roomId)))
					;
		} catch (Exception e) {
			log.error("Unexpected error while creating jnlp file", e);
		}
		StringResourceStream srs = new StringResourceStream(app, "application/x-java-jnlp-file");
		srs.setCharset(StandardCharsets.UTF_8);
		download.setResourceStream(srs);
		download.initiate(target);
	}

	private static String getLabels(int ... ids) {
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
	
	private static String addKeystore(String app, Protocol protocol) {
		log.debug("RTMP Sharer Keystore :: start");
		String keystore = "--dummy--", password = "--dummy--";
		if (Protocol.rtmps == protocol) {
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
		}
		return app.replace("$keystore", CDATA_BEGIN + keystore + CDATA_END)
				.replace("$password", CDATA_BEGIN + password + CDATA_END);
	}
}
