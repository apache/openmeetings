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
package org.apache.openmeetings.db.dao.basic;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OmVersion.getLine;
import static org.apache.openmeetings.util.OpenmeetingsVariables.*;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.SELF;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.STRICT_DYNAMIC;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.event.RemoteCommitProvider;
import org.apache.openjpa.event.TCPRemoteCommitProvider;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.wicket.Application;
import org.apache.wicket.csp.CSPDirective;
import org.apache.wicket.csp.CSPHeaderConfiguration;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.openjson.JSONObject;

/**
 * Insert/update/Delete on {@link Configuration}
 *
 * It provides basic mechanism to get a Conf Key:
 * {@link #getBool(String, boolean)}
 * {@link #getLong(String, Long)}
 * {@link #getInt(String, int)}
 * {@link #getString(String, String)}
 *
 * <b> {@link #get(String)} is deprecated!</b>
 *
 * @author swagner
 *
 */
@Repository
@Transactional
public class ConfigurationDao implements IDataProviderDao<Configuration> {
	private static final Logger log = LoggerFactory.getLogger(ConfigurationDao.class);
	private static final List<String> searchFields = List.of("key", "value");

	@PersistenceContext
	private EntityManager em;

	@Inject
	private UserDao userDao;
	@Inject
	private OAuth2Dao oauthDao;
	@Inject
	private IApplication app;

	public void updateClusterAddresses(String addresses) throws UnknownHostException {
		OpenJPAConfiguration cfg = ((OpenJPAEntityManagerSPI)OpenJPAPersistence.cast(em)).getConfiguration();
		RemoteCommitProvider prov = cfg.getRemoteCommitEventManager().getRemoteCommitProvider();
		if (prov instanceof TCPRemoteCommitProvider tcpProv) {
			tcpProv.setAddresses(addresses);
		}
	}

	/**
	 * Retrieves Configuration regardless of its deleted status
	 *
	 * @param key - key of the {@link Configuration} to get
	 * @return correspondent {@link Configuration} or null
	 */
	public Configuration forceGet(String key) {
		try {
			List<Configuration> list = em.createNamedQuery("forceGetConfigurationByKey", Configuration.class)
					.setParameter("key", key).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			Configuration c = list.get(0);
			return c.getKey().equals(key) ? c : null;
		} catch (Exception e) {
			log.error("[forceGet]: ", e);
		}
		return null;
	}

	public List<Configuration> get(String... keys) {
		List<Configuration> result = new ArrayList<>();
		for (String key : keys) { //iteration is necessary to fill list with all values
			List<Configuration> r = em.createNamedQuery("getConfigurationsByKeys", Configuration.class)
					.setParameter("keys", List.of(key))
					.getResultList();
			result.add(r.isEmpty() ? null : r.get(0));
		}
		return result;
	}

	public Configuration get(String key) {
		List<Configuration> list = get(new String[] {key});

		if (list == null || list.isEmpty() || list.get(0) == null) {
			log.warn("Could not find key in configurations: {}", key);
			return null;
		}
		return list.get(0);
	}

	public boolean getBool(String key, boolean def) {
		Configuration c = get(key);

		if (c != null) {
			try {
				return c.getValueB();
			} catch (Exception e) {
				//no-op, parsing exception
			}
		}
		return def;
	}

	public Long getLong(String key, Long def) {
		Configuration c = get(key);

		if (c != null) {
			try {
				return c.getValueN();
			} catch (Exception e) {
				//no-op, parsing exception
			}
		}
		return def;
	}

	public int getInt(String key, int def) {
		Configuration c = get(key);

		if (c != null) {
			try {
				Long val = c.getValueN();
				return val == null ? def : val.intValue();
			} catch (Exception e) {
				//no-op, parsing exception
			}
		}
		return def;
	}

	public String getString(String key, String def) {
		Configuration c = get(key);
		return c != null && c.getValue() != null ? c.getValue() : def;
	}

	@Override
	public Configuration get(Long id) {
		if (id == null) {
			return null;
		}
		return em.createNamedQuery("getConfigurationById", Configuration.class)
				.setParameter("id", id).getSingleResult();
	}

	@Override
	public List<Configuration> get(long start, long count) {
		return setLimits(em.createNamedQuery("getNondeletedConfiguration", Configuration.class)
				, start, count).getResultList();
	}

