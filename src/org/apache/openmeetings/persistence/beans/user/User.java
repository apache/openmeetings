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
package org.apache.openmeetings.persistence.beans.user;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.LoadFetchGroup;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.sip.asterisk.AsteriskSipUser;
import org.apache.openmeetings.utils.crypt.MD5;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
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
 @FetchGroup(name = "backupexport",
		 attributes = { @FetchAttribute(name = "password")
 })
})
@NamedQueries({
	@NamedQuery(name = "getUserById", query = "select c from User as c where c.user_id = :user_id"),
	@NamedQuery(name = "checkUserLogin", query = "select c from User as c where c.login = :DataValue AND c.deleted <> :deleted"),
	@NamedQuery(name = "getUserByName", query = "SELECT u FROM User as u "
			+ " where u.login = :login" + " AND u.deleted <> :deleted"),
	@NamedQuery(name = "getUserByEmail", query = "SELECT u FROM User as u "
			+ " where u.adresses.email = :email"
			+ " AND u.deleted <> :deleted"),
	@NamedQuery(name = "getUserByHash", query = "SELECT u FROM User as u "
			+ " where u.resethash = :resethash"
			+ " AND u.deleted <> :deleted"),
	@NamedQuery(name = "selectMaxFromUsersWithSearch", query = "select count(c.user_id) from User c "
			+ "where c.deleted = false " + "AND ("
			+ "lower(c.login) LIKE :search "
			+ "OR lower(c.firstname) LIKE :search "
			+ "OR lower(c.lastname) LIKE :search )"),
	@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u"),
	@NamedQuery(name = "checkPassword", query = "select count(c.user_id) from User c "
			+ "where c.deleted = false " //
			+ "AND c.user_id = :userId " //
			+ "AND c.password LIKE :password"), //
	@NamedQuery(name = "updatePassword", query = "UPDATE User u " //
			+ "SET u.password = :password " //
			+ "WHERE u.user_id = :userId"), //
	@NamedQuery(name = "getNondeletedUsers", query = "SELECT u FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "countNondeletedUsers", query = "SELECT COUNT(u) FROM User u WHERE u.deleted = false"),
	@NamedQuery(name = "getUsersByOrganisationId", query = "SELECT u FROM User u WHERE u.deleted = false AND u.organisation_users.organisation.organisation_id = :organisation_id") 
})
@Table(name = "om_user")
@Root(name = "user")
public class User implements Serializable, IDataProviderEntity {

	private static final long serialVersionUID = -2265479712596674065L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true)
	private Long user_id;

	@Column(name = "age")
	@Element(data = true)
	private Date age;

	@Column(name = "availible")
	@Element(data = true, required = false)
	private Integer availible;

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

	@Column(name = "level_id")
	@Element(data = true, required = false)
	private Long level_id;

	@Column(name = "login")
	@Element(data = true)
	private String login;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "password")
	@LoadFetchGroup("backupexport")
	@Element(name = "pass", data = true, required = false)
	private String password;

	@Column(name = "regdate")
	@Element(data = true)
	private Date regdate;

	@Column(name = "status")
	@Element(data = true, required = false)
	private Integer status;

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
	@Element(data = true)
	private boolean deleted;

	@Column(name = "language_id")
	@Element(data = true, required = false)
	private Long language_id;

	@Column(name = "resethash")
	@Element(data = true, required = false)
	private String resethash;

	@Column(name = "activatehash")
	@Element(data = true, required = false)
	private String activatehash;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "adresses_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(name = "address", required = false)
	private Address adresses;

	@Transient
	private Userlevel userlevel;

	@Transient
	private Userdata rechnungsaddressen;

	@Transient
	private Userdata lieferadressen;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	@ElementList(name = "organisations")
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "omtimezoneId", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private OmTimeZone omTimeZone; // In UTC +/- hours

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

	public void setAdresses(String street, String zip, String town,
			State state, String additionalname, String comment, String fax,
			String phone, String email) {
		if (this.adresses == null) {
			this.adresses = new Address();
		}
		this.adresses.setStreet(street);
		this.adresses.setZip(zip);
		this.adresses.setTown(town);
		this.adresses.setStates(state);
		this.adresses.setAdditionalname(additionalname);
		this.adresses.setComment(comment);
		this.adresses.setFax(fax);
		this.adresses.setPhone(phone);
		this.adresses.setEmail(email);
	}

	public Date getAge() {
		return age;
	}

	public void setAge(Date age) {
		if (age == null)
			age = new Date();
		this.age = age;
	}

	public Integer getAvailible() {
		return availible;
	}

	public void setAvailible(Integer availible) {
		this.availible = availible;
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

	public Long getLevel_id() {
		return level_id;
	}

	public void setLevel_id(Long level_id) {
		this.level_id = level_id;
	}

	public Userdata getLieferadressen() {
		return lieferadressen;
	}

	public void setLieferadressen(Userdata lieferadressen) {
		this.lieferadressen = lieferadressen;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void updatePassword(ManageCryptStyle crypt, ConfigurationDao configDao, String pass) throws NoSuchAlgorithmException {
		updatePassword(crypt, configDao, pass, false);
	}
	
	public void updatePassword(ManageCryptStyle crypt, ConfigurationDao configDao, String pass, boolean empty) throws NoSuchAlgorithmException {
		if (!empty) {
			Integer userPassMinimumLength = configDao.getConfValue("user.pass.minimum.length", Integer.class, "4");
	
			if (userPassMinimumLength == null) {
				throw new RuntimeException("user.pass.minimum.length problem");
			}
			if (pass == null || pass.length() < userPassMinimumLength) {
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
		password = crypt.getInstanceOfCrypt().createPassPhrase(pass);
	}
	
	public String getPassword() {
		return password;
	}

	@Deprecated //should not be used directly (for bean usage only)
	public void setPassword(String password) {
		this.password = password;
	}

	public Userdata getRechnungsaddressen() {
		return rechnungsaddressen;
	}

	public void setRechnungsaddressen(Userdata rechnungsaddressen) {
		this.rechnungsaddressen = rechnungsaddressen;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getSalutations_id() {
		return salutations_id;
	}

	public void setSalutations_id(Long salutations_id) {
		this.salutations_id = salutations_id;
	}

	public Userlevel getUserlevel() {
		return userlevel;
	}

	public void setUserlevel(Userlevel userlevel) {
		this.userlevel = userlevel;
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
		return organisation_users;
	}

	public void setOrganisation_users(
			List<Organisation_Users> organisation_users) {
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

	public OmTimeZone getOmTimeZone() {
		return omTimeZone;
	}

	public void setOmTimeZone(OmTimeZone omTimeZone) {
		this.omTimeZone = omTimeZone;
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

}
