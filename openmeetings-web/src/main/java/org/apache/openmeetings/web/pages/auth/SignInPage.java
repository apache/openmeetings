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
package org.apache.openmeetings.web.pages.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterFrontend;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.urlForPage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.BaseInitedPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;

public class SignInPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(SignInPage.class, getWebAppRootKey());
	private SignInDialog d;
	private KickMessageDialog m;

	public SignInPage() {
		this(new PageParameters());
	}

	public SignInPage(PageParameters p) {
		super();
		StringValue oauthid = p.get("oauthid");
		if (!oauthid.isEmpty()) { // oauth2 login
			try {
				long serverId = oauthid.toLong(-1);
				OAuthServer server = getBean(OAuth2Dao.class).get(serverId);
				log.debug("OAuthServer={}", server);
				if (server == null) {
					log.warn("OAuth server id={} not found", serverId);
					return;
				}

				if (!p.get("code").isNull()) { // got code
					String code = p.get("code").toString();
					log.debug("OAuth response code={}", code);
					AuthInfo authInfo = getToken(code, server);
					if (authInfo == null) {
						return;
					}
					log.debug("OAuthInfo={}", authInfo);
					OAuthUser user = getAuthParams(authInfo, code, server);
					loginViaOAuth2(user, serverId);
				} else { // redirect to get code
					showAuth(server);
				}
			} catch (IOException|NoSuchAlgorithmException|JSONException e) {
				log.error("OAuth2 login error", e);
			}
		}
		//will try to login directly using parameters sent by POST
		IRequestParameters pp = RequestCycle.get().getRequest().getPostParameters();
		StringValue login = pp.getParameterValue("login"), password = pp.getParameterValue("password");
		if (!login.isEmpty() && !password.isEmpty()) {
			try {
				if (WebSession.get().signIn(login.toString(), password.toString(), Type.user, null)) {
					setResponsePage(Application.get().getHomePage());
				} else {
					log.error("Failed to login using POST parameters passed");
				}
			} catch (OmException e) {
				log.error("Exception while login with POST parameters passed", e);
			}
		}

		RegisterDialog r = new RegisterDialog("register");
		ForgetPasswordDialog f = new ForgetPasswordDialog("forget");
		d = new SignInDialog("signin");
		d.setRegisterDialog(r);
		d.setForgetPasswordDialog(f);
		r.setSignInDialog(d);
		f.setSignInDialog(d);
		m = new KickMessageDialog("kick");
		add(d.setVisible(!WebSession.get().isKickedByAdmin()),
				r.setVisible(allowRegister()), f, m.setVisible(WebSession.get().isKickedByAdmin()));
	}

	static boolean allowRegister() {
		return isAllowRegisterFrontend();
	}

	static boolean allowOAuthLogin() {
		return !getBean(OAuth2Dao.class).getActive().isEmpty();
	}

	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget arg1) {
		WebSession.get().setArea(getUrlFragment(params));
	}

	// ============= OAuth2 methods =============
	private static Map<String, String> getInitParams(final OAuthServer s) {
		Map<String, String> params = new HashMap<>();
		params.put("{$client_id}", s.getClientId());
		params.put("{$redirect_uri}", getRedirectUri(s));
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
				try {
					result = result.replace(e.getKey(), URLEncoder.encode(e.getValue(), UTF_8.name()));
				} catch (UnsupportedEncodingException err) {
					log.error("Unexpected exception while encoding URI param {}", e, err);
				}
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

	private static void prepareConnection(URLConnection _connection) {
		if (!(_connection instanceof HttpsURLConnection)) {
			return;
		}
		if (!getBean(ConfigurationDao.class).getBool(CONFIG_IGNORE_BAD_SSL, false)) {
			return;
		}
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				//no-op
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				//no-op
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		}};
		try {
			HttpsURLConnection connection = (HttpsURLConnection)_connection;
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			connection.setSSLSocketFactory(sslSocketFactory);
			connection.setHostnameVerifier((arg0, arg1) -> true);
		} catch (Exception e) {
			log.error("[prepareConnection]", e);
		}
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

	private static AuthInfo getToken(String code, OAuthServer server) throws IOException {
		String requestTokenBaseUrl = server.getRequestTokenUrl();
		// build url params to request auth token
		String requestTokenParams = server.getRequestTokenAttributes();
		requestTokenParams = prepareUrl(requestTokenParams, getParams(server, code, null));
		// request auth token
		HttpURLConnection connection = (HttpURLConnection) new URL(requestTokenBaseUrl).openConnection();
		prepareConnection(connection);
		connection.setRequestMethod(server.getRequestTokenMethod().name());
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", UTF_8.name());
		connection.setRequestProperty("Content-Length", String.valueOf(requestTokenParams.length()));
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		DataOutputStream paramsOutputStream = new DataOutputStream(connection.getOutputStream());
		paramsOutputStream.writeBytes(requestTokenParams);
		paramsOutputStream.flush();
		String sourceResponse = IOUtils.toString(connection.getInputStream(), UTF_8);
		// parse json result
		AuthInfo result = new AuthInfo(sourceResponse);
		// access token must be specified
		if (result.accessToken == null) {
			log.error("Response doesn't contain access_token field:\n {}", sourceResponse);
			return null;
		}
		return result;
	}

	private static OAuthUser getAuthParams(AuthInfo authInfo, String code, OAuthServer server) throws IOException {
		// prepare url
		String requestInfoUrl = server.getRequestInfoUrl();
		requestInfoUrl = prepareUrl(requestInfoUrl, getParams(server, code, authInfo));
		// send request
		HttpURLConnection connection = (HttpURLConnection) new URL(requestInfoUrl).openConnection();
		if (server.getRequestInfoMethod() == RequestInfoMethod.HEADER) {
			connection.setRequestProperty("Authorization", String.format("bearer %s", authInfo.accessToken));
		} else {
			connection.setRequestMethod(server.getRequestInfoMethod().name());
		}
		prepareConnection(connection);
		String json = IOUtils.toString(connection.getInputStream(), UTF_8);
		log.debug("User info={}", json);
		// parse json result
		return new OAuthUser(json, server);
	}

	private void loginViaOAuth2(OAuthUser user, long serverId) throws IOException, NoSuchAlgorithmException {
		User u = getBean(IUserManager.class).loginOAuth(user, serverId);

		if (u != null && WebSession.get().signIn(u)) {
			setResponsePage(Application.get().getHomePage());
		} else {
			log.error("Failed to login via OAuth2!");
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
