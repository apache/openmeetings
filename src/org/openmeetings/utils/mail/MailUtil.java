package org.openmeetings.utils.mail;

import java.util.regex.Pattern;

public class MailUtil {
	
	private static final Pattern rfc2822 = Pattern.compile(
	        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
	);
	
	public static boolean matches(String email) {
		
		return rfc2822.matcher(email).matches();
		
	}

}
