package org.openmeetings.test.dao;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityTransaction;

import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;
import org.openmeetings.app.hibernate.beans.user.Salutations;
import org.openmeetings.test.dao.base.AbstractTestCase;


public class SalutationEntityTest extends AbstractTestCase {
	
	
	public SalutationEntityTest(String testName) {
        super(testName);
    }
	
	final public void testAddSalutation() throws Exception {
		ArrayList<Long> Ids = new ArrayList<Long>();
		Ids.add(11L);
		PrivateMessagesDaoImpl.getInstance().updatePrivateMessagesToTrash(Ids, true, 0L);

		EntityTransaction tx = getEntityManager().getTransaction();
		Salutations sl = new Salutations();
		sl.setDeleted("false");
		sl.setName("addSalutation");
		sl.setFieldvalues_id(1000L);
		sl.setStarttime(new Date());
		tx.begin();
		try {
			getEntityManager().merge(sl);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}
		
	}
	
	final public void testAddFieldvalues() throws Exception {

		EntityTransaction tx = getEntityManager().getTransaction();
		tx.begin();
		Fieldvalues fl = new Fieldvalues();
		fl.setStarttime(new Date());
		fl.setName("test value");
		fl.setDeleted("false");
		try {
			getEntityManager().merge(fl);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}
	}
}
