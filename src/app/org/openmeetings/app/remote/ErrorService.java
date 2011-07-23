package org.openmeetings.app.remote;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;


import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

/**
 * 
 * @author swagner
 *
 */
public class ErrorService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, ScopeApplicationAdapter.webAppRootKey);
	
	/**
	 * Gets an Error-Object by its id
	 * TODO: add error-code-handlers
	 * -20 duplicate FileName
	 * -21 FileName too short (length = 0)
	 * and make the persistent in the DataBase
	 * @param SID
	 * @param errorid
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id){
        //Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        //long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);

        if (errorid<0){
//        	log.debug("errorid,language_id: "+errorid+"|"+language_id);
        	ErrorValues eValues = ErrorManagement.getInstance().getErrorValuesById(errorid*(-1));
	        if (eValues!=null){
//	        	log.debug(eValues.getFieldvalues());
//	        	log.debug(eValues.getFieldvalues().getFieldvalues_id());
//	        	log.debug(eValues.getErrorType());
//	        	log.debug(eValues.getErrorType().getErrortype_id());
//	        	log.debug(eValues.getErrorType().getFieldvalues());
//	        	log.debug(eValues.getErrorType().getFieldvalues().getFieldvalues_id());
	        	Fieldlanguagesvalues errorValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(eValues.getFieldvalues().getFieldvalues_id(),language_id);
	        	Fieldlanguagesvalues typeValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(eValues.getErrorType().getFieldvalues().getFieldvalues_id(),language_id);
	        	if (errorValue!=null) {
	        		return new ErrorResult(errorid,errorValue.getValue(),typeValue.getValue());
	        	}
        	}
        } else {
        	return new ErrorResult(errorid,"Error ... please check your input","Error");
        }
        
        return null;
	}
	

}
