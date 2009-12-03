package org.openmeetings.app.data.flvrecord;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvRecordingMetaDataDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDataDaoImpl.class);

	private static FlvRecordingMetaDataDaoImpl instance;

	private FlvRecordingMetaDataDaoImpl() {}

	public static synchronized FlvRecordingMetaDataDaoImpl getInstance() {
		if (instance == null) {
			instance = new FlvRecordingMetaDataDaoImpl();
		}
		return instance;
	}
	
	public FlvRecordingMetaData getFlvRecordingMetaDataById(Long flvRecordingMetaDataId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecordingMetaDataId = :flvRecordingMetaDataId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			FlvRecordingMetaData flvRecordingMetaData = (FlvRecordingMetaData) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaData;
			
		} catch (HibernateException ex) {
			log.error("[getFlvRecordingMetaDataById]: ",ex);
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataById]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaDataByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("flvRecordingId", flvRecordingId);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDatas;
			
		} catch (HibernateException ex) {
			log.error("[getFlvRecordingMetaDataByRecording]: ",ex);
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataByRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingMetaData(Long flvRecordingId, String freeTextUserName, 
					Date recordEnd, Date recordStart, Boolean isAudioOnly, Boolean isVideoOnly, 
					Boolean isScreenData, String streamName) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = new FlvRecordingMetaData();
			
			flvRecordingMetaData.setDeleted("false");
			
			flvRecordingMetaData.setFlvRecording(FlvRecordingDaoImpl.getInstance().getFlvRecordingById(flvRecordingId));
			flvRecordingMetaData.setFreeTextUserName(freeTextUserName);
			flvRecordingMetaData.setInserted(new Date());
			
			flvRecordingMetaData.setRecordEnd(recordEnd);
			flvRecordingMetaData.setRecordStart(recordStart);
			
			flvRecordingMetaData.setIsAudioOnly(isAudioOnly);
			flvRecordingMetaData.setIsVideoOnly(isVideoOnly);
			flvRecordingMetaData.setIsScreenData(isScreenData);
			
			flvRecordingMetaData.setStreamName(streamName);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long flvRecordingMetaDataId = (Long) session.save(flvRecordingMetaData);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDataId;
			
		} catch (HibernateException ex) {
			log.error("[addFlvRecordingMetaData]: ",ex);
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaData]: ",ex2);
		}
		return null;
	}
	
	public Long updateFlvRecordingMetaDataEndDate(Long flvRecordingMetaDataId, 
										Date recordEnd) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = this.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			
			flvRecordingMetaData.setRecordEnd(recordEnd);
			
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(flvRecordingMetaData);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingMetaDataId;
			
		} catch (HibernateException ex) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex2);
		}
		return null;
	}
	
}
