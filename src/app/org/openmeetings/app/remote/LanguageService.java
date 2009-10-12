package org.openmeetings.app.remote;

import java.util.List;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;

/**
 * 
 * @author sebastianwagner
 *
 */
public class LanguageService {
	
	private static final Logger log = Logger.getLogger(LanguageService.class);
	
	/**
	 * get a List of all availible Languages
	 * @return
	 */
	public List getLanguages(){
		return FieldLanguageDaoImpl.getInstance().getLanguages();
	}
	
	/**
	 * get all fields of a given Language_id
	 * @param language_id
	 * @deprecated
	 * @return
	 */
	public List<Fieldlanguagesvalues> getLanguageById(Long language_id){
		return Fieldmanagment.getInstance().getAllFieldsByLanguage(language_id);
	}
	
	public Integer getDefaultLanguage() {
		return Integer.valueOf(Configurationmanagement.getInstance().
				getConfKey(3,"default_lang_id").getConf_value()).intValue();
	}
	
	
	/**
	 * get all fields of a given Language_id by params
	 * @param language_id
	 * @return
	 */
	public List<Fieldlanguagesvalues> getLanguageByIdAndMax(Long language_id, int start, int max){
		return Fieldmanagment.getInstance().getAllFieldsByLanguage(language_id,start,max);
	}
	
	public Fieldvalues getFieldvalueById(String SID, Long fieldvalues_id, Long language_id) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
        	return Fieldmanagment.getInstance().getFieldvaluesById(fieldvalues_id, language_id);
        }
        return null;
	}
	
	public Long addLanguage(String SID, String langName) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
        	if (langName.length()==0) return new Long(-30);
        	return FieldLanguageDaoImpl.getInstance().addLanguage(langName,false);
        }
        return null;
	}
	
	public Long updateLanguage(String SID, Long language_id, String langName) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
        	if (langName.length()==0) return new Long(-30);
        	return FieldLanguageDaoImpl.getInstance().updateFieldLanguage(language_id, langName, "false");
        }
        return null;
	}
	
	public Long deleteLanguage(String SID, Long language_id) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
        	return FieldLanguageDaoImpl.getInstance().updateFieldLanguage(language_id, "", "true");
        }
        return null;
	}
	
	public Long deleteFieldlanguagesvaluesById(String SID, Long fieldlanguagesvalues_id) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
        	return Fieldmanagment.getInstance().deleteFieldlanguagesvaluesById(fieldlanguagesvalues_id);
        }
        return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @param language_id
	 * @return
	 */
	public SearchResult getFieldsByLanguage(String SID, int start, int max, String orderby, boolean asc, Long language_id){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
        	return Fieldmanagment.getInstance().getFieldsByLanguage(start, max, orderby, asc, language_id);
        }
		return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param values
	 * @return
	 */
	public Long saveOrUpdateLabel(String SID, LinkedHashMap<Object,Object> values)  {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			Long fieldvalues_id = Long.valueOf(values.get("fieldvalues_id").toString()).longValue();
			String name = values.get("name").toString(); 
			Long fieldlanguagesvalues_id = Long.valueOf(values.get("fieldlanguagesvalues_id").toString()).longValue();
			Long language_id = Long.valueOf(values.get("language_id").toString()).longValue();
			String value = values.get("value").toString(); 
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				if (fieldvalues_id>0 && fieldlanguagesvalues_id>0){
					log.error("UPDATE LABEL");
					return Fieldmanagment.getInstance().updateLabel(fieldvalues_id, name, fieldlanguagesvalues_id, value);
				} else if (fieldvalues_id>0 && fieldlanguagesvalues_id==0) {
					log.error("INSERT NEW LABEL");
					return Fieldmanagment.getInstance().addAndUpdateLabel(fieldvalues_id, name, value, language_id);
				} else {
					log.error("INSERT NEW FIELD AND LABEL");
					return Fieldmanagment.getInstance().addFieldAndLabel(name, value, language_id);
				}
			}
			return new Long(-26);	
		} catch (Exception e) {
			log.error("[saveOrUpdateLabel]",e);
		}
		return new Long(-1);	
	}	

}
