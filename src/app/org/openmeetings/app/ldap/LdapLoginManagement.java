package org.openmeetings.app.ldap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.LdapConfigDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.ldap.config.ConfigReader;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.LdapConfig;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
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
			LdapLoginManagement.class, ScopeApplicationAdapter.webAppRootKey);

	private static LdapLoginManagement instance = null;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Statemanagement statemanagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private LdapConfigDaoImpl ldapConfigDao;

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

	private LdapLoginManagement() {
		// do not use -> singleton
	}

	/** Getting instance */
	public static synchronized LdapLoginManagement getInstance() {
		if (instance == null) {
			instance = new LdapLoginManagement();
		}
		return instance;
	}

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

		LdapConfig ldapConfig = ldapConfigDao.getLdapConfigById(ldapConfigId);

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

		// String path = configVal.getConf_value().trim();

		String path = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "conf" + File.separatorChar + ldapConfigfileName;

		return readConfig(path);

	}

	// ----------------------------------------------------------------------------------------

	/**
	 * Reading Ldap Config via ConfigReader
	 */
	// ----------------------------------------------------------------------------------------
	private HashMap<String, String> readConfig(String configPath)
			throws Exception {
		log.debug("LdapLoginmanagement.readConfig : " + configPath);

		File configFile = new File(configPath);

		if (!configFile.isFile())
			return null;

		ConfigReader reader = new ConfigReader();
		reader.readConfig(configPath);

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
			RoomClient currentClient, String SID, String domain) {
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
		String ldap_sync_passwd_to_om = configData
				.get(CONFIGKEY_LDAP_SYNC_PASSWD_OM);

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

		if (ldap_user_attr_lastname == null) {
			ldap_user_attr_lastname = LDAP_KEY_LASTNAME;
		}
		if (ldap_user_attr_firstname == null) {
			ldap_user_attr_firstname = LDAP_KEY_LASTNAME;
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
					ldap_search_scope, ldap_search_filter);
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

		// Pr�fen, ob user bereits vorhanden ist

		Users u = null;

		try {
			u = userManagement.getUserByLogin(user);

		} catch (Exception e) {
			log.error("Error retrieving Userdata : " + e.getMessage());
		}

		// User not existant in local database -> take over data for referential
		// integrity
		if (u == null) {
			log.debug("user doesnt exist local -> create new");

			// Attributes to retrieve from ldap
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

			try {
				// Create User with LdapData
				Long userid;
				if (ldap_sync_passwd_to_om != null
						&& ldap_sync_passwd_to_om.equals("no")) {
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
				}

				// Update Session
				Boolean bool = sessionManagement.updateUser(SID, userid);

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
				Users u2 = userManagement.getUserById(userid);

				if (u2 == null)
					return new Long(-1);

				u2.setExternalUserType(EXTERNAL_USER_TYPE_LDAP); // TIBO

				// initialize lazy collection
				userManagement.refreshUserObject(u2);

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
			Boolean bool = sessionManagement.updateUser(SID, u.getUser_id());

			if (bool == null) {
				// Exception
				log.error("Error on Updating Session");
				return new Long(-1);
			} else if (!bool) {
				// invalid Session-Object
				log.error("Invalid Session Object");
				return new Long(-35);
			}

			// Update password (could have changed in LDAP)
			if (ldap_sync_passwd_to_om == null
					|| !ldap_sync_passwd_to_om.equals("no")) {
				u.setPassword(passwd);
			}
			try {
				userManagement.updateUserObject(u, true);
			} catch (Exception e) {
				log.error("Error updating user : " + e.getMessage());
				return new Long(-1);
			}

			return u;

		}
	}

	// ----------------------------------------------------------------------------------------

	/**
	 * Creation on User with LDAP - Data AutoCreation of Country if not existant
	 * Added to Default Organisation
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

		String state = null;
		if (userdata.containsKey(ldapAttrs.get("countryAttr"))
				&& userdata.get(ldapAttrs.get("countryAttr")) != null)
			state = userdata.get(ldapAttrs.get("countryAttr"));

		String phone = "phone";
		if (userdata.containsKey(ldapAttrs.get("phoneAttr"))
				&& userdata.get(ldapAttrs.get("phoneAttr")) != null)
			phone = userdata.get(ldapAttrs.get("phoneAttr"));

		long state_id = -1;

		if (state != null) {
			// Lookup for states
			List<States> states = statemanagement.getStates();

			for (int i = 0; i < states.size(); i++) {
				States oneState = states.get(i);

				if (oneState.getName().equals(state)) {
					state_id = oneState.getState_id();
					break;
				}
			}

		}

		// Create Country
		if (state_id < 0) {
			Long id = statemanagement.addState(state);

			if (id != null)
				state_id = id;

		}

		String town = "town";
		if (userdata.containsKey(ldapAttrs.get("townAttr"))
				&& userdata.get(ldapAttrs.get("townAttr")) != null)
			town = userdata.get(ldapAttrs.get("townAttr"));

		Long newUserId = null;

		try {

			Configuration conf = cfgManagement.getConfKey(3L,
					"default.timezone");
			String jName_timeZone = "";

			if (conf != null) {
				jName_timeZone = conf.getConf_value();
			}

			// CHeck if LDAP Users get a SIP Account Issue 1099

			newUserId = userManagement.registerUserInit(
					2,// user_level
					1,// level_id
					1,// available
					1,// status
					login,// loginname
					passwd,// passwd
					lastname, firstname, email, new java.util.Date(), street,
					additionalname, fax, zip, state_id, town, 0, false, // sendWelcomeMessage
					null, phone, "",// BaseURL is empty as we do not send an
									// Email here
					false,// send verification code
					"", "", "",// sip_user, sip_pass, sip_auth
					true, // generate SIP Data if the config is enabled
					jName_timeZone, false, "", "", false, true);

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

			// Set him to Default Organisation
			long organisation_id = Long.valueOf(
					cfgManagement.getConfKey(3, "default_domain_id")
							.getConf_value()).longValue();
			log.debug("Adding user '" + newUserId + "' to organization '"
					+ organisation_id + "'");
			organisationmanagement.addUserToOrganisation(newUserId,
					organisation_id, newUserId, "");
		}

		return newUserId;
	}
	// ----------------------------------------------------------------------------------------

}
