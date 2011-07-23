package org.openmeetings.app.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.openmeetings.app.persistence.beans.adresses.Adresses;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.domain.Organisation_Users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "users")
public class Users implements Serializable {
	

	private static final long serialVersionUID = -2265479712596674065L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name="user_id")
	private Long user_id;
	@Column(name="age")
	private Date age;
	@Column(name="availible")
	private Integer availible;
	@Column(name="firstname")
	private String firstname;
	@Column(name="lastlogin")
	private Date lastlogin;
	@Column(name="lastname")
	private String lastname;
	@Column(name="lasttrans")
	private Long lasttrans;
	@Column(name="level_id")
	private Long level_id;
	@Column(name="login")
	private String login;
	@Column(name="password")
	private String password;
	@Column(name="regdate")
	private Date regdate;
	@Column(name="status")
	private Integer status;
	@Column(name="title_id")
	private Integer title_id;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="pictureuri")
	private String pictureuri;
	@Column(name="deleted")
	private String deleted;
	@Column(name="language_id")
	private Long language_id;
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="adresses_id", insertable=true, updatable=true)
	private Adresses adresses;
	@Column(name="resethash")
	private String resethash;
	@Column(name="activatehash")
	private String activatehash;
	
	@Transient
	private Userlevel userlevel;

	@Transient
	private Userdata rechnungsaddressen;
	@Transient
	private Userdata lieferadressen;
    private Usergroups[] usergroups; 

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
    private Set<Organisation_Users> organisation_users;
    
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userSipDataId", insertable=true, updatable=true)
    private UserSipData userSipData;
    
    //Vars to simulate external Users
	@Column(name="externalUserId")
    private Long externalUserId;
	@Column(name="externalUserType")
    private String externalUserType;
    
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="omtimezoneId", insertable=true, updatable=true)
    private OmTimeZone omTimeZone; //In UTC +/- hours
    
	@Transient
    private Sessiondata sessionData;
	@Column(name="forceTimeZoneCheck")
    private Boolean forceTimeZoneCheck;
    
	@Column(name="user_offers")
    private String userOffers;
	@Column(name="user_searchs")
    private String userSearchs;
	@Column(name="show_contact_data")
    private Boolean showContactData;
	@Column(name="show_contact_data_to_contacts")
    private Boolean showContactDataToContacts;
    
	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}	

	public Adresses getAdresses() {
		return adresses;
	}
	public void setAdresses(Adresses adresses) {
		this.adresses = adresses;
	}
	
	public Date getAge() {
		return age;
	}
	public void setAge(Date age) {
		if(age==null)
			age=new Date();
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

	public String getPassword() {
		return password;
	}
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
    
	public Integer getTitle_id() {
		return title_id;
	}
	public void setTitle_id(Integer title_id) {
		this.title_id = title_id;
	}
	
	public Usergroups[] getUsergroups() {
		return usergroups;
	}
	public void setUsergroups(Usergroups[] usergroups) {
		this.usergroups = usergroups;
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
	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
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
		

	public Set<Organisation_Users> getOrganisation_users() {
		return organisation_users;
	}
	public void setOrganisation_users(Set<Organisation_Users> organisation_users) {
		this.organisation_users = organisation_users;
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

	public Long getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(Long externalUserId) {
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

	public UserSipData getUserSipData() {
		return userSipData;
	}
	public void setUserSipData(UserSipData userSipData) {
		this.userSipData = userSipData;
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
	
	
}
