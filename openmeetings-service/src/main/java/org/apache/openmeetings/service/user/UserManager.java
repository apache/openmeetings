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
package org.apache.openmeetings.service.user;

import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OmFileHelper.HIBERNATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.OmException;
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
	private ConfigurationDao cfgDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EmailManager emailManagement;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private TimezoneUtil timezoneUtil;

	/**
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Group with the id specified in the configuration value default.group.id
	 *
	 * @param login
	 * @param Userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param age
	 * @param street
	 * @param additionalname
	 * @param fax
	 * @param zip
	 * @param stateId
	 * @param town
	 * @param languageId
	 * @param phone
	 * @param sendSMS
	 * @param generateSipUserData
	 * @param jNameTimeZone
	 * @param sendConfirmation
	 * @return
	 */
	@Override
	public Object registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, String country,
			String town, long languageId, String phone, boolean sendSMS,
			boolean generateSipUserData, String jNameTimeZone, Boolean _sendConfirmation) {
		try {
			// Checks if FrontEndUsers can register
			if (cfgDao.getBool(CONFIG_REGISTER_SOAP, false)) {
				boolean sendConfirmation;
				if (_sendConfirmation == null) {
					String baseURL = cfgDao.getBaseUrl();
					sendConfirmation = baseURL != null
							&& !baseURL.isEmpty()
							&& cfgDao.getBool(CONFIG_EMAIL_VERIFICATION, false);
				} else {
					sendConfirmation = _sendConfirmation.booleanValue();
				}
				// TODO: Read and generate SIP-Data via RPC-Interface Issue 1098

				Object user = registerUserInit(UserDao.getDefaultRights(), login,
						Userpass, lastname, firstname, email, age, street,
						additionalname, fax, zip, country, town, languageId,
						true, Arrays.asList(cfgDao.getLong(CONFIG_DEFAULT_GROUP_ID, null)), phone,
						sendSMS, sendConfirmation, timezoneUtil.getTimeZone(jNameTimeZone), false, "", "", false, true, null);

				if (user instanceof User && sendConfirmation) {
					return -40L;
				}

				return user;
			} else {
				return "error.reg.disabled";
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return null;
	}

	/**
	 * @param user_level
	 * @param availible
	 * @param status
	 * @param login
	 * @param password
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param age
	 * @param street
	 * @param additionalname
	 * @param fax
	 * @param zip
	 * @param stateId
	 * @param town
	 * @param languageId
	 * @param sendWelcomeMessage
	 * @param groups
	 * @param phone
	 * @param sendSMS
	 * @param sendConfirmation
	 * @param timezone
	 * @param forceTimeZoneCheck
	 * @param userOffers
	 * @param userSearchs
	 * @param showContactData
	 * @param showContactDataToContacts
	 * @return new user OR error code
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	@Override
	public Object registerUserInit(Set<Right> rights, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, String country,
			String town, long languageId, boolean sendWelcomeMessage,
			List<Long> groups, String phone, boolean sendSMS, Boolean sendConfirmation,
			TimeZone timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts, String activatedHash) throws OmException, NoSuchAlgorithmException {
		// TODO: make phone number persistent
		// Check for required data
		if (login.length() >= getMinLoginLength(cfgDao)) {
			// Check for duplicates
			boolean checkName = userDao.checkLogin(login, User.Type.user, null, null);
			boolean checkEmail = Strings.isEmpty(email) || userDao.checkEmail(email, User.Type.user, null, null);
			if (checkName && checkEmail) {
				String hash = Strings.isEmpty(activatedHash) ? UUID.randomUUID().toString() : activatedHash;
				if (sendWelcomeMessage && email.length() != 0) {
					emailManagement.sendMail(login, email, hash, sendConfirmation, languageId);
				}
				Address adr =  userDao.getAddress(street, zip, town, country, additionalname, fax, phone, email);

				// If this user needs first to click his E-Mail verification
				// code then set the status to 0
				if (sendConfirmation && rights.contains(Right.Login)) {
					rights.remove(Right.Login);
				}

				User u = userDao.addUser(rights, firstname, login, lastname, languageId,
						password, adr, sendSMS, age, hash, timezone,
						forceTimeZoneCheck, userOffers, userSearchs, showContactData,
						showContactDataToContacts, null, null, groups, null);
				log.debug("Added user-Id " + u.getId());

				if (adr.getId() != null && u.getId() != null) {
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
	 * @param sid
	 * @param room_id
	 * @return
	 */
	@Override
	public boolean kickUsersByRoomId(Long roomId) {
		try {
			sessionDao.clearSessionByRoomId(roomId);

			for (StreamClient rcl : sessionManager.listByRoom(roomId)) {
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
			return true;
		} catch (Exception err) {
			log.error("[kickUsersByRoomId]", err);
		}
		return false;
	}

	@Override
	public boolean kickById(String uid) {
		try {
			StreamClient rcl = sessionManager.get(uid);

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
		if (loc != null) {
			for (Map.Entry<Long, Locale> e : LabelDao.languages.entrySet()) {
				if (loc.equals(e.getValue())) {
					return e.getKey();
				}
			}
		}
		return cfgDao.getLong(CONFIG_DEFAULT_LANG, 1L);
	}

	@Override
	public User loginOAuth(Map<String, String> params, long serverId) throws IOException, NoSuchAlgorithmException {
		String login = params.get("login");
		String email = params.get("email");
		String lastname = params.get("lastname");
		String firstname = params.get("firstname");
		if (firstname == null) {
			firstname = "";
		}
		if (lastname == null) {
			lastname = "";
		}
		if (!userDao.validLogin(login)) {
			log.error("Invalid login, please check parameters");
			return null; //TODO FIXME need to be checked
		}
		User u = userDao.getByLogin(login, Type.oauth, serverId);
		if (!userDao.checkEmail(email, Type.oauth, serverId, u == null ? null : u.getId())) {
			log.error("Another user with the same email exists");
			return null; //TODO FIXME need to be checked
		}
		// generate random password
		SecureRandom rnd = new SecureRandom();
		byte[] rawPass = new byte[25];
		rnd.nextBytes(rawPass);
		String pass = Base64.encodeBase64String(rawPass);
		// check if the user already exists and register new one if it's needed
		if (u == null) {
			u = userDao.getNewUserInstance(null);
			u.setType(Type.oauth);
			u.getRights().remove(Right.Login);;
			u.setDomainId(serverId);
			u.getGroupUsers().add(new GroupUser(groupDao.get(cfgDao.getLong(CONFIG_DEFAULT_GROUP_ID, null)), u));
			u.setLogin(login);
			u.setShowContactDataToContacts(true);
			u.setLastname(lastname);
			u.setFirstname(firstname);
			u.getAddress().setEmail(email);
			String picture = params.get("picture");
			if (picture != null) {
				u.setPictureuri(picture);
			}
			String locale = params.get("locale");
			if (locale != null) {
				Locale loc = Locale.forLanguageTag(locale);
				if (loc != null) {
					u.setLanguageId(getLanguage(loc));
					u.getAddress().setCountry(loc.getCountry());
				}
			}
		}
		//TODO FIXME should we update fields on login ????
		u.setLastlogin(new Date());
		u = userDao.update(u, pass, Long.valueOf(-1));

		return u;
	}
}
