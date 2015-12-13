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

import static org.apache.openmeetings.db.util.UserHelper.invalidPassword;

import java.io.Serializable;
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
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.util.crypt.MD5;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
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
	, @FetchGroup(name = "orgUsers", attributes = { @FetchAttribute(name = "organisation_users")})
})
@NamedQueries({
	@NamedQuery(name = "getUserById", query = "SELECT u FROM User u WHERE u.user_id = :id"),
	@NamedQuery(name = "getUsersByIds", query = "select c from User c where c.user_id IN :ids"),
	@NamedQuery(name = "getUserByLogin", query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.login = :login AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))"),
	@NamedQuery(name = "getUserByEmail", query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.adresses.email = :email AND ((:domainId = 0 AND u.domainId IS NULL) OR (:domainId > 0 AND u.domainId = :domainId))"),
	@NamedQuery(name = "getUserByHash",  query = "SELECT u FROM User u WHERE u.deleted = false AND u.type = :type AND u.resethash = :resethash"),
	@NamedQuery(name = "getContactByEmailAndUser", query = "SELECT u FROM User u WHERE u.deleted = false AND u.adresses.email = :email AND u.type = :type AND u.ownerId = :ownerId"), 
	@NamedQuery(name = "selectMaxFromUsersWithSearch", query = "select count(c.user_id) from User c "
			+ "where c.deleted = false " + "AND ("
			+ "lower(c.login) LIKE :search "
			+ "OR lower(c.firstname) LIKE :search "
			+ "OR lower(c.lastname) LIKE :search )"),
	@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u ORDER BY u.user_id"),
	@NamedQuery(name = "checkPassword", query = "select count(c.user_id) from User c "
			+ "where c.deleted = false " //
			+ "AND c.user_id = :userId " //
			+ "AND c.password LIKE :password"), //
	@NamedQuery(name = "updatePassword", query = "UPDATE User u SET u.password = :password WHERE u.user_id = :userId"), //
	@NamedQuery(name = "getNondeletedUsers", query = "SELECT u FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "countNondeletedUsers", query = "SELECT COUNT(u) FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "getUsersByOrganisationId", query = "SELECT u FROM User u WHERE u.deleted = false AND u.organisation_users.organisation.organisation_id = :organisation_id"), 
	@NamedQuery(name = "getExternalUser", query = "SELECT u FROM User u WHERE u.deleted = false AND u.externalUserId LIKE :externalId AND u.externalUserType LIKE :externalType"),
	@NamedQuery(name = "getUserByLoginOrEmail", query = "SELECT u from User u WHERE u.deleted = false AND u.type = :type AND (u.login = :userOrEmail OR u.adresses.email = :userOrEmail)")
})
@Table(name = "om_user")
@Root(name = "user")
public class User implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	
	@XmlType(namespace="org.apache.openmeetings.user.user.right")
	public enum Right {
		Admin			// access to Admin module
		, Room			// enter the room
		, Dashboard		// access the dashboard
		, Login			// login to Om internal DB
		, Soap			// use rest/soap calls
	}
	
	@XmlType(namespace="org.apache.openmeetings.user.user.type")
	public enum Type {
		user
		, ldap
		, oauth
		, external
		, contact
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "user_id")
	private Long user_id;

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

	@Column(name = "lasttrans")
	@Element(data = true, required = false)
	private Long lasttrans;

	@Column(name = "login")
	@Element(data = true, required = false)
	private String login;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "password")
	@LoadFetchGroup("backupexport")
	@Element(name = "pass", data = true, required = false)
	private String password;

	@Column(name = "regdate")
	@Element(data = true, required = false)
	private Date regdate;

	@Column(name = "salutations_id")
	@Element(name = "title_id", data = true, required = false)
	private Long salutations_id;

	@Column(name = "starttime")
	private Date starttime;

	@Column(name = "updatetime")
	private Date updatetime;

	@Column(name = "pictureuri")
	@Element(data = true, required = false)
	private String pictureuri;

	@Column(name = "deleted")
	@Element(data = true, required = false)
	private boolean deleted;

	@Column(name = "language_id")
	@Element(name = "language_id", data = true, required = false)
	private Long language_id;

	@Column(name = "resethash")
	@Element(data = true, required = false)
	private String resethash;

	@Column(name = "activatehash")
	@Element(data = true, required = false)
	private String activatehash;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "adresses_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(name = "address", required = false)
	private Address adresses;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	@ElementList(name = "organisations", required = false)
	@ElementDependent
	private List<Organisation_Users> organisation_users = new ArrayList<Organisation_Users>();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn(name="sip_user_id", referencedColumnName="id")
	@Element(name = "sipUser", required = false)
	private AsteriskSipUser sipUser;

	// Vars to simulate external Users
	@Column(name = "externalUserId")
	@Element(data = true, required = false)
	private String externalUserId;

	@Column(name = "externalUserType")
	@Element(data = true, required = false)
	private String externalUserType;

	/**
	 * java.util.TimeZone Id
	 */
	@Column(name = "time_zone_id")
	@Element(data = true, required = false)
	private String timeZoneId;

	@Transient
	private Sessiondata sessionData;

	@Column(name = "forceTimeZoneCheck")
	@Element(data = true, required = false)
	private Boolean forceTimeZoneCheck;

	@Column(name = "sendSMS")
	@Element(data = false, required = false)
	private boolean sendSMS;

	@Column(name = "user_offers")
	@Element(data = true, required = false)
	private String userOffers;

	@Column(name = "user_searchs")
	@Element(data = true, required = false)
	private String userSearchs;

	@Column(name = "show_contact_data")
	@Element(data = true, required = false)
	private Boolean showContactData;

	@Column(name = "show_contact_data_to_contacts")
	@Element(data = true, required = false)
	private Boolean showContactDataToContacts;

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
	private Set<Right> rights = new HashSet<User.Right>();
	
	@Column(name = "domain_id")
	@Element(data = true, required = false)
	private Long domainId; // LDAP config id for LDAP, OAuth server id for OAuth
	
	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Address getAdresses() {
		return adresses;
	}

	public void setAdresses(Address adresses) {
		this.adresses = adresses;
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

	public void setFirstname(String firstname) {
		this.firstname = firstname;
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

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Long getLasttrans() {
		return lasttrans;
	}

	public void setLasttrans(Long lasttrans) {
		this.lasttrans = lasttrans;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void updatePassword(ConfigurationDao configDao, String pass) throws NoSuchAlgorithmException {
		updatePassword(configDao, pass, false);
	}
	
	public void updatePassword(ConfigurationDao configDao, String pass, boolean empty) throws NoSuchAlgorithmException {
		if (!empty) {
			if (invalidPassword(pass, configDao)) {
				throw new RuntimeException("Password of invalid length is provided");
			}
		}
		String sipEnabled = configDao.getConfValue("red5sip.enable", String.class, "no");
        if("yes".equals(sipEnabled)) {
        	if (getSipUser() == null) {
        		setSipUser(new AsteriskSipUser());
        	}
        	AsteriskSipUser u = getSipUser();
        	String defaultRoomContext = configDao.getConfValue("red5sip.exten_context", String.class, "rooms");
        	u.setName(login);
        	u.setDefaultuser(login);
        	u.setMd5secret(MD5.do_checksum(login + ":asterisk:" + pass));
        	u.setContext(defaultRoomContext);
        	u.setHost("dynamic");
        } else {
        	setSipUser(null);
        }
		password = ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(pass);
	}
	
	public String getPassword() {
		return password;
	}

	@Deprecated //should not be used directly (for bean usage only)
	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public Long getSalutations_id() {
		return salutations_id;
	}

	public void setSalutations_id(Long salutations_id) {
		this.salutations_id = salutations_id;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getPictureuri() {
		return pictureuri;
	}

	public void setPictureuri(String pictureuri) {
		this.pictureuri = pictureuri;
	}

	public Long getLanguage_id() {
		return language_id;
	}

	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	public List<Organisation_Users> getOrganisation_users() {
		if (organisation_users == null) {
			organisation_users = new ArrayList<Organisation_Users>();
		}
		return organisation_users;
	}

	public void setOrganisation_users(List<Organisation_Users> organisation_users) {
		if (organisation_users != null) {
			this.organisation_users = organisation_users;
		}
	}

	public String getResethash() {
		return resethash;
	}

	public void setResethash(String resethash) {
		this.resethash = resethash;
	}

	public String getActivatehash() {
		return activatehash;
	}

	public void setActivatehash(String activatehash) {
		this.activatehash = activatehash;
	}

	public String getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getExternalUserType() {
		return externalUserType;
	}

	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
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

	public Boolean getForceTimeZoneCheck() {
		return forceTimeZoneCheck;
	}

	public void setForceTimeZoneCheck(Boolean forceTimeZoneCheck) {
		this.forceTimeZoneCheck = forceTimeZoneCheck;
	}

	public boolean getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(boolean sendSMS) {
		this.sendSMS = sendSMS;
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

	public Boolean getShowContactData() {
		return showContactData;
	}

	public void setShowContactData(Boolean showContactData) {
		this.showContactData = showContactData;
	}

	public Boolean getShowContactDataToContacts() {
		return showContactDataToContacts;
	}

	public void setShowContactDataToContacts(Boolean showContactDataToContacts) {
		this.showContactDataToContacts = showContactDataToContacts;
	}

	public String getPhoneForSMS() {
		return getSendSMS() ? getAdresses().getPhone() : "";
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
		return "User [id=" + user_id + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", login=" + login
				+ ", pictureuri=" + pictureuri + ", deleted=" + deleted
				+ ", language_id=" + language_id + ", adresses=" + adresses
				+ ", externalId=" + externalUserId + ", externalType=" + externalUserType
				+ ", type=" + type + "]";
	}
}
