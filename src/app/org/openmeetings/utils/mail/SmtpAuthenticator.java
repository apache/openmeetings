package org.openmeetings.utils.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 
 * @author swagner
 * 
 */
public class SmtpAuthenticator extends Authenticator {

	private final String username;
	private final String password;

	public SmtpAuthenticator(String emailUsername, String emailUserpass) {
		this.username = emailUsername;
		this.password = emailUserpass;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {

		return new PasswordAuthentication(username, password);
	}

}
