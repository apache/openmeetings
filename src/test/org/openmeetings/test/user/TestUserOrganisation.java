package org.openmeetings.test.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.openmeetings.app.persistence.beans.domain.Organisation_Users;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserOrganisation extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private Organisationmanagement orgManagement;
	@Autowired
	private UsersDaoImpl usersDao;
	
	private Users getValidUser() {
		for (Users u : usersDao.getAllUsers()) {
			if ("false".equalsIgnoreCase(u.getDeleted()) && u.getOrganisation_users().size() > 0) {
				return u;
			}
		}
		fail("Unable to find valid user");
		return null;  //unreachable
	}
	
	@Test
	public void getUsersByOrganisationId() {
		Users u = getValidUser();
		Long orgId = u.getOrganisation_users().get(0).getOrganisation().getOrganisation_id();
		List<Users> ul = orgManagement.getUsersByOrganisationId(orgId, 0, 9999, "login", true);
		assertTrue("Default Organisation should contain at least 1 user: " + ul.size(), ul.size() > 0);
		
		Organisation_Users ou = orgManagement.getOrganisation_UserByUserAndOrganisation(u.getUser_id(), orgId);
		assertNotNull("Unable to found [organisation, user] pair - [" + orgId + "," + u.getUser_id() + "]", ou);
	}
	
	@Test
	public void addOrganisation() {
		Long orgId = orgManagement.addOrganisation("Test Org", 1); //inserted by not checked
		assertNotNull("New Organisation have valid id", orgId);
		
		List<Users> ul = orgManagement.getUsersByOrganisationId(orgId, 0, 9999, "login", true);
		assertTrue("New Organisation should contain NO users: " + ul.size(), ul.size() == 0);
		
		boolean found = false;
		List<Organisation> restL = orgManagement.getRestOrganisationsByUserId(3, 1, 0, 9999, "name", true);
		for (Organisation o : restL) {
			if (orgId.equals(o.getOrganisation_id())) {
				found = true;
				break;
			}
		}
		assertTrue("New organisation should not be included into organisation list of any user", found);
	}
}
