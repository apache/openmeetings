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

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.db.util.LocaleHelper.validateCountry;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OmException.BAD_CREDENTIALS;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OmFileHelper.loadLdapConf;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.CursorLdapReferralException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.ldap.client.api.EntryCursorImpl;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.openmeetings.core.converter.ImageConverter;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.StoredFile;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

/**
 * Management of optional LDAP Login
 *
 * @author o.becherer
 *
 */
@Component
public class LdapLoginManager {
	private static final Logger log = LoggerFactory.getLogger(LdapLoginManager.class);
	private static final String WARN_REFERRAL = "Referral LDAP entry found, ignore it";
	// LDAP custom attribute mapping keys
	private static final String CONFIGKEY_LDAP_KEY_LOGIN = "ldap_user_attr_login";
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
	private static final String CONFIGKEY_LDAP_KEY_GROUP = "ldap_group_attr";
	public static final String CONFIGKEY_LDAP_KEY_PICTURE = "ldap_user_attr_picture";

	// LDAP default attributes mapping
	private static final String LDAP_KEY_LOGIN = "uid";
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
	private static final String LDAP_KEY_GROUP = "memberOf";

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

	public enum GroupMode {
		NONE
		, ATTRIBUTE
		, QUERY
	}

	@Inject
	private LdapConfigDao ldapConfigDao;
	@Inject
	private UserDao userDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private ImageConverter imageConverter;

	private static void bindAdmin(LdapConnection conn, LdapOptions options) throws LdapException {
		if (!Strings.isEmpty(options.adminDn)) {
			conn.bind(options.adminDn, options.adminPasswd);
		} else {
			conn.bind();
		}
	}

	private static Attribute getAttr(Properties config, Entry entry, String aliasCode, String defaultAlias) {
		String alias = config.getProperty(aliasCode, "");
		if (Strings.isEmpty(alias)) {
			alias = defaultAlias;
		}
		return Strings.isEmpty(alias) ? null : entry.get(alias);
	}

	private static String getStringAttr(Properties config, Entry entry, String aliasCode, String defaultAlias) throws LdapInvalidAttributeValueException {
		Attribute a = getAttr(config, entry, aliasCode, defaultAlias);
		return a == null ? null : a.getString();
	}

