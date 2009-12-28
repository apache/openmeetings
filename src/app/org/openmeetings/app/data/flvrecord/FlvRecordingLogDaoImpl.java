package org.openmeetings.app.data.flvrecord;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingLog;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long flvRecordingLogId = (Long) session.save(flvRecordingLog);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingLogId;
		} catch (HibernateException ex) {
			log.error("[addFLVRecordingLog]: ",ex);
		} catch (Exception ex2) {
			log.error("[addFLVRecordingLog]: ",ex2);
		}
		return -1L;
	}
}
