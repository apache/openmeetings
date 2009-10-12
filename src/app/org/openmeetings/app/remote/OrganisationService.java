package org.openmeetings.app.remote;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.hibernate.beans.domain.Organisation;

/**
 * 
 * @author swagner
 *
 */
public class OrganisationService {
	
	private static final Logger log = Logger.getLogger(OrganisationService.class);
	
	/**
	 * Loads a List of all availible Organisations (ADmin-role only)
	 * @param SID
	 * @return
	 */
	public SearchResult getOrganisations(String SID, int start ,int max, String orderby, boolean asc){
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        return Organisationmanagement.getInstance().getOrganisations(user_level,start,max,orderby,asc);
		} catch (Exception e){
			log.error("getOrganisations",e);
		}
		return null;
	}
	
	public List<Organisation> getAllOrganisations(String SID){
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        return Organisationmanagement.getInstance().getOrganisations(user_level);
		} catch (Exception e){
			log.error("getAllOrganisations",e);
		}
		return null;
	}	
	
	/**
	 * get an organisation by a given id
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public Organisation getOrganisationById(String SID, long organisation_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Organisationmanagement.getInstance().getOrganisationById(user_level, organisation_id);
	}
	
	/**
	 * deletes a organisation by a given id
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public Long deleteOrganisation(String SID, long organisation_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        return Organisationmanagement.getInstance().deleteOrganisation(user_level, organisation_id, users_id);
	}
	
	/**
	 * adds or updates an Organisation
	 * @param SID
	 * @param organisation_id
	 * @param orgname
	 * @return
	 */
	public Long saveOrUpdateOrganisation(String SID, Object regObjectObj){
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        LinkedHashMap argObjectMap = (LinkedHashMap) regObjectObj;
	        long organisation_id = Long.valueOf(argObjectMap.get("organisation_id").toString()).longValue();
	        if (organisation_id==0){
	        	return Organisationmanagement.getInstance().addOrganisation(user_level, argObjectMap.get("orgname").toString(), users_id);
	        } else {
	        	return Organisationmanagement.getInstance().updateOrganisation(user_level, organisation_id, argObjectMap.get("orgname").toString(), users_id);
	        }
		} catch (Exception err) {
			log.error("saveOrUpdateOrganisation",err);
		}
		return null;
        
	}
	
	/**
	 * gets all users of a given organisation
	 * @param SID
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
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
	
	public Long addUserToOrganisation(String SID, Long organisation_id, Long user_id, String comment) {
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
	        	return Organisationmanagement.getInstance().addUserToOrganisation(user_id, organisation_id, users_id, comment);
	        } else {
	        	return -26L;
	        }
		} catch (Exception err) {
			log.error("getUsersByOrganisation",err);
		}
		return null;
	}
	
	public Long deleteUserFromOrganisation(String SID, Long organisation_id, Long user_id, String comment) {
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        return Organisationmanagement.getInstance().deleteUserFromOrganisation(user_level, user_id, organisation_id);
		} catch (Exception err) {
			log.error("getUsersByOrganisation",err);
		}
		return null;
	}

}
