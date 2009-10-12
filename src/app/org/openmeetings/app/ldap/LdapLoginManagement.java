package org.openmeetings.app.ldap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.adresses.States;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.ldap.config.ConfigReader;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Management of optional LDAP Login
 * @author o.becherer
 *
 */
public class LdapLoginManagement {
	
	private static final Logger log = Logger.getLogger(Usermanagement.class);

	private static LdapLoginManagement instance = null;
	
	// ConfigConstants
	public static final String CONFIGKEY_LDAP_URL = "ldap_conn_url";
	public static final String CONFIGKEY_LDAP_ADMIN_DN = "ldap_admin_dn";
	public static final String CONFIGKEY_LDAP_ADMIN_PASSWD = "ldap_passwd";
	public static final String CONFIGKEY_LDAP_SEARCH_SCOPE = "ldap_search_base";
	public static final String CONFIGKEY_LDAP_AUTH_TYPE = "ldap_auth_type";
	
	public static final String CONFIGKEY_LDAP_FIELDNAME_USER_PRINCIPAL = "field_user_principal";
	
	
	// LDAP KEYS
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
	 * @param authType
	 */
	 //-------------------------------------------------------------------------------------------------------
	 public static boolean isValidAuthType(String authType){
	 		log.debug("isValidAuthType");
	 		
	 	    if (authType != null){
	 			if (!authType.isEmpty() ){
 				if (authType.equalsIgnoreCase(LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE))
						return true;
	 			if (authType.equalsIgnoreCase(LdapAuthBase.LDAP_AUTH_TYPE_NONE))
	 				return true;
	 		}
	 	}
	 	    
	 	   return false;
	 
	 }
	 //-------------------------------------------------------------------------------------------------------
	 	
	
	/**
	 * Ldap Login configured?
	 */
	//----------------------------------------------------------------------------------------
	public boolean isLdapConfigured(){
		log.debug("LdapLoginmanagement.isLdapConfigured");
		
		Configuration configVal = Configurationmanagement.getInstance().getConfKey(3, "ldap_config_path");
		
		if(configVal == null){
			log.error("Error retrieving ConfigKey ldap_config_path!");
			return false;
		}
		
		File tester = new File(configVal.getConf_value());
		
		if(!tester.isFile()){
			log.error("ConfigVal ldap_config_path not describes a valid File : " + configVal);
			return false;
		}
		
		return true;
		
	}
	//----------------------------------------------------------------------------------------
	
