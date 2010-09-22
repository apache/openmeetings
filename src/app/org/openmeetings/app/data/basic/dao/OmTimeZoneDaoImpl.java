package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.basic.OmTimeZone;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OmTimeZoneDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OmTimeZoneDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private OmTimeZoneDaoImpl() {
	}

	private static OmTimeZoneDaoImpl instance = null;
 
	public static synchronized OmTimeZoneDaoImpl getInstance() {
		if (instance == null) {
			instance = new OmTimeZoneDaoImpl();
		}
		return instance;
	}
	
	public Long addOmTimeZone(String name, String label, String iCal) {
		try {
			
			OmTimeZone omTimeZone = new OmTimeZone();
			
			omTimeZone.setJname(name);
			omTimeZone.setLabel(label);
			omTimeZone.setIcal(iCal);
			omTimeZone.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long omTimeZoneId = (Long) session.save(omTimeZone);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return omTimeZoneId;
			
		} catch (Exception ex) {
			log.error("[addOmTimeZone]",ex);
		}
		return null;
	}
	
	public List<OmTimeZone> getOmTimeZones() {
		try {
			String hql = "select sl from OmTimeZone as sl ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			List<OmTimeZone> sList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			for (OmTimeZone omTimeZone  : sList) {
				omTimeZone.setFrontEndLabel(omTimeZone.getJname()+ " (" + omTimeZone.getLabel() + ")");
			}
			
			return sList;
			
		} catch (HibernateException ex) {
			log.error("[getOmTimeZones]: ",ex);
		} catch (Exception ex2) {
			log.error("[getOmTimeZones]: ",ex2);
		}
		return null;
	}
	
	public OmTimeZone getOmTimeZone(String jname) {
		try {
			String hql = "select sl from OmTimeZone as sl " +
							"WHERE sl.jname LIKE :jname";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("jname", jname);
			List<OmTimeZone> sList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (sList.size() > 0) {
				return sList.get(0);
			}
			
		} catch (HibernateException ex) {
			log.error("[getOmTimeZone]: ",ex);
		} catch (Exception ex2) {
			log.error("[getOmTimeZone]: ",ex2);
		}
		return null;
	}

}
