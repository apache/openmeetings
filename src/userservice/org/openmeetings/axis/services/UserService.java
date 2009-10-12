package org.openmeetings.axis.services;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.basic.ErrorValues;
import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.basic.RemoteSessionObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class UserService {

	private static final Logger log = Logger.getLogger(UserService.class);
	
	/**
	 * load this session id before doing anything else
	 * @return Sessiondata-Object
	 */
    public Sessiondata getSession(){
    	return MainService.getInstance().getsessiondata();
    }   
    
    /**
     * auth function, use the SID you get by getSession
     * @param SID
     * @param Username
     * @param Userpass
     * @return positive means Loggedin, if negativ its an ErrorCode, you have to invoke the Method
     * getErrorByCode to get the Text-Description of that ErrorCode
     */  
    public Long loginUser(String SID, String username, String userpass){
    	log.debug("UserService.loginuser");
    	try {
    		Object obj = Usermanagement.getInstance().loginUser(SID,username,userpass, null);
    		if (obj==null){
    			return new Long(-1);
    		}
    		String objName = obj.getClass().getName();
    		log.debug("objName: "+objName);
    		if (objName.equals("java.lang.Long")){
    			return (Long) obj;
    		} else {
    			return new Long(1);
    		}
    	} catch (Exception err) {
    		log.error("[loginUser]",err);
    	}
    	return new Long(-1);
    } 
    
    /**
     * Gets the Error-Object
     * @param SID
     * @param errorid
     * @param language_id
     * @return
     */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id){
		log.debug("UserService.getErrorbyCode");
        try {
            if (errorid<0){
            	ErrorValues eValues = ErrorManagement.getInstance().getErrorValuesById(errorid*(-1));
    	        if (eValues!=null){
    	        	Fieldlanguagesvalues errorValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(eValues.getFieldvalues().getFieldvalues_id(),language_id);
    	        	Fieldlanguagesvalues typeValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(eValues.getErrorType().getFieldvalues().getFieldvalues_id(),language_id);
    	        	if (errorValue!=null) {
    	        		return new ErrorResult(errorid,errorValue.getValue(),typeValue.getValue());
    	        	}
            	}
            } else {
            	return new ErrorResult(errorid,"Error ... please check your input","Error");
            }
        } catch (Exception err) {
        	log.error("[getErrorByCode] ",err);
        }
        return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @return
	 */
	public Long setUserObject(String SID, String username, String firstname, String lastname, 
			String profilePictureUrl, String email){
		log.debug("UserService.setUserObject");
	     
		try {
	    	Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(username, firstname, lastname, 
						profilePictureUrl, email);
				
				
				log.debug("username "+username);
				log.debug("firstname "+firstname);
				log.debug("lastname "+lastname);
				log.debug("profilePictureUrl "+profilePictureUrl);
				log.debug("email "+email);
				
				//XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);
				
				log.debug("xmlString "+xmlString);
				
				Sessionmanagement.getInstance().updateUserRemoteSession(SID, xmlString);
				
				return new Long(1);
			} else {
				return new Long(-26);
			}
		} catch (Exception err){
			log.error("sendInvitationLink",err);
		}
		return new Long(-1);			
	}
	
	/**
	 * This is the advanced technique to set the User Object + simulate a User from 
	 * the external system, this is needed cause you can that always simulate to same
	 * user in openmeetings
	 * 
	 * @param SID
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @param externalUserId the User Id of the external System
	 * @param externalUserType the Name of the external system, for example you can run several external system and one meeting server
	 * @return
	 */
	public Long setUserObjectWithExternalUser(String SID, String username, String firstname, String lastname, 
			String profilePictureUrl, String email, Long externalUserId, String externalUserType){
		log.debug("UserService.setUserObject");
	     
		try {
	    	Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(username, firstname, lastname, 
						profilePictureUrl, email, externalUserId, externalUserType);
				
				
				log.debug("username "+username);
				log.debug("firstname "+firstname);
				log.debug("lastname "+lastname);
				log.debug("profilePictureUrl "+profilePictureUrl);
				log.debug("email "+email);
				log.debug("externalUserId "+externalUserId);
				log.debug("externalUserType " +externalUserType);
				
				//XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);
				
				log.debug("xmlString "+xmlString);
				
				Sessionmanagement.getInstance().updateUserRemoteSession(SID, xmlString);
				
				return new Long(1);
			} else {
				return new Long(-26);
			}
		} catch (Exception err){
			log.error("sendInvitationLink",err);
		}
		return new Long(-1);			
	}
	
	public Long addUserToOrganisation(String SID, Long user_id, Long organisation_id,
			Long insertedby, String comment) {
		try {
	    	Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				return Organisationmanagement.getInstance().addUserToOrganisation(user_id, organisation_id, users_id, comment);
				
			} else {
				return new Long(-26);
			}
		} catch (Exception err){
			log.error("addUserToOrganisation",err);
		}
		return new Long(-1);
	}
	
	public SearchResult getUsersByOrganisation(String SID, long organisation_id, int start, int max, String orderby, boolean asc){
		try {   
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
	        	return Organisationmanagement.getInstance().getUsersSearchResultByOrganisationId(organisation_id, start, max, orderby, asc);
	        } else {
	        	log.error("Need Administration Account");
	        	SearchResult sResult = new SearchResult();
	        	sResult.setErrorId(-26L);
	        	return sResult;
	        }
		} catch (Exception err) {
			log.error("getUsersByOrganisation",err);
		}
		return null;
	}
	
}
