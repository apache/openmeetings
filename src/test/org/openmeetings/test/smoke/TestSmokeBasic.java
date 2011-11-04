package org.openmeetings.test.smoke;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestSmokeBasic extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private Usermanagement userManagement;
	
	@Test
	public void createErrorValueAndTest() {
        assertNotNull(userManagement.getUserById(1L));
	}	
}