	/**
	 * Retrieving LdapData from Config
	 */
	//----------------------------------------------------------------------------------------
	public HashMap<String, String> getLdapConfigData() throws Exception{
		log.debug("LdapLoginmanagement.getLdapConfigData");
		
		// Retrieving Path to Config
		Configuration configVal = Configurationmanagement.getInstance().getConfKey(3, "ldap_config_path");
		
		if(configVal == null){
			log.error("Error retrieving ConfigKey ldap_config_path!");
			return null;
		}
		
		String path = configVal.getConf_value().trim();
		
		return readConfig(path);
		
	}
	//----------------------------------------------------------------------------------------
	
	
	/**
	 * Reading Ldap Config via ConfigReader
	 */
	//----------------------------------------------------------------------------------------
	private HashMap<String, String> readConfig(String configPath) throws Exception{
		log.debug("LdapLoginmanagement.readConfig : " + configPath);
		
		File configFile = new File(configPath);
		
		if(!configFile.isFile())
			return null;
		
		ConfigReader reader = new ConfigReader();
		reader.readConfig(configPath);
		
		return reader.getConfigMap();
		
	}
	//----------------------------------------------------------------------------------------
	
	
	/**
	 * Ldap Login
	 * 
	 * Connection Data is retrieved from ConfigurationFile
	 * 
	 */
	//----------------------------------------------------------------------------------------
	public Object doLdapLogin(String user, String passwd, RoomClient currentClient, String SID) {
		log.debug("LdapLoginmanagement.doLdapLogin");
		
		// Retrieve Configuration Data
		HashMap<String, String> configData;
		
		try{
			configData= getLdapConfigData();
		}catch(Exception e){
			log.error("Error on LdapAuth : " + e.getMessage());
			return null;
		}
		
		if(configData == null || configData.size() < 1){
			log.error("Error on LdapLogin : Configurationdata couldnt be retrieved!");
			return null;
		}
		
		// Connection URL
		String ldap_url = configData.get(CONFIGKEY_LDAP_URL);
		
		// Username for LDAP SERVER himself
		String ldap_admin_dn = configData.get(CONFIGKEY_LDAP_ADMIN_DN);
		
		// Password for LDAP SERVER himself
		String ldap_passwd = configData.get(CONFIGKEY_LDAP_ADMIN_PASSWD);
				
		// SearchScope for retrievment of userdata
		String ldap_search_scope = configData.get(CONFIGKEY_LDAP_SEARCH_SCOPE);
		
		// FieldName for Users's Prinicipal Name
		String ldap_fieldname_user_principal = configData.get(CONFIGKEY_LDAP_FIELDNAME_USER_PRINCIPAL);
		
		// Auth Type
		String ldap_auth_type = configData.get(CONFIGKEY_LDAP_AUTH_TYPE);
		
		if(ldap_auth_type == null)
			ldap_auth_type = "";
		
		if ( ! isValidAuthType(ldap_auth_type) ){
			log.error("ConfigKey in Ldap Config contains invalid auth type : '" + ldap_auth_type + "' -> Defaulting to " + LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE);	
			ldap_auth_type = LdapAuthBase.LDAP_AUTH_TYPE_SIMPLE;
		}
		
		// Filter for Search of UserData
		String ldap_search_filter = "(" + ldap_fieldname_user_principal + "=" + user + ")";
		
		log.debug("Searching userdata with LDAP Search Filter :" + ldap_search_filter);
		
		// replace : -> in config = are replaced by : to be able to build valid key=value pairs
		ldap_search_scope = ldap_search_scope.replaceAll(":", "=");
		ldap_admin_dn = ldap_admin_dn.replaceAll(":", "=");
		
		LdapAuthBase lAuth = new LdapAuthBase(ldap_url, ldap_admin_dn, ldap_passwd, ldap_auth_type);
		
		try{
			if(!lAuth.authenticateUser(user, passwd))
				return new Long(-11);
		}catch(Exception e ){
			log.error("Error on LdapAuth : " + e.getMessage());
			return null;
		}
		
		// Prüfen, ob user bereits vorhanden ist
		
		Users u = null;
		
		try{
			u= Usermanagement.getInstance().getUserByLogin(user);
			
		}catch(Exception e){
			log.error("Error retrieving Userdata : " + e.getMessage());
		}
		
		// User not existant in local database -> take over data for referential integrity
		if(u == null){
			log.debug("user doenst exist local -> create new");
			
			// Attributes to retrieve from ldap
			List<String> attributes = new ArrayList<String>();
			attributes.add(LDAP_KEY_LASTNAME); // Lastname
			attributes.add(LDAP_KEY_FIRSTNAME); //Firstname
			attributes.add(LDAP_KEY_MAIL);// mail
			attributes.add(LDAP_KEY_STREET); //Strasse
			attributes.add(LDAP_KEY_ADDITIONAL_NAME); // Additional name
			attributes.add(LDAP_KEY_FAX); //Fax
			attributes.add(LDAP_KEY_ZIP); // ZIP
			attributes.add(LDAP_KEY_COUNTRY); // Country
			attributes.add(LDAP_KEY_TOWN); // Town
			attributes.add(LDAP_KEY_PHONE); // Phone
			
			
			Vector<HashMap<String, String>> result = lAuth.getData(ldap_search_scope, ldap_search_filter, attributes);
			
			if(result == null || result.size() < 1){
				log.error("Error on Ldap request - no result for user " + user);
				return new Long(-10);
			}
			
			if(result.size() > 1){
				log.error("Error on Ldap request - more than one result for user " + user);
				return null;
			}
			
			HashMap<String, String> userData = result.get(0);
			
			try{
				//Create User with LdapData
				Long userid = createUserFromLdapData(userData, passwd, user);
				log.debug("New User ID : " + userid);
				
				
				//If invoked via SOAP this is NULL
				if (currentClient!=null){
					currentClient.setUser_id(userid);
				}
				
				// Update Session
				Boolean bool = Sessionmanagement.getInstance().updateUser(SID, userid);
				
				if (bool==null){
					//Exception
					log.error("Error on Updating Session");
					return new Long(-1);
				} else if (!bool) {
					//invalid Session-Object
					log.error("Invalid Session Object");
					return new Long(-35);
				}
				
				// Return UserObject
				Users u2 =  Usermanagement.getInstance().getUserById(userid);
				
				if(u2 == null)
					return userid;
				
				log.debug("getUserbyId : " + userid + " : " + u2.getLogin());
				
				return u2;
				
			}catch(Exception e){
				log.error("Error on Working Userdata : " + e.getMessage());
				return null;
			}
		}
		else{
			// User exists, just update necessary values
			log.debug("User already exists -> Update of current passwd");
			
			//If invoked via SOAP this is NULL
			if (currentClient!=null){
				currentClient.setUser_id(u.getUser_id());
			}
			
			// Update Session
			Boolean bool = Sessionmanagement.getInstance().updateUser(SID, u.getUser_id());
			
			if (bool==null){
				//Exception
				log.error("Error on Updating Session");
				return new Long(-1);
			} else if (!bool) {
				//invalid Session-Object
				log.error("Invalid Session Object");
				return new Long(-35);
			}
			
			// Update password (could have changed in LDAP)
			
			u.setPassword(passwd);
			try{
				Usermanagement.getInstance().updateUserObject(u,true );
			}catch(Exception e){
				log.error("Error updating user : " + e.getMessage());
				return null;
			}
			
			return u;
			
		}
	}
	//----------------------------------------------------------------------------------------
	
	
	/**
	 * Creation on User with LDAP - Data
	 * AutoCreation of Country if not existant
	 * Added to Default Organisation
	 */
	//----------------------------------------------------------------------------------------
	private Long createUserFromLdapData(HashMap<String, String> userdata, String passwd, String login) throws Exception{
		log.debug("LdapLoginmanagement.createUserFromLdapData");
		
		// Retrieve Data from LDAP - Data
		
		String lastname = "lastname";
		if(userdata.containsKey(LDAP_KEY_LASTNAME) && userdata.get(LDAP_KEY_LASTNAME) != null)
			lastname = userdata.get(LDAP_KEY_LASTNAME);
		
		String firstname = "firstname";
		if(userdata.containsKey(LDAP_KEY_FIRSTNAME) && userdata.get(LDAP_KEY_FIRSTNAME) != null)
			firstname = userdata.get(LDAP_KEY_FIRSTNAME);
		
		String email = "email";
		if(userdata.containsKey(LDAP_KEY_MAIL) && userdata.get(LDAP_KEY_MAIL) != null)
			email = userdata.get(LDAP_KEY_MAIL);
		
		String street = "street";
		if(userdata.containsKey(LDAP_KEY_STREET) && userdata.get(LDAP_KEY_STREET) != null)
			street = userdata.get(LDAP_KEY_STREET);
		
		String additionalname = "additionalname";
		if(userdata.containsKey(LDAP_KEY_ADDITIONAL_NAME) && userdata.get(LDAP_KEY_ADDITIONAL_NAME) != null)
			additionalname = userdata.get(LDAP_KEY_ADDITIONAL_NAME);
		
		String fax = "fax";
		if(userdata.containsKey(LDAP_KEY_FAX) && userdata.get(LDAP_KEY_FAX) != null)
			fax = userdata.get(LDAP_KEY_FAX);
		
		String zip = "zip";
		if(userdata.containsKey(LDAP_KEY_ZIP) && userdata.get(LDAP_KEY_ZIP) != null)
			zip = userdata.get(LDAP_KEY_ZIP);
		
		String state = null;
		if(userdata.containsKey(LDAP_KEY_COUNTRY) && userdata.get(LDAP_KEY_COUNTRY) != null)
			state = userdata.get(LDAP_KEY_COUNTRY);
		
		String phone = "phone";
		if(userdata.containsKey(LDAP_KEY_PHONE) && userdata.get(LDAP_KEY_PHONE) != null)
			phone = userdata.get(LDAP_KEY_PHONE);
		
		long state_id = -1;
		
		if(state != null){
			// Lookup for states
			List<States> states = Statemanagement.getInstance().getStates();
			
			for(int i = 0; i < states.size(); i++){
				States oneState = states.get(i);
			
				if(oneState.getName().equals(state)){
					state_id = oneState.getState_id();
					break;
				}
			}
			
		}
		
		// Create Country
		if(state_id < 0){
			Long id = Statemanagement.getInstance().addState(state);
			
			if(id != null)
				state_id = id;
				
		}
		
		String town = "town";
		if(userdata.containsKey(LDAP_KEY_TOWN) && userdata.get(LDAP_KEY_TOWN) != null)
			town = userdata.get(LDAP_KEY_TOWN);
		
		
		Long newUserId = null;
		
		try{
			newUserId= Usermanagement.getInstance().registerUserInit(
				2,//user_level
				1,//level_id
				1,//available
				1,// status
				login,// loginname
				passwd,//passwd
				lastname,
				firstname,
				email,
				new java.util.Date(),
				street,
				additionalname,
				fax,
				zip,
				state_id,
				town,
				0,
				false, //sendWelcomeMessage
				null,
				phone,
				"",//BaseURL is empty as we do not send an Email here
				false);//send verification code
		}catch(Exception e){
			log.error("Error creating user : " + e.getMessage());
		}
		
		
		if(newUserId == -1){
			log.error("Error occured creating user");
		}
		else if(newUserId == -15){
			log.error("Error creating user : username already exists!");
		}
		else if( newUserId == -17){
			log.error("Error creating user : email already exists!");
		}
		else if( newUserId == -3){
			log.error("Error creating user : missing values");
		}
		else{
			log.debug("User Created!");
			
			// Set him to Default Organisation
			long organisation_id = Long.valueOf(Configurationmanagement.getInstance().getConfKey(3,"default_domain_id").getConf_value()).longValue();
			Organisationmanagement.getInstance().addUserToOrganisation(newUserId,organisation_id, newUserId, "");
		}
		
		return newUserId;
	}
	//----------------------------------------------------------------------------------------
	
}
