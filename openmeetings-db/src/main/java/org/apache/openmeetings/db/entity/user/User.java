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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getSipContext;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;
import static org.apache.wicket.util.string.Strings.escapeMarkup;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlType;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.LoadFetchGroup;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.crypt.MD5;
import org.apache.wicket.util.string.Strings;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Entity to store user data, password field is {@link FetchType#LAZY}, so that
 * is why there is an extra udpate statement at this moment
 *
 * @author sebawagner, solomax
 *
 */
@Entity
@FetchGroups({
	@FetchGroup(name = "backupexport", attributes = { @FetchAttribute(name = "password") })
	, @FetchGroup(name = "groupUsers", attributes = { @FetchAttribute(name = "groupUsers")})
})
@NamedQueries({
	@NamedQuery(name = "getUserById", query = "SELECT u FROM User u WHERE u.id = :id"),
	@NamedQuery(name = "getUsersByIds", query = "select c from User c where c.id IN :ids"),
	@NamedQuery(name = "getUserByLogin", query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.login = :login AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))"),
	@NamedQuery(name = "getUserByEmail", query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.address.email = :email AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))"),
	@NamedQuery(name = "getUserByHash",  query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.resethash = :resethash"),
	@NamedQuery(name = "getUserByExpiredHash",  query = "SELECT u FROM User u WHERE u.resetDate < :date"),
	@NamedQuery(name = "getContactByEmailAndUser", query = "SELECT u FROM User u WHERE u.deleted = false AND u.address.email = :email AND u.type = :type AND u.ownerId = :ownerId"),
	@NamedQuery(name = "selectMaxFromUsersWithSearch", query = "select count(c.id) from User c "
			+ "where c.deleted = false " + "AND ("
			+ "lower(c.login) LIKE :search "
			+ "OR lower(c.firstname) LIKE :search "
			+ "OR lower(c.lastname) LIKE :search )"),
	@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u ORDER BY u.id"),
	@NamedQuery(name = "getPassword", query = "SELECT u.password FROM User u WHERE u.deleted = false AND u.id = :userId "),
	@NamedQuery(name = "updatePassword", query = "UPDATE User u SET u.password = :password WHERE u.id = :userId"), //
	@NamedQuery(name = "getNondeletedUsers", query = "SELECT u FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "countNondeletedUsers", query = "SELECT COUNT(u) FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "getUsersByGroupId", query = "SELECT u FROM User u WHERE u.deleted = false AND u.groupUsers.group.id = :groupId"),
	@NamedQuery(name = "getExternalUser", query = "SELECT u FROM User u WHERE u.deleted = false AND u.externalId LIKE :externalId AND u.externalType LIKE :externalType"),
	@NamedQuery(name = "getUserByLoginOrEmail", query = "SELECT u from User u WHERE u.deleted = false AND u.type = :type AND (u.login = :userOrEmail OR u.address.email = :userOrEmail)")
})
@Table(name = "om_user")
@Root(name = "user")
public class User extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final int SALUTATION_MR_ID = 1;
	public static final int SALUTATION_MS_ID = 2;
	public static final int SALUTATION_MRS_ID = 3;
	public static final int SALUTATION_DR_ID = 4;
	public static final int SALUTATION_PROF_ID = 5;

	@XmlType(namespace="org.apache.openmeetings.user.right")
	public enum Right {
		Admin			// access to Admin module
		, GroupAdmin	// partial access to Admin module (should not be directly assigned)
		, Room			// enter the room
		, Dashboard		// access the dashboard
		, Login			// login to Om internal DB
		, Soap			// use rest/soap calls
	}

	@XmlType(namespace="org.apache.openmeetings.user.type")
	public enum Type {
		user
		, ldap
		, oauth
		, external
		, contact
	}
	@XmlType(namespace="org.apache.openmeetings.user.salutation")
	public enum Salutation {
		mr(SALUTATION_MR_ID)
		, ms(SALUTATION_MS_ID)
		, mrs(SALUTATION_MRS_ID)
		, dr(SALUTATION_DR_ID)
		, prof(SALUTATION_PROF_ID);
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
			Salutation rt = Salutation.mr;
			switch (type) {
				case SALUTATION_MS_ID:
					rt = Salutation.ms;
					break;
				case SALUTATION_MRS_ID:
					rt = Salutation.mrs;
					break;
				case SALUTATION_DR_ID:
					rt = Salutation.dr;
					break;
				case SALUTATION_PROF_ID:
					rt = Salutation.prof;
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
	@Element(data = true, name = "user_id")
	private Long id;

	@Column(name = "age")
	@Element(data = true, required = false)
	private Date age;

	@Column(name = "firstname")
	@Element(data = true, required = false)
	private String firstname;

	@Column(name = "lastlogin")
	private Date lastlogin;

	@Column(name = "lastname")
	@Element(data = true, required = false)
	private String lastname;

	@Column(name = "displayName")
	@Element(data = true, required = false)
	private String displayName;

	@Column(name = "login")
	@Element(data = true, required = false)
	private String login;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "password", length = 1024)
	@LoadFetchGroup("backupexport")
	@Element(name = "pass", data = true, required = false)
	private String password;

	@Column(name = "regdate")
	@Element(data = true, required = false)
	private Date regdate;

	@Column(name = "salutation")
	@Enumerated(EnumType.STRING)
	@Element(name = "title_id", data = true, required = false)
	private Salutation salutation;

	@Column(name = "pictureuri")
	@Element(data = true, required = false)
	private String pictureUri;

	@Column(name = "language_id")
	@Element(name = "language_id", data = true, required = false)
	private long languageId;

	@Column(name = "resethash")
	@Element(data = true, required = false)
	private String resethash;

	@Column(name = "resetdate")
	@Element(data = true, required = false)
	private Date resetDate;

	@Column(name = "activatehash")
	@Element(data = true, required = false)
	private String activatehash;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(name = "address", required = false)
	private Address address;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id", insertable = true, updatable = true, nullable = false)
	@ElementList(name = "organisations", required = false)
	@ElementDependent
	private List<GroupUser> groupUsers = new ArrayList<>();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumn(name="sip_user_id", referencedColumnName="id")
	@Element(name = "sipUser", required = false)
	private AsteriskSipUser sipUser;

	// Vars to simulate external Users
	@Column(name = "external_id")
	@Element(name = "externalUserId", data = true, required = false)
	private String externalId;

	@Column(name = "external_type")
	@Element(name = "externalUserType", data = true, required = false)
	private String externalType;

	/**
	 * java.util.TimeZone Id
	 */
	@Column(name = "time_zone_id")
	@Element(data = true, required = false)
	private String timeZoneId;

	@Transient
	private Sessiondata sessionData;

	@Column(name = "forceTimeZoneCheck", nullable = false)
	@Element(data = true, required = false)
	private boolean forceTimeZoneCheck;

	@Column(name = "user_offers")
	@Element(data = true, required = false)
	private String userOffers;

	@Column(name = "user_searchs")
	@Element(data = true, required = false)
	private String userSearchs;

	@Column(name = "show_contact_data", nullable = false)
	@Element(data = true, required = false)
	private boolean showContactData;

	@Column(name = "show_contact_data_to_contacts", nullable = false)
	@Element(data = true, required = false)
	private boolean showContactDataToContacts;

	@Column(name = "type")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private Type type = Type.user;

	@Column(name = "owner_id")
	@Element(data = true, required = false)
	private Long ownerId;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "om_right")
	@CollectionTable(name = "om_user_right", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@ElementList(name="rights", data = true, required = false)
	private Set<Right> rights = new HashSet<>();

	@Column(name = "domain_id")
	@Element(data = true, required = false)
	private Long domainId; // LDAP config id for LDAP, OAuth server id for OAuth

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

	public Date getAge() {
		return age;
	}

	public void setAge(Date age) {
		this.age = age == null ? new Date() :age;
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
		if (!Strings.isEmpty(displayName)) {
			this.displayName = escapeMarkup(displayName).toString();
		}
		return this;
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
	 * @deprecated should not be used directly (for bean usage only)
	 *
	 * @param password - password to set
	 */
	@Deprecated
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

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
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

	public boolean getForceTimeZoneCheck() {
		return forceTimeZoneCheck;
	}

	public void setForceTimeZoneCheck(boolean forceTimeZoneCheck) {
		this.forceTimeZoneCheck = forceTimeZoneCheck;
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

	@Override
	public String toString() {
		return "User [id=" + id + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", login=" + login
				+ ", pictureuri=" + pictureUri + ", deleted=" + isDeleted()
				+ ", languageId=" + languageId + ", address=" + address
				+ ", externalId=" + externalId + ", externalType=" + externalType
				+ ", type=" + type + "]";
	}

	private String generateDisplayName() {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		OmLanguage l = LabelDao.getLanguage(languageId);
		String first = l.isRtl() ? getLastname() : getFirstname();
		String last = l.isRtl() ? getFirstname() : getLastname();
		if (!Strings.isEmpty(first)) {
			sb.append(first);
			delim = " ";
		}
		if (!Strings.isEmpty(last)) {
			sb.append(delim).append(last);
		}
		if (Strings.isEmpty(sb) && address != null && !Strings.isEmpty(address.getEmail())) {
			sb.append(delim).append(address.getEmail());
		}
		if (Strings.isEmpty(sb)) {
			sb.append("N/A");
		}
		return escapeMarkup(sb).toString();
	}
}
