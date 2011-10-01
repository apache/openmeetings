package org.openmeetings.test.poll;

import org.junit.Test;
import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

public class TestPollManagement extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private PollManagement pollManagement;
	
	@Test
	public void addPollType() {
		long maxId = 1;
		for (PollType pt : pollManagement.getPollTypes()) {
			maxId = Math.max(maxId, pt.getPollTypesId());
		}
		assertNotNull("Poll created is null", pollManagement.addPollType(26L, false));
	}
}
