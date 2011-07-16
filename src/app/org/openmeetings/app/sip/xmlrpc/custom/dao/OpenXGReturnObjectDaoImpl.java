package org.openmeetings.app.sip.xmlrpc.custom.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.hibernate.beans.sip.OpenXGReturnObject;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OpenXGReturnObjectDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGReturnObjectDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private OpenXGReturnObjectDaoImpl() {
	}

	private static OpenXGReturnObjectDaoImpl instance = null;
 
	public static synchronized OpenXGReturnObjectDaoImpl getInstance() {
		if (instance == null) {
			instance = new OpenXGReturnObjectDaoImpl();
		}
		return instance;
	}
	
	public String addOpenXGReturnObject(OpenXGReturnObject openXGReturnObject) {
	try {
		
		openXGReturnObject.setInserted(new Date());
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();

		session.merge(openXGReturnObject);

		tx.commit();
		HibernateUtil.closeSession(idf);
		
	} catch (Exception ex2) {
		log.error("[addOpenXGReturnObject]: ",ex2);
	}
	return null;
}
}
