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

import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IStreamClientManager;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author swagner
 *
 */
@Component
public class UserManager implements IUserManager {
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);

	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private ConfigurationDao cfgDao;
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
	 * @param age - user birthday
	 * @param street - user address street
	 * @param additionalname - user additional name
	 * @param fax - user fax
	 * @param zip - user zip code
	 * @param country - user country code
	 * @param town - user town
	 * @param languageId - language id
	 * @param phone - user phone
	 * @param sendSMS - should SMS be sent to this user
	 * @param generateSipUserData - should SIP data be generated
	 * @param jNameTimeZone - the name of the time zone
	 * @param _sendConfirmation - should confirmation be sent
	 * @return {@link User} of code of error as {@link String}
	 */
	@Override
	public Object registerUser(String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, String country,
			String town, long languageId, String phone, boolean sendSMS,
			boolean generateSipUserData, String jNameTimeZone, Boolean _sendConfirmation) {
		try {
			// Checks if FrontEndUsers can register
			if (cfgDao.getBool(CONFIG_REGISTER_SOAP, false)) {
				boolean sendConfirmation;
				if (_sendConfirmation == null) {
					String baseURL = getBaseUrl();
					sendConfirmation = baseURL != null
							&& !baseURL.isEmpty()
							&& cfgDao.getBool(CONFIG_EMAIL_VERIFICATION, false);
				} else {
					sendConfirmation = _sendConfirmation.booleanValue();
				}
				Object user = registerUserInit(UserDao.getDefaultRights(), login,
						password, lastname, firstname, email, age, street,
						additionalname, fax, zip, country, town, languageId,
						true, Arrays.asList(cfgDao.getLong(CONFIG_DEFAULT_GROUP_ID, null)), phone,
						sendSMS, sendConfirmation, getTimeZone(jNameTimeZone), false, "", "", false, true, null);

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
	 * @param rights - {@link Right} to be assigned to the user
	 * @param login - user login
	 * @param password - user password
	 * @param lastname - user last name
	 * @param firstname - user first name
	 * @param email - user email
	 * @param age - user birthday
	 * @param street - user address street
	 * @param additionalname - user additional name
	 * @param fax - user fax
	 * @param zip - user zip code
	 * @param country - user country code
	 * @param town - user town
	 * @param languageId - language id
	 * @param sendWelcomeMessage - should confirmation email be sent
	 * @param groups - ids of user groups
	 * @param phone - user phone
	 * @param sendSMS - should SMS be sent to this user
	 * @param sendConfirmation - should confirmation be sent
	 * @param timezone - the name of the time zone
	 * @param forceTimeZoneCheck - should time zone be verified by user
	 * @param userOffers - what user offers
	 * @param userSearchs - what user searches
	 * @param showContactData - is contact data publicly visible
	 * @param showContactDataToContacts - is contact data visible to contacts
	 * @param activatedHash - activation hash
	 * @return {@link User} of code of error as {@link String}
	 * @throws NoSuchAlgorithmException in case password hashing algorithm is not found
	 * @throws OmException in case of any issues with provided data
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
		// Check for required data
		if (login.length() >= getMinLoginLength(cfgDao)) {
			// Check for duplicates
			boolean checkName = userDao.checkLogin(login, User.Type.user, null, null);
			boolean checkEmail = Strings.isEmpty(email) || userDao.checkEmail(email, User.Type.user, null, null);
			if (checkName && checkEmail) {
				String hash = Strings.isEmpty(activatedHash) ? UUID.randomUUID().toString() : activatedHash;
				if (sendWelcomeMessage && email.length() != 0) {
					emailManager.sendMail(login, email, hash, sendConfirmation, languageId);
				}
				Address a =  new Address();
				a.setStreet(street);
				a.setZip(zip);
				a.setTown(town);
				a.setCountry(country);
				a.setAdditionalname(additionalname);
				a.setComment("");
				a.setFax(fax);
				a.setPhone(phone);
				a.setEmail(email);

				// If this user needs first to click his E-Mail verification
				// code then set the status to 0
				if (sendConfirmation && rights.contains(Right.Login)) {
					rights.remove(Right.Login);
				}

				User u = new User();
				u.setFirstname(firstname);
				u.setLogin(login);
				u.setLastname(lastname);
				u.setAge(age);
				u.setAddress(a);
				u.setSendSMS(sendSMS);
				u.setRights(rights);
				u.setLastlogin(new Date());
				u.setSalutation(Salutation.mr);
				u.setActivatehash(hash);
				u.setTimeZoneId(timezone.getID());
				u.setForceTimeZoneCheck(forceTimeZoneCheck);
				if (!Strings.isEmpty(u.getExternalType())) {
					u.setType(Type.external);
				}

				u.setUserOffers(userOffers);
				u.setUserSearchs(userSearchs);
				u.setShowContactData(showContactData);
				u.setShowContactDataToContacts(showContactDataToContacts);

				// this is needed cause the language is not a needed data at registering
				u.setLanguageId(languageId != 0 ? languageId : 1);
				if (!Strings.isEmpty(password)) {
					u.updatePassword(cfgDao, password);
				}
				if (groups != null) {
					for (Long grpId : groups) {
						u.getGroupUsers().add(new GroupUser(groupDao.get(grpId), u));
					}
				}
				u = userDao.update(u, null);
				log.debug("Added user-Id " + u.getId());

				if (a.getId() != null && u.getId() != null) {
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
		/*
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
			return true;
		} catch (Exception err) {
			log.error("[kickUsersByRoomId]", err);
		}
		*/
		return false;
	}

	@Override
	public boolean kickById(String uid) {
		/*
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
		*/
		return false;
	}

	@Override
	public Long getLanguage(Locale loc) {
		return LabelDao.getLanguage(loc, getDefaultLang());
	}

	@Override
	public User loginOAuth(OAuthUser user, long serverId) throws IOException, NoSuchAlgorithmException {
		if (!userDao.validLogin(user.getUid())) {
			log.error("Invalid login, please check parameters");
			return null;
		}
		User u = userDao.getByLogin(user.getUid(), Type.oauth, serverId);
		if (!userDao.checkEmail(user.getEmail(), Type.oauth, serverId, u == null ? null : u.getId())) {
			log.error("Another user with the same email exists");
			return null;
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
			u.getRights().remove(Right.Login);
			u.setDomainId(serverId);
			u.getGroupUsers().add(new GroupUser(groupDao.get(cfgDao.getLong(CONFIG_DEFAULT_GROUP_ID, null)), u));
			u.setLogin(user.getUid());
			u.setShowContactDataToContacts(true);
			u.setLastname(user.getLastName());
			u.setFirstname(user.getFirstName());
			u.getAddress().setEmail(user.getEmail());
			String picture = user.getPicture();
			if (picture != null) {
				u.setPictureuri(picture);
			}
			String locale = user.getLocale();
			if (locale != null) {
				Locale loc = Locale.forLanguageTag(locale);
				if (loc != null) {
					u.setLanguageId(getLanguage(loc));
					u.getAddress().setCountry(loc.getCountry());
				}
			}
		}
		u.setLastlogin(new Date());
		u = userDao.update(u, pass, Long.valueOf(-1));

		return u;
	}
}
