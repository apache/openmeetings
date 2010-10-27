package org.openmeetings.app.remote;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Addressmanagement;
import org.openmeetings.app.data.user.Emailmanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.PrivateMessageFolderDaoImpl;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.PrivateMessageFolder;
import org.openmeetings.app.hibernate.beans.user.PrivateMessages;
import org.openmeetings.app.hibernate.beans.user.UserContacts;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.templates.RequestContactConfirmTemplate;
import org.openmeetings.app.templates.RequestContactTemplate;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.MailHandler;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.IScope;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

/**
 * 
 * @author swagner
 *
 */
public class UserService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(UserService.class, ScopeApplicationAdapter.webAppRootKey);	
	
	//Spring Beans
	private ClientListManager clientListManager = null;
	private ScopeApplicationAdapter scopeApplicationAdapter = null;
	
	public ClientListManager getClientListManager() {
		return clientListManager;
	}
	public void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}
	public ScopeApplicationAdapter getScopeApplicationAdapter() {
		return scopeApplicationAdapter;
	}
	public void setScopeApplicationAdapter(
			ScopeApplicationAdapter scopeApplicationAdapter) {
		this.scopeApplicationAdapter = scopeApplicationAdapter;
	}
	
	/**
	 * get your own user-object
	 * @param SID
	 * @param user_id
	 * @return
	 */
	public Users getUserSelf(String SID){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        return UsersDaoImpl.getInstance().getUser(users_id);
	}
	
	public Long resetUserPwd(String SID, String email, String login, String applink){
		Sessionmanagement.getInstance().checkSession(SID);
		return Usermanagement.getInstance().resetUser(email, login, applink);
	}
	
	public Object getUserByHash(String SID, String hash) {
		Sessionmanagement.getInstance().checkSession(SID);
		return UsersDaoImpl.getInstance().getUserByHash(hash);
	}
	
	public Object resetPassByHash(String SID, String hash, String pass) {
		Sessionmanagement.getInstance().checkSession(SID);
		return UsersDaoImpl.getInstance().resetPassByHash(hash,pass);
	}
	
	/**
	 * get user by id, admin only
	 * @param SID
	 * @param user_id
	 * @return
	 */
	public Users getUserById(String SID, long user_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Usermanagement.getInstance().checkAdmingetUserById(user_level,user_id);
	}

	/**
	 * refreshes the current SID
	 * @param SID
	 * @return
	 */
	public String refreshSession(String SID){
		try {
	        Sessionmanagement.getInstance().checkSession(SID);
	        return "ok";
		} catch (Exception err) {
			log.error("[refreshSession]",err);
		}
		return "error";
	}
	
	/**
	 * get all availible Salutations
	 * @param SID
	 * @return
	 */
	public List getUserSalutations(String SID, long language_id){
        return Salutationmanagement.getInstance().getUserSalutations(language_id);
	}
	
	/**
	 * 
	 * @param SID
	 * @param searchcriteria login,lastname,firstname,user_id
	 * @param searchstring
	 * @param max
	 * @param start
	 * @param orderby login,lastname,firstname,user_id
	 * @param asc
	 * @return
	 */
    public List searchUser(String SID, String searchcriteria ,String searchstring, int max, int start, String orderby, boolean asc){   	
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);    	
    	return Usermanagement.getInstance().searchUser(user_level,searchcriteria,searchstring,max,start,orderby,asc);
    }    
    
    /**
     * get a list of all users of an organisation
     * @param SID
     * @param organisation_id
     * @param start
     * @param max
     * @param orderby
     * @param asc
     * @return
     */
    public List getOrgUserList(String SID, long organisation_id, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Organisationmanagement.getInstance().getUsersByOrganisationId(organisation_id, start, max, orderby, asc);
    }
    
    public List getUserListByModForm(String SID){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Usermanagement.getInstance().getUserByMod(user_level, users_id);
    }
    
    /**
     * gat a list of all organisations of an user
     * @param SID
     * @param client_user
     * @param start
     * @param max
     * @param orderby
     * @param asc
     * @return
     */
    public List getOrganisationListByUser(String SID, long client_user, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Organisationmanagement.getInstance().getOrganisationsByUserId(user_level, client_user, start, max, orderby, asc);
    }    
    
    /**
     * gets a list of all not assigned organisations of a user
     * @param SID
     * @param client_user
     * @param start
     * @param max
     * @param orderby
     * @param asc
     * @return
     */
    public List getRestOrganisationListByUser(String SID, long client_user, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Organisationmanagement.getInstance().getRestOrganisationsByUserId(user_level, client_user, start, max, orderby, asc);
    }
    
    
    /**
     * gets a hole user-list(admin-role only)
     * @param SID
     * @param start
     * @param max
     * @param orderby
     * @return
     */
    public SearchResult getUserList(String SID, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Usermanagement.getInstance().getUsersList(user_level, start, max, orderby, asc);
    }
    
    
    public SearchResult getUserListWithSearch(String SID, int start, int max, 
    		String orderby, boolean asc, String search){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Usermanagement.getInstance().getUsersListWithSearch(user_level, start, max, orderby, asc, search);
    }
    
    /**
     * gets a user-list by search criteria
     * @param SID
     * @param search
     * @param start
     * @param max
     * @param orderby
     * @return
     */
    public SearchResult getAllUserBySearchRange(String SID, String search, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Usermanagement.getInstance().getAllUserByRange(search, start, max, orderby, asc);
    }
    
    /**
     * updates the user profile, every user can update his own profile
     * @param SID
     * @param argObject
     * @return user_id or NULL or negativ value (error_id)
     */
    public Long updateUserSelfSmall(String SID, ObjectMap values ){
    	try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if(user_level!=null && user_level>=1){
	        	return Usermanagement.getInstance().saveOrUpdateUser(new Long(3),values, users_id);
	        } else {
	            return new Long(-2);
	        }
    	} catch (Exception err){
    		log.error("[updateUserSelfSmall] "+err);
    		return new Long(-1);
    	}
    }    
    
    /**
     * 
     * @param SID
     * @param regObjectObj
     * @return
     */
    public Long saveOrUpdateUser(String SID, Object regObjectObj){
    	try {
    		LinkedHashMap argObjectMap = (LinkedHashMap) regObjectObj;
    		Long user_idClient = null;
    		if (argObjectMap.get("user_idClient")!=null){
    			user_idClient = Long.valueOf(argObjectMap.get("user_idClient").toString()).longValue();
    		}
    		//log.error("saveOrUpdateUser1: ");
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        //log.error("saveOrUpdateUser2: ");
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);	
	        
//	        log.error("saveOrUpdateUser1: "+argObjectMap.get("organisations"));
//	        log.error("saveOrUpdateUser2: "+argObjectMap.get("organisations").getClass());
//	        log.error("saveOrUpdateUser3: "+argObjectMap.get("user_idClient"));
	        //TODO: there is a BUG here: value send is Time as GMT but here it gets CEST which is wrong	  
	        //but maybe a OS-related-issue
	        //log.error("saveOrUpdateUser4: "+argObjectMap.get("userage"));
	        //log.error("saveOrUpdateUser5: "+argObjectMap.get("userage").getClass());
	        
	        List organisations = (List) argObjectMap.get("organisations");
	        Date age = null;
	        if (argObjectMap.get("userage") instanceof Date){
	        	age = (Date) argObjectMap.get("userage");
	        	log.error("saveOrUpdateUser7: "+age.getTimezoneOffset());
	        }	   
	        
	        
	        log.debug("Mail : " + argObjectMap.get("email").toString());
	        log.debug("Phone : " + argObjectMap.get("phone").toString());

	        //log.error("saveOrUpdateUser6: "+age);
	       
    		if (user_idClient==null || user_idClient==0){
	        	return Usermanagement.getInstance().registerUserInit(user_level, 
	        			Long.valueOf(argObjectMap.get("level_id").toString()).longValue(), 
	        			Integer.valueOf(argObjectMap.get("availible").toString()).intValue(),Integer.valueOf(argObjectMap.get("status").toString()).intValue(),
	        			argObjectMap.get("login").toString(), argObjectMap.get("password").toString(), 
	        			argObjectMap.get("lastname").toString(), argObjectMap.get("firstname").toString(), 
	        			argObjectMap.get("email").toString(), age, 
	        			argObjectMap.get("street").toString(), argObjectMap.get("additionalname").toString(), 
	        			argObjectMap.get("fax").toString(), argObjectMap.get("zip").toString(), 
	        			Long.valueOf(argObjectMap.get("states_id").toString()).longValue(), argObjectMap.get("town").toString(), 
	        			0,
	        			true,
	        			organisations, argObjectMap.get("phone").toString(),
	        			"",false,
		        		argObjectMap.get("sip_user").toString(),
		        		argObjectMap.get("sip_pass").toString(),
		        		argObjectMap.get("sip_auth").toString(),
		        		Boolean.valueOf(argObjectMap.get("generateSipUserData").toString()).booleanValue(),
		        		argObjectMap.get("jNameTimeZone").toString(),
		        		Boolean.valueOf(argObjectMap.get("forceTimeZoneCheck").toString()).booleanValue(),
		        		argObjectMap.get("userOffers").toString(),
		        		argObjectMap.get("userSearchs").toString(),
		        		Boolean.valueOf(argObjectMap.get("showContactData").toString()).booleanValue(),
		        		Boolean.valueOf(argObjectMap.get("showContactDataToContacts").toString()).booleanValue()
		        		); 	
    		} else {
		        return Usermanagement.getInstance().updateUser(user_level,user_idClient, 
		        		Long.valueOf(argObjectMap.get("level_id").toString()).longValue(), argObjectMap.get("login").toString(), 
		        		argObjectMap.get("password").toString(), argObjectMap.get("lastname").toString(), 
		        		argObjectMap.get("firstname").toString(), age, 
		        		argObjectMap.get("street").toString(), argObjectMap.get("additionalname").toString(), 
		        		argObjectMap.get("zip").toString(), Long.valueOf(argObjectMap.get("states_id").toString()).longValue(), 
		        		argObjectMap.get("town").toString(), Integer.valueOf(argObjectMap.get("availible").toString()).intValue(),
		        		argObjectMap.get("telefon").toString(),argObjectMap.get("fax").toString(),
		        		argObjectMap.get("mobil").toString(),
		        		argObjectMap.get("email").toString(),argObjectMap.get("comment").toString(),
		        		Integer.valueOf(argObjectMap.get("status").toString()).intValue(),
		        		organisations,
		        		Integer.valueOf(argObjectMap.get("title_id").toString()).intValue(),
		        		argObjectMap.get("phone").toString(),
		        		argObjectMap.get("sip_user").toString(),
		        		argObjectMap.get("sip_pass").toString(),
		        		argObjectMap.get("sip_auth").toString(),
		        		Boolean.valueOf(argObjectMap.get("generateSipUserData").toString()).booleanValue(),
		        		argObjectMap.get("jNameTimeZone").toString(),
		        		Boolean.valueOf(argObjectMap.get("forceTimeZoneCheck").toString()).booleanValue(),
		        		argObjectMap.get("userOffers").toString(),
		        		argObjectMap.get("userSearchs").toString(),
		        		Boolean.valueOf(argObjectMap.get("showContactData").toString()).booleanValue(),
		        		Boolean.valueOf(argObjectMap.get("showContactDataToContacts").toString()).booleanValue()
		        		); 
    		}
    	} catch (Exception ex) {
    		log.error("[saveOrUpdateUser]: ",ex);
    	}
    	return null;
    }    
    
    /**
     * deletes a user
     * @param SID
     * @param user_idClient
     * @return
     */
    public Long deleteUserAdmin(String SID, Long user_idClient) {
		log.debug("deleteUserAdmin");
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);

			// admins only
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				// no self destruction ;-)
				if (!users_id.equals(user_idClient)) {

					// Setting user deleted
					Long userId = UsersDaoImpl.getInstance().deleteUserID(
							user_idClient);

					Users user = Usermanagement.getInstance()
							.checkAdmingetUserById(user_level, userId);

					// Updating address
					Adresses ad = user.getAdresses();

					if (ad != null) {
						ad.setDeleted("true");

						Addressmanagement.getInstance().updateAdress(ad);
						log.debug("deleteUserId : Address updated");

						
					}

					return userId;
				} else {
					return new Long(-38);
				}
			} else {
				return new Long(-11);
			}
		} catch (Exception err) {
			log.error("[deleteUserAdmin]", err);
		}
		return null;
	} 
    
   public Boolean kickUserByStreamId(String SID, String streamid) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // admins only
		   if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
			   RoomClient rcl = this.clientListManager.getClientByStreamId(streamid);
			   
			   if (rcl == null) {
				   return true;
			   }
			   String scopeName = "hibernate";
			   if (rcl.getRoom_id() != null) {
				   scopeName = rcl.getRoom_id().toString();
			   }
			   IScope currentScope = this.scopeApplicationAdapter.getRoomScope(scopeName);
			   this.scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope);
			   
			   
			   HashMap<Integer,String> messageObj = new HashMap<Integer,String>();
			   messageObj.put(0, "kick");
			   this.scopeApplicationAdapter.sendMessageById(messageObj, streamid, currentScope);
			   
			   return true;
		   }
		   
	   } catch (Exception err) {
		   log.error("[kickUserByStreamId]",err);
	   }
	   return null;
   }
   
   public Users updateUserSelfTimeZone(String SID, String jname) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   Users us = Usermanagement.getInstance().getUserById(users_id);
			   
			   us.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jname));
			   us.setForceTimeZoneCheck(false);
			   us.setUpdatetime(new Date());
			   
			   Usermanagement.getInstance().updateUser(us);
			   
			   return us;
			   
		   }
	   }  catch (Exception err) {
		   log.error("[updateUserTimeZone]",err);
	   }
	   return null;
   }
   
   public SearchResult searchUserProfile(String SID, String searchTxt, String userOffers, 
		   String userSearchs, String orderBy, int start, int max, boolean asc) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   SearchResult searchResult = new SearchResult();
			   searchResult.setObjectName(Users.class.getName());
			   List<Users> userList = Usermanagement.getInstance().searchUserProfile(searchTxt,userOffers,userSearchs,orderBy,start,max,asc);
			   searchResult.setResult(userList);
			   Long resultInt = Usermanagement.getInstance().searchCountUserProfile(searchTxt, userOffers, userSearchs);
			   searchResult.setRecords(resultInt);
			   
			   return searchResult;
		   }
	   }  catch (Exception err) {
		   log.error("[searchUserProfile]",err);
	   }
	   return null;
   }
   
   public Long requestUserToContactList(String SID, Long userToAdd_id, 
		   		String domain, String port, String webapp) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   Long countContacts = UserContactsDaoImpl.getInstance().checkUserContacts(userToAdd_id, users_id);
			   
			   if (countContacts != null && countContacts > 0) {
				   return -45L;
			   }
			   
			   String hash = ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));
				
			   Long userContactId = UserContactsDaoImpl.getInstance().addUserContact(userToAdd_id, users_id, true, hash);
			   
			   Users user = Usermanagement.getInstance().getUserById(users_id);
			   
			   Users userToAdd = Usermanagement.getInstance().getUserById(userToAdd_id);
			   
			   Long language_id = userToAdd.getLanguage_id();
			   if (language_id == null) {
				   language_id = Long.valueOf(Configurationmanagement.getInstance().
		        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
			   }
			   
			   String message = "";
			   
			   Fieldlanguagesvalues fValue1192 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1192L, language_id);
			   Fieldlanguagesvalues fValue1193 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1193L, language_id);
			   Fieldlanguagesvalues fValue1194 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1194L, language_id);
			   Fieldlanguagesvalues fValue1190 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1190L, language_id);
			   Fieldlanguagesvalues fValue1191 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1191L, language_id);
			   Fieldlanguagesvalues fValue1196 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1196L, language_id);
	        	
		   	   message += fValue1192.getValue() + " " + userToAdd.getFirstname() + " " + userToAdd.getLastname() + "<br/><br/>";
			   message += user.getFirstname() + " " + user.getLastname()+ " "  + fValue1193.getValue() + "<br/>";
			   message += fValue1194.getValue() + "<br/>";
			   
			   String baseURL = "http://" + domain + ":" + port + webapp;
			   if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
			   } else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
			   }
			   
			   PrivateMessagesDaoImpl.getInstance().addPrivateMessage(user.getFirstname() + " " + user.getLastname()+ " "  + fValue1193.getValue(), message, 0L, user, userToAdd, userToAdd, false, null, true, userContactId);
			   
			   String link = baseURL+"?cuser="+hash;
			   
			   String accept_link = link + "&tAccept=yes";
			   String deny_link = link + "&tAccept=no";
			   
			   String aLinkHTML = "<a href='"+accept_link+"'>"+fValue1190.getValue()+"</a><br/>";
			   String denyLinkHTML = "<a href='"+deny_link+"'>"+fValue1191.getValue()+"</a><br/>";
			   String profileLinkHTML = "<a href='"+link+"'>"+fValue1196.getValue()+"</a><br/>";
			   
			   String template = RequestContactTemplate.getInstance().getRequestContactTemplate(message, 
					   aLinkHTML, denyLinkHTML, profileLinkHTML);
			   
			   if (userToAdd.getAdresses() != null) {
				   MailHandler.sendMail(userToAdd.getAdresses().getEmail(), user.getFirstname() + " " + user.getLastname()+ " "  + fValue1193.getValue(), template);
			   }
			   
			   return userContactId;
		   }
	   }  catch (Exception err) {
		   log.error("[requestuserToContactList]",err);
	   }
	   return null;
   }
   
   public List<UserContacts> getPendingUserContacts(String SID) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   List<UserContacts> uList = UserContactsDaoImpl.getInstance().getContactRequestsByUserAndStatus(users_id, true);
			   
			   return uList;
		   }
		   
	   } catch (Exception err) {
		   log.error("[getPendingUserContact]",err);
	   }
	   return null;
   }
   
   public Object changeUserContactByHash(String SID, String hash, Boolean status) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   UserContacts userContact = UserContactsDaoImpl.getInstance().getContactsByHash(hash);
			   
			   if (userContact == null) {
				   
				   log.error("changeUserContactByHash "+hash);
				   
				   return -48L;
			   }
			   
			   if (userContact.getContact().getUser_id().equals(users_id)) {

				   return this.changePendingStatusUserContacts(SID, userContact.getUserContactId(), status);
				   
			   } else {
				   return -48L;
			   }
			   
		   }
		   
	   } catch (Exception err) {
		   log.error("[changeUserContactByHash]",err);
	   }
	   return null;
   }
   
   public List<UserContacts> getUserContacts(String SID) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   List<UserContacts> uList = UserContactsDaoImpl.getInstance().getContactsByUserAndStatus(users_id, false);
			   
			   return uList;
		   }
		   
	   } catch (Exception err) {
		   log.error("[getPendingUserContact]",err);
	   }
	   return null;
   }
   
   public Long checkPendingStatus(String SID, Long userContactId) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   UserContacts userContacts = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
			   
			   if (userContacts == null) {
				   return -46L;
			   }
			   
			   if (userContacts.getPending() != null && !userContacts.getPending()) {
				   return -47L;
			   }
			   
			   return userContactId;
		   }
	   } catch (Exception err) {
		   log.error("[checkPendingStatus]",err);
	   }
	   return null;
   }
   
   public Integer removeContactUser(String SID, Long userContactId) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   UserContacts userContacts = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
			   
			   if (userContacts == null) {
				   return -49;
			   }
			   
			   return UserContactsDaoImpl.getInstance().deleteUserContact(userContactId);
			   
		   }
	   } catch (Exception err) {
		   log.error("[removeContactUser]",err);
	   }
	   return null;
   }
   
   public Long changePendingStatusUserContacts(String SID, Long userContactId, Boolean pending) {
	   try {
		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		   // users only
		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
			   UserContacts userContacts = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
			   
			   if (userContacts == null) {
				   return -46L;
			   }
			   
			   if (userContacts.getPending() != null && !userContacts.getPending()) {
				   return -47L;
			   }
			   
			   if (pending) {
				   
				   UserContactsDaoImpl.getInstance().updateContactStatus(userContactId, false);
				   
				   userContacts = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
			   
				   UserContactsDaoImpl.getInstance().addUserContact(userContacts.getOwner().getUser_id(), users_id, false, "");
				   
				   Users user = userContacts.getOwner();
				   
				   if (user.getAdresses() != null) {
					   
					   Long language_id = user.getLanguage_id();
					   if (language_id == null) {
						   language_id = Long.valueOf(Configurationmanagement.getInstance().
				        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
					   }
					   
					   String message = "";
					   
					   Fieldlanguagesvalues fValue1192 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1192L, language_id);
					   Fieldlanguagesvalues fValue1198 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1198L, language_id);
					   
					   message += fValue1192.getValue() + " " + user.getFirstname() + " " + user.getLastname() + "<br/><br/>";
					   message += userContacts.getContact().getFirstname() + " " + userContacts.getContact().getLastname() + " " + fValue1198.getValue();
					   
					   String template = RequestContactConfirmTemplate.getInstance().getRequestContactTemplate(message);
					   
					   PrivateMessagesDaoImpl.getInstance().addPrivateMessage(user.getFirstname() + " " + user.getLastname()+ " "  + fValue1198.getValue(), message, 0L, userContacts.getContact(), user, user, false, null, false, 0L);
					   
					   MailHandler.sendMail(user.getAdresses().getEmail(), userContacts.getContact().getFirstname() + " " + userContacts.getContact().getLastname() + " " + fValue1198.getValue(), template);
					   
				   }
				   
			   } else {
				   
				   UserContactsDaoImpl.getInstance().deleteUserContact(userContactId);
				   
			   }
			   
			   return userContactId;
		   }
		   
	   } catch (Exception err) {
		   log.error("[getPendingUserContact]",err);
	   }
	   return null;
   }
   
    public Long composeMail(String SID, List<String> recipients, String subject, String message, Boolean bookedRoom, 
    		Date validFromDate, String validFromTime, Date validToDate, String validToTime,
    		Long parentMessageId, Long roomtype_id) {
    	try {
    		
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		    Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		    // users only
 		    if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 		    	
 		    	Integer validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
 		    	Integer validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();
 		    	
 		    	Integer validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
 		    	Integer validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();
 		    	
 		    	log.info("validFromHour: "+validFromHour);
 		    	log.info("validFromMinute: "+validFromMinute);
 		    	
 		    	//TODO: Remove deprecated Java-Date handlers
 		    	Calendar calFrom = Calendar.getInstance();
 		    	int year = validFromDate.getYear()+1900;
 		    	int month = validFromDate.getMonth();
 		    	int date = validFromDate.getDate();
 		    	calFrom.set(year, month, date, validFromHour, validFromMinute, 0);
 				
 				
 				Calendar calTo= Calendar.getInstance();
 		    	int yearTo = validToDate.getYear()+1900;
 		    	int monthTo = validToDate.getMonth();
 		    	int dateTo = validToDate.getDate();
 		    	calTo.set(yearTo, monthTo, dateTo, validToHour, validToMinute, 0);
 		    	
 		    	Date appointmentstart = calFrom.getTime();
 		    	Date appointmentend = calTo.getTime();
 		    	
 		    	log.info("validFromDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(appointmentstart));
 		    	log.info("validToDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(appointmentend));
 		    	
 		    	Users from = Usermanagement.getInstance().getUserById(users_id);
 		    	
 		    	Rooms room = null;
 		    	
 		    	if (bookedRoom) {
 		    		Long room_id = Roommanagement.getInstance().addRoom(
 		   				3,					// Userlevel
 		   				subject,	// name	
 		   				roomtype_id,					// RoomType	
 		   				"",					// Comment
 		   				new Long(100),		// Number of participants
 		   				false,				// public
 		   				null,				// Organisations
 		   				true,				// Appointment
 		   				false,				// Demo Room => Meeting Timer
 		   				null,               // Meeting Timer time in seconds
 		   				false, 				// Is Moderated Room
 		   				null, 				// Moderation List Room
 		   				true,				// Allow User Questions
 		   				false,              // isAudioOnly
 		   				false, 		        // isClosed
 		   				"", 				// redirectURL
 		   				"", 				// sipNumber
 		   				"");				// conferencePIN
 		    		
 		    		room = Roommanagement.getInstance().getRoomById(room_id);
 		    		
 		    	}
 		    		
 		    	recipients.add(from.getAdresses().getEmail());
 		    	
 		    	String sendJNameTimeZone = from.getOmTimeZone().getJname();
 		    	
 		    	for (String email : recipients) {
 		    		
 		    		//Map receipent = (Map) recipients.get(iter.next());
 		    		
 		    		//String email = receipent.get("email").toString();
 		    		
 		    		Users to = Usermanagement.getInstance().getUserByLoginOrEmail(email);
 		    		
 		    		if (to == null){
	 		       		throw new Exception("Could not find user "+email);
	 		       	}
 		    		
 		    		Boolean invitor = false;
	    			if (email.equals(from.getAdresses().getEmail())) {
	    				invitor = true;
	    			} else {
	    				
	    				//One message to the Send
	    				PrivateMessagesDaoImpl.getInstance().addPrivateMessage(subject, message, parentMessageId, from, to, from, bookedRoom, room, false, 0L);
	    				
	    				//One message to the Inbox
	    				PrivateMessagesDaoImpl.getInstance().addPrivateMessage(subject, message, parentMessageId, from, to, to, bookedRoom, room, false, 0L);
	    				
	    			}
 		    		
 		    		if (bookedRoom) {
 		    			
 		    			//But add the appointment to everybody
 		    			this.addAppointmentToUser(subject, message, to, recipients, 
 		    						room, appointmentstart, appointmentend, invitor, 
 		    						true, sendJNameTimeZone);
 		    			
 		    		}
 		    	}
 		    	
 		    }
    		
    	} catch (Exception err) {
 		   log.error("[composeMail]",err);
 	   }
 	   return null;
    }
    
    /*
     * Date appointmentstart = calFrom.getTime();
 	 * Date appointmentend = calTo.getTime();
     */
    private void addAppointmentToUser(String subject, String message, Users to, List<String> recipients, Rooms room, 
    		Date appointmentstart, Date appointmentend, Boolean invitor, 
    		Boolean isConnectedEvent, String sendJNameTimeZone) throws Exception {
    	
    	Long appointmentId =  AppointmentDaoImpl.getInstance().addAppointment(subject, to.getUser_id(), "", message,
   				appointmentstart, appointmentend, false, false, 
   				false, false, 1L, 2L, room, to.getLanguage_id(), 
   				false, "", isConnectedEvent, sendJNameTimeZone);
    	
    	for (String email : recipients) {
	    		
	    		//Map receipent = (Map) recipients.get(iter.next());
    		
	    		//String email = receipent.get("email").toString();
	    		
	    		Users meetingMember = Usermanagement.getInstance().getUserByLoginOrEmail(email);
	    		
	    		String firstname = meetingMember.getFirstname();
	    		String lastname = meetingMember.getLastname();
	    		
	    		MeetingMemberDaoImpl.getInstance().addMeetingMember(firstname,  lastname,  "0",
	  				 "0",  appointmentId,  meetingMember.getUser_id(),  email, invitor, 
	  				 meetingMember.getOmTimeZone().getJname(), isConnectedEvent);
	    		
	    	}
    	
    }
    
	public SearchResult getInbox(String SID, String search, String orderBy, int start, Boolean asc, Integer max) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = PrivateMessagesDaoImpl
									.getInstance().getPrivateMessagesByUser(users_id,
											search, orderBy, start, asc, 0L, max);
				
				searchResult.setResult(userList);
				
				Long resultInt = PrivateMessagesDaoImpl
									.getInstance().countPrivateMessagesByUser(users_id,search, 0L);
				
				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}
	
	public SearchResult getSend(String SID, String search, String orderBy, Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = PrivateMessagesDaoImpl
									.getInstance().getSendPrivateMessagesByUser(users_id,
											search, orderBy, start, asc, 0L, max);
				
				searchResult.setResult(userList);
				
				Long resultInt = PrivateMessagesDaoImpl
									.getInstance().countSendPrivateMessagesByUser(users_id, search, 0L);
				
				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}
	
	public SearchResult getTrash(String SID, String search, String orderBy, Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = PrivateMessagesDaoImpl
									.getInstance().getTrashPrivateMessagesByUser(users_id,
											search, orderBy, start, asc, max);
				
				searchResult.setResult(userList);
				
				Long resultInt = PrivateMessagesDaoImpl
									.getInstance().countTrashPrivateMessagesByUser(users_id, search);
				
				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}
    
	public SearchResult getFolder(String SID, Long privateMessageFolderId, String search, String orderBy, 
			Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = PrivateMessagesDaoImpl
									.getInstance().getFolderPrivateMessagesByUser(users_id,
											search, orderBy, start, asc, privateMessageFolderId, max);
				
				searchResult.setResult(userList);
				
				Long resultInt = PrivateMessagesDaoImpl
									.getInstance().countFolderPrivateMessagesByUser(users_id,privateMessageFolderId, search);
				
				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}
	
	public Long getFolderCount(String SID, Long privateMessageFolderId) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				return PrivateMessagesDaoImpl.getInstance().countFolderPrivateMessagesByUser(users_id,privateMessageFolderId, "");
				

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}
	
	public Integer moveMailsToFolder(String SID, List privateMessageIntsIds,
			Long newFolderId) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();
				
				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString()).longValue());
				}
				
				return PrivateMessagesDaoImpl.getInstance().moveMailsToFolder(privateMessageIds, newFolderId);
				
			}
		} catch (Exception err) {
			log.error("[moveMailsToFolder]", err);
		}
		return null;
	}
	
	public Integer moveMailsToTrash(String SID, List privateMessageIntsIds, Boolean isTrash) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				List<Long> privateMessageIds = new LinkedList<Long>();
				
				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString()).longValue());
				}
				
				log.debug("moveMailsToTrash :: "+isTrash);

				return PrivateMessagesDaoImpl.getInstance().updatePrivateMessagesToTrash(privateMessageIds, isTrash, 0L);
				
			}
		} catch (Exception err) {
			log.error("[moveMailsToTrash]", err);
		}
		return -1;
	}
	
	public Integer deletePrivateMessages(String SID, List privateMessageIntsIds) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				List<Long> privateMessageIds = new LinkedList<Long>();
				
				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString()).longValue());
				}
				
				return PrivateMessagesDaoImpl.getInstance().deletePrivateMessages(privateMessageIds);
				
			}
		} catch (Exception err) {
			log.error("[markReadStatusMails]", err);
		}
		return -1;
	}
	
	public Integer markReadStatusMails(String SID, List privateMessageIntsIds, Boolean isRead) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				List<Long> privateMessageIds = new LinkedList<Long>();
				
				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString()).longValue());
				}
				
				log.debug("markReadStatusMails :: "+isRead);

				return PrivateMessagesDaoImpl.getInstance().updatePrivateMessagesReadStatus(privateMessageIds, isRead);
				
			}
		} catch (Exception err) {
			log.error("[markReadStatusMails]", err);
		}
		return -1;
	}
	
	public Integer markReadStatusMail(String SID, Long privateMessageId,
			Boolean isRead) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				List<Long> privateMessageIds = new LinkedList<Long>();
				privateMessageIds.add(privateMessageId);
				
				return PrivateMessagesDaoImpl.getInstance().updatePrivateMessagesReadStatus(privateMessageIds, isRead);
				
