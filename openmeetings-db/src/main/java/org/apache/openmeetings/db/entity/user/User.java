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
package org.apache.openmeetings.db.entity.user;

import static org.apache.openmeetings.db.bind.Constants.USER_NODE;
import static org.apache.openmeetings.db.dao.user.UserDao.FETCH_GROUP_BACKUP;
import static org.apache.openmeetings.db.dao.user.UserDao.FETCH_GROUP_GROUP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSipContext;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;
import static org.apache.wicket.util.string.Strings.escapeMarkup;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.LoadFetchGroup;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.LocalDateAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.SalutationAdapter;
import org.apache.openmeetings.db.bind.adapter.UserRightAdapter;
import org.apache.openmeetings.db.bind.adapter.UserTypeAdapter;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.util.MD5;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.wicket.util.string.Strings;

/**
 * Entity to store user data, password field is {@link FetchType#LAZY}, so that
 * is why there is an extra udpate statement at this moment
 *
 * @author sebawagner
 *
 */
@Entity
@FetchGroups({
	@FetchGroup(name = FETCH_GROUP_BACKUP, attributes = { @FetchAttribute(name = "password") })
	, @FetchGroup(name = FETCH_GROUP_GROUP, attributes = { @FetchAttribute(name = "groupUsers")})
})
@NamedQuery(name = "getUserById", query = "SELECT u FROM User u WHERE u.id = :id")
@NamedQuery(name = "getUsersByIds", query = "select c from User c where c.id IN :ids")
@NamedQuery(name = "getUserByLogin", query = """
	SELECT u
	FROM User u
	WHERE
		u.deleted = false
		AND u.type = :type
		AND lower(trim(both from u.login)) = :login
		AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))""")
@NamedQuery(name = "getUserByEmail", query = """
	SELECT u
	FROM User u
	WHERE
		u.deleted = false
		AND u.type = :type
		AND lower(trim(both from u.address.email)) = :email
		AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))""")
@NamedQuery(name = "getUserByHash",  query = """
	SELECT u
	FROM User u
	WHERE
		u.deleted = false
		AND u.type = :type
		AND u.resethash = :resethash""")
@NamedQuery(name = "getUserByExpiredHash",  query = "SELECT u FROM User u WHERE u.resetDate < :date")
@NamedQuery(name = "getContactByEmailAndUser", query = """
	SELECT u
	FROM User u
	WHERE
		u.deleted = false
		AND u.address.email = :email
		AND u.type = :type
		AND u.ownerId = :ownerId""")
@NamedQuery(name = "selectMaxFromUsersWithSearch", query = """
	SELECT count(c.id)
	FROM User c
	WHERE
		c.deleted = false
		AND (
			lower(c.login) LIKE :search
			OR lower(c.firstname) LIKE :search
			OR lower(c.lastname) LIKE :search
		)""")
@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u ORDER BY u.id")
@NamedQuery(name = "getPassword", query = "SELECT u.password FROM User u WHERE u.deleted = false AND u.id = :userId ")
@NamedQuery(name = "updatePassword", query = "UPDATE User u SET u.password = :password WHERE u.id = :userId")
@NamedQuery(name = "getNondeletedUsers", query = "SELECT u FROM User u WHERE u.deleted = false")
@NamedQuery(name = "countNondeletedUsers", query = "SELECT COUNT(u) FROM User u WHERE u.deleted = false")
@NamedQuery(name = "getUsersByGroupId", query = "SELECT u FROM User u WHERE u.deleted = false AND u.groupUsers.group.id = :groupId")
@NamedQuery(name = "getExternalUser", query = """
	SELECT gu.user
	FROM GroupUser gu
	WHERE
		gu.group.deleted = false
		AND gu.group.external = true
		AND gu.group.name = :externalType
		AND gu.user.deleted = false
		AND gu.user.type = :type
		AND gu.user.externalId = :externalId""")
@NamedQuery(name = "getUserByLoginOrEmail", query = """
	SELECT u
	FROM User u
	WHERE
		u.deleted = false
		AND u.type = :type
		AND (
			lower(trim(both from u.login)) = :userOrEmail
			OR lower(trim(both from u.address.email)) = :userOrEmail
		)""")
