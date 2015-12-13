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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.LdapConfigDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.ldap.config.ConfigReader;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Management of optional LDAP Login
 * 
 * @author o.becherer
 * 
 */
public class LdapLoginManagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			LdapLoginManagement.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private ManageCryptStyle cryptManager;
	@Autowired
	private UsersDao usersDao;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";

	// ConfigConstants
	public static final String CONFIGKEY_LDAP_URL = "ldap_conn_url";
	public static final String CONFIGKEY_LDAP_ADMIN_DN = "ldap_admin_dn";
	public static final String CONFIGKEY_LDAP_ADMIN_PASSWD = "ldap_passwd";
	public static final String CONFIGKEY_LDAP_SEARCH_SCOPE = "ldap_search_base";
	public static final String CONFIGKEY_LDAP_SERVER_TYPE = "ldap_server_type"; // for
																				// OpenLDAP
																				// use
																				// only
	public static final String CONFIGKEY_LDAP_AUTH_TYPE = "ldap_auth_type";

	public static final String CONFIGKEY_LDAP_FIELDNAME_USER_PRINCIPAL = "field_user_principal";
	public static final String CONFIGKEY_LDAP_SYNC_PASSWD_OM = "ldap_sync_password_to_om"; // 'yes'
																							// or
																							// 'no'

	public static final String CONFIGKEY_LDAP_USE_LOWER_CASE = "ldap_use_lower_case";
	
	public static final String CONFIGKEY_LDAP_TIMEZONE_NAME = "ldap_user_timezone";
	
	/***
	 * for future use (lemeur) public static final String
	 * CONFIGKEY_LDAP_USER_EXTRAFILTER = "ldap_user_extrafilter"; public static
	 * final String CONFIGKEY_LDAP_GROUP_FILTER_NUM = "ldap_group_filter_num";
	 * public static final String CONFIGKEY_LDAP_GROUP_FILTER_NAME_PREFIX =
	 * "ldap_group_filter_name_"; public static final String
	 * CONFIGKEY_LDAP_GROUP_FILTER_BASE_PREFIX = "ldap_group_filter_base_";
	 * public static final String CONFIGKEY_LDAP_GROUP_FILTER_TYPE_PREFIX =
	 * "ldap_group_filter_type_"; public static final String
	 * CONFIGKEY_LDAP_GROUP_FILTER_TEXT_PREFIX = "ldap_group_filter_text_";
	 ***/

	// LDAP default attributes mapping
	public static final String LDAP_KEY_LASTNAME = "sn";
	public static final String LDAP_KEY_FIRSTNAME = "givenName";
	public static final String LDAP_KEY_MAIL = "mail";
	public static final String LDAP_KEY_STREET = "streetAddress";
	public static final String LDAP_KEY_ADDITIONAL_NAME = "description";
	public static final String LDAP_KEY_FAX = "facsimileTelephoneNumber";
	public static final String LDAP_KEY_ZIP = "postalCode";
	public static final String LDAP_KEY_COUNTRY = "co";
	public static final String LDAP_KEY_TOWN = "l";
	public static final String LDAP_KEY_PHONE = "telephoneNumber";
	public static final String LDAP_KEY_TIMEZONE = "timezone";
	public static final String LDAP_KEY_PICTURE_URI = "pictureUri";

	// LDAP custom attribute mapping keys
	public static final String CONFIGKEY_LDAP_KEY_LASTNAME = "ldap_user_attr_lastname";
	public static final String CONFIGKEY_LDAP_KEY_FIRSTNAME = "ldap_user_attr_firstname";
	public static final String CONFIGKEY_LDAP_KEY_MAIL = "ldap_user_attr_mail";
	public static final String CONFIGKEY_LDAP_KEY_STREET = "ldap_user_attr_street";
	public static final String CONFIGKEY_LDAP_KEY_ADDITIONAL_NAME = "ldap_user_attr_additionalname";
	public static final String CONFIGKEY_LDAP_KEY_FAX = "ldap_user_attr_fax";
	public static final String CONFIGKEY_LDAP_KEY_ZIP = "ldap_user_attr_zip";
	public static final String CONFIGKEY_LDAP_KEY_COUNTRY = "ldap_user_attr_country";
	public static final String CONFIGKEY_LDAP_KEY_TOWN = "ldap_user_attr_town";
	public static final String CONFIGKEY_LDAP_KEY_PHONE = "ldap_user_attr_phone";
	public static final String CONFIGKEY_LDAP_PICTURE_URI = "ldap_user_picture_uri";

	/**
	 * Determine if is a supported Auth Type
	 * 
	 * @param authType
	 */
	// -------------------------------------------------------------------------------------------------------
	public static boolean isValidAuthType(String authType) {
		log.debug("isValidAuthType");

		if (authType != null) {
			if (!authType.isEmpty()) {
				if (authType
						.equalsIgnoreCase(LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE))
					return true;
				if (authType.equalsIgnoreCase(LdapAuthBase.LDAP_AUTH_TYPE_NONE))
					return true;
			}
		}

		return false;

	}

	// -------------------------------------------------------------------------------------------------------

	/**
	 * Ldap Password Synch to OM DB set active ? defaults to true in case of
	 * error so as to keep old behaviour
	 */
	public boolean getLdapPwdSynchStatus(Long ldapConfigId) { // TIBO
		// Retrieve Configuration Data
		HashMap<String, String> configData;

		LdapConfig ldapConfig = ldapConfigDao.get(ldapConfigId);

		try {
			configData = getLdapConfigData(ldapConfig.getConfigFileName());
		} catch (Exception e) {
			log.error("Error on getLdapPwdSynchStatus : " + e.getMessage());
			return true;
		}

		if (configData == null || configData.size() < 1) {
			log.error("Error on getLdapPwdSynchStatus : Configurationdata couldnt be retrieved!");
			return true;
		}

		// Connection URL
		String ldap_synch_passwd_to_om = configData
				.get(CONFIGKEY_LDAP_SYNC_PASSWD_OM);
		if (ldap_synch_passwd_to_om.equals("no")) {
			log.debug("getLdapPwdSynchStatus: returns FALSE (val="
					+ ldap_synch_passwd_to_om + ")");
			return false;
		} else {
			log.debug("getLdapPwdSynchStatus: returns TRUE (val="
					+ ldap_synch_passwd_to_om + ")");
			return true;
		}
	}

	/**
	 * Retrieving LdapData from Config
	 */
	// ----------------------------------------------------------------------------------------
	public HashMap<String, String> getLdapConfigData(String ldapConfigfileName)
			throws Exception {
		log.debug("LdapLoginmanagement.getLdapConfigData");

		return readConfig(new File(OmFileHelper.getConfDir(), ldapConfigfileName));
	}

	// ----------------------------------------------------------------------------------------

	/**
	 * Reading Ldap Config via ConfigReader
	 */
	// ----------------------------------------------------------------------------------------
	private HashMap<String, String> readConfig(File config)
			throws Exception {
		log.debug("LdapLoginmanagement.readConfig : " + config);

		if (!config.isFile())
			return null;

		ConfigReader reader = new ConfigReader();
		reader.readConfig(config);

		return reader.getConfigMap();

	}

	// ----------------------------------------------------------------------------------------

	/**
	 * Ldap Login
	 * 
	 * Connection Data is retrieved from ConfigurationFile
	 * 
	 */
	// ----------------------------------------------------------------------------------------
	public Object doLdapLogin(String user, String passwd,
			Client currentClient, IClient client, String SID, String domain) {
		log.debug("LdapLoginmanagement.doLdapLogin");

		// Retrieve Configuration Data
		HashMap<String, String> configData;

		try {
			configData = getLdapConfigData(domain);
		} catch (Exception e) {
			log.error("Error on LdapAuth : " + e.getMessage());
			return null;
		}

		if (configData == null || configData.size() < 1) {
			log.error("Error on LdapLogin : Configurationdata couldnt be retrieved!");
			return null;
		}

		// Connection URL
		String ldap_url = configData.get(CONFIGKEY_LDAP_URL);

		// for OpenLDAP only
		// LDAP SERVER TYPE to search accordingly
		String ldap_server_type = configData.get(CONFIGKEY_LDAP_SERVER_TYPE);

		// Username for LDAP SERVER himself
		String ldap_admin_dn = configData.get(CONFIGKEY_LDAP_ADMIN_DN);

		// Password for LDAP SERVER himself
		String ldap_passwd = configData.get(CONFIGKEY_LDAP_ADMIN_PASSWD);

		// SearchScope for retrievment of userdata
		String ldap_search_scope = configData.get(CONFIGKEY_LDAP_SEARCH_SCOPE);

		// FieldName for Users's Principal Name
		String ldap_fieldname_user_principal = configData
				.get(CONFIGKEY_LDAP_FIELDNAME_USER_PRINCIPAL);

		// Wether or not we'll store Ldap passwd into OM db
		boolean ldap_sync_passwd_to_om = "no".equals(configData.get(CONFIGKEY_LDAP_SYNC_PASSWD_OM));

		/***
		 * for future use (lemeur) // Ldap user filter to refine the search
		 * String ldap_user_extrafilter =
		 * configData.get(CONFIGKEY_LDAP_USER_EXTRAFILTER);
		 * 
		 * // Count of Ldap group filters String ldap_group_filter_num =
		 * configData.get(CONFIGKEY_LDAP_GROUP_FILTER_NUM);
		 * 
		 * // Prefix name of Ldap group filter name String
		 * ldap_group_filter_name_prefix =
		 * configData.get(CONFIGKEY_LDAP_GROUP_FILTER_NAME_PREFIX);
		 * 
		 * // Prefix name of Ldap group filter base String
		 * ldap_group_filter_base_prefix =
		 * configData.get(CONFIGKEY_LDAP_GROUP_FILTER_NAME_PREFIX);
		 * 
		 * // Prefix name of Ldap group filter type String
		 * ldap_group_filter_type_prefix =
		 * configData.get(CONFIGKEY_LDAP_GROUP_FILTER_TYPE_PREFIX);
		 * 
		 * // Prefix name of Ldap group filter text String
		 * ldap_group_filter_text_prefix =
		 * configData.get(CONFIGKEY_LDAP_GROUP_FILTER_TEXT_PREFIX);
		 ***/

		// Get custom Ldap attributes mapping
		String ldap_user_attr_lastname = configData
				.get(CONFIGKEY_LDAP_KEY_LASTNAME);
		String ldap_user_attr_firstname = configData
				.get(CONFIGKEY_LDAP_KEY_FIRSTNAME);
		String ldap_user_attr_mail = configData.get(CONFIGKEY_LDAP_KEY_MAIL);
		String ldap_user_attr_street = configData
				.get(CONFIGKEY_LDAP_KEY_STREET);
		String ldap_user_attr_additional_name = configData
				.get(CONFIGKEY_LDAP_KEY_ADDITIONAL_NAME);
		String ldap_user_attr_fax = configData.get(CONFIGKEY_LDAP_KEY_FAX);
		String ldap_user_attr_zip = configData.get(CONFIGKEY_LDAP_KEY_ZIP);
		String ldap_user_attr_country = configData
				.get(CONFIGKEY_LDAP_KEY_COUNTRY);
		String ldap_user_attr_town = configData.get(CONFIGKEY_LDAP_KEY_TOWN);
		String ldap_user_attr_phone = configData.get(CONFIGKEY_LDAP_KEY_PHONE);
		String ldap_user_attr_timezone = configData.get(CONFIGKEY_LDAP_TIMEZONE_NAME);
		String ldap_user_picture_uri = configData.get(CONFIGKEY_LDAP_PICTURE_URI);
		String ldap_use_lower_case = configData.get(CONFIGKEY_LDAP_USE_LOWER_CASE);
		
		
		if (ldap_use_lower_case != null && ldap_use_lower_case.equals("true")) {
			user = user.toLowerCase();
		}

		if (ldap_user_attr_lastname == null) {
			ldap_user_attr_lastname = LDAP_KEY_LASTNAME;
		}
		if (ldap_user_attr_firstname == null) {
			ldap_user_attr_firstname = LDAP_KEY_FIRSTNAME;
		}
		if (ldap_user_attr_mail == null) {
			ldap_user_attr_mail = LDAP_KEY_MAIL;
		}
		if (ldap_user_attr_street == null) {
			ldap_user_attr_street = LDAP_KEY_STREET;
		}
		if (ldap_user_attr_additional_name == null) {
			ldap_user_attr_additional_name = LDAP_KEY_ADDITIONAL_NAME;
		}
		if (ldap_user_attr_fax == null) {
			ldap_user_attr_fax = LDAP_KEY_FAX;
		}
		if (ldap_user_attr_zip == null) {
			ldap_user_attr_zip = LDAP_KEY_ZIP;
		}
		if (ldap_user_attr_country == null) {
			ldap_user_attr_country = LDAP_KEY_COUNTRY;
		}
		if (ldap_user_attr_town == null) {
			ldap_user_attr_town = LDAP_KEY_TOWN;
		}
		if (ldap_user_attr_phone == null) {
			ldap_user_attr_phone = LDAP_KEY_PHONE;
		}
		if (ldap_user_attr_timezone == null) {
			ldap_user_attr_timezone = LDAP_KEY_TIMEZONE;
		}
		if (ldap_user_picture_uri == null) {
			ldap_user_picture_uri = LDAP_KEY_PICTURE_URI;
		}

		// Auth Type
		String ldap_auth_type = configData.get(CONFIGKEY_LDAP_AUTH_TYPE);

		if (ldap_auth_type == null)
			ldap_auth_type = "";

		if (!isValidAuthType(ldap_auth_type)) {
			log.error("ConfigKey in Ldap Config contains invalid auth type : '"
					+ ldap_auth_type + "' -> Defaulting to "
					+ LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE);
			ldap_auth_type = LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE;
		}

		// Filter for Search of UserData
		String ldap_search_filter = "(" + ldap_fieldname_user_principal + "="
				+ user + ")";

		log.debug("Searching userdata with LDAP Search Filter :"
				+ ldap_search_filter);

		// replace : -> in config = are replaced by : to be able to build valid
		// key=value pairs
		ldap_search_scope = ldap_search_scope.replaceAll(":", "=");
		ldap_admin_dn = ldap_admin_dn.replaceAll(":", "=");

		LdapAuthBase lAuth = new LdapAuthBase(ldap_url, ldap_admin_dn,
				ldap_passwd, ldap_auth_type);

		log.debug("authenticating admin...");
		lAuth.authenticateUser(ldap_admin_dn, ldap_passwd);

		log.debug("Checking server type...");
		// for OpenLDAP only
		if (ldap_server_type.equalsIgnoreCase("OpenLDAP")) {
			String ldapUserDN = user;
			log.debug("LDAP server is OpenLDAP");
			log.debug("LDAP search base: " + ldap_search_scope);
			HashMap<String, String> uidCnDictionary = lAuth.getUidCnHashMap(
					ldap_search_scope, ldap_search_filter, ldap_fieldname_user_principal);
			if (uidCnDictionary.get(user) != null) {
				ldapUserDN = uidCnDictionary.get(user) + ","
						+ ldap_search_scope;
				log.debug("Authentication with DN: " + ldapUserDN);
			}
			try {
				if (!lAuth.authenticateUser(ldapUserDN, passwd)) {
					log.error(ldapUserDN + " not authenticated.");
					return new Long(-11);
				}
			} catch (Exception e) {
				log.error("Error on LdapAuth : " + e.getMessage());
				return null;
			}
		} else {
			try {
				if (!lAuth.authenticateUser(user, passwd))
					return new Long(-11);
			} catch (Exception e) {
				log.error("Error on LdapAuth : " + e.getMessage());
				return null;
			}
		}

		// check if user already exists

		User u = null;

		try {
			u = userManager.getUserByLogin(user);

		} catch (Exception e) {
			log.error("Error retrieving Userdata : " + e.getMessage());
		}
		
		// Attributes to retrieve from ldap to either create or update the user
		List<String> attributes = new ArrayList<String>();
		attributes.add(ldap_user_attr_lastname); // Lastname
		attributes.add(ldap_user_attr_firstname); // Firstname
		attributes.add(ldap_user_attr_mail);// mail
		attributes.add(ldap_user_attr_street); // Street
		attributes.add(ldap_user_attr_additional_name); // Additional name
		attributes.add(ldap_user_attr_fax); // Fax
		attributes.add(ldap_user_attr_zip); // ZIP
		attributes.add(ldap_user_attr_country); // Country
		attributes.add(ldap_user_attr_town); // Town
		attributes.add(ldap_user_attr_phone); // Phone
		attributes.add(ldap_user_attr_timezone); // timezone
		attributes.add(ldap_user_picture_uri); //picture uri
		
		HashMap<String, String> ldapAttrs = new HashMap<String, String>();
		ldapAttrs.put("lastnameAttr", ldap_user_attr_lastname);
		ldapAttrs.put("firstnameAttr", ldap_user_attr_firstname);
		ldapAttrs.put("mailAttr", ldap_user_attr_mail);
		ldapAttrs.put("streetAttr", ldap_user_attr_street);
		ldapAttrs.put("additionalNameAttr", ldap_user_attr_additional_name);
		ldapAttrs.put("faxAttr", ldap_user_attr_fax);
		ldapAttrs.put("zipAttr", ldap_user_attr_zip);
		ldapAttrs.put("countryAttr", ldap_user_attr_country);
		ldapAttrs.put("townAttr", ldap_user_attr_town);
		ldapAttrs.put("phoneAttr", ldap_user_attr_phone);
		ldapAttrs.put("timezoneAttr", ldap_user_attr_timezone);
		ldapAttrs.put("pictureUri", ldap_user_picture_uri);

		Vector<HashMap<String, String>> result = lAuth.getData(
				ldap_search_scope, ldap_search_filter, attributes);

		if (result == null || result.size() < 1) {
			log.error("Error on Ldap request - no result for user " + user);
			return new Long(-10);
		}
		
		if (result.size() > 1) {
			log.error("Error on Ldap request - more than one result for user "
					+ user);
			return null;
		}
		
		HashMap<String, String> userData = result.get(0);


		// User not existant in local database -> take over data for referential
		// integrity
		if (u == null) {
			log.debug("user doesnt exist local -> create new");

			try {
				// Create User with LdapData
				Long userid;
				if (ldap_sync_passwd_to_om) {
					Random r = new Random();
					String token = Long.toString(Math.abs(r.nextLong()), 36);
					log.debug("Synching Ldap user to OM DB with RANDOM password: "
							+ token);
					userid = createUserFromLdapData(userData, token, user,
							ldapAttrs);
				} else {
					log.debug("Synching Ldap user to OM DB with password");
					userid = createUserFromLdapData(userData, passwd, user,
							ldapAttrs);
				}
				log.debug("New User ID : " + userid);

				// If invoked via SOAP this is NULL
				if (currentClient != null) {
					currentClient.setUser_id(userid);
					SessionVariablesUtil.setUserId(client, userid);
				}

				// Update Session
				Boolean bool = sessiondataDao.updateUser(SID, userid);

				if (bool == null) {
					// Exception
					log.error("Error on Updating Session");
					return new Long(-1);
				} else if (!bool) {
					// invalid Session-Object
					log.error("Invalid Session Object");
					return new Long(-35);
				}

				// Return UserObject
				User u2 = userManager.getUserById(userid);

				if (u2 == null)
					return new Long(-1);

				u2.setExternalUserType(EXTERNAL_USER_TYPE_LDAP); // TIBO

				// initialize lazy collection
				userManager.refreshUserObject(u2);

				log.debug("getUserbyId : " + userid + " : " + u2.getLogin());

				return u2;

			} catch (Exception e) {
				log.error("Error on Working Userdata : ", e);
				return new Long(-1);
			}
		} else {
			// User exists, just update necessary values
			log.debug("User already exists -> Update of current passwd");

			// If invoked via SOAP this is NULL
			if (currentClient != null) {
				currentClient.setUser_id(u.getUser_id());
			}

			// Update Session
			Boolean bool = sessiondataDao.updateUser(SID, u.getUser_id());

			if (bool == null) {
				// Exception
				log.error("Error on Updating Session");
				return new Long(-1);
			} else if (!bool) {
				// invalid Session-Object
				log.error("Invalid Session Object");
				return new Long(-35);
			}

			try {
				// Update password (could have changed in LDAP)
				if (ldap_sync_passwd_to_om) {
					u.updatePassword(cryptManager, configurationDao, passwd);
				}
				
				//update all other attributes in case ldap provides some and the parameter is configured
				updateUserFromLdap(userData, ldapAttrs, u);

				usersDao.update(u, null);
			} catch (Exception e) {
				log.error("Error updating user : " + e.getMessage());
				return new Long(-1);
			}

			return u;

		}
	}
	
	// ----------------------------------------------------------------------------------------

	/**
	 * Creation on User with LDAP - Data AutoCreation of Country if does not exist
	 * Added to default organization
	 */
	// ----------------------------------------------------------------------------------------
	private Long createUserFromLdapData(HashMap<String, String> userdata,
			String passwd, String login, HashMap<String, String> ldapAttrs)
			throws Exception {
		log.debug("LdapLoginmanagement.createUserFromLdapData");

		// Retrieve Data from LDAP - Data

		String lastname = "lastname";
		if (userdata.containsKey(ldapAttrs.get("lastnameAttr"))
				&& userdata.get(ldapAttrs.get("lastnameAttr")) != null)
			lastname = userdata.get(ldapAttrs.get("lastnameAttr"));

		String firstname = "firstname";
		if (userdata.containsKey(ldapAttrs.get("firstnameAttr"))
				&& userdata.get(ldapAttrs.get("firstnameAttr")) != null)
			firstname = userdata.get(ldapAttrs.get("firstnameAttr"));

		String email = "email";
		if (userdata.containsKey(ldapAttrs.get("mailAttr"))
				&& userdata.get(ldapAttrs.get("mailAttr")) != null)
			email = userdata.get(ldapAttrs.get("mailAttr"));

		String street = "street";
		if (userdata.containsKey(ldapAttrs.get("streetAttr"))
				&& userdata.get(ldapAttrs.get("streetAttr")) != null)
			street = userdata.get(ldapAttrs.get("streetAttr"));

		String additionalname = "additionalname";
		if (userdata.containsKey(ldapAttrs.get("additionalNameAttr"))
				&& userdata.get(ldapAttrs.get("additionalNameAttr")) != null)
			additionalname = userdata.get(ldapAttrs.get("additionalNameAttr"));

		String fax = "fax";
		if (userdata.containsKey(ldapAttrs.get("faxAttr"))
				&& userdata.get(ldapAttrs.get("faxAttr")) != null)
			fax = userdata.get(ldapAttrs.get("faxAttr"));

		String zip = "zip";
		if (userdata.containsKey(ldapAttrs.get("zipAttr"))
				&& userdata.get(ldapAttrs.get("zipAttr")) != null)
			zip = userdata.get(ldapAttrs.get("zipAttr"));

		long state_id = -1;
		String state = null;
		if (userdata.containsKey(ldapAttrs.get("countryAttr"))
				&& userdata.get(ldapAttrs.get("countryAttr")) != null)
			state = userdata.get(ldapAttrs.get("countryAttr"));
		
		if (state != null) {
			// Lookup for states
			State oneState = statemanagement.getStateByName(state);
			if (oneState != null) {
				state_id = oneState.getState_id();
			}
		}

		// Create Country
		if (state_id < 0) {
			Long id = statemanagement.addState(state);
			if (id != null)
				state_id = id;

		}
		

		String phone = "phone";
		if (userdata.containsKey(ldapAttrs.get("phoneAttr"))
				&& userdata.get(ldapAttrs.get("phoneAttr")) != null)
			phone = userdata.get(ldapAttrs.get("phoneAttr"));
		
		String pictureUri = "pictureUri";
		if (userdata.containsKey(ldapAttrs.get("pictureUri"))
				&& userdata.get(ldapAttrs.get("pictureUri")) != null)
			pictureUri = userdata.get(ldapAttrs.get("pictureUri"));

		String jName_timeZone = "";
		if (userdata.containsKey(ldapAttrs.get("timezoneAttr"))
				&& userdata.get(ldapAttrs.get("timezoneAttr")) != null)
			jName_timeZone = userdata.get(ldapAttrs.get("timezoneAttr"));
		
		if (omTimeZoneDaoImpl.getOmTimeZone(jName_timeZone) == null) {
			jName_timeZone = configurationDao.getConfValue(
					"default.timezone", String.class, "Europe/Berlin");
		}

		String town = "town";
		if (userdata.containsKey(ldapAttrs.get("townAttr"))
				&& userdata.get(ldapAttrs.get("townAttr")) != null)
			town = userdata.get(ldapAttrs.get("townAttr"));

		Long newUserId = null;

		try {

			// Check if LDAP Users get a SIP Account Issue 1099

			newUserId = userManager.registerUserInit(
					2,// user_level
					1,// level_id
					1,// available
					1,// status
					login,// loginname
					passwd,// passwd
					lastname, firstname, email, 
					new java.util.Date(), //age
					street,
					additionalname, fax, zip, state_id, town, 
					configurationDao.getConfValue("default_lang_id",
							Long.class, "0"), // language_id
					false, // sendWelcomeMessage
					Arrays.asList(configurationDao.getConfValue(
							"default_domain_id", Long.class, null)), // organozation
																		// Ids
					phone, 
					false,
					"",// BaseURL is empty as we do not send an Email here
					false,// send verification code
					jName_timeZone, 
					false, // forceTimeZoneCheck
					"", //userOffers
					"", //userSearchs
					false, //showContactData
					true //showContactDataToContacts
					);
			
			User user = usersDao.get(newUserId);
			user.setPictureuri(pictureUri);
			usersDao.update(user, null);

		} catch (Exception e) {
			log.error("Error creating user : " + e.getMessage());
		}

		if (newUserId == -1) {
			log.error("Error occured creating user");
		} else if (newUserId == -15) {
			log.error("Error creating user : username already exists!");
		} else if (newUserId == -17) {
			log.error("Error creating user : email already exists!");
		} else if (newUserId == -3) {
			log.error("Error creating user : missing values");
		} else {
			log.debug("User Created!");
		}

		return newUserId;
	}

	public void updateUserFromLdap(HashMap<String, String> userdata,
				HashMap<String, String> ldapAttrs, User user)
			throws Exception {
		log.debug("LdapLoginmanagement.createUserFromLdapData");
	
		// Retrieve Data from LDAP - Data
	
		if (userdata.containsKey(ldapAttrs.get("lastnameAttr"))
				&& userdata.get(ldapAttrs.get("lastnameAttr")) != null)
			user.setLastname(userdata.get(ldapAttrs.get("lastnameAttr")));
	
		if (userdata.containsKey(ldapAttrs.get("firstnameAttr"))
				&& userdata.get(ldapAttrs.get("firstnameAttr")) != null)
			user.setFirstname(userdata.get(ldapAttrs.get("firstnameAttr")));
	
		if (userdata.containsKey(ldapAttrs.get("mailAttr"))
				&& userdata.get(ldapAttrs.get("mailAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setEmail(userdata.get(ldapAttrs.get("mailAttr")));
		}
			
		if (userdata.containsKey(ldapAttrs.get("streetAttr"))
				&& userdata.get(ldapAttrs.get("streetAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setStreet(userdata.get(ldapAttrs.get("streetAttr")));
		}
		
		if (userdata.containsKey(ldapAttrs.get("additionalNameAttr"))
				&& userdata.get(ldapAttrs.get("additionalNameAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setAdditionalname(userdata.get(ldapAttrs.get("additionalNameAttr")));
		}
	
		if (userdata.containsKey(ldapAttrs.get("faxAttr"))
				&& userdata.get(ldapAttrs.get("faxAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setFax(userdata.get(ldapAttrs.get("faxAttr")));
		}
	
		if (userdata.containsKey(ldapAttrs.get("zipAttr"))
				&& userdata.get(ldapAttrs.get("zipAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setZip(userdata.get(ldapAttrs.get("zipAttr")));
		}
	
		long state_id = -1;
		String state = null;
		if (userdata.containsKey(ldapAttrs.get("countryAttr"))
				&& userdata.get(ldapAttrs.get("countryAttr")) != null)
			state = userdata.get(ldapAttrs.get("countryAttr"));
		
		if (state != null) {
			// Lookup for states
			State oneState = statemanagement.getStateByName(state);
			if (oneState != null) {
				state_id = oneState.getState_id();
			}
		}
		// Create Country if not found
		if (state_id < 0) {
			Long id = statemanagement.addState(state);
			if (id != null)
				state_id = id;

		}
		if (user.getAdresses() != null && state_id > 0) {
			user.getAdresses().setStates(statemanagement.getStateById(state_id));
		}
		
		if (userdata.containsKey(ldapAttrs.get("townAttr"))
				&& userdata.get(ldapAttrs.get("townAttr")) != null) {
			if (user.getAdresses() != null )
				user.getAdresses().setTown(userdata.get(ldapAttrs.get("townAttr")));
		}

		if (userdata.containsKey(ldapAttrs.get("phoneAttr"))
				&& userdata.get(ldapAttrs.get("phoneAttr")) != null) {
			if (user.getAdresses() != null ) 
				user.getAdresses().setPhone(userdata.get(ldapAttrs.get("phoneAttr")));
		}
		
		if (userdata.containsKey(ldapAttrs.get("pictureUri"))
				&& userdata.get(ldapAttrs.get("pictureUri")) != null) {
			user.setPictureuri(userdata.get(ldapAttrs.get("pictureUri")));
		}
	
		String jName_timeZone = "";
		if (userdata.containsKey(ldapAttrs.get("timezoneAttr"))
				&& userdata.get(ldapAttrs.get("timezoneAttr")) != null)
			jName_timeZone = userdata.get(ldapAttrs.get("timezoneAttr"));
		
		//only change the timezone if there can be found some in the OpenMeetings database
		OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jName_timeZone);
		if (omTimeZone != null) {
			user.setOmTimeZone(omTimeZone);
		}
	
	}
	
}
