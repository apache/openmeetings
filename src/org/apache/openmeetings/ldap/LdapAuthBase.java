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
package org.apache.openmeetings.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


/**
 * 
 * @author o.becherer
 * 
 * BaseClass for optional LDAP Authentification
 *
 */
public class LdapAuthBase {
	/** LdapConnectionUrl */
	private String ldap_connection_url = "";
	
	/** LdapServer Login distinguished name */
	private String ldap_admin_dn = "";
	
	/** LdapServer Passwd */
	private String ldap_passwd = "";
		
	/** Security Authentification Type */
	private String ldap_auth_type = "simple";
	
	/** Directory Context */
	private DirContext authContext = null;
	
	/** ContextFactory */
	private static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	
	public static final String LDAP_AUTH_TYPE_SIMPLE = "simple";
	public static final String LDAP_AUTH_TYPE_NONE = "none";
	
	private static final Logger log = Red5LoggerFactory.getLogger(LdapAuthBase.class, OpenmeetingsVariables.webAppRootKey);

	
	/**
	 * Configuring LdapConnection
	 * @param connectionUrl
	 * @param admin
	 * @param passwd
	 * @param base
	 * @param authType
	 */
	//------------------------------------------------------------------------------------------------------
	public LdapAuthBase(String connectionUrl, String admin_dn, String passwd, String authType){
		log.debug("LdapAuthBase");
		
		this.ldap_connection_url = connectionUrl;
		this.ldap_admin_dn = admin_dn;
		this.ldap_passwd = passwd;
		this.ldap_auth_type = authType;
	}
	//------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Autentificate User
	 * @param username
	 * @param passwd
	 */
	//-------------------------------------------------------------------------------------------------------
	public boolean authenticateUser(String username, String passwd){
		log.debug("authenticateUser");
		
		Properties ldapAuthenticateProperties = new Properties();
	    ldapAuthenticateProperties.put(Context.PROVIDER_URL, ldap_connection_url);
	    ldapAuthenticateProperties.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
	    ldapAuthenticateProperties.put(Context.REFERRAL, "follow");
	    ldapAuthenticateProperties.put(Context.SECURITY_AUTHENTICATION, ldap_auth_type);
	    ldapAuthenticateProperties.put(Context.SECURITY_PRINCIPAL, username);
	    ldapAuthenticateProperties.put(Context.SECURITY_CREDENTIALS, passwd);
	    ldapAuthenticateProperties.put("java.naming.ldap.referral.bind", "true");
		
	    
	    if(ldap_admin_dn == null)
	    	ldap_admin_dn = "";
	    
	    if(!ldap_auth_type.equals(LDAP_AUTH_TYPE_NONE) && ldap_admin_dn.length() > 0){
	    	log.debug("\n\nAuthentification to LDAP - Server start");
	    	try {
	    		loginToLdapServer();
	    	
	    		authContext = new InitialDirContext(ldapAuthenticateProperties);
	    	} catch (Exception ae){
	    		log.error("\n\nAuthentification on LDAP Server failed : " + ae.getMessage());
	    		log.error("[Authentification on LDAP Server failed]",ae);
	    		return false;
	    	}
	    }
	    
	    else{
	    	log.debug("\n\nConnection to LDAP - Server start (without Server login)");
	    	try{
	    		authContext = new InitialDirContext(ldapAuthenticateProperties);
	    	}catch(Exception e){
	    		log.error("\n\nConnection to LDAP Server failed : " + e.getMessage());
	    		log.error("[Connection to LDAP Server failed]",e);
	    		return false;
	    	}
	    }
	    
	    return true;
		
	}
	//-------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Login to LdapServer
	 */
	//-------------------------------------------------------------------------------------------------------
	private void loginToLdapServer() throws Exception{
		log.debug("loginToLdapServer");
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, ldap_connection_url);
		env.put(Context.SECURITY_AUTHENTICATION, ldap_auth_type);
		env.put(Context.SECURITY_PRINCIPAL, ldap_admin_dn);
		env.put(Context.SECURITY_CREDENTIALS, ldap_passwd);
		
		authContext = new InitialDirContext(env);
		
	}
	//-------------------------------------------------------------------------------------------------------
	
	
	 /**
	  * @param searchScope LDAP Url to search within
	  * @param filter LDAP Filter
	  * @param attributes Attributes to extract from LDAP Search
	  */
	//-------------------------------------------------------------------------------------------------------
	public Vector<HashMap<String, String>> getData(String searchScope, String filter, List<String> attributes){
		log.debug("getData");
		
		// Searchparams
		SearchControls constraints = new SearchControls();
		 
		// Recursive Search
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		// Result
		Vector<HashMap<String, String>>  result = new Vector<HashMap<String, String>>() ;
		
		try{
			// search
			NamingEnumeration<SearchResult> results = authContext.search(searchScope, filter, constraints);
			
			// Stepping through the data
			while (results != null && results.hasMore()) {
			     HashMap<String, String> innerMap = new HashMap<String, String>();
			     
				 // Next result
			     SearchResult si = results.next();
			     
			     if(si == null){
			    	 continue;
			     }
			     
			     // Attribute
			     javax.naming.directory.Attributes attrs = si.getAttributes();
			     
			     if(attrs != null){
			    	 for(int i = 0; i < attributes.size(); i++){
			    		 
			    		 String key = attributes.get(i);
			    		 String val = "";
			    		
			    		 Attribute but = attrs.get(key);
			    		 
			    		 if(but!= null){
			    		 	 Object obj = but.get();
				    		 
				    		 if(obj != null)
				    			 val = String.valueOf(obj);
			    		 }
			    		 innerMap.put(key,val );
			    	 }
			     }
			     
			     result.add(innerMap);
			     
			    
			  }
		}catch(Exception e){
			log.error("Error occured on LDAP Search : " , e);
		}
		
		return result;
	}
	
	//-------------------------------------------------------------------------------------------------------

	/**
	 * @param searchBase Ldap base to begin de SUB scope search for the userDN
	 * @param searchFilter Ldap filter to search only for the specified loginame while looking for the userDN
	 * @param ldap_fieldname_user_principal ldap sttribute name that contains the user loginame
	 * @return
	 */
	public HashMap<String, String> getUidCnHashMap(String searchBase, String searchFilter, String ldap_fieldname_user_principal) {
		HashMap<String, String> uidCnDictionary = new HashMap<String, String>();

		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> results = null;
		try {
			results = authContext.search(searchBase, searchFilter,  searchCtls);
			while (results.hasMore()) {
				SearchResult searchResult = results.next();
				// 'cn' gets the name of the entry relative to searchbase for instance: "uid=user,ou=people"
				// The entry DN is found by appending the searchbase. For instance if searchbase 
				// is (dc=mydomain,dc=org), then DN="uid=user,ou=people,dc=mydomain,dc=org"
				String cn = searchResult.getName();
				Attributes attributes = searchResult.getAttributes();
				Attribute attrib = attributes.get(ldap_fieldname_user_principal);
				
				if (attrib != null) {
					String uid = (String) attrib.get();
					uidCnDictionary.put(uid, cn);
				}
			}
		} catch (NamingException e) {
			log.error("Error occured on LDAP Search : " + e.getMessage());
			log.error("Error occured on LDAP Search : " , e);
		}
		return uidCnDictionary;
	}
	
}