@Table(name = "om_user", indexes = {
		@Index(name = "login_idx", columnList = "login")
		, @Index(name = "lastname_idx", columnList = "lastname")
		, @Index(name = "firstname_idx", columnList = "firstname")
		, @Index(name = "type_idx", columnList = "type")
})
@XmlRootElement(name = USER_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final String DISPLAY_NAME_NA = "N/A";
	public static final int SALUTATION_MR_ID = 1;
	public static final int SALUTATION_MS_ID = 2;
	public static final int SALUTATION_MRS_ID = 3;
	public static final int SALUTATION_DR_ID = 4;
	public static final int SALUTATION_PROF_ID = 5;

	@XmlType(namespace="org.apache.openmeetings.user.right")
	public enum Right {
		ADMIN(false)			// access to Admin module
		, GROUP_ADMIN(false)	// partial access to Admin module (should not be directly assigned)
		, ADMIN_CONFIG(false)
		, ADMIN_CONNECTIONS(false)
		, ADMIN_BACKUP(false)
		, ADMIN_LABEL(false)
		, ROOM(true)			// enter the room
		, DASHBOARD(true)		// access the dashboard
		, LOGIN(true)			// login to Om internal DB
		, SOAP(false);			// use rest/soap calls

		private final boolean groupAdminAllowed;

		private Right(boolean groupAdminAllowed) {
			this.groupAdminAllowed = groupAdminAllowed;
		}

		public boolean isGroupAdminAllowed() {
			return groupAdminAllowed;
		}

		public static List<Right> getAllowed(boolean groupAdmin) {
			Stream<Right> stream = Stream.of(Right.values())
					.filter(r -> Right.GROUP_ADMIN != r);
			if (groupAdmin) {
				stream = stream.filter(Right::isGroupAdminAllowed);
			}
			return stream.toList();
		}
	}

	@XmlType(namespace="org.apache.openmeetings.user.type")
	public enum Type {
		USER
		, LDAP
		, OAUTH
		, EXTERNAL
		, CONTACT
	}
	@XmlType(namespace="org.apache.openmeetings.user.salutation")
	public enum Salutation {
		MR(SALUTATION_MR_ID)
		, MS(SALUTATION_MS_ID)
		, MRS(SALUTATION_MRS_ID)
		, DR(SALUTATION_DR_ID)
		, PROF(SALUTATION_PROF_ID);
		private int id;

		Salutation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Salutation get(Long type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Salutation get(Integer type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Salutation get(int type) {
			Salutation rt = Salutation.MR;
			switch (type) {
				case SALUTATION_MS_ID:
					rt = Salutation.MS;
					break;
				case SALUTATION_MRS_ID:
					rt = Salutation.MRS;
					break;
				case SALUTATION_DR_ID:
					rt = Salutation.DR;
					break;
				case SALUTATION_PROF_ID:
					rt = Salutation.PROF;
					break;
				default:
					//no-op
			}
			return rt;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "user_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "age")
	@XmlElement(name = "age", required = false)
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	private LocalDate age;

	@Column(name = "firstname")
	@XmlElement(name = "firstname", required = false)
	private String firstname;

	@Column(name = "lastlogin")
	@XmlTransient
	private Date lastlogin;

	@Column(name = "lastname")
	@XmlElement(name = "lastname", required = false)
	private String lastname;

	@Column(name = "displayName")
	@XmlElement(name = "displayName", required = false)
	private String displayName;

	@Column(name = "login")
	@XmlElement(name = "login", required = false)
	private String login;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "password", length = 1024)
	@LoadFetchGroup(FETCH_GROUP_BACKUP)
	@XmlElement(name = "pass", required = false)
	private String password;

	@Column(name = "regdate")
	@XmlElement(name = "regdate", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date regdate;

	@Column(name = "salutation")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "title_id", required = false)
	@XmlJavaTypeAdapter(SalutationAdapter.class)
	private Salutation salutation;

	@Column(name = "pictureuri")
	@XmlElement(name = "pictureUri", required = false)
	private String pictureUri;

	@Column(name = "language_id")
	@XmlElement(name = "language_id", required = false)
	@XmlJavaTypeAdapter(value = LongAdapter.class, type = long.class)
	private long languageId;

	@Column(name = "resethash")
	@XmlElement(name = "resethash", required = false)
	private String resethash;

	@Column(name = "resetdate")
	@XmlElement(name = "resetDate", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date resetDate;

	@Column(name = "activatehash")
	@XmlElement(name = "activatehash", required = false)
	private String activatehash;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "address", required = false)
	private Address address;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id", insertable = true, updatable = true, nullable = false)
	@ElementDependent
	@XmlElementWrapper(name = "organisations", required = false)
	@XmlElement(name = "user_organisation", required = false)
	private List<GroupUser> groupUsers = new ArrayList<>();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumn(name="sip_user_id", referencedColumnName="id")
	@XmlElement(name = "sipUser", required = false)
	private AsteriskSipUser sipUser;

	// Vars to simulate external Users
	@Column(name = "external_id")
	@XmlElement(name = "externalUserId", required = false)
	private String externalId;

	/**
	 * java.util.TimeZone Id
	 */
	@Column(name = "time_zone_id")
	@XmlElement(name = "timeZoneId", required = false)
	private String timeZoneId;

	@Transient
	@XmlTransient
	private Sessiondata sessionData;

	@Column(name = "user_offers")
	@XmlElement(name = "userOffers", required = false)
	private String userOffers;

	@Column(name = "user_searchs")
	@XmlElement(name = "userSearchs", required = false)
	private String userSearchs;

	@Column(name = "show_contact_data", nullable = false)
	@XmlElement(name = "showContactData", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean showContactData;

	@Column(name = "show_contact_data_to_contacts", nullable = false)
	@XmlElement(name = "showContactDataToContacts", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean showContactDataToContacts;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "type", required = false)
	@XmlJavaTypeAdapter(UserTypeAdapter.class)
	private Type type = Type.USER;

	@Column(name = "owner_id")
	@XmlElement(name = "ownerId", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long ownerId;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "om_right")
	@CollectionTable(name = "om_user_right", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@XmlElementWrapper(name = "rights", required = false)
	@XmlElement(name = "right", required = false)
	@XmlJavaTypeAdapter(UserRightAdapter.class)
	private Set<Right> rights = new HashSet<>();

	@Column(name = "domain_id")
	@XmlElement(name = "domainId", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long domainId; // LDAP config id for LDAP, OAuth server id for OAuth

	@Column(name = "otp_secret")
	@XmlElement(name = "otpSecret", required = false)
	private String otpSecret;

	@Column(name = "otp_recovery", length=350)
	@XmlElement(name = "otpRecovery", required = false)
	private String otpRecoveryCodes;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public LocalDate getAge() {
		return age;
	}

	public void setAge(LocalDate age) {
		this.age = age == null ? LocalDate.now() : age;
	}

	public String getFirstname() {
		return firstname;
	}

	public User setFirstname(String firstname) {
		this.firstname = firstname;
		return this;
	}

	public Date getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}

	public String getLastname() {
		return lastname;
	}

	public User setLastname(String lastname) {
		this.lastname = lastname;
		return this;
	}

	public String getDisplayName() {
		return Strings.isEmpty(displayName) ? generateDisplayName() : displayName;
	}

	public User setDisplayName(String displayName) {
		if (Strings.isEmpty(displayName)) {
			resetDisplayName();
		} else {
			this.displayName = escapeMarkup(displayName).toString();
		}
		return this;
	}

	public void resetDisplayName() {
		displayName = generateDisplayName();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void updatePassword(String pass) throws NoSuchAlgorithmException {
		if (isSipEnabled()) {
			AsteriskSipUser u = getSipUser();
			if (u == null) {
				setSipUser(u = new AsteriskSipUser());
			}
			String defaultRoomContext = getSipContext();
			u.setName(login);
			u.setDefaultuser(login);
			u.setMd5secret(MD5.checksum(login + ":asterisk:" + pass));
			u.setContext(defaultRoomContext);
			u.setHost("dynamic");
		} else {
			setSipUser(null);
		}
		password = CryptProvider.get().hash(pass);
	}

	public String getPassword() {
		return password;
	}

	/**
	 * For internal use only
	 * should not be used directly (for bean usage only)
	 *
	 * @param password - password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getPictureUri() {
		return pictureUri;
	}

	public void setPictureUri(String pictureUri) {
		this.pictureUri = pictureUri;
	}

	public long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	public List<GroupUser> getGroupUsers() {
		return groupUsers;
	}

	public void addGroup(Group g) {
		if (groupUsers == null) {
			groupUsers = new ArrayList<>();
		}
		groupUsers.add(new GroupUser(g, this));
	}

	public void setGroupUsers(List<GroupUser> groupUsers) {
		if (groupUsers != null) {
			this.groupUsers = groupUsers;
		}
	}

	public String getResethash() {
		return resethash;
	}

	public void setResethash(String resethash) {
		this.resethash = resethash;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public String getActivatehash() {
		return activatehash;
	}

	public void setActivatehash(String activatehash) {
		this.activatehash = activatehash;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String externalType() {
		Optional<String> extType = groupUsers == null
				? Optional.empty()
				: groupUsers.stream().filter(gu -> gu.getGroup().isExternal()).findFirst()
				.map(gu -> gu.getGroup().getName());
		return extType.isPresent() ? extType.get() : null;
	}

	public Sessiondata getSessionData() {
		return sessionData;
	}

	public void setSessionData(Sessiondata sessionData) {
		this.sessionData = sessionData;
	}

	public AsteriskSipUser getSipUser() {
		return sipUser;
	}

	public void setSipUser(AsteriskSipUser sipUser) {
		this.sipUser = sipUser;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getUserOffers() {
		return userOffers;
	}

	public void setUserOffers(String userOffers) {
		this.userOffers = userOffers;
	}

	public String getUserSearchs() {
		return userSearchs;
	}

	public void setUserSearchs(String userSearchs) {
		this.userSearchs = userSearchs;
	}

	public boolean isShowContactData() {
		return showContactData;
	}

	public void setShowContactData(boolean showContactData) {
		this.showContactData = showContactData;
	}

	public boolean isShowContactDataToContacts() {
		return showContactDataToContacts;
	}

	public void setShowContactDataToContacts(boolean showContactDataToContacts) {
		this.showContactDataToContacts = showContactDataToContacts;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Long getOwnerId(){
		return ownerId;
	}

	public void setOwnerId(Long ownerId){
		this.ownerId = ownerId;
	}

	public Set<Right> getRights() {
		return rights;
	}

	public void setRights(Set<Right> rights) {
		this.rights = rights;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getOtpSecret() {
		return otpSecret;
	}

	public void setOtpSecret(String otpSecret) {
		this.otpSecret = otpSecret;
	}

	public String getOtpRecoveryCodes() {
		return otpRecoveryCodes;
	}

	public void setOtpRecoveryCodes(String otpRecoveryCodes) {
		this.otpRecoveryCodes = otpRecoveryCodes;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", login=" + login
				+ ", pictureuri=" + pictureUri + ", deleted=" + isDeleted()
				+ ", languageId=" + languageId + ", address=" + address
				+ ", externalId=" + externalId + ", type=" + type + "]";
	}

	private String generateDisplayName() {
		StringJoiner joiner = new StringJoiner(" ")
				.setEmptyValue(DISPLAY_NAME_NA);
		OmLanguage l = LabelDao.getLanguage(languageId);
		String first = l.isRtl() ? lastname : firstname;
		String last = l.isRtl() ? firstname : lastname;
		if (!Strings.isEmpty(first)) {
			joiner.add(first);
		}
		if (!Strings.isEmpty(last)) {
			joiner.add(last);
		}
		if (id != null && joiner.length() == 0) {
			if (Type.CONTACT == type && address != null && !Strings.isEmpty(address.getEmail())) {
				joiner.add(address.getEmail());
			} else {
				joiner.add(login);
			}
		}
		return escapeMarkup(joiner.toString()).toString();
	}
}
