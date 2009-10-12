package org.openmeetings.utils.crypt;

/**
 * interface for Encryption-Class
 * see: http://code.google.com/p/openmeetings/w/edit/CustomCryptMechanism
 * 
 * @author sebastianwagner
 *
 */

public interface CryptString {
	
	public String createPassPhrase(String userGivenPass);
	
	public Boolean verifyPassword(String passGiven, String passwdFromDb);

}
