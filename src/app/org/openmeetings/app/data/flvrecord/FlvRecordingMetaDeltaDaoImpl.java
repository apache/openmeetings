package org.openmeetings.app.data.flvrecord;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingMetaDelta;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class FlvRecordingMetaDeltaDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDeltaDaoImpl.class);

	private static FlvRecordingMetaDeltaDaoImpl instance;

	private FlvRecordingMetaDeltaDaoImpl() {}

	public static synchronized FlvRecordingMetaDeltaDaoImpl getInstance() {
		if (instance == null) {
			instance = new FlvRecordingMetaDeltaDaoImpl();
		}
		return instance;
	}
	
	public List<FlvRecordingMetaDelta> getFlvRecordingMetaDeltaByMetaId(Long flvRecordingMetaDataId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaDelta c " +
					"WHERE c.flvRecordingMetaDataId = :flvRecordingMetaDataId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDeltas;
		} catch (HibernateException ex) {
			log.error("[getFlvRecordingMetaDeltaByMetaId]: ",ex);
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDeltaByMetaId]: ",ex2);
		}
		return null;
	}
	
	
	
	public Long addFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long flvRecordingMetaDeltaId = (Long) session.save(flvRecordingMetaDelta);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			log.debug("flvRecordingMetaDeltaId "+flvRecordingMetaDeltaId);
			
			return flvRecordingMetaDeltaId;
		} catch (HibernateException ex) {
			log.error("[addFlvRecordingMetaDelta]: ",ex);
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}
	
	public Long updateFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(flvRecordingMetaDelta);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDelta.getFlvRecordingMetaDataId();
		} catch (HibernateException ex) {
			log.error("[updateFlvRecordingMetaDelta]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}	
	
}
