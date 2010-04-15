package org.openmeetings.app.sip.xmlrpc.custom.dao;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.sip.OpenXGReturnObject;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OpenXGReturnObjectDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGReturnObjectDaoImpl.class, "openmeetings");

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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();

		session.save(openXGReturnObject);

		tx.commit();
		HibernateUtil.closeSession(idf);
		
	} catch (HibernateException ex) {
		log.error("[addOpenXGReturnObject]: ",ex);
	} catch (Exception ex2) {
		log.error("[addOpenXGReturnObject]: ",ex2);
	}
	return null;
}
}
