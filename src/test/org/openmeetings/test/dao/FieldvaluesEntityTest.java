package org.openmeetings.test.dao;

import java.util.Date;

import javax.persistence.EntityTransaction;

import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class FieldvaluesEntityTest extends AbstractTestCase {
	
	public FieldvaluesEntityTest(String name){
		super(name);
	}

	final public void testAddFieldvalues() throws Exception {

		EntityTransaction tx = getEntityManager().getTransaction();
		tx.begin();
		Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
		flv.setStarttime(new Date());
		flv.setValue("test");
		flv.setLanguage_id(33L);
		flv.setDeleted("false");
		try {
			flv = getEntityManager().merge(flv);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}

		tx.begin();
		Fieldvalues fl = new Fieldvalues();
		fl.setStarttime(new Date());
		fl.setName("test value");
		fl.setFieldlanguagesvalue(flv);
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
