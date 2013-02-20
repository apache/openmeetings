package org.apache.openmeetings.test.user;

import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestStateToLower extends AbstractOpenmeetingsSpringTest {
	
	@Autowired
	private StateDao stateDao;
	
	@Test
	public void doTestQuery() {
		
		State state_alabania = stateDao.getStateByName("Albania");
		
		State state_alabania_lower = stateDao.getStateByName("albaNia");
		
		assertTrue(state_alabania != null);
		assertTrue(state_alabania_lower != null);
		assertTrue(state_alabania.getState_id().equals(3L));
		assertTrue(state_alabania_lower.getState_id().equals(3L));
		assertTrue(state_alabania.getState_id().equals(state_alabania_lower.getState_id()));
		
	}

}
