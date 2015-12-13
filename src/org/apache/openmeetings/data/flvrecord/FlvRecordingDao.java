/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.data.flvrecord;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Transactional
public class FlvRecordingDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingDao.class,
			OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public FlvRecording getFlvRecordingById(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecording c " +
					"WHERE c.flvRecordingId = :flvRecordingId";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			FlvRecording flvRecording = null;
			try {
				flvRecording = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			return flvRecording;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingById]: ",ex2);
		}
		return null;
	}

	public FlvRecording getRecordingByHash(String hash) {
		try {
			TypedQuery<FlvRecording> query = em.createNamedQuery("getRecordingByHash", FlvRecording.class);
			query.setParameter("fileHash", hash);
			
			try {
				return query.getSingleResult();
		    } catch (NoResultException ex) {
		    	//noop
		    }
		} catch (Exception ex2) {
			log.error("[getRecordingByHash]: ",ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordings() {
		try { 
			
			String hql = "SELECT c FROM FlvRecording c " +
							"WHERE c.deleted <> :deleted ";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordings = query.getResultList();
			
			return flvRecordings;
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ",ex2);
		}
		return null;
	}
	
	
	public List<FlvRecording> getFlvRecordingByExternalUserId(String externalUserId) {
		try { 
			
			log.debug("getFlvRecordingByExternalUserId :externalUserId: "+externalUserId);
			
			String hql = "SELECT c FROM FlvRecording c, Room r, User u " +
					"WHERE c.room_id = r.rooms_id " +
					"AND c.insertedBy = u.user_id " +
					"AND u.externalUserId LIKE :externalUserId " +
					"AND c.deleted <> :deleted ";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("externalUserId", externalUserId);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
						
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
			
			String hql = "SELECT c FROM FlvRecording c, Room r " +
					"WHERE c.room_id = r.rooms_id " +
					"AND r.externalRoomType LIKE :externalRoomType " +
					"AND c.insertedBy LIKE :insertedBy " +
					"AND c.deleted <> :deleted ";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("insertedBy", insertedBy);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
						
			log.debug("getFlvRecordingByExternalRoomType :: "+flvRecordingList.size());
			
			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getAllFlvRecordings() {
		try { 
			String hql = "SELECT c FROM FlvRecording c LEFT JOIN FETCH c.flvRecordingMetaData";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			
			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecording> getFlvRecordingByExternalRoomType(String externalRoomType) {
		try { 
			
			log.debug("getFlvRecordingByExternalRoomType :externalRoomType: "+externalRoomType);
			
			String hql = "SELECT c FROM FlvRecording c, Room r " +
					"WHERE c.room_id = r.rooms_id " +
					"AND r.externalRoomType LIKE :externalRoomType " +
					"AND c.deleted <> :deleted ";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
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
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("organization_id", organization_id);
			query.setParameter("deleted", true);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("ownerId",ownerId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
					"AND (c.parentFileExplorerItemId IS NULL OR c.parentFileExplorerItemId = :parentFileExplorerItemId)" +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("ownerId",ownerId);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("room_id",room_id);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
			
			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
			List<FlvRecording> flvRecordingList = query.getResultList();
			
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
			
			flvRecording.setDeleted(false);
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
			
			flvRecording = em.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
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
			
			flvRecording.setDeleted(false);
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
			
			flvRecording = em.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
			return flvRecordingId;
		} catch (Exception ex2) {
			log.error("[addFlvRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingObj(FlvRecording flvRecording) {
		try { 
			
			flvRecording = em.merge(flvRecording);
			Long flvRecordingId = flvRecording.getFlvRecordingId();
			
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
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
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
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}
	
	public void updateFlvRecordingProgress(Long flvRecordingId, Integer progress) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			fId.setProgressPostProcessing(progress);
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
	}
	
	/**
	 * @param fileExplorerItemId
	 */
	public boolean deleteFlvRecording(Long flvRecordingId) {
		try {
			
			FlvRecording fId = this.getFlvRecordingById(flvRecordingId);
			
			if (fId == null) {
				return false;
			}
			
			fId.setDeleted(true);
			fId.setUpdated(new Date());
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
			return true;
			
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ",ex2);
		}
		
		return false;
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
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[updateFileOrFolderName]: ",ex2);
		}
	}
	
	public void updateFlvRecording(FlvRecording fId) {
		try {
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
			
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
			
			if (fId.getFlvRecordingId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
		} catch (Exception ex2) {
			log.error("[moveFile]: ",ex2);
		}
	}
	
}
