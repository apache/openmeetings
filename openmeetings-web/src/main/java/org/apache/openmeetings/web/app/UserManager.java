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
package org.apache.openmeetings.web.app;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.web.app.Application.urlForPage;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendVerificationEmail;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.crypt.ICrypt;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;
import com.hazelcast.map.IMap;

import jakarta.inject.Inject;

/**
 *
 * @author swagner
 *
 */
@Component
public class UserManager implements IUserManager {
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);
	private static final String SAVE_USER_MAP_KEY = "SAVE_USER_MAP_KEY";
	private static final String SAVE_USER_LOCK = "org.apache.openmeetings.user.save";

	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private UserDao userDao;
	@Inject
	private EmailManager emailManager;
	@Inject
	private IClientManager cm;
	private HttpClient httpClient;

	private static boolean sendConfirmation() {
		String baseURL = getBaseUrl();
		return !Strings.isEmpty(baseURL) && isSendVerificationEmail();
	}

	/**
	 * @param u - User with basic parametrs set
	 * @param password - user password
	 * @param hash - activation hash
	 * @return {@link User} of code of error as {@link String}
	 */
	@Override
	public Object registerUser(User u, String password, String hash) {
		return saveUser(u, user -> {
			// Check for required data
			String login = user.getLogin();
			if (!userDao.validLogin(login)) {
				return "error.short.login";
			}
			if (!userDao.checkLogin(login, User.Type.USER, null, null)) {
				return "error.login.inuse";
			}
			String email = user.getAddress() == null ? null : user.getAddress().getEmail();
			if (Strings.isEmpty(email)) {
				return "234"; // invalid email
			}
			if (!userDao.checkEmail(email, User.Type.USER, null, null)) {
				return "error.email.inuse";
			}
			String ahash = Strings.isEmpty(hash) ? randomUUID().toString() : hash;
			user.setActivatehash(ahash);

			// If this user needs first to click his E-Mail verification
			// code then set the status to 0
			if (sendConfirmation() && user.getRights().contains(Right.LOGIN)) {
				user.getRights().remove(Right.LOGIN);
			}

			if (!Strings.isEmpty(password)) {
				user.updatePassword(password);
			}
			User created = userDao.update(user, null);
			if (created == null || created.getId() == null) {
				return UNKNOWN.getKey();
			}
			if (Type.EXTERNAL != created.getType()) {
				emailManager.queueMail(login, email, ahash, sendConfirmation(), user.getLanguageId());
			}
			log.debug("Added user-Id {}", created.getId());
			return created;
		}, UNKNOWN.getKey());
	}

	/**
	 * Perform required actions holding cluster wide lock to avoid concurrent related issues
	 *
	 * @param u - User object to process
	 * @param process - Function to be executed with lock
	 * @param errValue - Value to return in case lock can't be held
	 * @return - the result of User processing
	 */
	public Object saveUser(User u, Function<User, Object> process, Object errValue) {
		IMap<String, String> lockMap = Application.getHazelcast().getMap(SAVE_USER_MAP_KEY);
		boolean locked = false;
		try {
			locked = lockMap.tryLock(SAVE_USER_LOCK
				, 10, TimeUnit.SECONDS
				, 20, TimeUnit.SECONDS // this one should be redundant
			); // 10 sec should be enough for everything
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("[saveUser] interrupted while acquiring registration lock", e);
		} catch (Exception e) {
			log.error("[saveUser] cannot acquire registration lock", e);
		}
		if (!locked) {
			return errValue; // no lock - no registration
		}
		try {
			return process.apply(u);
		} finally {
			try {
				lockMap.unlock(SAVE_USER_LOCK);
			} catch (IllegalMonitorStateException e) {
				log.warn("[saveUser] registration lock lease expired, nothing to unlock", e);
			}
		}
	}

	/**
	 * @param roomId - id of the room user should be kicked from
	 * @return <code>true</code> if there were no errors
	 */
	@Override
	public boolean kickUsersByRoomId(Long roomId) {
		try {
			cm.streamByRoom(roomId).forEach(Application::kickUser);
			return true;
		} catch (Exception err) {
			log.error("[kickUsersByRoomId]", err);
		}
		return false;
	}

	@Override
	public boolean kickExternal(Long roomId, String externalType, String externalId) {
		Boolean result = false;
		try {
			if (roomId == null) {
				return result;
			}
			User u = userDao.getExternalUser(externalId, externalType);
			if (u != null) {
				for (Client c : cm.listByUser(u.getId())) {
					if (roomId.equals(c.getRoomId())) {
						Application.kickUser(c);
						result = true;
					}
				}
			}
		} catch (Exception e) {
			log.error("[kickExternal]", e);
		}
		return result;
	}

	@Override
	public Long getLanguage(Locale loc) {
		return LabelDao.getLanguage(loc, getDefaultLang());
	}

	// ============= OAuth2 methods =============
	public void initHttpClient() {
		HttpClient.Builder builder = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.connectTimeout(Duration.ofSeconds(10));
		final boolean ignoreBadSsl = cfgDao.getBool(CONFIG_IGNORE_BAD_SSL, false);
		System.setProperty("jdk.internal.httpclient.disableHostnameVerification", String.valueOf(ignoreBadSsl));
		if (ignoreBadSsl) {
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					//no-op
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					//no-op
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}
			}};
			try {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, trustAllCerts, new SecureRandom());
				SSLParameters sslParams = new SSLParameters();
				sslParams.setEndpointIdentificationAlgorithm("");
				builder.sslContext(sslContext)
						.sslParameters(sslParams);
			} catch (Exception e) {
				log.error("[initHttpClient]", e);
			}
		}
		httpClient = builder.build();
	}

	public User loginOAuth(String code, OAuthServer server) throws IOException, InterruptedException {
		if (code == null) {
			showAuth(server);
			return null;
		}
		log.debug("OAuth response code={}", code);
		AuthInfo authInfo = getToken(code, server);
		if (authInfo == null) {
			return null;
		}
		log.debug("OAuthInfo={}", authInfo);
		OAuthUser user = getAuthParams(authInfo, code, server);

		if (!userDao.validLogin(user.getLogin())) {
			log.error("Invalid login, please check parameters");
			return null;
		}
		return (User)saveUser(userDao.getByLogin(user.getLogin(), Type.OAUTH, server.getId()), u -> {
			if (!userDao.checkEmail(user.getEmail(), Type.OAUTH, server.getId(), u == null ? null : u.getId())) {
				log.error("Another user with the same email exists");
				return null;
			}
			// generate random password
			// check if the user already exists and register new one if it's needed
			User omUser = u;
			if (u == null) {
				final User fUser = getNewUserInstance(null);
				fUser.setType(Type.OAUTH);
				fUser.getRights().remove(Right.LOGIN);
				fUser.setDomainId(server.getId());
				fUser.addGroup(groupDao.get(getDefaultGroup()));
				for (Map.Entry<String, String> entry : user.getUserData().entrySet()) {
					final String expression = entry.getKey();
					PropertyResolver.setValue(expression, fUser, entry.getValue(), new LanguageConverter(expression, fUser, null, null));
				}
				fUser.setShowContactDataToContacts(true);
				omUser = fUser;
			}
			omUser.setLastlogin(new Date());
			ICrypt crypt = CryptProvider.get();
			omUser = userDao.update(omUser, crypt.randomPassword(25), Long.valueOf(-1));

			return omUser;
		}, null);
	}

	private static Map<String, String> getInitParams(final OAuthServer s) {
		Map<String, String> params = new HashMap<>();
		params.put("{$client_id}", s.getClientId());
		params.put("{$redirect_uri}", getRedirectUri(s));
		return params;
	}

	public static void showAuth(final OAuthServer s) {
		final String state = randomUUID().toString();
		WebSession.get().setOauthState(state);
		String authUrl = prepareUrl(s.getRequestKeyUrl(), getInitParams(s)) + "&state=" + state;
		log.debug("redirectUrl={}", authUrl);
		throw new RedirectToUrlException(authUrl);
	}

	private static String prepareUrl(String urlTemplate, Map<String, String> params) {
		String result = urlTemplate;
		for (Entry<String, String> e : params.entrySet()) {
			if (e.getValue() != null) {
				result = result.replace(e.getKey(), URLEncoder.encode(e.getValue(), UTF_8));
			}
		}
		return result;
	}

	public static String getRedirectUri(OAuthServer server) {
		String result = "";
		if (server.getId() != null) {
			String base = getBaseUrl();
			result = urlForPage(SignInPage.class, new PageParameters().add("oauthid", server.getId()), base);
		}
		return result;
	}

	private static Map<String, String> getParams(final OAuthServer s, String code, AuthInfo authInfo) {
		Map<String, String> params = getInitParams(s);
		params.put("{$client_id}", s.getClientId());
		params.put("{$client_secret}", s.getClientSecret());
		if (authInfo != null) {
			params.put("{$access_token}", authInfo.accessToken);
			params.put("{$user_id}", authInfo.userId);
		}
		if (code != null) {
			params.put("{$code}", code);
		}
		return params;
	}

	private static HttpRequest.Builder setNoCache(HttpRequest.Builder builder) {
		return builder
				.header("Cache-Control", "no-cache, no-store, must-revalidate")
				.header("Pragma", "no-cache")
				.header("Expires", "0");
	}

	String doRequest(HttpRequest request) throws IOException, InterruptedException { // extracted as package private for testing
		return httpClient.send(request, BodyHandlers.ofString()).body();
	}

	private AuthInfo getToken(String code, OAuthServer server) throws IOException, InterruptedException {
		// build url params to request auth token
		String requestTokenParams = prepareUrl(server.getRequestTokenAttributes(), getParams(server, code, null));
		// request auth token
		HttpRequest request = setNoCache(
				HttpRequest.newBuilder()
					.uri(URI.create(server.getRequestTokenUrl()))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("charset", UTF_8.name())
					.method(server.getRequestTokenMethod().name(), BodyPublishers.ofString(requestTokenParams))
				).build();

		String resp = doRequest(request);
		// parse json result
		AuthInfo result = new AuthInfo(resp);
		// access token must be specified
		if (result.accessToken == null) {
			log.error("Response doesn't contain access_token field:\n {}", resp);
			return null;
		}
		return result;
	}

	private OAuthUser getAuthParams(AuthInfo authInfo, String code, OAuthServer server) throws IOException, InterruptedException {
		// prepare url
		String requestInfoUrl = prepareUrl(server.getRequestInfoUrl(), getParams(server, code, authInfo));
		HttpRequest.Builder builder = setNoCache(HttpRequest.newBuilder().uri(URI.create(requestInfoUrl)));

		if (server.getRequestInfoMethod() == RequestInfoMethod.HEADER) {
			builder.header("Authorization", "Bearer " + authInfo.accessToken);
		} else {
			builder.method(server.getRequestInfoMethod().name(), BodyPublishers.noBody());
		}

		String json = doRequest(builder.build());
		log.debug("User info={}", json);
		// parse json result
		return new OAuthUser(json, server);
	}

	private class LanguageConverter extends PropertyResolverConverter {
		private static final long serialVersionUID = 1L;
		final String expression;
		final User fUser;

		public LanguageConverter(final String expression, final User fUser, IConverterLocator converterSupplier, Locale locale) {
			super(converterSupplier, locale);
			this.expression = expression;
			this.fUser = fUser;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Object object, Class<T> clz) {
			if ("languageId".equals(expression) && Long.class.isAssignableFrom(clz)) {
				Long language = 1L;
				String locale = (String)object;
				if (locale != null) {
					Locale loc = Locale.forLanguageTag(locale);
					if (loc != null) {
						language = getLanguage(loc);
						fUser.setLanguageId(language);
						fUser.getAddress().setCountry(loc.getCountry());
					}
				}
				return (T)language;
			}
			return (T)object;
		}

		@Override
		protected <C> String convertToString(C object, Locale locale) {
			return String.valueOf(object);
		}
	}

	private static class AuthInfo {
		final String accessToken;
		final String refreshToken;
		final String tokenType;
		final String userId;
		final long expiresIn;

		AuthInfo(String jsonStr) {
			log.debug("AuthInfo={}", jsonStr);
			JSONObject json = new JSONObject(jsonStr);
			accessToken = json.optString("access_token");
			refreshToken = json.optString("refresh_token");
			tokenType = json.optString("token_type");
			userId = json.optString("user_id");
			expiresIn = json.optLong("expires_in");
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append("AuthInfo [accessToken=").append(accessToken)
				.append(", refreshToken=").append(refreshToken)
				.append(", tokenType=").append(tokenType)
				.append(", userId=").append(userId)
				.append(", expiresIn=").append(expiresIn)
				.append("]").toString();
		}
	}
}
