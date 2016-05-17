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

import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_BASE_URL;

import java.io.Serializable;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.util.crypt.SHA256Implementation;

public class InstallationConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String appName = ConfigurationDao.DEFAULT_APP_NAME;
	public String username;
	public String password;
	public String email;
	public String group;
	public String allowFrontendRegister = "1";
	public String createDefaultRooms = "1";
	public String ical_timeZone = "Europe/Berlin";
	
	public String cryptClassName = SHA256Implementation.class.getCanonicalName();
	//email
	public Integer smtpPort = 25;
	public String smtpServer = "localhost";
	public String mailAuthName = "";
	public String mailAuthPass = "";
	public String mailReferer = "noreply@openmeetings.apache.org";
	public String mailUseTls = "0";
	//paths
	public Integer swfZoom = 100;
	public Integer swfJpegQuality = 85;
	public String swfPath = "";
	public String imageMagicPath = "";
	public String ffmpegPath = "";
	public String soxPath = "";
	public String jodPath = "/opt/jod/lib";
	public String officePath = "";
	
	public String defaultLangId = "1";
	public String sendEmailAtRegister = "0";
	public String urlFeed = "http://mail-archives.apache.org/mod_mbox/openmeetings-user/?format=atom";
	public String urlFeed2 = "http://mail-archives.apache.org/mod_mbox/openmeetings-dev/?format=atom";
	public String sendEmailWithVerficationCode = "0";
	public String defaultExportFont = "TimesNewRoman";
    public String red5SipEnable = "no";
    public String red5SipRoomPrefix = "400";
    public String red5SipExtenContext = "rooms";
    public String replyToOrganizer = "1";
    public String baseUrl = DEFAULT_BASE_URL;
    
	@Override
	public String toString() {
		return "InstallationConfig [allowFrontendRegister="
				+ allowFrontendRegister + ", createDefaultRooms="
				+ createDefaultRooms + ", cryptClassName=" + cryptClassName
				+ ", smtpPort=" + smtpPort + ", smtpServer=" + smtpServer
				+ ", mailAuthName=" + mailAuthName + ", mailAuthPass="
				+ mailAuthPass + ", mailReferer=" + mailReferer
				+ ", mailUseTls=" + mailUseTls + ", swfZoom=" + swfZoom
				+ ", swfJpegQuality=" + swfJpegQuality  + ", swfPath=" + swfPath
				+ ", imageMagicPath=" + imageMagicPath + ", ffmpegPath="
				+ ffmpegPath + ", soxPath=" + soxPath + ", jodPath=" + jodPath
				+ ", defaultLangId=" + defaultLangId + ", sendEmailAtRegister="
				+ sendEmailAtRegister + ", urlFeed=" + urlFeed + ", urlFeed2="
				+ urlFeed2 + ", sendEmailWithVerficationCode="
				+ sendEmailWithVerficationCode + ", defaultExportFont="
				+ defaultExportFont + ", red5SipEnable="
				+ red5SipEnable + ", red5SipRoomPrefix=" + red5SipRoomPrefix
				+ ", red5SipExtenContext=" + red5SipExtenContext
				+ ", replyToOrganizer=" + replyToOrganizer
				+ ", ical_timeZone=" + ical_timeZone 
				+ "]";
	}
}