//				PrivateMessages privateMessage = PrivateMessagesDaoImpl.getInstance().getPrivateMessagesById(privateMessageId);
//				
//				privateMessage.setIsRead(isRead);
//				
//				PrivateMessagesDaoImpl.getInstance().updatePrivateMessages(privateMessage);

			}
		} catch (Exception err) {
			log.error("[markReadStatusMail]", err);
		}
		return null;
	}
	
	public List<PrivateMessageFolder> getPrivateMessageFoldersByUser(String SID) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		    Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		    // users only
		    if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			   
	    		return PrivateMessageFolderDaoImpl.getInstance().getPrivateMessageFolderByUserId(users_id);
			   
		    }
 		   
 	   } catch (Exception err) {
 		   log.error("[getPrivateMessageFolderByUser]",err);
 	   }
 	   return null;
    }
	
	public Long addPrivateMessageFolder(String SID, String folderName) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		   // users only
 		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 			   
 			  PrivateMessageFolderDaoImpl.getInstance().addPrivateMessageFolder(folderName, users_id);
 			   
 		   }
 		   
 	   } catch (Exception err) {
 		   log.error("[addPrivateMessageFolder]",err);
 	   }
 	   return null;
    }
	
	public Boolean checkUserIsInContactList(String SID, Long user_id) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				List<UserContacts> uList = UserContactsDaoImpl.getInstance().getContactsByUserAndStatus(users_id, false);
				
				for (UserContacts userContact : uList) {
					
					if (userContact.getContact().getUser_id().equals(user_id)) {
						return true;
					}
					
				}
				
				return false;
				
			}

		} catch (Exception err) {
			log.error("[checkUserIsInContactList]", err);
		}
		return null;
	}
	
	public void shareCalendarUserContact(String SID, Long userContactId, Boolean shareCalendar) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
			// users only
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				
				UserContacts userContacts = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
				
				userContacts.setShareCalendar(shareCalendar);
				
				UserContactsDaoImpl.getInstance().updateContact(userContacts);
				
			}

		} catch (Exception err) {
			log.error("[shareCalendarUserContact]", err);
		}
	}
	
	public Long updatePrivateMessageFolder(String SID, Long privateMessageFolderId, String folderName) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		   // users only
 		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 			   
 			  PrivateMessageFolder privateMessageFolder = PrivateMessageFolderDaoImpl.getInstance().getPrivateMessageFolderById(privateMessageFolderId);
 			   
 			  privateMessageFolder.setFolderName(folderName);
 			  privateMessageFolder.setUpdated(new Date());
 			 
 			  PrivateMessageFolderDaoImpl.getInstance().updatePrivateMessages(privateMessageFolder);
 			 
 			  return privateMessageFolderId;
 			 
 		   }
 		   
 	   } catch (Exception err) {
 		   log.error("[updatePrivateMessageFolder]",err);
 	   }
 	   return null;
    }
	
	public Long deletePrivateMessageFolder(String SID, Long privateMessageFolderId) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		   // users only
 		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 			   
 			  PrivateMessageFolder privateMessageFolder = PrivateMessageFolderDaoImpl.getInstance().getPrivateMessageFolderById(privateMessageFolderId);
 			   
 			  PrivateMessageFolderDaoImpl.getInstance().deletePrivateMessages(privateMessageFolder);
 			 
 		   }
 		   
 	   } catch (Exception err) {
 		   log.error("[deletePrivateMessageFolder]",err);
 	   }
 	   return null;
    }
	
	public List<UserContacts> getUserContactsWithShareCalendar(String SID) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		   // users only
 		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 			   
		   	   return UserContactsDaoImpl.getInstance().getContactsByShareCalendar(users_id, true);
 			 
 		   }
 		   
 	   } catch (Exception err) {
 		   log.error("[getContactsByShareCalendar]",err);
 	   }
 	   return null;
    }
	
	 
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
 	   try {
 		   Long users_id = Sessionmanagement.getInstance().checkSession(SID);
 		   Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
 		   // users only
 		   if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
 			   
 			   RoomClient rcl = this.clientListManager.getClientByPublicSID(publicSID);
 			   
 			   if (rcl == null) {
 				   return true;
 			   }
 			   String scopeName = "hibernate";
 			   if (rcl.getRoom_id() != null) {
 				   scopeName = rcl.getRoom_id().toString();
 			   }
 			   IScope currentScope = this.scopeApplicationAdapter.getRoomScope(scopeName);
 			   
 			   HashMap<Integer,String> messageObj = new HashMap<Integer,String>();
 			   messageObj.put(0, "kick");
 			   
 			   this.scopeApplicationAdapter.sendMessageById(messageObj, rcl.getStreamid(), currentScope);
 			   
 			   
 			   this.scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope);
 			   
 			   return true;
 		   }
 		   
 	   } catch (Exception err) {
 		   log.error("[kickUserByStreamId]",err);
 	   }
 	   return null;
    }
}
