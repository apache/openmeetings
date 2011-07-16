package org.openmeetings.test.dao;

import java.util.Date;
import java.util.List;

import org.openmeetings.test.dao.base.AbstractTestCase;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openmeetings.app.hibernate.beans.basic.Configuration;


public class CriteriaBuilderTest extends AbstractTestCase {
	
	public CriteriaBuilderTest(String name){
		super(name);
	}

	final public void testCriteriaBuilder() throws Exception{
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Configuration> cq = cb.createQuery(Configuration.class);
		Root<Configuration> c = cq.from(Configuration.class);
		Predicate condition = cb.equal(c.get("deleted"), "false");
		cq.where(condition);
		TypedQuery<Configuration> q = em.createQuery(cq); 
		List<Configuration> result = q.getResultList();
		try {
			tx.commit();
			assertTrue("result should be empty", result.size()==0);
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}

		// add new configuration
		tx.begin();
		Configuration conf = new Configuration();
		conf.setStarttime(new Date());
		conf.setDeleted("false");
		conf.setConf_key("key1");
		conf.setConf_value("value1");
		try {
			conf = getEntityManager().merge(conf);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}
		
		result = q.getResultList();
		assertTrue("result should not be empty", result.size()>0);

		// delete configuration
		Long id = conf.getConfiguration_id();
		tx.begin();
		try {
			conf = getEntityManager().find(Configuration.class, id);
			getEntityManager().remove(conf);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new Exception(e.getMessage());
		}
		result = q.getResultList();
		assertTrue("result should be empty", result.size() == 0);
	}

}
