package org.openmeetings.utils.crypt;

import java.security.NoSuchAlgorithmException;

public class MD5Implementation extends CryptStringAdapter implements CryptString {

	@Override
	public String createPassPhrase(String userGivenPass) {
		String passPhrase = null;
		try {
			passPhrase = MD5.do_checksum(userGivenPass);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return passPhrase;
	}

	@Override
	public Boolean verifyPassword(String passGiven, String passwdFromDb) {
		return (passwdFromDb.equals(createPassPhrase(passGiven)));
	}
	
}
