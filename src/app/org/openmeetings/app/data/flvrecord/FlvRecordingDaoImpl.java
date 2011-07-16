package org.openmeetings.app.data.flvrecord;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class FlvRecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingDaoImpl.class);

	private static FlvRecordingDaoImpl instance;

	private FlvRecordingDaoImpl() {}

	public static synchronized FlvRecordingDaoImpl getInstance() {
		if (instance == null) {
			instance = new FlvRecordingDaoImpl();
		}
		return instance;
	}
	
	public FlvRecording getFlvRecordingById(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.flvRecordingId = :flvRecordingId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			FlvRecording flvRecording = null;
			try {
				flvRecording = (FlvRecording) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			session.refresh(flvRecording);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecording;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingById]: ",ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordings() {
		try { 
			
			String hql = "SELECT c FROM FlvRecording c " +
							"WHERE c.deleted <> :deleted ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordings = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordings;
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ",ex2);
		}
		return null;
	}
	
	
	public List<FlvRecording> getFlvRecordingByExternalUserId(Long externalUserId) {
		try { 
			
			log.debug("getFlvRecordingByExternalUserId :externalUserId: "+externalUserId);
			
			String hql = "SELECT c FROM FlvRecording c, Rooms r, Users u " +
					"WHERE c.room_id = r.rooms_id " +
					"AND c.insertedBy = u.user_id " +
					"AND u.externalUserId = :externalUserId " +
					"AND c.deleted <> :deleted ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("externalUserId", externalUserId);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
						
			log.debug("getFlvRecordingByExternalRoomType :: "+flvRecordingList.size());
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingByExternalRoomTypeAndCreator(String externalRoomType, Long insertedBy) {
		try { 
			
			log.debug("getFlvRecordingByExternalRoomType :externalRoomType: "+externalRoomType);
			
			String hql = "SELECT c FROM FlvRecording c, Rooms r " +
					"WHERE c.room_id = r.rooms_id " +
					"AND r.externalRoomType LIKE :externalRoomType " +
					"AND c.insertedBy LIKE :insertedBy " +
					"AND c.deleted <> :deleted ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("insertedBy", insertedBy);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
						
			log.debug("getFlvRecordingByExternalRoomType :: "+flvRecordingList.size());
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getAllFlvRecordings() {
		try { 
			
			String hql = "SELECT c FROM FlvRecording c ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			
			List<FlvRecording> flvRecordings = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordings;
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingByExternalRoomType(String externalRoomType) {
		try { 
			
			log.debug("getFlvRecordingByExternalRoomType :externalRoomType: "+externalRoomType);
			
			String hql = "SELECT c FROM FlvRecording c, Rooms r " +
					"WHERE c.room_id = r.rooms_id " +
					"AND r.externalRoomType LIKE :externalRoomType " +
					"AND c.deleted <> :deleted ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			
			log.debug("getFlvRecordingByExternalRoomType :: "+flvRecordingList.size());
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingsPublic() {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND (c.ownerId IS NULL OR c.ownerId = 0)  " +
					"AND (c.parentFileExplorerItemId IS NULL OR c.parentFileExplorerItemId = 0) " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingsPublic]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingRootByPublic(Long organization_id) {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.ownerId IS NULL " +
					"AND c.organization_id = :organization_id " +
					"AND (c.parentFileExplorerItemId IS NULL OR c.parentFileExplorerItemId = 0) " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("organization_id", organization_id);
			query.setParameter("deleted", "true");
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByOwner]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingRootByOwner(Long ownerId) {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.ownerId = :ownerId " +
					"AND (c.parentFileExplorerItemId IS NULL OR c.parentFileExplorerItemId = 0) " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("ownerId",ownerId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByOwner]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingByOwner(Long ownerId, Long parentFileExplorerItemId) {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.ownerId = :ownerId " +
					"AND c.parentFileExplorerItemId = :parentFileExplorerItemId " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("ownerId",ownerId);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByOwner]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingByRoomId(Long room_id) {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.room_id = :room_id " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("room_id",room_id);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByOwner]: ",ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordingByParent(Long parentFileExplorerItemId) {
		try {
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.parentFileExplorerItemId = :parentFileExplorerItemId " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByParent]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvFolderRecording(String fileHash, String fileName, Long fileSize, Long user_id, 
			Long room_id, Date recordStart, Date recordEnd, Long ownerId, String comment, 
			Long  parentFileExplorerItemId, Long organization_id) {
		try { 
			
			FlvRecording flvRecording = new FlvRecording();
			
			flvRecording.setParentFileExplorerItemId(parentFileExplorerItemId);
			
			flvRecording.setDeleted("false");
			flvRecording.setFileHash(fileHash);
			flvRecording.setFileName(fileName);
			flvRecording.setFileSize(fileSize);
			flvRecording.setInserted(new Date());
			flvRecording.setInsertedBy(user_id);
			flvRecording.setIsFolder(true);
			flvRecording.setIsImage(false);
			flvRecording.setIsPresentation(false);
			flvRecording.setIsRecording(true);
			flvRecording.setComment(comment);
			flvRecording.setOrganization_id(organization_id);
			
			flvRecording.setRoom_id(room_id);
			flvRecording.setRecordStart(recordStart);
			flvRecording.setRecordEnd(recordEnd);
			
			flvRecording.setOwnerId(ownerId);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecording = session.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingId;
		} catch (Exception ex2) {
			log.error("[addFlvRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecording(String fileHash, String fileName, Long fileSize, Long user_id, 
			Long room_id, Date recordStart, Date recordEnd, Long ownerId, String comment, 
			String recorderStreamId, Integer width, Integer height, Boolean isInterview) {
		try { 
			
			FlvRecording flvRecording = new FlvRecording();
			
			flvRecording.setDeleted("false");
			flvRecording.setFileHash(fileHash);
			flvRecording.setFileName(fileName);
			flvRecording.setFileSize(fileSize);
			flvRecording.setInserted(new Date());
			flvRecording.setInsertedBy(user_id);
			flvRecording.setIsFolder(false);
			flvRecording.setIsImage(false);
			flvRecording.setIsPresentation(false);
			flvRecording.setIsRecording(true);
			flvRecording.setComment(comment);
			flvRecording.setIsInterview(isInterview);
			
			flvRecording.setRoom_id(room_id);
			flvRecording.setRecordStart(recordStart);
			flvRecording.setRecordEnd(recordEnd);
			
			flvRecording.setWidth(width);
			flvRecording.setHeight(height);
			
			flvRecording.setOwnerId(ownerId);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecording = session.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingId;
		} catch (Exception ex2) {
			log.error("[addFlvRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingObj(FlvRecording flvRecording) {
		try { 
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			flvRecording = session.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return flvRecordingId;
		} catch (Exception ex2) {
			log.error("[addFlvRecording]: ",ex2);
		}
		return null;
	}
	
	public void updateFlvRecordingOrganization(Long flvRecordingId, Long organization_id) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setOrganization_id(organization_id);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}
	
	public void updateFlvRecordingEndTime(Long flvRecordingId, Date recordEnd, Long organization_id) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setProgressPostProcessing(0);
			fId.setRecordEnd(recordEnd);
			fId.setOrganization_id(organization_id);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}
	
	public void updateFlvRecordingProgress(Long flvRecordingId, Integer progress) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setProgressPostProcessing(progress);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}
	
	/**
	 * @param fileExplorerItemId
	 */
	public void deleteFlvRecording(Long flvRecordingId) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setDeleted("true");
			fId.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}

	/**
	 * @param fileExplorerItemId
	 * @param fileName
	 */
	public void updateFileOrFolderName(Long flvRecordingId, String fileName) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setFileName(fileName);
			fId.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[updateFileOrFolderName]: ",ex2);
		}
	}
	
	public void updateFlvRecording(FlvRecording fId) {
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[updateFileOrFolderName]: ",ex2);
		}
	}

	/**
	 * @param fileExplorerItemId
	 * @param newParentFileExplorerItemId
	 * @param isOwner
	 */
	public void moveFile(Long flvRecordingId, Long parentFileExplorerItemId, 
				Boolean isOwner, Long ownerId) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setParentFileExplorerItemId(parentFileExplorerItemId);
			
			if (parentFileExplorerItemId == 0) {
				if (isOwner) {
					//move to personal Folder
					fId.setOwnerId(ownerId);
				} else {
					//move to public room folder
					fId.setOwnerId(null);
				}
			} else {
				fId.setOwnerId(null);
			}
			
			fId.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (fId.getFlvRecordingId() == 0) {
				session.persist(fId);
			    } else {
			    	if (!session.contains(fId)) {
			    		session.merge(fId);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[moveFile]: ",ex2);
		}
	}
	
}
