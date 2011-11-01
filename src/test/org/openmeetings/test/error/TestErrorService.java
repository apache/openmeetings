package org.openmeetings.test.error;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.remote.ErrorService;
import org.openmeetings.app.remote.LanguageService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestErrorService extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private LanguageService languageService;
	@Autowired
	private ErrorService errorService;
	
	@Test
	public void getErrorByCode() {
		for (FieldLanguage lng : languageService.getLanguages()) {
			for (long i = -52; i < 0; ++i) {
				assertNotNull(
					"Not null error result should be returned: i : " + i + "; lng_id = " + lng.getLanguage_id()
					, errorService.getErrorByCode(null, i, lng.getLanguage_id())
					);
			}
		}
	}
}
