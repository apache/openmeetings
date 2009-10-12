package org.openmeetings.test.init;

import junit.framework.TestCase;

import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;

public class AdddefaultLanugages extends TestCase {

	public AdddefaultLanugages(String testname){
		super(testname);
	}
	
	public void testAdddefaultLanugages(){
		
		FieldLanguageDaoImpl.getInstance().addLanguage("deutsch",false);
		FieldLanguageDaoImpl.getInstance().addLanguage("english",false);
		FieldLanguageDaoImpl.getInstance().addLanguage("french",false);
		
	}
}
