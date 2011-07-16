package org.openmeetings.utils;

import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class DefaultSchemeCreator {
    public static void makeDefaultScheme() throws Exception {

        String username = ("username");
        String userpass = ("userpass");
        String useremail = ("useremail");
        String orgname = ("orgname");
        String configdefault = ("configdefault");

        String configreferer = ("configreferer");
        String configsmtp = ("configsmtp");
        String configsmtpport = ("configsmtpport");
        String configmailuser = ("configmailuser");
        String configmailpass = ("configmailpass");
        String mailusetls = ("mailusetls");

        String configdefaultLang = ("configdefaultLang");
        String swf_path = ("swftools_path");
        String im_path = ("imagemagick_path");
        String sendEmailAtRegister = ("sendEmailAtRegister");
        String sendEmailWithVerficationCode = ("sendEmailWithVerficationCode");

        String default_export_font = ("default_export_font");

        String crypt_ClassName = ("org.openmeetings.utils.crypt.MD5CryptImplementation");

        String ffmpeg_path = ("ffmpeg_path");

        String sox_path = ("sox_path");

        String screen_viewer = ("screen_viewer");


        //SIP Applet Configuration
        String sip_enable = ("sip_enable");
        String sip_realm = ("sip_realm");
        String sip_port = ("sip_port");
        String sip_proxyname = ("sip_proxyname");
        String sip_tunnel = ("sip_tunnel");
        String sip_codebase = ("sip_codebase");
        String sip_forcetunnel = ("sip_forcetunnel");


        //OpenXG / OpenSIPg Configuration
        String sip_openxg_enable = ("sip_openxg_enable");
        String openxg_wrapper_url = ("openxg_wrapper_url");
        String openxg_client_id = ("openxg_client_id");
        String openxg_client_secret = ("openxg_client_secret");
        String openxg_client_domain = ("openxg_client_domain");
        String openxg_community_code = ("openxg_community_code");
        String openxg_language_code = ("openxg_language_code");
        String openxg_adminid = ("openxg_adminid");

        //SIP Phone Range Configuration
        String sip_language_phonecode = ("sip_language_phonecode");
        String sip_phonerange_start = ("sip_phonerange_start");
        String sip_phonerange = ("sip_phonerange");

        String timeZone = ("timeZone");

        String filePath = ScopeApplicationAdapter.webAppPath + "/languages/";

        String url_feed = "http://groups.google.com/group/openmeetings-dev/feed/atom_v1_0_msgs.xml";
        String url_feed2 = "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";

        ImportInitvalues.getInstance().loadMainMenu();
        ImportInitvalues.getInstance().loadErrorMappingsFromXML(filePath);
        ImportInitvalues.getInstance().loadSalutations();

        ImportInitvalues.getInstance().loadConfiguration(crypt_ClassName, configdefault, configsmtp, configsmtpport,
                configreferer, configmailuser, configmailpass, mailusetls, configdefaultLang,
                swf_path, im_path, url_feed, url_feed2,
                sendEmailAtRegister, sendEmailWithVerficationCode, default_export_font, screen_viewer,
                ffmpeg_path, sox_path,
                sip_enable, sip_realm,
                sip_port,
                sip_proxyname,
                sip_tunnel,
                sip_codebase,
                sip_forcetunnel,
                sip_openxg_enable,
                openxg_wrapper_url,
                openxg_client_id,
                openxg_client_secret,
                openxg_client_domain,
                openxg_community_code,
                openxg_language_code,
                openxg_adminid,
                sip_language_phonecode,
                sip_phonerange_start,
                sip_phonerange);

        ImportInitvalues.getInstance().loadInitUserAndOrganisation(username, userpass, useremail, orgname, timeZone);
        ImportInitvalues.getInstance().loadDefaultRooms();

        // AppointMent Categories
        ImportInitvalues.getInstance().loadInitAppointmentCategories();

        // Appointment Remindertypes
        ImportInitvalues.getInstance().loadInitAppointmentReminderTypes();
    }
}