	@Override
	public List<Configuration> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, Configuration.class, true, search, searchFields, false, null, sort, start, count);
	}

	@Override
	public long count() {
		return em.createNamedQuery("countConfigurations", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, Configuration.class, search, searchFields, true, null);
	}

	@Override
	public Configuration update(Configuration entity, Long userId) {
		return update(entity, userId, false);
	}

	public Configuration update(Configuration entity, Long userId, boolean deleted) {
		String key = entity.getKey();
		String value = entity.getValue();
		if (entity.getId() == null || entity.getId().longValue() <= 0) {
			entity.setDeleted(deleted);
			em.persist(entity);
		} else {
			entity.setUser(userDao.get(userId));
			entity.setDeleted(deleted);
			entity = em.merge(entity);
		}
		switch (key) {
			case CONFIG_CAM_FPS, CONFIG_MIC_ECHO, CONFIG_MIC_NOISE, CONFIG_MIC_RATE, CONFIG_KEYCODE_ARRANGE
					, CONFIG_KEYCODE_MUTE_OTHERS, CONFIG_KEYCODE_MUTE, CONFIG_KEYCODE_QUICKPOLL
					, CONFIG_KEYCODE_ARRANGE_RESIZE, CONFIG_AUTO_OPEN_SHARING:
				reloadRoomSettings();
				break;
			case CONFIG_MAX_UPLOAD_SIZE:
				reloadMaxUpload();
				break;
			case CONFIG_CRYPT:
				reloadCrypt();
				break;
			case CONFIG_APPLICATION_NAME:
				setApplicationName(value);
				break;
			case CONFIG_APPLICATION_BASE_URL:
				reloadBaseUrl();
				break;
			case CONFIG_SIP_ENABLED:
				reloadSipEnabled();
				break;
			case CONFIG_EXT_PROCESS_TTL:
				setExtProcessTtl(toInt(value));
				break;
			case CONFIG_DEFAULT_LANG:
				reloadDefaultLang();
				break;
			case CONFIG_MP4_AUDIO_RATE:
				reloadAudioRate();
				break;
			case CONFIG_MP4_AUDIO_BITRATE:
				reloadAudioBitrate();
				break;
			case CONFIG_MP4_VIDEO_PRESET:
				reloadVideoPreset();
				break;
			case CONFIG_DEFAULT_TIMEZONE:
				reloadTimezone();
				break;
			case CONFIG_REST_ALLOW_ORIGIN:
				reloadRestAllowOrigin();
				break;
			case CONFIG_LOGIN_MIN_LENGTH, CONFIG_PASS_MIN_LENGTH, CONFIG_PASS_CHECK_UPPER
					, CONFIG_PASS_CHECK_DIGIT, CONFIG_PASS_CHECK_SPECIAL:
				reloadLoginPassword();
				break;
			case CONFIG_DEFAULT_GROUP_ID:
				reloadDefaultGroup();
				break;
			case CONFIG_SIP_EXTEN_CONTEXT:
				reloadSipContext();
				break;
			case CONFIG_FNAME_MIN_LENGTH:
				reloadFnameMinLength();
				break;
			case CONFIG_LNAME_MIN_LENGTH:
				reloadLnameMinLength();
				break;
			case CONFIG_CHAT_SEND_ON_ENTER:
				reloadChatSendOnEnter();
				break;
			case CONFIG_REGISTER_FRONTEND, CONFIG_REGISTER_SOAP, CONFIG_REGISTER_OAUTH:
				reloadRegister();
				break;
			case CONFIG_EMAIL_VERIFICATION:
				reloadSendVerificationEmail();
				break;
			case CONFIG_EMAIL_AT_REGISTER:
				reloadSendRegisterEmail();
				break;
			case CONFIG_DISPLAY_NAME_EDITABLE:
				reloadDisplayNameEditable();
				break;
			case CONFIG_MYROOMS_ENABLED:
				reloadMyRoomsEnabled();
				break;
			case CONFIG_GOOGLE_ANALYTICS_CODE, CONFIG_CSP_FONT, CONFIG_CSP_FRAME, CONFIG_CSP_IMAGE
					, CONFIG_CSP_MEDIA, CONFIG_CSP_SCRIPT, CONFIG_CSP_STYLE, CONFIG_CSP_ENABLED:
				updateCsp();
				break;
			case CONFIG_SMTP_SERVER, CONFIG_SMTP_PORT, CONFIG_SMTP_SYSTEM_EMAIL, CONFIG_SMTP_USER
					, CONFIG_SMTP_PASS, CONFIG_SMTP_TLS, CONFIG_SMTP_SSL, CONFIG_REPLY_TO_ORGANIZER
					, CONFIG_SMTP_TIMEOUT_CON, CONFIG_SMTP_TIMEOUT:
				reloadMailSettings();
				break;
			case CONFIG_APPOINTMENT_REMINDER_MINUTES, CONFIG_APPOINTMENT_PRE_START_MINUTES:
				reloadAppointmentSettings();
				break;
			case CONFIG_RECORDING_ENABLED:
				reloadRecordingEnabled();
				break;
			case CONFIG_THEME:
				reloadTheme();
				break;
			case CONFIG_OTP_ENABLED:
				reloadOtpEnabled();
				break;
			default:
				break;
		}
		return entity;
	}

	@Override
	public void delete(Configuration entity, Long userId) {
		this.update(entity, userId, true);
	}

	private void reloadMaxUpload() {
		try {
			setMaxUploadSize(getLong(CONFIG_MAX_UPLOAD_SIZE, DEFAULT_MAX_UPLOAD_SIZE));
		} catch (Exception e) {
			log.error("Invalid value saved for max_upload_size conf key: ", e);
		}
	}

	private void reloadCrypt() {
		String cryptClass = getString(CONFIG_CRYPT, null);
		if (cryptClass != null) {
			setCryptClassName(cryptClass);
			CryptProvider.reset();
		}
	}

	private void reloadBaseUrl() {
		String val = getString(CONFIG_APPLICATION_BASE_URL, DEFAULT_BASE_URL);
		if (val != null && !val.endsWith("/")) {
			val += "/";
		}
		setBaseUrl(val);
	}

	private void reloadSipEnabled() {
		setSipEnabled(getBool(CONFIG_SIP_ENABLED, false));
	}

	private void reloadDefaultLang() {
		setDefaultLang(getLong(CONFIG_DEFAULT_LANG, 1L));
	}

	private void reloadAudioRate() {
		setAudioRate(getInt(CONFIG_MP4_AUDIO_RATE, 22050));
	}

	private void reloadAudioBitrate() {
		setAudioBitrate(getString(CONFIG_MP4_AUDIO_BITRATE, "32k"));
	}

	private void reloadVideoPreset() {
		setVideoPreset(getString(CONFIG_MP4_VIDEO_PRESET, "medium"));
	}

	private void reloadTimezone() {
		String defaultTzName = getString(CONFIG_DEFAULT_TIMEZONE, "Europe/Berlin");

		TimeZone timeZoneByOmTimeZone = TimeZone.getTimeZone(defaultTzName);

		if (timeZoneByOmTimeZone == null) { //this seems to be impossible
			// If everything fails take the servers default one
			log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
			defaultTzName = TimeZone.getDefault().getID();
		}
		setDefaultTimezone(defaultTzName);
	}

	private void reloadRestAllowOrigin() {
		setRestAllowOrigin(getString(CONFIG_REST_ALLOW_ORIGIN, null));
	}

	private void reloadLoginPassword() {
		setMinLoginLength(getInt(CONFIG_LOGIN_MIN_LENGTH, USER_LOGIN_MINIMUM_LENGTH));
		setMinPasswdLength(getInt(CONFIG_LOGIN_MIN_LENGTH, USER_PASSWORD_MINIMUM_LENGTH));
		setPwdCheckUpper(getBool(CONFIG_PASS_CHECK_UPPER, true));
		setPwdCheckDigit(getBool(CONFIG_PASS_CHECK_DIGIT, true));
		setPwdCheckSpecial(getBool(CONFIG_PASS_CHECK_SPECIAL, true));
	}

	private void reloadDefaultGroup() {
		setDefaultGroup(getLong(CONFIG_DEFAULT_GROUP_ID, null));
	}

	private void reloadSipContext() {
		setSipContext(getString(CONFIG_SIP_EXTEN_CONTEXT, DEFAULT_SIP_CONTEXT));
	}

	private void reloadFnameMinLength() {
		setMinFnameLength(getInt(CONFIG_FNAME_MIN_LENGTH, USER_LOGIN_MINIMUM_LENGTH));
	}

	private void reloadLnameMinLength() {
		setMinLnameLength(getInt(CONFIG_LNAME_MIN_LENGTH, USER_LOGIN_MINIMUM_LENGTH));
	}

	private void reloadChatSendOnEnter() {
		setChatSendOnEnter(getBool(CONFIG_CHAT_SEND_ON_ENTER, false));
	}

	private void reloadRegister() {
		setAllowRegisterFrontend(getBool(CONFIG_REGISTER_FRONTEND, false));
		setAllowRegisterSoap(getBool(CONFIG_REGISTER_SOAP, false));
		setAllowRegisterOauth(getBool(CONFIG_REGISTER_OAUTH, false));
	}

	private void reloadSendVerificationEmail() {
		setSendVerificationEmail(getBool(CONFIG_EMAIL_VERIFICATION, false));
	}

	private void reloadSendRegisterEmail() {
		setSendRegisterEmail(getBool(CONFIG_EMAIL_AT_REGISTER, false));
	}

	private void reloadDisplayNameEditable() {
		setDisplayNameEditable(getBool(CONFIG_DISPLAY_NAME_EDITABLE, false));
	}

	private void reloadMyRoomsEnabled() {
		setMyRoomsEnabled(getBool(CONFIG_MYROOMS_ENABLED, true));
	}

	private void reloadMailSettings() {
		setSmtpServer(getString(CONFIG_SMTP_SERVER, null));
		setSmtpPort(getInt(CONFIG_SMTP_PORT, 25));
		setSmtpUser(getString(CONFIG_SMTP_USER, null));
		setSmtpPass(getString(CONFIG_SMTP_PASS, null));
		setSmtpUseTls(getBool(CONFIG_SMTP_TLS, false));
		setSmtpUseSsl(getBool(CONFIG_SMTP_SSL, false));
		setSmtpTimeOut(getInt(CONFIG_SMTP_TIMEOUT, 30000));
		setSmtpConnectionTimeOut(getInt(CONFIG_SMTP_TIMEOUT_CON, 30000));
		setMailFrom(getString(CONFIG_SMTP_SYSTEM_EMAIL, null));
		setMailAddReplyTo(getBool(CONFIG_REPLY_TO_ORGANIZER, true));
	}

	private void reloadAppointmentSettings() {
		setAppointmentPreStartMinutes(getInt(CONFIG_APPOINTMENT_PRE_START_MINUTES, 5));
		setAppointmentReminderMinutes(getInt(CONFIG_APPOINTMENT_REMINDER_MINUTES, 15));
	}

	private void reloadRecordingEnabled() {
		setRecordingsEnabled(getBool(CONFIG_RECORDING_ENABLED, true));
	}

	private void reloadTheme() {
		setTheme(getString(CONFIG_THEME, ""));
		app.updateTheme();
	}

	private void reloadOtpEnabled() {
		setOtpEnabled(getBool(CONFIG_OTP_ENABLED, false));
	}

	public void reinit() {
		reloadMaxUpload();
		reloadCrypt();
		setApplicationName(getString(CONFIG_APPLICATION_NAME, DEFAULT_APP_NAME));
		reloadDefaultLang();
		reloadBaseUrl();
		reloadSipEnabled();
		reloadAudioRate();
		reloadAudioBitrate();
		reloadVideoPreset();
		reloadTimezone();
		reloadRestAllowOrigin();
		reloadRoomSettings();
		reloadLoginPassword();
		reloadDefaultGroup();
		reloadSipContext();
		reloadFnameMinLength();
		reloadLnameMinLength();
		reloadChatSendOnEnter();
		reloadRegister();
		reloadSendVerificationEmail();
		reloadSendRegisterEmail();
		reloadDisplayNameEditable();
		reloadMyRoomsEnabled();
		reloadMailSettings();
		reloadAppointmentSettings();
		reloadRecordingEnabled();
		reloadTheme();
		reloadOtpEnabled();

		updateCsp();
	}

	private static JSONObject getHotkey(String value) {
		List<String> partList = List.of(value.split("\\+"));
		Set<String> parts = new HashSet<>(partList);
		return new JSONObject()
				.put("alt", parts.contains("Alt"))
				.put("shift", parts.contains("Shift"))
				.put("ctrl", parts.contains("Ctrl"))
				.put("code", partList.get(partList.size() - 1));
	}

	private JSONObject reloadRoomSettings() {
		try {
			setRoomSettings(new JSONObject()
					.put("keycode", new JSONObject()
							.put("arrange", getHotkey(getString(CONFIG_KEYCODE_ARRANGE, "Shift+F8")))
							.put("arrangeresize", getHotkey(getString(CONFIG_KEYCODE_ARRANGE_RESIZE, "Ctrl+Shift+KeyA")))
							.put("muteothers", getHotkey(getString(CONFIG_KEYCODE_MUTE_OTHERS, "Shift+F12")))
							.put("mute", getHotkey(getString(CONFIG_KEYCODE_MUTE, "Shift+F7")))
							.put("quickpoll", getHotkey(getString(CONFIG_KEYCODE_QUICKPOLL, "Ctrl+Alt+KeyQ")))
							)
					.put("camera", new JSONObject().put("fps", getLong(CONFIG_CAM_FPS, 30L)))
					.put("microphone", new JSONObject()
							.put("rate", getLong(CONFIG_MIC_RATE, 30L))
							.put("echo", getBool(CONFIG_MIC_ECHO, true))
							.put("noise", getBool(CONFIG_MIC_NOISE, true))
						)
					.put("autoOpenSharing", getBool(CONFIG_AUTO_OPEN_SHARING, false))
				);
		} catch (Exception e) {
			log.error("Unexpected exception while reloading room settings: ", e);
		}
		return getRoomSettings();
	}

	private void addCspRule(CSPHeaderConfiguration cspConfig, CSPDirective key, String val) {
		addCspRule(cspConfig, key, val, true);
	}

	private void addCspRule(CSPHeaderConfiguration cspConfig, CSPDirective key, String val, boolean remove) {
		if (!Strings.isEmpty(val)) {
			for(String str : val.split(",")) {
				if (!Strings.isEmpty(str)) {
					try {
						cspConfig.add(key, str.trim());
					} catch (Exception e) {
						log.error("Unexpected error while adding CSP rule: key '{}', value '{}', part '{}'", key, val, str, e);
					}
				}
			}
		} else if (remove) {
			cspConfig.remove(key);
		}
	}

	public void updateCsp() {
		setGaCode(getString(CONFIG_GOOGLE_ANALYTICS_CODE, null));

		if (!getBool(CONFIG_CSP_ENABLED, true)) {
			StringBuilder sb = new StringBuilder("\n");
			getLine(sb, "", '#');
			getLine(sb, "CSP headers are DISABLED", ' ');
			getLine(sb, "Disabling CSP can lead to XSS attacks! Use this mode only if you must!", ' ');
			getLine(sb, "", '#');
			log.warn("{}", sb);
			WebApplication.get().getCspSettings().blocking().disabled();
			return;
		}

		setCspFontSrc(getString(CONFIG_CSP_FONT, DEFAULT_CSP_FONT));
		setCspFrameSrc(getString(CONFIG_CSP_FRAME, SELF.getValue()));
		setCspImageSrc(getString(CONFIG_CSP_IMAGE, DEFAULT_CSP_DATA));
		setCspMediaSrc(getString(CONFIG_CSP_MEDIA, DEFAULT_CSP_DATA));
		setCspScriptSrc(getString(CONFIG_CSP_SCRIPT, STRICT_DYNAMIC.getValue()));
		setCspStyleSrc(getString(CONFIG_CSP_STYLE, DEFAULT_CSP_STYLE));
		if (Application.exists()) {
			final CSPHeaderConfiguration cspConfig = WebApplication.get().getCspSettings().blocking().strict();
			addCspRule(cspConfig, CSPDirective.FONT_SRC, getCspFontSrc());
			addCspRule(cspConfig, CSPDirective.FRAME_SRC, getCspFrameSrc());
			addCspRule(cspConfig, CSPDirective.IMG_SRC, getCspImageSrc());
			addCspRule(cspConfig, CSPDirective.MEDIA_SRC, getCspMediaSrc());
			addCspRule(cspConfig, CSPDirective.SCRIPT_SRC, getCspScriptSrc());
			addCspRule(cspConfig, CSPDirective.STYLE_SRC, getCspStyleSrc());
			app.getWsUrls().forEach(wsUrl -> addCspRule(cspConfig, CSPDirective.CONNECT_SRC, wsUrl, false)); // special code for Safari browser
			if (!Strings.isEmpty(getGaCode())) {
				// https://developers.google.com/tag-manager/web/csp#universal_analytics_google_analytics
				addCspRule(cspConfig, CSPDirective.IMG_SRC, "https://www.google-analytics.com");
				addCspRule(cspConfig, CSPDirective.CONNECT_SRC, "https://www.google-analytics.com");
				addCspRule(cspConfig, CSPDirective.SCRIPT_SRC, "https://www.googletagmanager.com");
			}
			oauthDao.getActive().forEach(oauth -> {
				if (!Strings.isEmpty(oauth.getIconUrl())) {
					addCspRule(cspConfig, CSPDirective.IMG_SRC, oauth.getIconUrl(), false);
				}
			});
		}
	}
}
