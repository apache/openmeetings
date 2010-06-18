package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;

/**
 * 
 * @hibernate.class table="users"
 * lazy="false"
 *
 */
public class Users {
	
	private Long user_id;
	private Date age;
	private Integer availible;
	private String firstname;
	private Date lastlogin;
	private String lastname;
	private Long lasttrans;
	private Long level_id;
	private String login;
	private String password;
	private Date regdate;
	private Integer status;
	private Integer title_id;
	private Date starttime;
	private Date updatetime;
	private String pictureuri;
	private String deleted;
	private Long language_id;
	private Adresses adresses;
	private String resethash;
	private String activatehash;
	
	private Userlevel userlevel;

	private Userdata rechnungsaddressen;
	private Userdata lieferadressen;
    private Usergroups[] usergroups; 
    
    private Set<Organisation_Users> organisation_users;
    
    private UserSipData userSipData;
    
    //Vars to simulate external Users
    private Long externalUserId;
    private String externalUserType;
    
    private Sessiondata sessionData;
    
	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    /**
     * 
     * @hibernate.id
     *  column="user_id"
     *  generator-class="increment"
     */ 	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}	

    /**
	 * @hibernate.many-to-one
	 * column = "adresses_id"
	 * class = "org.openmeetings.app.hibernate.beans.adresses.Adresses"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */		
	public Adresses getAdresses() {
		return adresses;
	}
	public void setAdresses(Adresses adresses) {
		this.adresses = adresses;
	}
	
    /**
     * @hibernate.property
     *  column="age"
     *  type="java.util.Date"
     */  
	public Date getAge() {
		return age;
	}
	public void setAge(Date age) {
		if(age==null)
			age=new Date();
		this.age = age;
	}
    
    /**
     * @hibernate.property
     *  column="availible"
     *  type="int"
     */  
	public Integer getAvailible() {
		return availible;
	}
	public void setAvailible(Integer availible) {
		this.availible = availible;
	}

    /**
     * @hibernate.property
     *  column="firstname"
     *  type="string"
     */ 	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

    /**
     * @hibernate.property
     *  column="lastlogin"
     *  type="java.util.Date"
     */ 
	public Date getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}

    /**
     * @hibernate.property
     *  column="lastname"
     *  type="string"
     */ 
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
    
    /**
     * @hibernate.property
     *  column="lasttrans"
     *  type="long"
     */ 
	public Long getLasttrans() {
		return lasttrans;
	}
	public void setLasttrans(Long lasttrans) {
		this.lasttrans = lasttrans;
	}
    
    /**
     * @hibernate.property
     *  column="level_id"
     *  type="long"
     */ 
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

    /**
     * @hibernate.property
     *  column="login"
     *  type="string"
     */ 
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

    /**
     * @hibernate.property
     *  column="password"
     *  type="string"
     */ 	
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
    
    /**
     * @hibernate.property
     *  column="regdate"
     *  type="java.util.Date"
     */ 
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
    
    /**
     * @hibernate.property
     *  column="status"
     *  type="int"
     */ 
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
    
    /**
     * @hibernate.property
     *  column="title_id"
     *  type="int"
     */
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
    
    
    /**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */  	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
    /**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */  	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
    /**
     * @hibernate.property
     *  column="deleted"
     *  type="string"
     */	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

    /**
     * @hibernate.property
     *  column="pictureuri"
     *  type="string"
     */	
	public String getPictureuri() {
		return pictureuri;
	}
	public void setPictureuri(String pictureuri) {
		this.pictureuri = pictureuri;
	}

    /**
     * @hibernate.property
     *  column="language_id"
     *  type="long"
     */	
	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}
		

    /**
     * @hibernate.set 
     * table = "organisation_users" 
     * inverse = "true" 
     * where ="deleted = 'false'"
     * lazy = "false"
     * cascade = "none"
     * @hibernate.one-to-many 
     * class = "org.openmeetings.app.hibernate.beans.domain.Organisation_Users"
     * @hibernate.key 
     * column = "user_id"
     */	
	public Set<Organisation_Users> getOrganisation_users() {
		return organisation_users;
	}
	public void setOrganisation_users(Set<Organisation_Users> organisation_users) {
		this.organisation_users = organisation_users;
	}

    /**
     * @hibernate.property
     *  column="resethash"
     *  type="string"
     */	
	public String getResethash() {
		return resethash;
	}
	public void setResethash(String resethash) {
		this.resethash = resethash;
	}

	/**
     * @hibernate.property
     *  column="activatehash"
     *  type="string"
     */
	public String getActivatehash() {
		return activatehash;
	}
	public void setActivatehash(String activatehash) {
		this.activatehash = activatehash;
	}

	/**
     * @hibernate.property
     *  column="externalUserId"
     *  type="long"
     */
	public Long getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(Long externalUserId) {
		this.externalUserId = externalUserId;
	}

	/**
     * @hibernate.property
     *  column="externalUserType"
     *  type="string"
     */
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

    /**
	 * @hibernate.many-to-one
	 * column = "userSipDataId"
	 * class = "org.openmeetings.app.hibernate.beans.user.UserSipData"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */	
	public UserSipData getUserSipData() {
		return userSipData;
	}
	public void setUserSipData(UserSipData userSipData) {
		this.userSipData = userSipData;
	}
	
}
