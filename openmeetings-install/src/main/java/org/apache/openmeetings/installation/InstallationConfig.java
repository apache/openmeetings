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
package org.apache.openmeetings.installation;

import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;

import java.io.Serializable;

import org.apache.openmeetings.util.crypt.SCryptImplementation;

public class InstallationConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private String appName = DEFAULT_APP_NAME;
	private String username;
	private String password;
	private String email;
	private String group;
	private boolean allowFrontendRegister = true;
	private boolean createDefaultObjects = true;
	private String timeZone = getDefaultTimezone();

	private String cryptClassName = SCryptImplementation.class.getCanonicalName();
	//email
	private Integer smtpPort = 25;
	private String smtpServer = "localhost";
	private String mailAuthName = "";
	private String mailAuthPass = "";
	private String mailReferer = "noreply@openmeetings.apache.org";
	private boolean mailUseTls = false;
	//paths
	private Integer docDpi = 150;
	private Integer docQuality = 90;
	private String imageMagicPath = "";
	private String ffmpegPath = "";
	private String soxPath = "";
	private String officePath = "";

	private int defaultLangId = 1;
	private boolean sendEmailAtRegister = false;
	private String urlFeed = "https://mail-archives.apache.org/mod_mbox/openmeetings-user/?format=atom";
	private String urlFeed2 = "https://mail-archives.apache.org/mod_mbox/openmeetings-dev/?format=atom";
	private boolean sendEmailWithVerficationCode = false;
	private boolean sipEnable = false;
	private String sipRoomPrefix = "400";
	private String sipExtenContext = "rooms";
	private boolean replyToOrganizer = true;
	private String baseUrl = DEFAULT_BASE_URL;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isAllowFrontendRegister() {
		return allowFrontendRegister;
	}

	public void setAllowFrontendRegister(boolean allowFrontendRegister) {
		this.allowFrontendRegister = allowFrontendRegister;
	}

	public boolean isCreateDefaultObjects() {
		return createDefaultObjects;
	}

	public void setCreateDefaultObjects(boolean createDefaultObjects) {
		this.createDefaultObjects = createDefaultObjects;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getCryptClassName() {
		return cryptClassName;
	}

	public void setCryptClassName(String cryptClassName) {
		this.cryptClassName = cryptClassName;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getMailAuthName() {
		return mailAuthName;
	}

	public void setMailAuthName(String mailAuthName) {
		this.mailAuthName = mailAuthName;
	}

	public String getMailAuthPass() {
		return mailAuthPass;
	}

	public void setMailAuthPass(String mailAuthPass) {
		this.mailAuthPass = mailAuthPass;
	}

	public String getMailReferer() {
		return mailReferer;
	}

	public void setMailReferer(String mailReferer) {
		this.mailReferer = mailReferer;
	}

	public boolean isMailUseTls() {
		return mailUseTls;
	}

	public void setMailUseTls(boolean mailUseTls) {
		this.mailUseTls = mailUseTls;
	}

	public Integer getDocDpi() {
		return docDpi;
	}

	public void setDocDpi(Integer docDpi) {
		this.docDpi = docDpi;
	}

	public Integer getDocQuality() {
		return docQuality;
	}

	public void setDocQuality(Integer docQuality) {
		this.docQuality = docQuality;
	}

	public String getImageMagicPath() {
		return imageMagicPath;
	}

	public void setImageMagicPath(String imageMagicPath) {
		this.imageMagicPath = imageMagicPath;
	}

	public String getFfmpegPath() {
		return ffmpegPath;
	}

	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	public String getSoxPath() {
		return soxPath;
	}

	public void setSoxPath(String soxPath) {
		this.soxPath = soxPath;
	}

	public String getOfficePath() {
		return officePath;
	}

	public void setOfficePath(String officePath) {
		this.officePath = officePath;
	}

	public int getDefaultLangId() {
		return defaultLangId;
	}

	public void setDefaultLangId(int defaultLangId) {
		this.defaultLangId = defaultLangId;
	}

	public boolean isSendEmailAtRegister() {
		return sendEmailAtRegister;
	}

	public void setSendEmailAtRegister(boolean sendEmailAtRegister) {
		this.sendEmailAtRegister = sendEmailAtRegister;
	}

	public String getUrlFeed() {
		return urlFeed;
	}

	public void setUrlFeed(String urlFeed) {
		this.urlFeed = urlFeed;
	}

	public String getUrlFeed2() {
		return urlFeed2;
	}

	public void setUrlFeed2(String urlFeed2) {
		this.urlFeed2 = urlFeed2;
	}

	public boolean isSendEmailWithVerficationCode() {
		return sendEmailWithVerficationCode;
	}

	public void setSendEmailWithVerficationCode(boolean sendEmailWithVerficationCode) {
		this.sendEmailWithVerficationCode = sendEmailWithVerficationCode;
	}

	public boolean isSipEnable() {
		return sipEnable;
	}

	public void setSipEnable(boolean sipEnable) {
		this.sipEnable = sipEnable;
	}

	public String getSipRoomPrefix() {
		return sipRoomPrefix;
	}

	public void setSipRoomPrefix(String sipRoomPrefix) {
		this.sipRoomPrefix = sipRoomPrefix;
	}

	public String getSipExtenContext() {
		return sipExtenContext;
	}

	public void setSipExtenContext(String sipExtenContext) {
		this.sipExtenContext = sipExtenContext;
	}

	public boolean isReplyToOrganizer() {
		return replyToOrganizer;
	}

	public void setReplyToOrganizer(boolean replyToOrganizer) {
		this.replyToOrganizer = replyToOrganizer;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String toString() {
		return "InstallationConfig [allowFrontendRegister="
				+ allowFrontendRegister + ", createDefaultObjects="
				+ createDefaultObjects + ", cryptClassName=" + cryptClassName
				+ ", smtpPort=" + smtpPort + ", smtpServer=" + smtpServer
				+ ", mailAuthName=" + mailAuthName + ", mailAuthPass="
				+ mailAuthPass + ", mailReferer=" + mailReferer
				+ ", mailUseTls=" + mailUseTls + ", docDpi=" + docDpi
				+ ", docQuality=" + docQuality
				+ ", imageMagicPath=" + imageMagicPath + ", ffmpegPath="
				+ ffmpegPath + ", soxPath=" + soxPath
				+ ", defaultLangId=" + defaultLangId + ", sendEmailAtRegister="
				+ sendEmailAtRegister + ", urlFeed=" + urlFeed + ", urlFeed2="
				+ urlFeed2 + ", sendEmailWithVerficationCode="
				+ sendEmailWithVerficationCode + ", sipEnable="
				+ sipEnable + ", sipRoomPrefix=" + sipRoomPrefix
				+ ", sipExtenContext=" + sipExtenContext
				+ ", replyToOrganizer=" + replyToOrganizer
				+ ", timeZone=" + timeZone
				+ "]";
	}
}
