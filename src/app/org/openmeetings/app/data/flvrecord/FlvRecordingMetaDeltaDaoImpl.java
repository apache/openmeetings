package org.openmeetings.app.data.flvrecord;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Transactional
public class FlvRecordingMetaDeltaDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDeltaDaoImpl.class);
	@PersistenceContext
	private EntityManager em;
	
	public List<FlvRecordingMetaDelta> getFlvRecordingMetaDeltaByMetaId(Long flvRecordingMetaDataId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaDelta c " +
					"WHERE c.flvRecordingMetaDataId = :flvRecordingMetaDataId";
			
			Query query = em.createQuery(hql);
			query.setParameter("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = query.getResultList();
			
			return flvRecordingMetaDeltas;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDeltaByMetaId]: ",ex2);
		}
		return null;
	}
	
	
	
	public Long addFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			flvRecordingMetaDelta = em.merge(flvRecordingMetaDelta);
			Long flvRecordingMetaDeltaId = flvRecordingMetaDelta.getFlvRecordingMetaDeltaId();
			
			log.debug("flvRecordingMetaDeltaId "+flvRecordingMetaDeltaId);
			
			return flvRecordingMetaDeltaId;
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}
	
	public Long updateFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			if (flvRecordingMetaDelta.getFlvRecordingMetaDataId() == 0) {
				em.persist(flvRecordingMetaDelta);
		    } else {
		    	if (!em.contains(flvRecordingMetaDelta)) {
		    		em.merge(flvRecordingMetaDelta);
			    }
			}
			
			return flvRecordingMetaDelta.getFlvRecordingMetaDataId();
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}	
	
}
