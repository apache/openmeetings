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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OmFileHelper.HIBERNATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterFrontend;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendVerificationEmail;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.manager.IStreamClientManager;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.crypt.ICrypt;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author swagner
 *
 */
@Component
public class UserManager implements IUserManager {
	private static final Logger log = Red5LoggerFactory.getLogger(UserManager.class, getWebAppRootKey());

	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EmailManager emailManager;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;
	@Autowired
	private IStreamClientManager streamClientManager;
	@Autowired
	private IClientManager cm;

	private boolean sendConfirmation() {
		String baseURL = getBaseUrl();
		return !Strings.isEmpty(baseURL) && isSendVerificationEmail();
	}

	/**
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Group with the id specified in the configuration value default.group.id
	 *
	 * @param login - user login
	 * @param password - user password
	 * @param lastname - user last name
	 * @param firstname - user first name
	 * @param email - user email
	 * @param country - user country code
	 * @param languageId - language id
	 * @param tzId - the name of the time zone
	 * @return {@link User} of code of error as {@link String}
	 */
	@Override
	public Object registerUser(String login, String password, String lastname,
			String firstname, String email, String country, long languageId, String tzId) {
		try {
			// Checks if FrontEndUsers can register
			if (isAllowRegisterFrontend()) {
				User u = getNewUserInstance(null);
				u.setFirstname(firstname);
				u.setLogin(login);
				u.setLastname(lastname);
				u.getAddress().setCountry(country);
				u.getAddress().setEmail(email);
				u.setTimeZoneId(getTimeZone(tzId).getID());

				// this is needed cause the language is not a necessary data at registering
				u.setLanguageId(languageId != 0 ? languageId : 1);
				u.getGroupUsers().add(new GroupUser(groupDao.get(getDefaultGroup()), u));

				Object user = registerUser(u, password, null);

				if (user instanceof User && sendConfirmation()) {
					log.debug("User created, confirmation should be sent");
					return -40L;
				}

				log.debug("User creation result: {}", user);
				return user;
			} else {
				log.warn("Frontend registering is disabled");
				return "error.reg.disabled";
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return null;
	}

	/**
	 * @param u - User with basic parametrs set
	 * @param password - user password
	 * @param hash - activation hash
	 * @return {@link User} of code of error as {@link String}
	 * @throws NoSuchAlgorithmException in case password hashing algorithm is not found
	 * @throws OmException in case of any issues with provided data
	 */
	@Override
	public Object registerUser(User u, String password, String hash) throws OmException, NoSuchAlgorithmException {
		// Check for required data
		String login = u.getLogin();
		if (!Strings.isEmpty(login) && login.length() >= getMinLoginLength()) {
			// Check for duplicates
			boolean checkName = userDao.checkLogin(login, User.Type.user, null, null);
			String email = u.getAddress() == null ? null : u.getAddress().getEmail();
			boolean checkEmail = Strings.isEmpty(email) || userDao.checkEmail(email, User.Type.user, null, null);
			if (checkName && checkEmail) {
				String ahash = Strings.isEmpty(hash) ? UUID.randomUUID().toString() : hash;
				if (Strings.isEmpty(u.getExternalType())) {
					if (!Strings.isEmpty(email)) {
						emailManager.sendMail(login, email, ahash, sendConfirmation(), u.getLanguageId());
					}
				} else {
					u.setType(Type.external);
				}

				// If this user needs first to click his E-Mail verification
				// code then set the status to 0
				if (sendConfirmation() && u.getRights().contains(Right.Login)) {
					u.getRights().remove(Right.Login);
				}

				u.setActivatehash(ahash);
				if (!Strings.isEmpty(password)) {
					u.updatePassword(password);
				}
				u = userDao.update(u, null);
				log.debug("Added user-Id {}", u.getId());

				if (u.getId() != null) {
					return u;
				}
			} else {
				if (!checkName) {
					return "error.login.inuse";
				} else {
					return "error.email.inuse";
				}
			}
		} else {
			return "error.short.login";
		}
		return UNKNOWN.getKey();
	}

	/**
	 * @param roomId - id of the room user should be kicked from
	 * @return <code>true</code> if there were no errors
	 */
	@Override
	public boolean kickUsersByRoomId(Long roomId) {
		try {
			sessionDao.clearSessionByRoomId(roomId);

			for (StreamClient rcl : streamClientManager.list(roomId)) {
				if (rcl == null) {
					return true;
				}
				String scopeName = rcl.getRoomId() == null ? HIBERNATE : rcl.getRoomId().toString();
				IScope currentScope = scopeAdapter.getChildScope(scopeName);
				scopeAdapter.roomLeaveByScope(rcl, currentScope);

				Map<Integer, String> messageObj = new HashMap<>();
				messageObj.put(0, "kick");
				scopeAdapter.sendMessageById(messageObj, rcl.getUid(), currentScope);
			}
			for (Client c : cm.listByRoom(roomId)) {
				Application.kickUser(c);
			}
			return true;
		} catch (Exception err) {
			log.error("[kickUsersByRoomId]", err);
		}
		return false;
	}

	@Override
	public boolean kickExternal(Long roomId, String externalType, String externalId) {
		boolean result = false;
		try {
			if (roomId == null) {
				return result;
			}
			User u = userDao.getExternalUser(externalId, externalType);
			if (u != null) {
				for (Client c : cm.listByUser(u.getId())) {
					if (roomId.equals(c.getRoomId())) {
						Application.kickUser(c);
						result = true;
					}
				}
			}
		} catch (Exception e) {
			log.error("[kickExternal]", e);
		}
		return result;
	}

	public boolean kickById(String uid) {
		try {
			StreamClient rcl = streamClientManager.get(uid);

			if (rcl == null) {
				return true;
			}

			String scopeName = rcl.getScope() == null ? HIBERNATE : rcl.getScope();
			IScope scope = scopeAdapter.getChildScope(scopeName);
			if (scope == null) {
				log.warn("### kickById ### The scope is NULL");
				return false;
			}

			Map<Integer, String> messageObj = new HashMap<>();
			messageObj.put(0, "kick");
			scopeAdapter.sendMessageById(messageObj, uid, scope);

			scopeAdapter.roomLeaveByScope(rcl, scope);

			return true;
		} catch (Exception err) {
			log.error("[kickById]", err);
		}
		return false;
	}

	@Override
	public Long getLanguage(Locale loc) {
		return LabelDao.getLanguage(loc, getDefaultLang());
	}

	@Override
	public User loginOAuth(OAuthUser user, long serverId) throws IOException, NoSuchAlgorithmException {
		if (!userDao.validLogin(user.getLogin())) {
			log.error("Invalid login, please check parameters");
			return null;
		}
		User u = userDao.getByLogin(user.getLogin(), Type.oauth, serverId);
		if (!userDao.checkEmail(user.getEmail(), Type.oauth, serverId, u == null ? null : u.getId())) {
			log.error("Another user with the same email exists");
			return null;
		}
		// generate random password
		// check if the user already exists and register new one if it's needed
		if (u == null) {
			final User fUser = getNewUserInstance(null);
			fUser.setType(Type.oauth);
			fUser.getRights().remove(Right.Login);
			fUser.setDomainId(serverId);
			fUser.getGroupUsers().add(new GroupUser(groupDao.get(getDefaultGroup()), fUser));
			for (Map.Entry<String, String> entry : user.getUserData().entrySet()) {
				final String expression = entry.getKey();
				PropertyResolver.setValue(expression, fUser, entry.getValue(), new PropertyResolverConverter(null, null) {
					private static final long serialVersionUID = 1L;

					@SuppressWarnings("unchecked")
					@Override
					public <T> T convert(Object object, Class<T> clz) {
						if ("languageId".equals(expression) && Long.class.isAssignableFrom(clz)) {
							Long language = 1L;
							String locale = (String)object;
							if (locale != null) {
								Locale loc = Locale.forLanguageTag(locale);
								if (loc != null) {
									language = getLanguage(loc);
									fUser.setLanguageId(language);
									fUser.getAddress().setCountry(loc.getCountry());
								}
							}
							return (T)language;
						}
						return (T)object;
					}

					@Override
					protected <C> String convertToString(C object, Locale locale) {
						return String.valueOf(object);
					}
				});
			}
			fUser.setShowContactDataToContacts(true);
			u = fUser;
		}
		u.setLastlogin(new Date());
		ICrypt crypt = CryptProvider.get();
		u = userDao.update(u, crypt.randomPassword(25), Long.valueOf(-1));

		return u;
	}
}
