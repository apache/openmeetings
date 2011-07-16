package org.openmeetings.test.dao;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.hibernate.beans.user.UserContacts;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class UserContactsDaoImplTest extends AbstractTestCase {

	public UserContactsDaoImplTest(String name){
		super(name);
	}
	
	final public void testUserContactsDaoImpl() throws Exception	{
		
		// Get instance
		UserContactsDaoImpl  contactDaoImpl = UserContactsDaoImpl.getInstance();
		
		assertNotNull("Cann't access to contacts dao implimentation", contactDaoImpl);
		Long id = contactDaoImpl.addUserContact(1L, 1L, false, "");
		assertTrue("New contact cann't added", id > 0);
		UserContacts userContact = contactDaoImpl.getUserContacts(id);

		assertTrue("Contact should be the same with user", userContact.getContact().equals(Usermanagement.getInstance().getUserById(id)));
		
	}
	
}
