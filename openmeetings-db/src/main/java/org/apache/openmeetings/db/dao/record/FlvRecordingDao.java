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
package org.apache.openmeetings.db.dao.record;

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.OGG_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getMp4Recording;
import static org.apache.openmeetings.util.OmFileHelper.getOggRecording;
import static org.apache.openmeetings.util.OmFileHelper.getRecording;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.file.RecordingContainerData;
import org.apache.openmeetings.db.dto.file.RecordingObject;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecording.Status;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 * 
 */
@Transactional
public class FlvRecordingDao {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingDao.class, webAppRootKey);
	
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao userDao;

	public FlvRecording get(Long flvRecordingId) {
		try {
			TypedQuery<FlvRecording> query = em.createNamedQuery("getRecordingById", FlvRecording.class);
			query.setParameter("id", flvRecordingId);

			FlvRecording flvRecording = null;
			try {
				flvRecording = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return flvRecording;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingById]: ", ex2);
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
				// noop
			}
		} catch (Exception ex2) {
			log.error("[getRecordingByHash]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordings() {
		try {
			return em.createQuery("SELECT c FROM FlvRecording c WHERE c.deleted = false", FlvRecording.class)
					.getResultList();
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ", ex2);
		}
		return null;
	}

	public List<RecordingObject> getFlvRecordingByExternalUserId(String externalUserId, String externalUserType) {
		try {
			log.debug("getFlvRecordingByExternalUserId :externalUserId: {}; externalType: {}", externalUserId, externalUserType);

			TypedQuery<RecordingObject> query = em.createNamedQuery("getRecordingsByExternalUser", RecordingObject.class);
			query.setParameter("externalUserId", externalUserId);
			query.setParameter("externalUserType", externalUserType);

			List<RecordingObject> flvRecordingList = query.getResultList();

			log.debug("getFlvRecordingByExternalUserId :: " + flvRecordingList.size());

			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalUserId]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordingByExternalRoomTypeAndCreator(String externalRoomType, Long insertedBy) {
		try {

			log.debug("getFlvRecordingByExternalRoomType :externalRoomType: " + externalRoomType);

			String hql = "SELECT c FROM FlvRecording c, Room r WHERE c.room_id = r.rooms_id "
					+ "AND r.externalRoomType LIKE :externalRoomType AND c.insertedBy LIKE :insertedBy "
					+ "AND c.deleted = false";

			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("insertedBy", insertedBy);
			query.setParameter("deleted", true);

			List<FlvRecording> flvRecordingList = query.getResultList();

			log.debug("getFlvRecordingByExternalRoomType :: " + flvRecordingList.size());

			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getAllFlvRecordings() {
		try {
			String hql = "SELECT c FROM FlvRecording c LEFT JOIN FETCH c.flvRecordingMetaData ORDER BY c.flvRecordingId";

			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getFlvRecordings]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordingByExternalRoomType(String externalRoomType) {
		try {

			log.debug("getFlvRecordingByExternalRoomType :externalRoomType: " + externalRoomType);

			String hql = "SELECT c FROM FlvRecording c, Room r " + "WHERE c.room_id = r.rooms_id "
					+ "AND r.externalRoomType LIKE :externalRoomType " + "AND c.deleted <> :deleted ";

			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("deleted", true);

			List<FlvRecording> flvRecordingList = query.getResultList();

			log.debug("getFlvRecordingByExternalRoomType :: " + flvRecordingList.size());

			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByExternalRoomType]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordingRootByPublic(Long orgId) {
		TypedQuery<FlvRecording> q = em.createNamedQuery(orgId == null ? "getRecordingsPublic" : "getRecordingsByOrganization", FlvRecording.class);
		if (orgId != null) {
			q.setParameter("organization_id", orgId);
		}
		return q.getResultList();
	}

	public List<FlvRecording> getFlvRecordingRootByOwner(Long ownerId) {
		return em.createNamedQuery("getRecordingsByOwner", FlvRecording.class).setParameter("ownerId", ownerId).getResultList();
	}

	public List<FlvRecording> getFlvRecordingByRoomId(Long room_id) {
		try {

			String hql = "SELECT c FROM FlvRecording c " + "WHERE c.deleted <> :deleted " + "AND c.room_id = :room_id "
					+ "ORDER BY c.folder DESC, c.fileName ";

			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("room_id", room_id);

			List<FlvRecording> flvRecordingList = query.getResultList();

			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByOwner]: ", ex2);
		}
		return null;
	}

	public List<FlvRecording> getFlvRecordingByParent(Long parentFileExplorerItemId) {
		try {

			String hql = "SELECT c FROM FlvRecording c " + "WHERE c.deleted <> :deleted "
					+ "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
					+ "ORDER BY c.folder DESC, c.fileName ";

			TypedQuery<FlvRecording> query = em.createQuery(hql, FlvRecording.class);
			query.setParameter("deleted", true);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);

			List<FlvRecording> flvRecordingList = query.getResultList();

			return flvRecordingList;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingByParent]: ", ex2);
		}
		return null;
	}

	public void updateFlvRecordingEndTime(Long flvRecordingId, Date recordEnd, Long organization_id) {
		try {

			FlvRecording fId = get(flvRecordingId);

			fId.setProgressPostProcessing(0);
			fId.setRecordEnd(recordEnd);
			fId.setOrganization_id(organization_id);

			update(fId);
		} catch (Exception ex2) {
			log.error("[deleteFileExplorerItem]: ", ex2);
		}
	}

	/**
	 * @param fileExplorerItemId
	 */
	public boolean delete(Long flvRecordingId) {
		try {

			FlvRecording f = get(flvRecordingId);
			return delete(f);
		} catch (Exception ex2) {
			log.error("[delete]: ", ex2);
		}

		return false;
	}

	public boolean delete(FlvRecording f) {
		if (f == null || f.getFlvRecordingId() == 0) {
			return false;
		}
		f.setDeleted(true);
		update(f);
		return true;
	}
	
	public FlvRecording update(FlvRecording f) {
		try {
			if (f.getFlvRecordingId() == 0) {
				f.setInserted(new Date());
				em.persist(f);
			} else {
				f.setUpdated(new Date());
				if (!em.contains(f)) {
					f = em.merge(f);
				}
			}

		} catch (Exception ex2) {
			log.error("[update]: ", ex2);
		}
		return f;
	}

	public void resetProcessingStatus() {
		em.createNamedQuery("resetRecordingProcessingStatus").setParameter("error", Status.ERROR).setParameter("processing", Status.PROCESSING).executeUpdate();
	}
	
	public RecordingContainerData getRecordingContainerData(long userId) {
		try {
			RecordingContainerData containerData = new RecordingContainerData();
	
			// User Home Recordings
			List<FlvRecording> homeFlvRecordings = getFlvRecordingRootByOwner(userId);
			long homeFileSize = 0;
	
			for (FlvRecording homeFlvRecording : homeFlvRecordings) {
				homeFileSize += getRecordingSize(homeFlvRecording);
			}
	
			containerData.setUserHomeSize(homeFileSize);
			
			// Public Recordings
			long publicFileSize = 0;
			
			//get all organizations the user can view
			for (Organisation_Users ou : userDao.get(userId).getOrganisation_users()) {
				List<FlvRecording>publicFlvRecordings = getFlvRecordingRootByPublic(ou.getOrganisation().getOrganisation_id());
				//get sizes
				for (FlvRecording publicFlvRecording : publicFlvRecordings) {
					publicFileSize += getRecordingSize(publicFlvRecording);
				}
			}
			containerData.setPublicFileSize(publicFileSize);

			return containerData;
		} catch (Exception ex2) {
			log.error("[getRecordingContainerData]: ", ex2);
		}
		return null;
	}
	
	private long getRecordingSize(FlvRecording r) {
		long size = 0;

		if (isRecordingExists(r.getFileHash())) {
			size += getRecording(r.getFileHash()).length();
		}
		if (isRecordingExists(r.getAlternateDownload())) {
			size += getRecording(r.getAlternateDownload()).length();
		}
		if (isRecordingExists(r.getPreviewImage())) {
			size += getRecording(r.getPreviewImage()).length();
		}
		if (isRecordingExists(r.getFileHash() + MP4_EXTENSION)) {
			size += getMp4Recording(r.getFileHash()).length();
		}
		if (isRecordingExists(r.getFileHash() + OGG_EXTENSION)) {
			size += getOggRecording(r.getFileHash()).length();
		}
		for (FlvRecording flvRecording : getFlvRecordingByParent(r.getFlvRecordingId())) {
			size += getRecordingSize(flvRecording);
		}
		return size;
	}
}
