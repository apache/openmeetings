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
package org.openmeetings.app.sip.xmlrpc.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class OpenXGWrapperClient {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGWrapperClient.class, ScopeApplicationAdapter.webAppRootKey);

	private static OpenXGWrapperClient instance = null;

	public static synchronized OpenXGWrapperClient getInstance() {
		if (instance == null) {
			instance = new OpenXGWrapperClient();
		}
		return instance;
	}
	
	/**
	 * Only for Testing
	 */
	public void testConnection() {
		
		String userid = "****";
		String domain = "****";
		String first_name = "****";
		String middle_i = "****";
		String last_name = "****";
		String password = "****";
		String community_code = "****";
		String language_code = "****";
		String email = "****";
		String adminid = "****";
		
		String client_id = "****";
		String client_secret = "*****";
		
		this.createUser(client_id, client_secret, userid, domain, first_name, middle_i, 
					last_name, password, community_code, 
					language_code, email, adminid);
	}
	
	public void createUser(String client_id, String client_secret, 
			String userid, String domain, String first_name, String middle_i,  
			String last_name, String password, String community_code, 
			String language_code, String email, String adminid
			) {
		try {
			
			log.debug("Test Connection");
			
			// Get target URL
	        String strURL = "http://127.0.0.1:5080/rpc_client/rpc_gateway_wrapper.php";
	        
	        // Prepare HTTP post
	        PostMethod post = new PostMethod(strURL);
	        
	        post.setParameter("userid", userid);
	        
	        post.setParameter("domain", domain);
	        post.setParameter("first_name", first_name);
	        post.setParameter("middle_i", middle_i);
	        post.setParameter("last_name", last_name);
	        post.setParameter("password", password);
	        post.setParameter("community_code", community_code);
	        post.setParameter("language_code", language_code);
	        post.setParameter("email", email);
	        post.setParameter("adminid", adminid);
	        post.setParameter("action", "OpenSIPg.UserCreate");
	        
	        String digest = this.digest_calculate(new Object[]{client_id, userid, domain,
					 first_name, middle_i, last_name,
					 password, community_code,
					 language_code, email, adminid,
					 client_secret});
	        
	        post.setParameter("digest", digest);
	        
	        // Get HTTP client
	        HttpClient httpclient = new HttpClient();
	        // Execute request
            int result = httpclient.executeMethod(post);
            // Display status code
            System.out.println("Response status code: " + result);
            // Display response
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());
			
		} catch (Exception err) {
			log.error("[testConnection]",err);
		}
	}
	
	public String digest_calculate(Object[] params) throws Exception {
		String stringToMd5 = "";
		
		for (int i=0;i<params.length;i++) {
			stringToMd5 += params[i];
		}
		
		return MD5.do_checksum(stringToMd5);
		
	}

}
