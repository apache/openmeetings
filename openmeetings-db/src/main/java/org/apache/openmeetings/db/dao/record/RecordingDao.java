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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.file.BaseFileItemDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Repository
@Transactional
public class RecordingDao extends BaseFileItemDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingDao.class, getWebAppRootKey());

	@Autowired
	private UserDao userDao;

	@Override
	public Recording get(Long id) {
		BaseFileItem bf = super.get(id);
		return bf instanceof Recording ? (Recording)bf : null;
	}

	public Recording getByHash(String hash) {
		BaseFileItem bf = super.get(hash);
		return bf instanceof Recording ? (Recording)bf : null;
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
		return new ArrayList<>();
	}
	public List<Recording> get() {
		return em.createNamedQuery("getRecordingsAll", Recording.class).getResultList();
	}

	public List<Recording> getByExternalType(String externalType) {
		log.debug("getByExternalType :externalType: " + externalType);

		TypedQuery<Recording> query = em.createNamedQuery("getRecordingsByExternalType", Recording.class);
		query.setParameter("externalType", externalType);

		return query.getResultList();
	}

	public List<Recording> getRootByPublic(Long groupId) {
		TypedQuery<Recording> q = em.createNamedQuery(groupId == null ? "getRecordingsPublic" : "getRecordingsByGroup", Recording.class);
		if (groupId != null) {
			q.setParameter("groupId", groupId);
		}
		return q.getResultList();
	}

	public List<Recording> getRootByOwner(Long ownerId) {
		return em.createNamedQuery("getRecordingsByOwner", Recording.class).setParameter("ownerId", ownerId).getResultList();
	}

	public List<Recording> getByRoomId(Long roomId) {
		return em.createNamedQuery("getRecordingsByRoom", Recording.class)
				.setParameter("roomId", roomId).getResultList();
	}

	public List<Recording> getExpiring(Long groupId, int reminderDays, boolean notified) {
		Instant date = Instant.now().minus(Duration.ofDays(reminderDays));
		return em.createNamedQuery("getExpiringRecordings", Recording.class)
				.setParameter("groupId", groupId)
				.setParameter("date", Date.from(date))
				.setParameter("notified", notified)
				.getResultList();
	}

	public List<Recording> getByParent(Long parentId) {
		return em.createNamedQuery("getRecordingsByParent", Recording.class).setParameter("parentId", parentId).getResultList();
	}

	public void updateEndTime(Long recordingId, Date recordEnd) {
		try {

			Recording fId = get(recordingId);

			fId.setRecordEnd(recordEnd);

			update(fId);
		} catch (Exception ex2) {
			log.error("[updateEndTime]: ", ex2);
		}
	}

	public Recording update(Recording f) {
		return (Recording)_update(f);
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

			//get all groups the user can view
			for (GroupUser ou : userDao.get(userId).getGroupUsers()) {
				List<Recording> publicRecordings = getRootByPublic(ou.getGroup().getId());
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

		if (r.exists(EXTENSION_PNG)) {
			size += r.getFile(EXTENSION_PNG).length();
		}
		if (r.exists(EXTENSION_MP4)) {
			size += r.getFile(EXTENSION_MP4).length();
		}
		for (Recording rec : getByParent(r.getId())) {
			size += getSize(rec);
		}
		return size;
	}
}
