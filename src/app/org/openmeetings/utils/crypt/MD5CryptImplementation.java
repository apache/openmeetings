package org.openmeetings.utils.crypt;

import java.security.NoSuchAlgorithmException;

public class MD5CryptImplementation extends CryptStringAdapter implements CryptString {

	@Override
	public String createPassPhrase(String userGivenPass) {
		String passPhrase = null;
		try {
			passPhrase = MD5Crypt.crypt(userGivenPass);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return passPhrase;
	}

	@Override
	public Boolean verifyPassword(String passGiven, String passwdFromDb) {
		boolean validPassword = false;
		String salt = passwdFromDb.split("\\$")[2];
	
		try {
			validPassword = passwdFromDb.equals(MD5Crypt.crypt(passGiven, salt));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return validPassword;
	}

}
