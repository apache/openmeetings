package org.openmeetings.app.installation;

import org.openmeetings.utils.crypt.MD5Implementation;

public class InstallationConfig {
	public static final int USER_LOGIN_MINIMUM_LENGTH = 4;
	public static final int USER_PASSWORD_MINIMUM_LENGTH = 4;
	public String allowFrontendRegister = "1";
	public String createDefaultRooms = "1";
	public String ical_timeZone = "Europe/Berlin";
	
	public String cryptClassName = MD5Implementation.class.getCanonicalName();
	//email
	public String smtpPort = "25";
	public String smtpServer = "localhost";
	public String mailAuthName = "";
	public String mailAuthPass = "";
	public String mailReferer = "noreply@openmeetings.apache.org";
	public String mailUseTls = "0";
	//paths
	public String swfZoom = "";
	public String swfJpegQuality = "";
	public String swfPath = "";
	public String imageMagicPath = "";
	public String ffmpegPath = "";
	public String soxPath = "";
	public String jodPath = "./jod/lib";
	public String officePath = "";
	
	public String defaultLangId = "1";
	public String sendEmailAtRegister = "0";
	public String urlFeed = "http://mail-archives.apache.org/mod_mbox/incubator-openmeetings-user/?format=atom";
	public String urlFeed2 = "http://mail-archives.apache.org/mod_mbox/incubator-openmeetings-dev/?format=atom";
	public String sendEmailWithVerficationCode = "0";
	public String defaultExportFont = "TimesNewRoman";
	public String screenViewer = "4";
	public String sipEnable = "0";
	public String sipProxyName = "";
	public String sipPort = "";
	public String sipTunnel = "";
	public String sipRealm = "";
	public String sipOpenxgEnable = "0";
	public String sipForceTunnel = "";
	public String sipCodebase = "";
	public String openxgClientSecret = "";
	public String openxgWrapperUrl = "";
	public String openxgClientId = "";
	public String openxgClientDomain = "";
	public String openxgCommunityCode = "";
	public String openxgLanguageCode = "";
	public String openxgAdminId = "";
	public String sipLanguagePhoneCode = "";
	public String sipPhoneRangeStart = "";
	public String sipPhoneRange = "";
    public String red5SipEnable = "no";
    public String red5SipRoomPrefix = "400";
    public String red5SipExtenContext = "rooms";
    public String replyToOrganizer = "1";
    
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
				+ defaultExportFont + ", screenViewer=" + screenViewer
				+ ", sipEnable=" + sipEnable + ", sipProxyName=" + sipProxyName
				+ ", sipPort=" + sipPort + ", sipTunnel=" + sipTunnel
				+ ", sipRealm=" + sipRealm + ", sipOpenxgEnable="
				+ sipOpenxgEnable + ", sipForceTunnel=" + sipForceTunnel
				+ ", sipCodebase=" + sipCodebase + ", openxgClientSecret="
				+ openxgClientSecret + ", openxgWrapperUrl=" + openxgWrapperUrl
				+ ", openxgClientId=" + openxgClientId
				+ ", openxgClientDomain=" + openxgClientDomain
				+ ", openxgCommunityCode=" + openxgCommunityCode
				+ ", openxgLanguageCode=" + openxgLanguageCode
				+ ", openxgAdminId=" + openxgAdminId
				+ ", sipLanguagePhoneCode=" + sipLanguagePhoneCode
				+ ", sipPhoneRangeStart=" + sipPhoneRangeStart
				+ ", sipPhoneRange=" + sipPhoneRange + ", red5SipEnable="
				+ red5SipEnable + ", red5SipRoomPrefix=" + red5SipRoomPrefix
				+ ", red5SipExtenContext=" + red5SipExtenContext
				+ ", replyToOrganizer=" + replyToOrganizer
				+ ", ical_timeZone=" + ical_timeZone 
				+ "]";
	}
}
