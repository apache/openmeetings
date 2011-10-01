package org.openmeetings.test.poll;

import java.util.Random;

import org.junit.Test;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

public class TestClientListManager extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private ClientListManager clientListManager;
	
	@Test
	public void addClientListItem() {
		Random rnd = new Random();
		assertNotNull("RoomClientId created is null", clientListManager.addClientListItem(rnd.nextLong() + "ABCDE" + rnd.nextLong(), "scopeName", 66666, "remoteAddress", "swfUrl"));
	}

}
