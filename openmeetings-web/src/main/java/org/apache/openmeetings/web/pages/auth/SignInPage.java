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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.BaseInitedPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SignInPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(SignInPage.class, webAppRootKey);
	private SignInDialog d;
	private KickMessageDialog m;
	
	static boolean allowRegister() {
		return "1".equals(getBean(ConfigurationDao.class).getConfValue(CONFIG_FRONTEND_REGISTER_KEY, String.class, "0"));
	}
	
	static boolean allowOAuthLogin() {
		return getBean(OAuth2Dao.class).getActive().size() > 0;
	}
	
	public SignInPage(PageParameters p) {
		super();
		StringValue oauthid = p.get("oauthid");
		if (!oauthid.isEmpty()) { // oauth2 login
			try {
				long serverId = oauthid.toLong(-1);
				OAuthServer server = getBean(OAuth2Dao.class).get(serverId);
				log.debug("OAuthServer=" + server);
				if (server == null) {
					log.warn("OAuth server id=" + serverId + " not found");
					return;
				}
				
				if (p.get("code").toString() != null) { // got code
					String code = p.get("code").toString();
					log.debug("OAuth response code=" + code);
				 	AuthInfo authInfo = getToken(code, server);
				 	if (authInfo == null) return;
				 	log.debug("OAuthInfo=" + authInfo);
				 	Map<String, String> authParams = getAuthParams(authInfo.accessToken, code, server);
				 	if (authParams != null) {
				 		loginViaOAuth2(authParams, serverId);
				 	}
				} else { // redirect to get code
					String redirectUrl = prepareUrlParams(server.getRequestKeyUrl(), server.getClientId(), 
							null, null, getRedirectUri(server, this), null);
					log.debug("redirectUrl=" + redirectUrl);
					throw new RedirectToUrlException(redirectUrl);
				}
			} catch (IOException e) {
				log.error("OAuth2 login error", e);
			} catch (NoSuchAlgorithmException e) {
				log.error("OAuth2 login error", e);
			}
		}
		//will try to login directly using parameters sent by POST
		IRequestParameters pp = RequestCycle.get().getRequest().getPostParameters();
		StringValue login = pp.getParameterValue("login"), password = pp.getParameterValue("password");
		if (!login.isEmpty() && !password.isEmpty()) {
			if (WebSession.get().signIn(login.toString(), password.toString(), Type.user, null)) {
	 			setResponsePage(Application.get().getHomePage());
			} else {
				log.error("Failed to login using POST parameters passed");
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
	
	public SignInPage() {
		this(new PageParameters());
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}
	
	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget arg1) {
		WebSession.get().setArea(getUrlFragment(params));
	}
	
	// ============= OAuth2 methods =============
		
	public String prepareUrlParams(String urlTemplate, String clientId, String clientSecret, 
			String clientToken, String redirectUri, String code) throws UnsupportedEncodingException {
		String result = urlTemplate;
		if (clientId != null) {
			result = result.replace("{$client_id}", clientId);
		}
		if (clientSecret != null) {
			result = result.replace("{$client_secret}", clientSecret);
		}
		if (clientToken != null) {
			result = result.replace("{$access_token}", clientToken);
		}
		if (redirectUri != null) {
			result = result.replace("{$redirect_uri}", URLEncoder.encode(redirectUri, UTF_8.name()));
		}
		if (code != null) {
			result = result.replace("{$code}", code);
		}
		return result;
	}
		
	public static String getRedirectUri(OAuthServer server, Component component) {
		String result = "";
		if (server.getId() != null) {
			try {
				String base = getBean(ConfigurationDao.class).getBaseUrl();
				URI uri = new URI(base + component.urlFor(SignInPage.class, new PageParameters().add("oauthid", server.getId())));
				result = uri.normalize().toString();
			} catch (URISyntaxException e) {
				log.error("Unexpected error while getting redirect URL", e);
			}
		}
		return result;
	}
		
	private static void prepareConnection(URLConnection connection) {
		if (!(connection instanceof HttpsURLConnection)) return;
		ConfigurationDao configurationDao = getBean(ConfigurationDao.class);
		Boolean ignoreBadSSL = configurationDao.getConfValue(CONFIG_IGNORE_BAD_SSL, String.class, "no").equals("yes");
		if (!ignoreBadSSL) return;
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
				
	    } };
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
		    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
					
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
					
			});
		} catch (Exception e) {
			log.error("[prepareConnection]", e);
		}
	}
		
	private AuthInfo getToken(String code, OAuthServer server) throws IOException {
		String requestTokenBaseUrl = server.getRequestTokenUrl();
		// build url params to request auth token
		String requestTokenParams = server.getRequestTokenAttributes();
		requestTokenParams = prepareUrlParams(requestTokenParams, server.getClientId(), server.getClientSecret(), 
				null, getRedirectUri(server, this), code);
		// request auth token
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(requestTokenBaseUrl).openConnection();
		prepareConnection(urlConnection);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConnection.setRequestProperty("charset", UTF_8.name());
		urlConnection.setRequestProperty("Content-Length", String.valueOf(requestTokenParams.length()));
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		DataOutputStream paramsOutputStream = new DataOutputStream(urlConnection.getOutputStream());
		paramsOutputStream.writeBytes(requestTokenParams);
		paramsOutputStream.flush();
		String sourceResponse = IOUtils.toString(urlConnection.getInputStream(), UTF_8);
		// parse json result
		AuthInfo result = new AuthInfo();
		try {
			JSONObject jsonResult = new JSONObject(sourceResponse.toString());
			if (jsonResult.has("access_token")) {
				result.accessToken = jsonResult.getString("access_token");
			}
			if (jsonResult.has("refresh_token")) {
				result.refreshToken = jsonResult.getString("refresh_token");
			}
			if (jsonResult.has("token_type")) {
				result.tokenType = jsonResult.getString("token_type");
			}
			if (jsonResult.has("expires_in")) {
				result.expiresIn = jsonResult.getLong("expires_in");
			}
		} catch (JSONException e) {
			// try to parse as canonical
			Map<String, String> parsedMap = parseCanonicalResponse(sourceResponse.toString());
			result.accessToken = parsedMap.get("access_token");
			result.refreshToken = parsedMap.get("refresh_token");
			result.tokenType = parsedMap.get("token_type");
			try {
				result.expiresIn = Long.valueOf(parsedMap.get("expires_in"));
			} catch (NumberFormatException nfe) {}
		}
		// access token must be specified
		if (result.accessToken == null) {
			log.error("Response doesn't contain access_token field:\n" + sourceResponse.toString());
			return null;
		}
		return result;
	}
	
	private static Map<String, String> parseCanonicalResponse(String response) {
		String[] parts = response.split("&");
		Map<String, String> result = new HashMap<String, String>();
		for (String part: parts) {
			String pair[] = part.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			}
		}
		return result;
	}
	
	private Map<String, String> getAuthParams(String token, String code, OAuthServer server) throws IOException {
		// get attributes names
		String loginAttributeName = server.getLoginParamName();
		String emailAttributeName = server.getEmailParamName();
		String firstname = server.getFirstnameParamName();
		String lastname = server.getLastnameParamName();
		// prepare url
		String requestInfoUrl = server.getRequestInfoUrl();
		requestInfoUrl = prepareUrlParams(requestInfoUrl, server.getClientId(), server.getClientSecret(), 
				token, getRedirectUri(server, this), code);
		// send request
		URLConnection connection = new URL(requestInfoUrl).openConnection();
		prepareConnection(connection);
		String sourceResponse = IOUtils.toString(connection.getInputStream(), UTF_8);
		// parse json result
		Map<String, String> result = new HashMap<String, String>();
		try {
			JSONObject parsedJson = new JSONObject(sourceResponse);
			result.put("login", parsedJson.getString(loginAttributeName));
			result.put("email", parsedJson.getString(emailAttributeName));
			if (parsedJson.has(firstname)) {
				result.put("firstname", parsedJson.getString(firstname));
			}
			if (parsedJson.has(lastname)) {
				result.put("lastname", parsedJson.getString(lastname));
			}
		} catch (JSONException e) {
			// try to parse response as canonical
			Map<String, String> parsedMap = parseCanonicalResponse(sourceResponse);
			result.put("login", parsedMap.get(loginAttributeName));
			result.put("email", parsedMap.get(emailAttributeName));
			if (parsedMap.containsKey(firstname)) {
				result.put("firstname", parsedMap.get(firstname));
			}
			if (parsedMap.containsKey(lastname)) {
				result.put("lastname", parsedMap.get(lastname));
			}
		}
		return result;
	}
	
	private void loginViaOAuth2(Map<String, String> params, long serverId) throws IOException, NoSuchAlgorithmException {
		User u = getBean(IUserManager.class).loginOAuth(params, serverId);
		
		if (u != null && WebSession.get().signIn(u)) {
 			setResponsePage(Application.get().getHomePage());
		} else {
			log.error("Failed to login via OAuth2!");
		}
	}
		
	private static class AuthInfo {
			
		String accessToken;
		String refreshToken;
		String tokenType;
		long expiresIn;
			
		@Override
		public String toString() {
			return "AuthInfo [accessToken=" + accessToken + ", refreshToken="
					+ refreshToken + ", tokenType=" + tokenType
					+ ", expiresIn=" + expiresIn + "]";
		}
			
	}
	
}