	private static String getLogin(Properties config, Entry entry) throws LdapInvalidAttributeValueException {
		return getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_LOGIN, LDAP_KEY_LOGIN);
	}

	private User doLogin(LdapWorker w, String login, String passwd, Long domainId) throws Exception {
		User u = null;
		boolean authenticated = true;
		Dn userDn = null;
		Entry entry = null;
		switch (w.options.type) {
			case SEARCHANDBIND:
				Map.Entry<Dn, Entry> search = searchAndBind(w, login, passwd);
				userDn = search.getKey();
				entry = search.getValue();
				break;
			case SIMPLEBIND:
				userDn = new Dn(String.format(w.options.userDn, login));
				w.conn.bind(userDn, passwd);
				break;
			case NONE:
			default:
				authenticated = false;
				break;
		}
		u = authenticated ? userDao.getByLogin(login, Type.LDAP, domainId) : userDao.login(login, passwd);
		log.debug("getByLogin:: authenticated ? {}, login = '{}', domain = {}, user = {}", authenticated, login, domainId, u);
		if (u == null && Provisionning.AUTOCREATE != w.options.prov) {
			log.error("User not found in OM DB and Provisionning.AUTOCREATE was not set");
			throw BAD_CREDENTIALS;
		}
		if (authenticated && entry == null) {
			if (w.options.useAdminForAttrs) {
				bindAdmin(w.conn, w.options);
			}
			entry = w.conn.lookup(userDn);
		}
		switch (w.options.prov) {
			case AUTOUPDATE, AUTOCREATE:
				u = w.getUser(entry, u);
				if (w.options.syncPasswd) {
					u.updatePassword(passwd);
				}
				u = userDao.update(u, null);
				u = w.setUserPicture(entry, u);
				break;
			case NONE:
			default:
				break;
		}
		return u;
	}
	/**
	 * Ldap Login
	 *
	 * Connection Data is retrieved from ConfigurationFile
	 *
	 * @param inLogin - user login
	 * @param passwd - user password
	 * @param domainId - user domain id
	 * @return - {@link User} with this credentials or <code>null</code>
	 * @throws OmException - in case of any error
	 */
	public User login(String inLogin, String passwd, Long domainId) throws OmException {
		log.debug("LdapLoginmanager.doLdapLogin");
		if (!userDao.validLogin(inLogin)) {
			log.error("Invalid login provided");
			return null;
		}

		User u = null;
		try (LdapWorker w = new LdapWorker(domainId)) {
			String login = w.options.useLowerCase ? inLogin.toLowerCase(Locale.ROOT) : inLogin;

			u = doLogin(w, login, passwd, domainId);
		} catch (LdapAuthenticationException ae) {
			log.error("Not authenticated.", ae);
			throw BAD_CREDENTIALS;
		} catch (OmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected exception.", e);
			throw new OmException(e);
		}
		return u;
	}

	private static Map.Entry<Dn, Entry> searchAndBind(LdapWorker w, String login, String passwd) throws LdapException, CursorException, OmException, IOException {
		Dn userDn = null;
		Entry entry = null;
		bindAdmin(w.conn, w.options);
		Dn baseDn = new Dn(w.options.searchBase);
		String searchQ = String.format(w.options.searchQuery, login);

		try (EntryCursor cursor = new EntryCursorImpl(w.conn.search(
				new SearchRequestImpl()
					.setBase(baseDn)
					.setFilter(searchQ)
					.setScope(w.options.scope)
					.addAttributes("*")
					.setDerefAliases(w.options.derefMode))))
		{
			while (cursor.next()) {
				try {
					Entry e = cursor.get();
					if (userDn != null) {
						log.error("more than 1 user found in LDAP");
						throw UNKNOWN;
					}
					userDn = e.getDn();
					if (w.options.useAdminForAttrs) {
						entry = e;
					}
				} catch (CursorLdapReferralException cle) {
					log.warn(WARN_REFERRAL);
				}
			}
		}
		if (userDn == null) {
			log.error("NONE users found in LDAP");
			throw BAD_CREDENTIALS;
		}
		w.conn.bind(userDn, passwd);
		return new AbstractMap.SimpleEntry<>(userDn, entry);
	}

	public void importUsers(Long domainId, boolean print) throws OmException {
		try (LdapWorker w = new LdapWorker(domainId)) {
			bindAdmin(w.conn, w.options);
			Dn baseDn = new Dn(w.options.searchBase);

			try (EntryCursor cursor = new EntryCursorImpl(w.conn.search(
					new SearchRequestImpl()
						.setBase(baseDn)
						.setFilter(w.options.importQuery)
						.setScope(w.options.scope)
						.addAttributes("*")
						.setDerefAliases(w.options.derefMode))))
			{
				importUsers(w, cursor, domainId, print);
			}
		} catch (LdapAuthenticationException ae) {
			log.error("Not authenticated.", ae);
			throw BAD_CREDENTIALS;
		} catch (OmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected exception.", e);
			throw new OmException(e);
		}
	}

	private void importUsers(LdapWorker w, EntryCursor cursor, Long domainId, boolean print) throws LdapException, CursorException, OmException, IOException {
		while (cursor.next()) {
			try {
				Entry e = cursor.get();
				User u = userDao.getByLogin(getLogin(w.config, e), Type.LDAP, domainId);
				u = w.getUser(e, u);
				if (print) {
					log.info("Going to import user: {}", u);
				} else {
					userDao.update(u, null);
					log.info("User {}, was imported", u);
				}
			} catch (CursorLdapReferralException cle) {
				log.warn(WARN_REFERRAL);
			}
		}
	}

	private class LdapWorker implements Closeable {
		final LdapConnection conn;
		final Properties config = new Properties();
		final LdapOptions options;
		final Long domainId;
		final LdapConfig ldapCfg;

		public LdapWorker(Long domainId) {
			this.domainId = domainId;
			ldapCfg = ldapConfigDao.get(domainId);
			loadLdapConf(ldapCfg.getConfigFileName(), config);
			options = new LdapOptions(config);

			conn = new LdapNetworkConnection(options.host, options.port, options.secure);
		}

		private User updatePic(User inUser, InputStream is) {
			User u = inUser;
			Path tempImage = null;
			try {
				tempImage = Files.createTempFile("omLdap", "img");
				FileUtils.copyToFile(is, tempImage.toFile());
				imageConverter.convertImageUserProfile(tempImage.toFile(), inUser.getId());
				u = userDao.get(inUser.getId());
			} catch (Exception e) {
				log.error("Unable to store binary image from LDAP", e);
			} finally {
				if (tempImage != null) {
					try {
						Files.deleteIfExists(tempImage);
					} catch (IOException e) {
						log.error("Unexpected error while clean-up", e);
					}
				}
			}
			return u;
		}

		public User setUserPicture(Entry entry, User inUser) {
			User user = Optional.ofNullable(getAttr(config, entry, CONFIGKEY_LDAP_KEY_PICTURE, ""))
					.map(Attribute::get)
					.filter(val -> val != null && val.getBytes() != null)
					.map(val -> {
						User u = inUser;
						InputStream is = new ByteArrayInputStream(val.getBytes());
						StoredFile sf = new StoredFile("picture", is);
						if (sf.isImage()) {
							u = updatePic(inUser, is);
						} else {
							u.setPictureUri(val.getString());
						}
						return u;
					}).orElse(inUser);
			if (Strings.isEmpty(user.getPictureUri()) && !Strings.isEmpty(options.pictureUri)) {
				user.setPictureUri(options.pictureUri);
			}
			return userDao.update(user, null);
		}

		private User create(Entry entry) throws LdapInvalidAttributeValueException {
			User u = getNewUserInstance(null);
			u.setType(Type.LDAP);
			u.getRights().remove(Right.LOGIN);
			u.setDomainId(domainId);
			Group g = groupDao.get(getDefaultGroup());
			if (g != null) {
				u.addGroup(g);
			}
			String login = getLogin(config, entry);
			if (ldapCfg.getAddDomainToUserName()) {
				login = login + "@" + ldapCfg.getDomain();
			}
			if (options.useLowerCase) {
				login = login.toLowerCase(Locale.ROOT);
			}
			u.setLogin(login);
			u.setShowContactDataToContacts(true);
			u.setAddress(new Address());
			return u;
		}

		private List<Dn> getGroupDns(Entry entry, User u) throws IOException, LdapException, CursorException {
			List<Dn> groups = new ArrayList<>();
			if (GroupMode.ATTRIBUTE == options.groupMode) {
				Attribute attr = getAttr(config, entry, CONFIGKEY_LDAP_KEY_GROUP, LDAP_KEY_GROUP);
				if (attr != null) {
					for (Value v : attr) {
						groups.add(new Dn(v.getString()));
					}
				}
			} else if (GroupMode.QUERY == options.groupMode) {
				Dn baseDn = new Dn(options.searchBase);
				String searchQ = String.format(options.groupQuery, u.getLogin());
				fillGroups(baseDn, searchQ, groups);
			}
			return groups;
		}

		public User getUser(Entry entry, User user) throws LdapException, CursorException, OmException, IOException {
			if (entry == null) {
				log.error("LDAP entry is null, search or lookup by Dn failed");
				throw BAD_CREDENTIALS;
			}
			User u = user == null ? create(entry) : user;
			u.setLastname(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_LASTNAME, LDAP_KEY_LASTNAME));
			u.setFirstname(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_FIRSTNAME, LDAP_KEY_FIRSTNAME));
			u.getAddress().setEmail(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_MAIL, LDAP_KEY_MAIL));
			u.getAddress().setStreet(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_STREET, LDAP_KEY_STREET));
			u.getAddress().setAdditionalname(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_ADDITIONAL_NAME, LDAP_KEY_ADDITIONAL_NAME));
			u.getAddress().setFax(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_FAX, LDAP_KEY_FAX));
			u.getAddress().setZip(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_ZIP, LDAP_KEY_ZIP));
			u.getAddress().setCountry(validateCountry(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_COUNTRY, LDAP_KEY_COUNTRY)));
			u.getAddress().setTown(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_TOWN, LDAP_KEY_TOWN));
			u.getAddress().setPhone(getStringAttr(config, entry, CONFIGKEY_LDAP_KEY_PHONE, LDAP_KEY_PHONE));
			String tz = getStringAttr(config, entry, LdapOptions.CONFIGKEY_LDAP_TIMEZONE_NAME, LDAP_KEY_TIMEZONE);
			if (tz == null) {
				tz = options.tz;
			}
			u.setTimeZoneId(getTimeZone(tz).getID());

			getGroupDns(entry, u).stream()
					.map(Dn::getRdn)
					.map(Rdn::getValue)
					.filter(name -> !Strings.isEmpty(name))
					.forEach(name -> {
						Group o = groupDao.get(name);
						boolean found = false;
						if (o == null) {
							o = new Group();
							o.setName(name);
							o = groupDao.update(o, u.getId());
						} else {
							for (GroupUser ou : u.getGroupUsers()) {
								if (ou.getGroup().getName().equals(name)) {
									found = true;
									break;
								}
							}
						}
						if (!found) {
							u.addGroup(o);
							log.debug("Going to add user to group:: {}", name);
						}
					});
			return u;
		}

		private void fillGroups(Dn baseDn, String searchQ, List<Dn> groups) throws IOException, LdapException, CursorException {
			try (EntryCursor cursor = new EntryCursorImpl(conn.search(
					new SearchRequestImpl()
						.setBase(baseDn)
						.setFilter(searchQ)
						.setScope(SearchScope.SUBTREE)
						.addAttributes("*")
						.setDerefAliases(AliasDerefMode.DEREF_ALWAYS))))
			{
				while (cursor.next()) {
					try {
						Entry e = cursor.get();
						groups.add(e.getDn());
					} catch (CursorLdapReferralException cle) {
						log.warn(WARN_REFERRAL);
					}
				}
			}
		}

		@Override
		public void close() throws IOException {
			if (conn != null) {
				conn.close();
			}
		}
	}
}
