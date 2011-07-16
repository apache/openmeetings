package org.openmeetings.app.data.flvrecord;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDeltas;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDeltaByMetaId]: ",ex2);
		}
		return null;
	}
	
	
	
	public Long addFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecordingMetaDelta = session.merge(flvRecordingMetaDelta);
			Long flvRecordingMetaDeltaId = flvRecordingMetaDelta.getFlvRecordingMetaDeltaId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			log.debug("flvRecordingMetaDeltaId "+flvRecordingMetaDeltaId);
			
			return flvRecordingMetaDeltaId;
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}
	
	public Long updateFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (flvRecordingMetaDelta.getFlvRecordingMetaDataId() == 0) {
				session.persist(flvRecordingMetaDelta);
			    } else {
			    	if (!session.contains(flvRecordingMetaDelta)) {
			    		session.merge(flvRecordingMetaDelta);
			    }
			}
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDelta.getFlvRecordingMetaDataId();
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}	
	
}
