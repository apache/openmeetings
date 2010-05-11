package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.RecordingConversionJob;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RecordingConversionJobDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingConversionJobDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private RecordingConversionJobDaoImpl() {
	}

	private static RecordingConversionJobDaoImpl instance = null;

	public static synchronized RecordingConversionJobDaoImpl getInstance() {
		if (instance == null) {
			instance = new RecordingConversionJobDaoImpl();
		}

		return instance;
	}
	
	public Long addRecordingConversionJob(RecordingConversionJob recordingConversionJob) {
		try {
			
			log.debug("[addRecordingConversionJob] RecordingConversionJobId "+recordingConversionJob.getRecordingConversionJobId());
			log.debug("[addRecordingConversionJob] Recording_id "+recordingConversionJob.getRecording().getRecording_id());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long recordingConversionJobId = (Long) session.save(recordingConversionJob);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingConversionJobId;
		} catch (HibernateException ex) {
			log.error("[addRecordingConversionJob]: " , ex);
		} catch (Exception ex2) {
			log.error("[addRecordingConversionJob]: " , ex2);
		}
		return null;
	}

	public RecordingConversionJob getRecordingConversionJobsByRecordingConversionJobsId(long recordingConversionJobId) {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.recordingConversionJobId = :recordingConversionJobId ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("recordingConversionJobId", recordingConversionJobId);
			RecordingConversionJob recordingConversionJob = (RecordingConversionJob) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingConversionJob;
	
		} catch (HibernateException ex) {
			log.error("[getRecordingConversionJobsByRecordingConversionJobsId]: " , ex);
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobsByRecordingConversionJobsId]: " , ex2);
		}
		return null;
	}
	
	public RecordingConversionJob getRecordingConversionJobsByRecording(long recording_id) {
		try {
			
			String hql = "select c from RecordingConversionJob as c " +
						"where c.recording.recording_id = :recording_id ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("recording_id", recording_id);
			RecordingConversionJob recordingConversionJob = (RecordingConversionJob) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingConversionJob;
	
		} catch (HibernateException ex) {
			log.error("[getRecordingConversionJobsByRecording]: " , ex);
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			List<RecordingConversionJob> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
		} catch (HibernateException ex) {
			log.error("[getRecordingConversionJobs]: " , ex);
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			List<RecordingConversionJob> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
		} catch (HibernateException ex) {
			log.error("[getRecordingConversionJobs]: " , ex);
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			List<RecordingConversionJob> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
		} catch (HibernateException ex) {
			log.error("[getRecordingConversionJobs]: " , ex);
		} catch (Exception ex2) {
			log.error("[getRecordingConversionJobs]: " , ex2);
		}
		return null;
	}
	
	public void updateRecordingConversionJobs(RecordingConversionJob recordingConversionJob) {
		try {
			
			log.debug("updateRecordingConversionJobs: "+recordingConversionJob.getRecordingConversionJobId());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(recordingConversionJob);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (HibernateException ex) {
			log.error("[updateRecordingConversionJobs]: " , ex);
		} catch (Exception ex2) {
			log.error("[updateRecordingConversionJobs]: " , ex2);
		}
		
	}
	
}
