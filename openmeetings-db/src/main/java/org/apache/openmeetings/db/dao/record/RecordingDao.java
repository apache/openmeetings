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

import static org.apache.openmeetings.db.dao.user.UserDao.FETCH_GROUP_BACKUP;
import static org.apache.openmeetings.db.util.DaoHelper.fillLazy;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.file.BaseFileItemDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Repository
@Transactional
public class RecordingDao extends BaseFileItemDao {
	private static final Logger log = LoggerFactory.getLogger(RecordingDao.class);

	@Inject
	private UserDao userDao;

	@Override
	public Recording get(Long id) {
		BaseFileItem bf = super.get(id);
		return bf instanceof Recording rec ? rec : null;
	}

	public List<Recording> getByExternalUser(String externalId, String externalType) {
		log.debug("getByExternalUser :externalId: {}; externalType: {}", externalId, externalType);

		return em.createNamedQuery("getRecordingsByExternalUser", Recording.class)
				.setParameter("externalId", externalId)
				.setParameter("externalType", externalType)
				.getResultList();
	}

	public List<Recording> get() {
		return fillLazy(em
				, oem -> oem.createNamedQuery("getRecordingsAll", Recording.class)
				, FETCH_GROUP_BACKUP);
	}

	public List<Recording> getByExternalType(String externalType) {
		log.debug("getByExternalType :externalType: {}", externalType);

		return em.createNamedQuery("getRecordingsByExternalType", Recording.class)
				.setParameter("externalType", externalType)
				.getResultList();
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
				.setParameter("roomId", roomId)
				.getResultList();
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
		return em.createNamedQuery("getRecordingsByParent", Recording.class)
				.setParameter("parentId", parentId)
				.getResultList();
	}

	public Recording update(Recording f) {
		return (Recording)updateBase(f);
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
