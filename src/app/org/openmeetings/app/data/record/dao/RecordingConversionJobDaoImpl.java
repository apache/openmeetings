package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.RecordingConversionJob;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecordingConversionJobDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingConversionJobDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public Long addRecordingConversionJob(RecordingConversionJob recordingConversionJob) {
		try {
			
			log.debug("[addRecordingConversionJob] RecordingConversionJobId "+recordingConversionJob.getRecordingConversionJobId());
			log.debug("[addRecordingConversionJob] Recording_id "+recordingConversionJob.getRecording().getRecording_id());
			
			recordingConversionJob = em.merge(recordingConversionJob);
			Long recordingConversionJobId = recordingConversionJob.getRecordingConversionJobId();
			
			return recordingConversionJobId;
		} catch (Exception ex2) {
			log.error("[addRecordingConversionJob]: " , ex2);
		}
		return null;
	}

	public RecordingConversionJob getRecordingConversionJobsByRecordingConversionJobsId(long recordingConversionJobId) {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.recordingConversionJobId = :recordingConversionJobId ";
			
			TypedQuery<RecordingConversionJob> query = em.createQuery(hql, RecordingConversionJob.class);
			query.setParameter("recordingConversionJobId", recordingConversionJobId);
			RecordingConversionJob recordingConversionJob = null;
			try {
				recordingConversionJob = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return recordingConversionJob;
	
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobsByRecordingConversionJobsId]: " , ex2);
		}
		return null;
	}
	
	public RecordingConversionJob getRecordingConversionJobsByRecording(long recording_id) {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.recording.recording_id = :recording_id ";
			
			TypedQuery<RecordingConversionJob> query = em.createQuery(hql, RecordingConversionJob.class);
			query.setParameter("recording_id", recording_id);
			RecordingConversionJob recordingConversionJob = null;
			try {
				recordingConversionJob = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return recordingConversionJob;
	
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobsByRecording]: " , ex2);
		}
		return null;
	}
	
	/**
	 * get all Conversion Jobs where END Time is not set for
	 * the SVG Conversion
	 * and the Batch process is not yet started
	 * 
	 * @return
	 */
	public List<RecordingConversionJob> getRecordingConversionJobs() {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.ended IS NULL " +
						"AND c.startedPngConverted IS NULL";
			
			TypedQuery<RecordingConversionJob> query = em.createQuery(hql, RecordingConversionJob.class);
			List<RecordingConversionJob> ll = query.getResultList();
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobs]: " , ex2);
		}
		return null;
	}
	
	/**
	 * Get all selected Conversion Jobs where SVG has 
	 * finished but Batch Process not yet
	 * 
	 * @return
	 */
	public List<RecordingConversionJob> getRecordingConversionBatchConversionJobs() {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.ended IS NOT NULL " +
						"AND c.endPngConverted IS NULL";
			
			TypedQuery<RecordingConversionJob> query = em.createQuery(hql, RecordingConversionJob.class);
			List<RecordingConversionJob> ll = query.getResultList();
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobs]: " , ex2);
		}
		return null;
	}
	
	
	public List<RecordingConversionJob> getRecordingConversionSWFConversionJobs() {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.endPngConverted IS NOT NULL " +
						"AND c.endSWFConverted IS NULL";
			
			TypedQuery<RecordingConversionJob> query = em.createQuery(hql, RecordingConversionJob.class);
			List<RecordingConversionJob> ll = query.getResultList();
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobs]: " , ex2);
		}
		return null;
	}
	
	public void updateRecordingConversionJobs(RecordingConversionJob recordingConversionJob) {
		try {
			
			log.debug("updateRecordingConversionJobs: "+recordingConversionJob.getRecordingConversionJobId());
			
			if (recordingConversionJob.getRecordingConversionJobId() == 0) {
				em.persist(recordingConversionJob);
		    } else {
		    	if (!em.contains(recordingConversionJob)) {
		    		em.merge(recordingConversionJob);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[updateRecordingConversionJobs]: " , ex2);
		}
		
	}
	
}
