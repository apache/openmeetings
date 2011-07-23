package org.openmeetings.app.data.flvrecord;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingLog;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvRecordingLogDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingLogDaoImpl.class);

	private static FlvRecordingLogDaoImpl instance;

	private FlvRecordingLogDaoImpl() {}

	public static synchronized FlvRecordingLogDaoImpl getInstance() {
		if (instance == null) {
			instance = new FlvRecordingLogDaoImpl();
		}
		return instance;
	}
	
	public List<FlvRecordingLog> getFLVRecordingLogByRecordingId(Long flvRecordingId){
		try {
			String hql = "select c from FlvRecordingLog as c where c.flvRecording.flvRecordingId = :flvRecordingId";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingId", flvRecordingId);
			List<FlvRecordingLog> flvRecordingList = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingList;
			
		} catch (Exception ex2) {
			log.error("[getFLVRecordingLogByRecordingId] ", ex2);
		}
		return null;
	}	
	
	public void deleteFLVRecordingLogByRecordingId(Long flvRecordingId){
		try {
			List<FlvRecordingLog> flvRecordingLogs = this.getFLVRecordingLogByRecordingId(flvRecordingId);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			for (FlvRecordingLog flvRecordingLog : flvRecordingLogs) {
				flvRecordingLog = session.find(FlvRecordingLog.class, flvRecordingLog.getFlvRecordingLogId());
				session.remove(flvRecordingLog);
			}
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[deleteFLVRecordingLogByRecordingId] ", ex2);
		}
	}
	
	public Long addFLVRecordingLog(String msgType, FlvRecording flvRecording, HashMap<String, Object> returnMap) {
		try { 
			
			String exitValue = returnMap.get("exitValue").toString();
			
			String fullMessage = "";
			
			for (Iterator<String> iter = returnMap.keySet().iterator();iter.hasNext();) {
				String key = iter.next();
				String value = returnMap.get(key).toString();
				fullMessage += key + "-" + value + "<br/>";
			}
			
			FlvRecordingLog flvRecordingLog = new FlvRecordingLog();
			
			flvRecordingLog.setInserted(new Date());
			flvRecordingLog.setExitValue(exitValue);
			flvRecordingLog.setFlvRecording(flvRecording);
			flvRecordingLog.setFullMessage(fullMessage);
			flvRecordingLog.setMsgType(msgType);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecordingLog = session.merge(flvRecordingLog);
			Long flvRecordingLogId = flvRecordingLog.getFlvRecordingLogId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return flvRecordingLogId;
		} catch (Exception ex2) {
			log.error("[addFLVRecordingLog]: ",ex2);
		}
		return -1L;
	}
	
}
