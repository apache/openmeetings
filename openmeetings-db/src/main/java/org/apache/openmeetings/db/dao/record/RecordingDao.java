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
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
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
public class RecordingDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingDao.class, webAppRootKey);
	
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao userDao;

	public Recording get(Long recordingId) {
		try {
			TypedQuery<Recording> query = em.createNamedQuery("getRecordingById", Recording.class);
			query.setParameter("id", recordingId);

			Recording recording = null;
			try {
				recording = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return recording;
		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
	}

	public Recording getByHash(String hash) {
		try {
			TypedQuery<Recording> query = em.createNamedQuery("getRecordingByHash", Recording.class);
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

	public List<Recording> getByExternalId(String externalId, String externalType) {
		try {
			log.debug("getFByExternalId :externalId: {}; externalType: {}", externalId, externalType);

			TypedQuery<Recording> query = em.createNamedQuery("getRecordingsByExternalUser", Recording.class);
			query.setParameter("externalId", externalId);
			query.setParameter("externalType", externalType);

			List<Recording> recordingList = query.getResultList();

			log.debug("getByExternalId :: " + recordingList.size());

			return recordingList;
		} catch (Exception ex2) {
			log.error("[getByExternalId]: ", ex2);
		}
		return null;
	}

	public List<Recording> getByExternalTypeAndCreator(String externalType, Long insertedBy) {
		try {

			log.debug("getByExternalType :externalType: " + externalType);

			TypedQuery<Recording> query = em.createNamedQuery("getRecordingsByExternalTypeAndOwner", Recording.class);
			query.setParameter("externalType", externalType);
			query.setParameter("insertedBy", insertedBy);

			List<Recording> recordingList = query.getResultList();

			log.debug("getByExternalType :: " + recordingList.size());

			return recordingList;
		} catch (Exception ex2) {
			log.error("[getByExternalType]: ", ex2);
		}
		return null;
	}

	public List<Recording> get() {
		try {
			TypedQuery<Recording> query = em.createNamedQuery("getRecordingsAll", Recording.class);

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
	}

	public List<Recording> getByExternalType(String externalType) {
		log.debug("getByExternalType :externalType: " + externalType);

		TypedQuery<Recording> query = em.createNamedQuery("getRecordingsByExternalType", Recording.class);
		query.setParameter("externalType", externalType);

		return query.getResultList();
	}

	public List<Recording> getRootByPublic(Long orgId) {
		TypedQuery<Recording> q = em.createNamedQuery(orgId == null ? "getRecordingsPublic" : "getRecordingsByOrganization", Recording.class);
		if (orgId != null) {
			q.setParameter("organization_id", orgId);
		}
		return q.getResultList();
	}

	public List<Recording> getRootByOwner(Long ownerId) {
		return em.createNamedQuery("getRecordingsByOwner", Recording.class).setParameter("ownerId", ownerId).getResultList();
	}

	public List<Recording> getByRoomId(Long roomId) {
		try {
			TypedQuery<Recording> query = em.createNamedQuery("getRecordingsByRoom", Recording.class);
			query.setParameter("roomId", roomId);

			List<Recording> recordingList = query.getResultList();

			return recordingList;
		} catch (Exception ex2) {
			log.error("[getByRoomId]: ", ex2);
		}
		return null;
	}

	public List<Recording> getByParent(Long parentId) {
		return em.createNamedQuery("getRecordingsByParent", Recording.class).setParameter("parentId", parentId).getResultList();
	}

	public void updateEndTime(Long recordingId, Date recordEnd) {
		try {

			Recording fId = get(recordingId);

			fId.setProgressPostProcessing(0);
			fId.setRecordEnd(recordEnd);

			update(fId);
		} catch (Exception ex2) {
			log.error("[updateEndTime]: ", ex2);
		}
	}

	/**
	 * @param fileExplorerItemId
	 */
	public boolean delete(Long id) {
		try {

			Recording f = get(id);
			return delete(f);
		} catch (Exception ex2) {
			log.error("[delete]: ", ex2);
		}

		return false;
	}

	public boolean delete(Recording f) {
		if (f == null || f.getId() == null) {
			return false;
		}
		f.setDeleted(true);
		update(f);
		return true;
	}
	
	public Recording update(Recording f) {
		try {
			if (f.getId() == null) {
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
		em.createNamedQuery("resetRecordingProcessingStatus")
			.setParameter("error", Status.ERROR)
			.setParameter("recording", Status.RECORDING)
			.setParameter("converting", Status.CONVERTING)
			.executeUpdate();
	}
	
	public RecordingContainerData getContainerData(long userId) {
		try {
			RecordingContainerData containerData = new RecordingContainerData();
	
			// User Home Recordings
			List<Recording> homes = getRootByOwner(userId);
			long homeFileSize = 0;
	
			for (Recording home : homes) {
				homeFileSize += getSize(home);
			}
	
			containerData.setUserHomeSize(homeFileSize);
			
			// Public Recordings
			long publicFileSize = 0;
			
			//get all organizations the user can view
			for (Organisation_Users ou : userDao.get(userId).getOrganisation_users()) {
				List<Recording> publicRecordings = getRootByPublic(ou.getOrganisation().getId());
				//get sizes
				for (Recording r : publicRecordings) {
					publicFileSize += getSize(r);
				}
			}
			containerData.setPublicFileSize(publicFileSize);

			return containerData;
		} catch (Exception ex2) {
			log.error("[getRecordingContainerData]: ", ex2);
		}
		return null;
	}
	
	private long getSize(Recording r) {
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
		for (Recording rec : getByParent(r.getId())) {
			size += getSize(rec);
		}
		return size;
	}
}
