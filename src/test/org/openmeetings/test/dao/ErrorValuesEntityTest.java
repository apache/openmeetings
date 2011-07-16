package org.openmeetings.test.dao;

import java.util.Date;

import javax.persistence.EntityTransaction;

import org.openmeetings.app.hibernate.beans.basic.ErrorValues;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class ErrorValuesEntityTest extends AbstractTestCase {

	public ErrorValuesEntityTest(String name){
		super(name);
	}
	
	final public void testAddErrorValues() throws Exception {

		EntityTransaction tx = getEntityManager().getTransaction();
		ErrorValues eValue = new ErrorValues();
		eValue.setErrorvalues_id(1L);
		eValue.setErrortype_id(1L);
		eValue.setDeleted("false");
		eValue.setStarttime(new Date());
		eValue.setFieldvalues_id(new Long(322));
		tx.begin();
		try {
			getEntityManager().merge(eValue);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}
	}
}
