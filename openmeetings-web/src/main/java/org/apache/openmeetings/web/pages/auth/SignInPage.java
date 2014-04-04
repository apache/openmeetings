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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

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
import java.util.Random;

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
import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.BaseInitedPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SignInPage extends BaseInitedPage {
	private static final long serialVersionUID = -3843571657066167592L;
	private static final Logger log = Red5LoggerFactory.getLogger(SignInPage.class, webAppRootKey);
	private SignInDialog d;
	
	static boolean allowRegister() {
		return "1".equals(getBean(ConfigurationDao.class).getConfValue(CONFIG_FRONTEND_REGISTER_KEY, String.class, "0"));
	}
	
	static boolean allowOAuthLogin() {
		return getBean(OAuth2Dao.class).getEnabledOAuthServers().size() > 0;
	}
	
	public SignInPage(PageParameters p) {
		super();
		if (p != null) {
			if (p.get("oauthid").toString() != null) { // oauth2 login
				try {
					long serverId = Long.valueOf(p.get("oauthid").toString());
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
		}
		
		RegisterDialog r = new RegisterDialog("register");
		ForgetPasswordDialog f = new ForgetPasswordDialog("forget");
		d = new SignInDialog("signin");
		d.setRegisterDialog(r);
		d.setForgetPasswordDialog(f);
		r.setSignInDialog(d);
		f.setSignInDialog(d);
		add(d, r.setVisible(allowRegister()), f);
	}
	
	public SignInPage() {
		this(null);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		//TODO need to be removed if autoOen will be enabled
		response.render(OnDomReadyHeaderItem.forScript("$('#" + d.getMarkupId() + "').dialog('open');"));
		response.render(new CssContentHeaderItem(".no-close .ui-dialog-titlebar-close { display: none; }", "dialog-noclose", ""));
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
			result = result.replace("{$redirect_uri}", URLEncoder.encode(redirectUri, "UTF-8"));
		}
		if (code != null) {
			result = result.replace("{$code}", code);
		}
		return result;
	}
		
	public static String getRedirectUri(OAuthServer server, Component component) {
		String baseUrl = RequestCycle.get().getUrlRenderer().renderFullUrl(
			    Url.parse(component.urlFor(SignInPage.class,null).toString()));
		
		return baseUrl + "?oauthid=" + server.getId();
	}
		
	private void prepareConnection(URLConnection connection) {
		if (!(connection instanceof HttpsURLConnection)) return;
		ConfigurationDao configurationDao = getBean(ConfigurationDao.class);
		Boolean ignoreBadSSL = configurationDao.getConfValue(CONFIG_IGNORE_BAD_SSL, String.class, "no").equals("yes");
		if (!ignoreBadSSL) return;
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

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
		urlConnection.setRequestProperty("charset", "utf-8");
		urlConnection.setRequestProperty("Content-Length", String.valueOf(requestTokenParams.length()));
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		DataOutputStream paramsOutputStream = new DataOutputStream(urlConnection.getOutputStream());
		paramsOutputStream.writeBytes(requestTokenParams);
		paramsOutputStream.flush();
		String sourceResponse = IOUtils.toString(urlConnection.getInputStream(), "utf-8");
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
	
	private Map<String, String> parseCanonicalResponse(String response) {
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
		String sourceResponse = IOUtils.toString(connection.getInputStream(), "utf-8");
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
		AdminUserDao userDao = getBean(AdminUserDao.class);
		IUserManager userManager = getBean(IUserManager.class); 
		ConfigurationDao configurationDao = getBean(ConfigurationDao.class);
		String login = params.get("login");
		String email = params.get("email");
		String lastname = params.get("lastname");
		String firstname = params.get("firstname");
		if (firstname == null) firstname = "";
		if (lastname == null) lastname = "";
		User user = userDao.getUserByName(login);
		// generate random password
		byte[] rawPass = new byte[16];
		Random rnd = new Random();
		for (int i = 0; i < 16; i++) {
			rawPass[i] = (byte) (97 + rnd.nextInt(25));
		}
		String pass = new String(rawPass);
		// check if the user already exists and register new one if it's needed
		if (user == null) {
			Integer defaultlangId = Integer.valueOf(configurationDao.getConfValue("default_lang_id", String.class, "1"));
			String defaultTimezone = configurationDao.getConfValue("default.timezone", String.class, "");		
			Long res = userManager.registerUserNoEmail(login, pass, lastname, firstname, email, null, null, 
					null, null, null, 0, null, defaultlangId, null, false, true, defaultTimezone);
			if (res == null || res < 0) {
				throw new RuntimeException("Couldn't register new oauth user");
			}
			user = userDao.get(res);
			user.setExternalUserType("oauth2." + serverId);
			userDao.update(user, null);
		} else { // just change password
			// check user type before changing password, it must be match oauthServerId
			if (!("oauth2." + serverId).equals(user.getExternalUserType())) {
				log.error("User already registered!");
				return;
			}
			user = userDao.update(user, pass, -1);
		}
		
		if (WebSession.get().signIn(login, pass, null)) {
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
