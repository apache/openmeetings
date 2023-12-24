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
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.web.app.Application.urlForPage;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterFrontend;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendVerificationEmail;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.openmeetings.util.OmException;
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

import jakarta.inject.Inject;

/**
 *
 * @author swagner
 *
 */
@Component
public class UserManager implements IUserManager {
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);

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
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Group with the id specified in the configuration value default.group.id
	 *
	 * @param login - user login
	 * @param password - user password
	 * @param lastname - user last name
	 * @param firstname - user first name
	 * @param email - user email
	 * @param country - user country code
	 * @param languageId - language id
	 * @param tzId - the name of the time zone
	 * @return {@link User} of code of error as {@link String}
	 */
	@Override
	public Object registerUser(String login, String password, String lastname,
			String firstname, String email, String country, long languageId, String tzId) {
		try {
			// Checks if FrontEndUsers can register
			if (isAllowRegisterFrontend()) {
				User u = getNewUserInstance(null);
				u.setFirstname(firstname);
				u.setLogin(login);
				u.setLastname(lastname);
				u.getAddress().setCountry(country);
				u.getAddress().setEmail(email);
				u.setTimeZoneId(getTimeZone(tzId).getID());

				// this is needed cause the language is not a necessary data at registering
				u.setLanguageId(languageId != 0 ? languageId : 1);
				u.addGroup(groupDao.get(getDefaultGroup()));

				Object user = registerUser(u, password, null);

				if (user instanceof User && sendConfirmation()) {
					log.debug("User created, confirmation should be sent");
					return -40L;
				}

				log.debug("User creation result: {}", user);
				return user;
			} else {
				log.warn("Frontend registering is disabled");
				return "error.reg.disabled";
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return null;
	}

	/**
	 * @param u - User with basic parametrs set
	 * @param password - user password
	 * @param hash - activation hash
	 * @return {@link User} of code of error as {@link String}
	 * @throws NoSuchAlgorithmException in case password hashing algorithm is not found
	 * @throws OmException in case of any issues with provided data
	 */
	@Override
	public Object registerUser(User u, String password, String hash) throws OmException, NoSuchAlgorithmException {
		// Check for required data
		String login = u.getLogin();
		if (!Strings.isEmpty(login) && login.length() >= getMinLoginLength()) {
			// Check for duplicates
			boolean checkName = userDao.checkLogin(login, User.Type.USER, null, null);
			String email = u.getAddress() == null ? null : u.getAddress().getEmail();
			boolean checkEmail = Strings.isEmpty(email) || userDao.checkEmail(email, User.Type.USER, null, null);
			if (checkName && checkEmail) {
				String ahash = Strings.isEmpty(hash) ? randomUUID().toString() : hash;
				if (Type.EXTERNAL != u.getType() && !Strings.isEmpty(email)) {
					emailManager.sendMail(login, email, ahash, sendConfirmation(), u.getLanguageId());
				}

				// If this user needs first to click his E-Mail verification
				// code then set the status to 0
				if (sendConfirmation() && u.getRights().contains(Right.LOGIN)) {
					u.getRights().remove(Right.LOGIN);
				}

				u.setActivatehash(ahash);
				if (!Strings.isEmpty(password)) {
					u.updatePassword(password);
				}
				u = userDao.update(u, null);
				log.debug("Added user-Id {}", u.getId());

				if (u.getId() != null) {
					return u;
				}
			} else {
				if (!checkName) {
					return "error.login.inuse";
				} else {
					return "error.email.inuse";
				}
			}
		} else {
			return "error.short.login";
		}
		return UNKNOWN.getKey();
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

	public User loginOAuth(String code, OAuthServer server) throws IOException, NoSuchAlgorithmException, InterruptedException {
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
		User u = userDao.getByLogin(user.getLogin(), Type.OAUTH, server.getId());
		if (!userDao.checkEmail(user.getEmail(), Type.OAUTH, server.getId(), u == null ? null : u.getId())) {
			log.error("Another user with the same email exists");
			return null;
		}
		// generate random password
		// check if the user already exists and register new one if it's needed
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
			u = fUser;
		}
		u.setLastlogin(new Date());
		ICrypt crypt = CryptProvider.get();
		u = userDao.update(u, crypt.randomPassword(25), Long.valueOf(-1));

		return u;
	}

	private static Map<String, String> getInitParams(final OAuthServer s) {
		Map<String, String> params = new HashMap<>();
		params.put("{$client_id}", s.getClientId());
		params.put("{$redirect_uri}", getRedirectUri(s));
		params.put("{$state}", randomUUID().toString());
		return params;
	}

	public static void showAuth(final OAuthServer s) {
		String authUrl = prepareUrl(s.getRequestKeyUrl(), getInitParams(s));
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
