package org.openmeetings.utils.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.openmeetings.app.data.basic.Configurationmanagement;

/**
 * 
 * @author swagner
 *
 */
public class SmtpAuthenticator extends Authenticator{

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
        //String username = "openmeetings@xmlcrm.org";
		String username = Configurationmanagement.getInstance().getConfKey(3, "email_username").getConf_value();
        //String password = "tony123";
		String password = Configurationmanagement.getInstance().getConfKey(3, "email_userpass").getConf_value();
	       
		return new PasswordAuthentication(username,password);
	}

	
}
