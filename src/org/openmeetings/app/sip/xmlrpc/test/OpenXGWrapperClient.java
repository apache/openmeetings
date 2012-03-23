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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class OpenXGWrapperClient {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGWrapperClient.class, OpenmeetingsVariables.webAppRootKey);

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
	        HttpPost post = new HttpPost(strURL);
	        
	        post.getParams().setParameter("userid", userid);
	        
	        post.getParams().setParameter("domain", domain);
	        post.getParams().setParameter("first_name", first_name);
	        post.getParams().setParameter("middle_i", middle_i);
	        post.getParams().setParameter("last_name", last_name);
	        post.getParams().setParameter("password", password);
	        post.getParams().setParameter("community_code", community_code);
	        post.getParams().setParameter("language_code", language_code);
	        post.getParams().setParameter("email", email);
	        post.getParams().setParameter("adminid", adminid);
	        post.getParams().setParameter("action", "OpenSIPg.UserCreate");
	        
	        String digest = this.digest_calculate(new Object[]{client_id, userid, domain,
					 first_name, middle_i, last_name,
					 password, community_code,
					 language_code, email, adminid,
					 client_secret});
	        
	        post.getParams().setParameter("digest", digest);
	        
	        // Get HTTP client
	        HttpClient httpclient = new DefaultHttpClient();
	        // Execute request
            HttpResponse response = httpclient.execute(post);
            // Display status code
            System.out.println("Response status code: " + response.getStatusLine().getStatusCode());
            // Display response
            System.out.println("Response body: ");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
            	System.out.println(EntityUtils.toString(entity));
            }
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
