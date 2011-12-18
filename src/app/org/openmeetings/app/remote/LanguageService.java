package org.openmeetings.app.remote;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.lang.Fieldvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class LanguageService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			LanguageService.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private FieldLanguageDaoImpl fieldLanguageDaoImpl;
	@Autowired
	private AuthLevelmanagement authLevelManagement;

	/**
	 * get a List of all availible Languages
	 * 
	 * @return
	 */
	public List<FieldLanguage> getLanguages() {
		return fieldLanguageDaoImpl.getLanguages();
	}

	/**
	 * get all fields of a given Language_id
	 * 
	 * @param language_id
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public List<Fieldlanguagesvalues> getLanguageById(Long language_id) {
		return fieldmanagment.getAllFieldsByLanguage(language_id);
	}

	public Integer getDefaultLanguage() {
		return Integer.valueOf(
				cfgManagement.getConfKey(3, "default_lang_id").getConf_value())
				.intValue();
	}

	/**
	 * get all fields of a given Language_id by params
	 * 
	 * @param language_id
	 * @return
	 */
	public List<Map<String, Object>> getLanguageByIdAndMax(Long language_id,
			int start, int max) {
		return fieldmanagment.getLabelsByLanguage(language_id, start, max);
	}

	public Fieldvalues getFieldvalueById(String SID, Long fieldvalues_id,
			Long language_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			return fieldmanagment.getFieldvaluesById(fieldvalues_id,
					language_id);
		}
		return null;
	}

	public Long addLanguage(String SID, String langName, String code) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			if (langName.length() == 0)
				return new Long(-30);
			return fieldLanguageDaoImpl.addLanguage(langName, false, code);
		}
		return null;
	}

	public Long updateLanguage(String SID, Long language_id, String langName, String code) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			if (langName.length() == 0)
				return new Long(-30);
			return fieldLanguageDaoImpl.updateFieldLanguage(language_id,
					langName, code, "false");
		}
		return null;
	}

	public Long deleteLanguage(String SID, Long language_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			return fieldLanguageDaoImpl.updateFieldLanguage(language_id, "",
					"", "true");
		}
		return null;
	}

	public Long deleteFieldlanguagesvaluesById(String SID,
			Long fieldlanguagesvalues_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			return fieldmanagment
					.deleteFieldlanguagesvaluesById(fieldlanguagesvalues_id);
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
	public SearchResult<Fieldvalues> getFieldsByLanguage(String SID, int start, int max,
			String orderby, boolean asc, Long language_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkAdminLevel(user_level)) {
			return fieldmanagment.getFieldsByLanguage(start, max, orderby, asc,
					language_id);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param values
	 * @return
	 */
	public Long saveOrUpdateLabel(String SID,
			LinkedHashMap<Object, Object> values) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			Long fieldvalues_id = Long.valueOf(
					values.get("fieldvalues_id").toString()).longValue();
			String name = values.get("name").toString();
			Long fieldlanguagesvalues_id = Long.valueOf(
					values.get("fieldlanguagesvalues_id").toString())
					.longValue();
			Long language_id = Long.valueOf(
					values.get("language_id").toString()).longValue();
			String value = values.get("value").toString();
			if (authLevelManagement.checkAdminLevel(user_level)) {
				if (fieldvalues_id > 0 && fieldlanguagesvalues_id > 0) {
					log.error("UPDATE LABEL");
					return fieldmanagment.updateLabel(fieldvalues_id, name,
							fieldlanguagesvalues_id, value);
				} else if (fieldvalues_id > 0 && fieldlanguagesvalues_id == 0) {
					log.error("INSERT NEW LABEL");
					return fieldmanagment.addAndUpdateLabel(fieldvalues_id,
							name, value, language_id);
				} else {
					log.error("INSERT NEW FIELD AND LABEL");
					return fieldmanagment.addFieldAndLabel(name, value,
							language_id);
				}
			}
			return new Long(-26);
		} catch (Exception e) {
			log.error("[saveOrUpdateLabel]", e);
		}
		return new Long(-1);
	}

}
