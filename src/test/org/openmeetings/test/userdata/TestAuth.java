package org.openmeetings.test.userdata;

import org.junit.Test;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAuth extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private ManageCryptStyle manageCryptStyle;
	@Autowired
	private MainService mService;

	@Test
	public void testTestAuth() {
		Sessiondata sessionData = mService.getsessiondata();

		System.out.println("sessionData: " + sessionData.getSession_id());

		String tTemp = manageCryptStyle.getInstanceOfCrypt().createPassPhrase(
				"test");

		System.out.println("tTemp: " + tTemp);

	}

}
