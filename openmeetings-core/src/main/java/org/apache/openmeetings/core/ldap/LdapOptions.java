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

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.Properties;

import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.openmeetings.core.ldap.LdapLoginManager.AuthType;
import org.apache.openmeetings.core.ldap.LdapLoginManager.GroupMode;
import org.apache.openmeetings.core.ldap.LdapLoginManager.Provisionning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapOptions {
	private static final Logger log = LoggerFactory.getLogger(LdapOptions.class);
	private static final String EMPTY_FORMAT = "%s";
	public static final String CONFIGKEY_LDAP_HOST = "ldap_conn_host";
	public static final String CONFIGKEY_LDAP_PORT = "ldap_conn_port";
	private static final String CONFIGKEY_LDAP_SECURE = "ldap_conn_secure";
	public static final String CONFIGKEY_LDAP_ADMIN_DN = "ldap_admin_dn";
	public static final String CONFIGKEY_LDAP_ADMIN_PASSWD = "ldap_passwd";
	public static final String CONFIGKEY_LDAP_AUTH_TYPE = "ldap_auth_type";
	private static final String CONFIGKEY_LDAP_PROV_TYPE = "ldap_provisionning";
	private static final String CONFIGKEY_LDAP_USE_LOWER_CASE = "ldap_use_lower_case";
	private static final String CONFIGKEY_LDAP_USE_ADMIN_4ATTRS = "ldap_use_admin_to_get_attrs";
	private static final String CONFIGKEY_LDAP_DEREF_MODE = "ldap_deref_mode";
	private static final String CONFIGKEY_LDAP_GROUP_MODE = "ldap_group_mode";
	public static final String CONFIGKEY_LDAP_SEARCH_BASE = "ldap_search_base";
	private static final String CONFIGKEY_LDAP_SEARCH_QUERY = "ldap_search_query";
	public static final String CONFIGKEY_LDAP_SEARCH_SCOPE = "ldap_search_scope";
	private static final String CONFIGKEY_LDAP_SYNC_PASSWD_OM = "ldap_sync_password_to_om"; // 'true' or 'false'
	static final String CONFIGKEY_LDAP_TIMEZONE_NAME = "ldap_user_timezone";
	private static final String CONFIGKEY_LDAP_USERDN_FORMAT = "ldap_userdn_format";
	private static final String CONFIGKEY_LDAP_GROUP_QUERY = "ldap_group_query";
	private static final String CONFIGKEY_LDAP_IMPORT_QUERY = "ldap_import_query";
	public static final String CONFIGKEY_LDAP_PICTURE_URI = "ldap_user_picture_uri";

	AuthType type = AuthType.SIMPLEBIND;
	Provisionning prov = Provisionning.AUTOCREATE;
	AliasDerefMode derefMode = AliasDerefMode.DEREF_ALWAYS;
	GroupMode groupMode = GroupMode.NONE;
	boolean useLowerCase = false;
	boolean useAdminForAttrs = true;
	String host = null;
	int port = 389;
	boolean secure = false;
	String adminDn = null;
	String adminPasswd = null;
	String searchBase = "";
	String searchQuery = EMPTY_FORMAT;
	SearchScope scope = SearchScope.ONELEVEL;
	boolean syncPasswd = false;
	String tz = null;
	String groupQuery = EMPTY_FORMAT;
	String userDn = EMPTY_FORMAT;
	String pictureUri = null;
	String importQuery = null;

	public LdapOptions(Properties config) {
		String useLowerCaseProp = config.getProperty(CONFIGKEY_LDAP_USE_LOWER_CASE, "false");
		useLowerCase = "true".equals(useLowerCaseProp);

		String authType = config.getProperty(CONFIGKEY_LDAP_AUTH_TYPE, "");
		try {
			type = AuthType.valueOf(authType);
		} catch (Exception e) {
			log.error("ConfigKey in Ldap Config contains invalid auth type : '{}' -> Defaulting to {}", authType, type);
		}

		String provType = config.getProperty(CONFIGKEY_LDAP_PROV_TYPE, "");
		try {
			prov = Provisionning.valueOf(provType);
		} catch (Exception e) {
			log.error("ConfigKey in Ldap Config contains invalid provisionning type : '{}' -> Defaulting to {}", provType, prov);
		}

		String derefModeProp = config.getProperty(CONFIGKEY_LDAP_DEREF_MODE, "");
		try {
			derefMode = AliasDerefMode.getDerefMode(derefModeProp);
		} catch (Exception e) {
			log.error("ConfigKey in Ldap Config contains invalid deref mode : '{}' -> Defaulting to {}", derefModeProp, derefMode);
		}

		if (AuthType.NONE == type && Provisionning.NONE == prov) {
			throw new RuntimeException("Both AuthType and Provisionning are NONE!");
		}
		try {
			useAdminForAttrs = "true".equals(config.getProperty(CONFIGKEY_LDAP_USE_ADMIN_4ATTRS, ""));
		} catch (Exception e) {
			//no-op
		}
		try {
			groupMode = GroupMode.valueOf(config.getProperty(CONFIGKEY_LDAP_GROUP_MODE, "NONE"));
		} catch (Exception e) {
			//no-op
		}
		if (AuthType.NONE == type && !useAdminForAttrs) {
			throw new RuntimeException("Unable to get Attributes, please change Auth type and/or Use Admin to get attributes");
		}

		// Connection URL
		host = config.getProperty(CONFIGKEY_LDAP_HOST);
		port = toInt(config.getProperty(CONFIGKEY_LDAP_PORT), 389);
		secure = "true".equals(config.getProperty(CONFIGKEY_LDAP_SECURE, "false"));

		// Username for LDAP SERVER himself
		adminDn = config.getProperty(CONFIGKEY_LDAP_ADMIN_DN);

		// Password for LDAP SERVER himself
		adminPasswd = config.getProperty(CONFIGKEY_LDAP_ADMIN_PASSWD);

		searchBase = config.getProperty(CONFIGKEY_LDAP_SEARCH_BASE, "");
		searchQuery = config.getProperty(CONFIGKEY_LDAP_SEARCH_QUERY, EMPTY_FORMAT);
		scope = SearchScope.valueOf(config.getProperty(CONFIGKEY_LDAP_SEARCH_SCOPE, SearchScope.ONELEVEL.name()));
		syncPasswd = "true".equals(config.getProperty(CONFIGKEY_LDAP_SYNC_PASSWD_OM, ""));
		tz = config.getProperty(CONFIGKEY_LDAP_TIMEZONE_NAME, null);
		groupQuery = config.getProperty(CONFIGKEY_LDAP_GROUP_QUERY, EMPTY_FORMAT);
		userDn = config.getProperty(CONFIGKEY_LDAP_USERDN_FORMAT, EMPTY_FORMAT);
		pictureUri = config.getProperty(CONFIGKEY_LDAP_PICTURE_URI, null);
		importQuery = config.getProperty(CONFIGKEY_LDAP_IMPORT_QUERY, "(objectClass=*)");
	}
}
