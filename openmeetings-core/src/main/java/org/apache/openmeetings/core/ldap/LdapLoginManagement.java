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
package org.apache.openmeetings.core.ldap;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.util.string.Strings;
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
	private static final Logger log = Red5LoggerFactory.getLogger(LdapLoginManagement.class, webAppRootKey);
	// ConfigConstants
	private static final String CONFIGKEY_LDAP_HOST = "ldap_conn_host";
	private static final String CONFIGKEY_LDAP_PORT = "ldap_conn_port";
	private static final String CONFIGKEY_LDAP_SECURE = "ldap_conn_secure";
	private static final String CONFIGKEY_LDAP_ADMIN_DN = "ldap_admin_dn";
	private static final String CONFIGKEY_LDAP_ADMIN_PASSWD = "ldap_passwd";
	private static final String CONFIGKEY_LDAP_AUTH_TYPE = "ldap_auth_type";
	private static final String CONFIGKEY_LDAP_PROV_TYPE = "ldap_provisionning";

	private static final String CONFIGKEY_LDAP_SYNC_PASSWD_OM = "ldap_sync_password_to_om"; // 'true' or 'false'
	private static final String CONFIGKEY_LDAP_USE_LOWER_CASE = "ldap_use_lower_case";
	private static final String CONFIGKEY_LDAP_TIMEZONE_NAME = "ldap_user_timezone";
	private static final String CONFIGKEY_LDAP_SEARCH_BASE = "ldap_search_base";
	private static final String CONFIGKEY_LDAP_SEARCH_QUERY = "ldap_search_query";
	private static final String CONFIGKEY_LDAP_SEARCH_SCOPE = "ldap_search_scope";
	private static final String CONFIGKEY_LDAP_USERDN_FORMAT = "ldap_userdn_format";
	private static final String CONFIGKEY_LDAP_USE_ADMIN_4ATTRS = "ldap_use_admin_to_get_attrs";
	
	// LDAP custom attribute mapping keys
	private static final String CONFIGKEY_LDAP_KEY_LASTNAME = "ldap_user_attr_lastname";
	private static final String CONFIGKEY_LDAP_KEY_FIRSTNAME = "ldap_user_attr_firstname";
	private static final String CONFIGKEY_LDAP_KEY_MAIL = "ldap_user_attr_mail";
	private static final String CONFIGKEY_LDAP_KEY_STREET = "ldap_user_attr_street";
	private static final String CONFIGKEY_LDAP_KEY_ADDITIONAL_NAME = "ldap_user_attr_additionalname";
	private static final String CONFIGKEY_LDAP_KEY_FAX = "ldap_user_attr_fax";
	private static final String CONFIGKEY_LDAP_KEY_ZIP = "ldap_user_attr_zip";
	private static final String CONFIGKEY_LDAP_KEY_COUNTRY = "ldap_user_attr_country";
	private static final String CONFIGKEY_LDAP_KEY_TOWN = "ldap_user_attr_town";
	private static final String CONFIGKEY_LDAP_KEY_PHONE = "ldap_user_attr_phone";
	private static final String CONFIGKEY_LDAP_PICTURE_URI = "ldap_user_picture_uri";

	// LDAP default attributes mapping
	private static final String LDAP_KEY_LASTNAME = "sn";
	private static final String LDAP_KEY_FIRSTNAME = "givenName";
	private static final String LDAP_KEY_MAIL = "mail";
	private static final String LDAP_KEY_STREET = "streetAddress";
	private static final String LDAP_KEY_ADDITIONAL_NAME = "description";
	private static final String LDAP_KEY_FAX = "facsimileTelephoneNumber";
	private static final String LDAP_KEY_ZIP = "postalCode";
	private static final String LDAP_KEY_COUNTRY = "co";
	private static final String LDAP_KEY_TOWN = "l";
	private static final String LDAP_KEY_PHONE = "telephoneNumber";
	private static final String LDAP_KEY_TIMEZONE = "timezone";
	private static final String LDAP_KEY_PICTURE_URI = "pictureUri";

	public enum AuthType {
		NONE
		, SEARCHANDBIND
		, SIMPLEBIND
	}
	
	public enum Provisionning {
		NONE
		, AUTOUPDATE
		, AUTOCREATE
	}
	
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private StateDao stateDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private TimezoneUtil timezoneUtil;

	private Dn getUserDn(Properties config, String user) throws LdapInvalidDnException {
		return new Dn(String.format(config.getProperty(CONFIGKEY_LDAP_USERDN_FORMAT, "%s"), user));
	}
	
	private void bindAdmin(LdapConnection conn, String admin, String pass) throws LdapException {
		if (!Strings.isEmpty(admin)) {
			conn.bind(admin, pass);
		} else {
			conn.bind();
		}
	}
	
	private String getAttr(Properties config, Entry entry, String aliasCode, String defaultAlias) throws LdapInvalidAttributeValueException {
		String alias = config.getProperty(aliasCode, "");
		Attribute a = entry.get(Strings.isEmpty(alias) ? defaultAlias : alias);
		return a == null ? null : a.getString();
	}
	/**
	 * Ldap Login
	 * 
	 * Connection Data is retrieved from ConfigurationFile
	 * 
	 */
	// ----------------------------------------------------------------------------------------
	public User login(String user, String passwd, Long domainId) throws OmException {
		log.debug("LdapLoginmanagement.doLdapLogin");

		Properties config = new Properties();
		try {
			LdapConfig ldapConfig = ldapConfigDao.get(domainId);
			config.load(new FileInputStream(new File(OmFileHelper.getConfDir(), ldapConfig.getConfigFileName())));
		} catch (Exception e) {
			log.error("Error on LdapLogin : Configurationdata couldnt be retrieved!");
			return null;
		}
		if (config.isEmpty()) {
			log.error("Error on LdapLogin : Configurationdata couldnt be retrieved!");
			return null;
		}

		String ldap_use_lower_case = config.getProperty(CONFIGKEY_LDAP_USE_LOWER_CASE, "false");
		if ("true".equals(ldap_use_lower_case)) {
			user = user.toLowerCase();
		}

		String ldap_auth_type = config.getProperty(CONFIGKEY_LDAP_AUTH_TYPE, "");
		AuthType type = AuthType.SIMPLEBIND;
		try {
			type = AuthType.valueOf(ldap_auth_type);
		} catch (Exception e) {
			log.error("ConfigKey in Ldap Config contains invalid auth type : '%s' -> Defaulting to %s", ldap_auth_type, type);
		}
		
		String ldap_prov_type = config.getProperty(CONFIGKEY_LDAP_PROV_TYPE, "");
		Provisionning prov = Provisionning.AUTOCREATE;
		try {
			prov = Provisionning.valueOf(ldap_prov_type);
		} catch (Exception e) {
			log.error("ConfigKey in Ldap Config contains invalid provisionning type : '%s' -> Defaulting to %s", ldap_prov_type, prov);
		}
		
		if (AuthType.NONE == type && Provisionning.NONE == prov) {
			log.error("Both AuthType and Provisionning are NONE!");
			return null;
		}
		boolean useAdminForAttrs = true;
		try {
			useAdminForAttrs = "true".equals(config.getProperty(CONFIGKEY_LDAP_USE_ADMIN_4ATTRS, ""));
		} catch (Exception e) {
			//no-op
		}
		if (AuthType.NONE == type && !useAdminForAttrs) {
			log.error("Unable to get Attributes, please change Auth type and/or Use Admin to get attributes");
			return null;
		}

		// Connection URL
		String ldap_host = config.getProperty(CONFIGKEY_LDAP_HOST);
		int ldap_port = Integer.parseInt(config.getProperty(CONFIGKEY_LDAP_PORT, "389"));
		boolean ldap_secure = "true".equals(config.getProperty(CONFIGKEY_LDAP_SECURE, "false"));

		// Username for LDAP SERVER himself
		String ldap_admin_dn = config.getProperty(CONFIGKEY_LDAP_ADMIN_DN);

		// Password for LDAP SERVER himself
		String ldap_admin_passwd = config.getProperty(CONFIGKEY_LDAP_ADMIN_PASSWD);

		User u = null;
		LdapConnection conn = null;
		try {
			boolean authenticated = true;
			conn = new LdapNetworkConnection(ldap_host, ldap_port, ldap_secure);
			Dn userDn = null;
			Entry entry = null;
			switch (type) {
				case SEARCHANDBIND:
				{
					bindAdmin(conn, ldap_admin_dn, ldap_admin_passwd);
					Dn baseDn = new Dn(config.getProperty(CONFIGKEY_LDAP_SEARCH_BASE, ""));
					String searchQ = String.format(config.getProperty(CONFIGKEY_LDAP_SEARCH_QUERY, "%s"), user);
					SearchScope scope = SearchScope.valueOf(config.getProperty(CONFIGKEY_LDAP_SEARCH_SCOPE, SearchScope.ONELEVEL.name()));
					EntryCursor cursor = conn.search(baseDn, searchQ, scope, "*");
					while (cursor.next()) {
						if (userDn != null) {
							log.error("more than 1 user found in LDAP");
							throw new OmException(-1L);
						}
						Entry e = cursor.get();
						userDn = e.getDn();
						if (useAdminForAttrs) {
							entry = e;
						}
					}
					cursor.close();
					if (userDn == null) {
						log.error("NONE users found in LDAP");
						throw new OmException(-11L);
					}
					conn.bind(userDn, passwd);
				}
					break;
				case SIMPLEBIND:
				{
					userDn = getUserDn(config, user);
					conn.bind(userDn, passwd);
				}
					break;
				case NONE:
				default:
					authenticated = false;
					break;
			}
			u = authenticated ? userDao.getByName(user, Type.ldap) : userDao.login(user, passwd);
			if (u == null && Provisionning.AUTOCREATE != prov) {
				log.error("User not found in OM DB and Provisionning.AUTOCREATE was not set");
				throw new OmException(-11L);
			} else if (u != null && !domainId.equals(u.getDomainId())) {
				log.error("User found in OM DB, but domains are differ");
				throw new OmException(-11L);
			}
			if (authenticated && entry == null) {
				if (useAdminForAttrs) {
					bindAdmin(conn, ldap_admin_dn, ldap_admin_passwd);
				}
				entry = conn.lookup(userDn);
			}
			switch (prov) {
				case AUTOUPDATE:
				case AUTOCREATE:
					if (entry == null) {
						log.error("LDAP entry is null, search or lookup by Dn failed");
						throw new OmException(-11L);
					}
					if (u == null) {
						Set<Right> rights = UserDao.getDefaultRights();
						rights.remove(Right.Login);

						u = new User();
						u.setType(Type.ldap);
						u.setRights(rights);
						u.setDomainId(domainId);
						u.getOrganisation_users().add(new Organisation_Users(orgDao.get(cfgDao.getConfValue("default_domain_id", Long.class, "-1"))));
						u.setLogin(user);
						u.setAge(new Date());
						u.setShowContactDataToContacts(true);
						u.setAdresses(new Address());
						u.setLanguage_id(cfgDao.getConfValue(CONFIG_DEFAUT_LANG_KEY, Long.class, "1"));
						u.setSalutations_id(1L);
					}
					if ("true".equals(config.getProperty(CONFIGKEY_LDAP_SYNC_PASSWD_OM, ""))) {
						u.updatePassword(cfgDao, passwd);
					}
					u.setLastname(getAttr(config, entry, CONFIGKEY_LDAP_KEY_LASTNAME, LDAP_KEY_LASTNAME));
					u.setFirstname(getAttr(config, entry, CONFIGKEY_LDAP_KEY_FIRSTNAME, LDAP_KEY_FIRSTNAME));
					u.getAdresses().setEmail(getAttr(config, entry, CONFIGKEY_LDAP_KEY_MAIL, LDAP_KEY_MAIL));
					u.getAdresses().setStreet(getAttr(config, entry, CONFIGKEY_LDAP_KEY_STREET, LDAP_KEY_STREET));
					u.getAdresses().setAdditionalname(getAttr(config, entry, CONFIGKEY_LDAP_KEY_ADDITIONAL_NAME, LDAP_KEY_ADDITIONAL_NAME));
					u.getAdresses().setFax(getAttr(config, entry, CONFIGKEY_LDAP_KEY_FAX, LDAP_KEY_FAX));
					u.getAdresses().setZip(getAttr(config, entry, CONFIGKEY_LDAP_KEY_ZIP, LDAP_KEY_ZIP));
					u.getAdresses().setStates(stateDao.getStateByName(getAttr(config, entry, CONFIGKEY_LDAP_KEY_COUNTRY, LDAP_KEY_COUNTRY)));
					u.getAdresses().setTown(getAttr(config, entry, CONFIGKEY_LDAP_KEY_TOWN, LDAP_KEY_TOWN));
					u.getAdresses().setPhone(getAttr(config, entry, CONFIGKEY_LDAP_KEY_PHONE, LDAP_KEY_PHONE));
					String tz = getAttr(config, entry, CONFIGKEY_LDAP_TIMEZONE_NAME, LDAP_KEY_TIMEZONE);
					if (tz == null) {
						tz = config.getProperty(CONFIGKEY_LDAP_TIMEZONE_NAME, null);
					}
					u.setTimeZoneId(timezoneUtil.getTimeZone(tz).getID());
					String picture = getAttr(config, entry, CONFIGKEY_LDAP_PICTURE_URI, LDAP_KEY_PICTURE_URI);
					if (picture == null) {
						picture = config.getProperty(CONFIGKEY_LDAP_PICTURE_URI, null);
					}
					u.setPictureuri(picture);
					
					u = userDao.update(u, null);
					break;
				case NONE:
				default:
					break;
			}
		} catch (LdapAuthenticationException ae) {
			log.error("Not authenticated.", ae);
			throw new OmException(-11L);
		} catch (OmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected exception.", e);
			throw new OmException(e);
		} finally {
			if (conn != null) {
				try {
					conn.unBind();
					conn.close();
				} catch (Exception e) {
					log.error("Unexpected exception.", e);
					throw new OmException(e);
				}
			}
		}
		return u;
	}
}
