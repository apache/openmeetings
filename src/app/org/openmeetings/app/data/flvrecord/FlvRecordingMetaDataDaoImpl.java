package org.openmeetings.app.data.flvrecord;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
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
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			FlvRecordingMetaData flvRecordingMetaData = null;
			try {
				flvRecordingMetaData = (FlvRecordingMetaData) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingMetaData;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataById]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaDataByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND c.deleted <> :deleted ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingId", flvRecordingId);
			query.setParameter("deleted", "true");
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingMetaDatas;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataByRecording]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaDataAudioFlvsByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND (" +
						"(c.isScreenData = false) " +
							" AND " +
						"(c.isAudioOnly = true OR (c.isAudioOnly = false AND c.isVideoOnly = false))" +
					")";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingMetaDatas;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataAudioFlvsByRecording]: ",ex2);
		}
		return null;
	}
	
	public FlvRecordingMetaData getFlvRecordingMetaDataScreenFlvByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND c.isScreenData = true";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			if (flvRecordingMetaDatas.size() > 0) {
				return flvRecordingMetaDatas.get(0);
			}
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataScreenFlvByRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingMetaData(Long flvRecordingId, String freeTextUserName, 
					Date recordStart, Boolean isAudioOnly, Boolean isVideoOnly, 
					Boolean isScreenData, String streamName, Integer interiewPodId) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = new FlvRecordingMetaData();
			
			flvRecordingMetaData.setDeleted("false");
			
			flvRecordingMetaData.setFlvRecording(FlvRecordingDaoImpl.getInstance().getFlvRecordingById(flvRecordingId));
			flvRecordingMetaData.setFreeTextUserName(freeTextUserName);
			flvRecordingMetaData.setInserted(new Date());
			
			flvRecordingMetaData.setRecordStart(recordStart);
			
			flvRecordingMetaData.setIsAudioOnly(isAudioOnly);
			flvRecordingMetaData.setIsVideoOnly(isVideoOnly);
			flvRecordingMetaData.setIsScreenData(isScreenData);
			
			flvRecordingMetaData.setStreamName(streamName);
			
			flvRecordingMetaData.setInteriewPodId(interiewPodId);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecordingMetaData = session.merge(flvRecordingMetaData);
			Long flvRecordingMetaDataId = flvRecordingMetaData.getFlvRecordingMetaDataId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaData]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingMetaDataObj(FlvRecordingMetaData flvRecordingMetaData) {
		try {

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();

			flvRecordingMetaData = session.merge(flvRecordingMetaData);
			Long flvRecordingMetaDataId = flvRecordingMetaData.getFlvRecordingMetaDataId();

			tx.commit();
			PersistenceSessionUtil.closeSession(idf);

			return flvRecordingMetaDataId;

		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDataObj]: ", ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaDataEndDate(Long flvRecordingMetaDataId, 
										Date recordEnd) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = this.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			
			flvRecordingMetaData.setRecordEnd(recordEnd);
			
			log.debug("updateFlvRecordingMetaDataEndDate :: Start Date :"+flvRecordingMetaData.getRecordStart());
			log.debug("updateFlvRecordingMetaDataEndDate :: End Date :"+flvRecordingMetaData.getRecordEnd());
			
			this.updateFlvRecordingMetaData(flvRecordingMetaData);
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaDataInitialGap(Long flvRecordingMetaDataId, 
										long initalGap) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = this.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			
			flvRecordingMetaData.setInitialGapSeconds(Long.valueOf(initalGap).intValue());
			
			this.updateFlvRecordingMetaData(flvRecordingMetaData);
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaData(FlvRecordingMetaData flvRecordingMetaData) {
		try { 
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (flvRecordingMetaData.getFlvRecordingMetaDataId() == 0) {
				session.persist(flvRecordingMetaData);
			    } else {
			    	if (!session.contains(flvRecordingMetaData)) {
			    		session.merge(flvRecordingMetaData);
			    }
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaData]: ",ex2);
		}
		return null;
	}
	
}
