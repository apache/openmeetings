package org.openmeetings.app.xmlimport;

import java.io.InputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.lang.Fieldvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;


public class LanguageImport {
	private static final Logger log = Red5LoggerFactory.getLogger(LanguageImport.class, ScopeApplicationAdapter.webAppRootKey);
	
	public LanguageImport() {	}

	private static LanguageImport instance = null;

	public static synchronized LanguageImport getInstance() {
		if (instance == null) {
			instance = new LanguageImport();
		}
		return instance;
	}	
	
	public Long addLanguageByDocument(Long language_id, InputStream is) throws Exception {
		
		//return null if no language availible
		if (FieldLanguageDaoImpl.getInstance().getFieldLanguageById(language_id)==null) {
			return null;
		}
		
		
		SAXReader reader = new SAXReader();
        Document document = reader.read(is);
        
        Element root = document.getRootElement();
        
        for (Iterator i = root.elementIterator(); i.hasNext(); ) {
        	Element itemObject = (Element) i.next();
        	Long fieldvalues_id = Long.valueOf(itemObject.attribute("id").getText()).longValue();
        	String fieldName = itemObject.attribute("name").getText();
        	String value = itemObject.element("value").getText();
        	log.error("CHECK "+language_id+","+fieldvalues_id+","+fieldName+","+value);
        	this.addFieldValueById(language_id, fieldvalues_id, fieldName, value);
        }
        
        return null;        
	}
	
	private void addFieldValueById(Long language_id, Long fieldvalues_id, String fieldName, String value) throws Exception {
		
		Fieldvalues fv = Fieldmanagment.getInstance().getFieldvaluesById(fieldvalues_id);
		
		if (fv==null) {
			Fieldmanagment.getInstance().addFieldById(fieldName, fieldvalues_id);
		}
		
		Fieldlanguagesvalues flv = Fieldmanagment.getInstance().getFieldByIdAndLanguage(fieldvalues_id, language_id);
		
		if (flv==null) {
			Fieldmanagment.getInstance().addFieldValueByFieldAndLanguage(fieldvalues_id, language_id, value);
		} else {
			flv.setValue(value);
			flv.setUpdatetime(new java.util.Date());
			Fieldmanagment.getInstance();
			Fieldmanagment.getInstance().updateFieldValueByFieldAndLanguage(flv);
		}
	}
	
	
}
