package org.openmeetings.utils.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static String toHexString(byte b) {
        int value = (b & 0x7F) + (b < 0 ? 128 : 0);
     
         String ret = (value < 16 ? "0" : "");
         ret += Integer.toHexString(value).toLowerCase();
     
     return ret;
    }
   
    public static String do_checksum(String data) throws NoSuchAlgorithmException {
    	MessageDigest md5 = MessageDigest.getInstance("MD5");
		StringBuffer strbuf = new StringBuffer();

		md5.update(data.getBytes(), 0, data.length());
		byte[] digest = md5.digest();

		for (int i = 0; i < digest.length; i++) {
			strbuf.append(toHexString(digest[i]));
		}

		return strbuf.toString();
	}
}
