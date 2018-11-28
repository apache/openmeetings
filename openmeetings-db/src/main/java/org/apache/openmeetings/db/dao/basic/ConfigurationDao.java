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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CHAT_SEND_ON_ENTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_TIMEZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DISPLAY_NAME_EDITABLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_CAM_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_ECHO_PATH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE_PROXY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_BANDWIDTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_GOOGLE_ANALYTICS_CODE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_CSP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_XFRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_EXCLUSIVE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_BITRATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_VIDEO_PRESET;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_FRONTEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_OAUTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REST_ALLOW_ORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_EXTEN_CONTEXT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_SIP_CONTEXT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_BANDWIDTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_ECHO_PATH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_NATIVE_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SSL_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getRoomSettings;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setAllowRegisterFrontend;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setAllowRegisterOauth;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setAllowRegisterSoap;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setAudioBitrate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setAudioRate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setChatSenndOnEnter;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setCryptClassName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setDisplayNameEditable;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setGaCode;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setMaxUploadSize;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setMinFnameLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setMinLnameLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setMinPasswdLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setRestAllowOrigin;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setRoomSettings;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSendRegisterEmail;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSendVerificationEmail;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSipContext;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSipEnabled;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setVideoPreset;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.event.RemoteCommitProvider;
import org.apache.openjpa.event.TCPRemoteCommitProvider;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.wicket.Application;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger log = Red5LoggerFactory.getLogger(ConfigurationDao.class, getWebAppRootKey());
	private static final String[] searchFields = {"key", "value"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserDao userDao;

	public void updateClusterAddresses(String addresses) throws UnknownHostException {
		OpenJPAConfiguration cfg = ((OpenJPAEntityManagerSPI)OpenJPAPersistence.cast(em)).getConfiguration();
		RemoteCommitProvider prov = cfg.getRemoteCommitEventManager().getRemoteCommitProvider();
		if (prov instanceof TCPRemoteCommitProvider) {
			((TCPRemoteCommitProvider)prov).setAddresses(addresses);
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
					.setParameter("keys", Arrays.asList(key))
					.getResultList();
			result.add(r.isEmpty() ? null : r.get(0));
		}
		return result;
	}

	public Configuration get(String key) {
		List<Configuration> list = get(new String[] {key});

		if (list == null || list.isEmpty() || list.get(0) == null) {
			log.warn("Could not find key in configurations: " + key);
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
	public List<Configuration> get(String search, long start, long count, String sort) {
		return setLimits(em.createQuery(DaoHelper.getSearchQuery("Configuration", "c", search, true, false, sort, searchFields), Configuration.class)
				, start, count).getResultList();
	}

	@Override
	public long count() {
		return em.createNamedQuery("countConfigurations", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Configuration", "c", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	@Override
	public Configuration update(Configuration entity, Long userId) {
		return update(entity, userId, false);
	}

	public Configuration update(Configuration entity, Long userId, boolean deleted) {
		String key = entity.getKey();
		String value = entity.getValue();
		if (entity.getId() == null || entity.getId().longValue() <= 0) {
			entity.setInserted(new Date());
			entity.setDeleted(deleted);
			em.persist(entity);
		} else {
			entity.setUser(userDao.get(userId));
			entity.setDeleted(deleted);
			entity.setUpdated(new Date());
			entity = em.merge(entity);
		}
		switch (key) {
			case CONFIG_FLASH_SECURE:
			case CONFIG_FLASH_SECURE_PROXY:
			case CONFIG_FLASH_VIDEO_CODEC:
			case CONFIG_FLASH_VIDEO_FPS:
			case CONFIG_FLASH_VIDEO_BANDWIDTH:
			case CONFIG_FLASH_CAM_QUALITY:
			case CONFIG_FLASH_ECHO_PATH:
			case CONFIG_FLASH_MIC_RATE:
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
			case CONFIG_GOOGLE_ANALYTICS_CODE:
				reloadGaCode();
				break;
			case CONFIG_HEADER_XFRAME:
			{
				IApplication iapp = (IApplication)Application.get(getWicketApplicationName());
				if (iapp != null) {
					iapp.setXFrameOptions(value);
				}
			}
				break;
			case CONFIG_HEADER_CSP:
			{
				IApplication iapp = (IApplication)Application.get(getWicketApplicationName());
				if (iapp != null) {
					iapp.setContentSecurityPolicy(value);
				}
			}
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
			case CONFIG_LOGIN_MIN_LENGTH:
				reloadLoginMinLength();
				break;
			case CONFIG_PASS_MIN_LENGTH:
				reloadPasswdMinLength();
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
			case CONFIG_REGISTER_FRONTEND:
				reloadAllowRegisterFront();
				break;
			case CONFIG_REGISTER_SOAP:
				reloadAllowRegisterSoap();
				break;
			case CONFIG_REGISTER_OAUTH:
				reloadAllowRegisterOauth();
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
		}
		return entity;
	}

	@Override
	public void delete(Configuration entity, Long userId) {
		entity.setUpdated(new Date());
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

	private void reloadGaCode() {
		setGaCode(getString(CONFIG_GOOGLE_ANALYTICS_CODE, null));
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
		OpenmeetingsVariables.setDefaultTimezone(defaultTzName);
	}

	private void reloadRestAllowOrigin() {
		setRestAllowOrigin(getString(CONFIG_REST_ALLOW_ORIGIN, null));
	}

	private void reloadLoginMinLength() {
		setMinLoginLength(getInt(CONFIG_LOGIN_MIN_LENGTH, USER_LOGIN_MINIMUM_LENGTH));
	}

	private void reloadPasswdMinLength() {
		setMinPasswdLength(getInt(CONFIG_LOGIN_MIN_LENGTH, USER_PASSWORD_MINIMUM_LENGTH));
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
		setChatSenndOnEnter(getBool(CONFIG_CHAT_SEND_ON_ENTER, false));
	}

	private void reloadAllowRegisterFront() {
		setAllowRegisterFrontend(getBool(CONFIG_REGISTER_FRONTEND, false));
	}

	private void reloadAllowRegisterSoap() {
		setAllowRegisterSoap(getBool(CONFIG_REGISTER_SOAP, false));
	}

	private void reloadAllowRegisterOauth() {
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

	public void reinit() {
		reloadMaxUpload();
		reloadCrypt();
		setApplicationName(getString(CONFIG_APPLICATION_NAME, DEFAULT_APP_NAME));
		reloadDefaultLang();
		reloadBaseUrl();
		reloadSipEnabled();
		reloadGaCode();
		reloadAudioRate();
		reloadAudioBitrate();
		reloadVideoPreset();
		reloadTimezone();
		reloadRestAllowOrigin();
		reloadRoomSettings();
		reloadLoginMinLength();
		reloadPasswdMinLength();
		reloadDefaultGroup();
		reloadSipContext();
		reloadFnameMinLength();
		reloadLnameMinLength();
		reloadChatSendOnEnter();
		reloadAllowRegisterFront();
		reloadAllowRegisterSoap();
		reloadAllowRegisterOauth();
		reloadSendVerificationEmail();
		reloadSendRegisterEmail();
		reloadDisplayNameEditable();
	}

	private JSONObject reloadRoomSettings() {
		try {
			Properties props = new Properties();
			try (InputStream is = new FileInputStream(new File(new File(OmFileHelper.getRootDir(), "conf"), "red5.properties"))) {
				props.load(is);
			}
			setRoomSettings(new JSONObject()
				.put(FLASH_SECURE, getBool(CONFIG_FLASH_SECURE, false))
				.put(FLASH_NATIVE_SSL, "best".equals(getString(CONFIG_FLASH_SECURE_PROXY, "none")))
				.put(FLASH_PORT, props.getProperty("rtmp.port"))
				.put(FLASH_SSL_PORT, props.getProperty("rtmps.port"))
				.put(FLASH_VIDEO_CODEC, getString(CONFIG_FLASH_VIDEO_CODEC, "h263"))
				.put(FLASH_FPS, getLong(CONFIG_FLASH_VIDEO_FPS, 30L))
				.put(FLASH_BANDWIDTH, getLong(CONFIG_FLASH_VIDEO_BANDWIDTH, 0L))
				.put(FLASH_QUALITY, getLong(CONFIG_FLASH_CAM_QUALITY, 90L))
				.put(FLASH_ECHO_PATH, getLong(CONFIG_FLASH_ECHO_PATH, 128L))
				.put(FLASH_MIC_RATE, getLong(CONFIG_FLASH_MIC_RATE, 22L))
				.put("keycode", new JSONObject()
						.put("arrange", getLong(CONFIG_KEYCODE_ARRANGE, 119L))
						.put("exclusive", getLong(CONFIG_KEYCODE_EXCLUSIVE, 123L))
						.put("mute", getLong(CONFIG_KEYCODE_MUTE, 118L))
						)
				);
		} catch (Exception e) {
			log.error("Unexpected exception while reloading room settings: ", e);
		}
		return getRoomSettings();
	}
}
