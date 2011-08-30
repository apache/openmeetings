package org.openmeetings.app.remote;

import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ErrorService {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ErrorService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private ErrorManagement errorManagement;

	/**
	 * Gets an Error-Object by its id TODO: add error-code-handlers -20
	 * duplicate FileName -21 FileName too short (length = 0) and make the
	 * persistent in the DataBase
	 * 
	 * @param SID
	 * @param errorid
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		// long user_level =
		// Usermanagement.getInstance().getUserLevelByID(users_id);

		if (errorid < 0) {
			log.debug("errorid, language_id: " + errorid + "|" + language_id);
			ErrorValues eValues = errorManagement.getErrorValuesById(-1
					* errorid);
			if (eValues != null) {
				log.debug("eValues.getFieldvalues_id() = " + eValues.getFieldvalues_id());
				// log.debug(eValues.getFieldvalues().getFieldvalues_id());
				log.debug("eValues.getErrorType() = " + errorManagement.getErrorType(eValues.getErrortype_id()));
				// log.debug(eValues.getErrorType().getErrortype_id());
				// log.debug(eValues.getErrorType().getFieldvalues());
				// log.debug(eValues.getErrorType().getFieldvalues().getFieldvalues_id());
				Fieldlanguagesvalues errorValue = fieldmanagment
						.getFieldByIdAndLanguage(eValues.getFieldvalues_id(),
								language_id);
				Fieldlanguagesvalues typeValue = fieldmanagment
						.getFieldByIdAndLanguage(errorManagement.getErrorType(eValues.getErrortype_id())
								.getFieldvalues_id(), language_id);
				if (errorValue != null) {
					return new ErrorResult(errorid, errorValue.getValue(),
							typeValue.getValue());
				}
			}
		} else {
			return new ErrorResult(errorid,
					"Error ... please check your input", "Error");
		}

		return null;
	}

}
