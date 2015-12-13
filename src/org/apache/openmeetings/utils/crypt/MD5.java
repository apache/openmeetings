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
package org.apache.openmeetings.utils.crypt;

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
