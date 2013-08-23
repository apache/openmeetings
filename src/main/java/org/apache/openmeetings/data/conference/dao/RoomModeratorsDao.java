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
package org.apache.openmeetings.data.conference.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.room.RoomModerator;
import org.apache.openmeetings.persistence.beans.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomModeratorsDao {

	private static final Logger log = Red5LoggerFactory
			.getLogger(RoomModeratorsDao.class);
	@Autowired
	private UserManager userManagement;
	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @param us
	 * @param isSuperModerator
	 * @return
	 */
	public Long addRoomModeratorByUserId(User us, Boolean isSuperModerator,
			Long roomId) {
		try {
			RoomModerator rModerator = new RoomModerator();
			rModerator.setUser(us);
			rModerator.setIsSuperModerator(isSuperModerator);
			rModerator.setStarttime(new Date());
			rModerator.setDeleted(false);
			rModerator.setRoomId(roomId);
			rModerator = em.merge(rModerator);
			long rModeratorId = rModerator.getRoomModeratorsId();
			return rModeratorId;
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserId] ", ex2);
		}
		return null;
	}

	public Long addRoomModeratorByObj(RoomModerator rModerator) {
		try {
			rModerator.setStarttime(new Date());
			rModerator = em.merge(rModerator);
			long rModeratorId = rModerator.getRoomModeratorsId();
			return rModeratorId;
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserId] ", ex2);
		}
		return null;
	}

	/**
	 * get all available RoomTypes
	 * 
	 * @return List of RoomTypes
	 */
	public RoomModerator getRoomModeratorById(Long roomModeratorsId) {
		try {
			TypedQuery<RoomModerator> query = em.createNamedQuery("getRoomModeratorById", RoomModerator.class);
			query.setParameter("roomModeratorsId", roomModeratorsId);
			RoomModerator roomModerators = null;
			try {
				roomModerators = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return roomModerators;

		} catch (Exception ex2) {
			log.error("[getRoomModeratorById] ", ex2);
		}
		return null;
	}

	public List<RoomModerator> getRoomModeratorByRoomId(Long roomId) {
		try {
			TypedQuery<RoomModerator> query = em.createNamedQuery("getRoomModeratorByRoomId", RoomModerator.class);
			query.setParameter("deleted", true);
			query.setParameter("roomId", roomId);
			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomModeratorByRoomId] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	public List<RoomModerator> getRoomModeratorByUserAndRoomId(Long roomId,
			Long user_id) {
		try {
			TypedQuery<RoomModerator> query = em.createNamedQuery("getRoomModeratorByUserAndRoomId", RoomModerator.class);
			query.setParameter("deleted", true);
			query.setParameter("roomId", roomId);
			query.setParameter("user_id", user_id);
			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomModeratorByUserAndRoomId] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param roomModeratorsId
	 */
	public void removeRoomModeratorByUserId(Long roomModeratorsId) {
		try {
			RoomModerator rModerator = this
					.getRoomModeratorById(roomModeratorsId);

			if (rModerator == null) {
				return;
			}

			rModerator.setUpdatetime(new Date());
			rModerator.setDeleted(true);

			if (rModerator.getRoomModeratorsId() == 0) {
				em.persist(rModerator);
			} else {
				if (!em.contains(rModerator)) {
					em.merge(rModerator);
				}
			}

		} catch (Exception ex2) {
			log.error("[removeRoomModeratorByUserId] ", ex2);
		}
	}

	public void updateRoomModeratorByUserId(Long roomModeratorsId,
			Boolean isSuperModerator) {
		try {
			RoomModerator rModerator = this
					.getRoomModeratorById(roomModeratorsId);

			if (rModerator == null) {
				return;
			}

			rModerator.setIsSuperModerator(isSuperModerator);
			rModerator.setUpdatetime(new Date());

			if (rModerator.getRoomModeratorsId() == 0) {
				em.persist(rModerator);
			} else {
				if (!em.contains(rModerator)) {
					em.merge(rModerator);
				}
			}

		} catch (Exception ex2) {
			log.error("[updateRoomModeratorByUserId] ", ex2);
		}
	}

	public void addRoomModeratorByUserList(
			List<Map<String, Object>> roomModerators, Long roomId) {
		try {

			for (Iterator<Map<String, Object>> iter = roomModerators.iterator(); iter
					.hasNext();) {

				Map<String, Object> roomModeratorObj = iter.next();

				Long userId = Long.parseLong(roomModeratorObj.get("userId")
						.toString());
				Boolean isSuperModerator = Boolean
						.parseBoolean(roomModeratorObj.get("isSuperModerator")
								.toString());

				this.addRoomModeratorByUserId(
						userManagement.getUserById(userId), isSuperModerator,
						roomId);

			}

		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserList] ", ex2);
			ex2.printStackTrace();
		}
	}

	public void updateRoomModeratorByUserList(
			List<Map<String, Object>> roomModerators, Long roomId) {
		try {

			// getLsit of RoomModerators before you add new ones
			List<RoomModerator> remoteRoomModeratorList = this
					.getRoomModeratorByRoomId(roomId);

			for (Iterator<Map<String, Object>> iter = roomModerators.iterator(); iter
					.hasNext();) {

				Map<String, Object> roomModeratorObj = iter.next();

				Long roomModeratorsId = Long.parseLong(roomModeratorObj.get(
						"roomModeratorsId").toString());
				Long userId = Long.parseLong(roomModeratorObj.get("userId")
						.toString());
				Boolean isSuperModerator = Boolean
						.parseBoolean(roomModeratorObj.get("isSuperModerator")
								.toString());

				if (roomModeratorsId == null || roomModeratorsId == 0) {
					Long newRoomModeratorId = this.addRoomModeratorByUserId(
							userManagement.getUserById(userId),
							isSuperModerator, roomId);

					roomModeratorObj
							.put("roomModeratorsId", newRoomModeratorId);

				} else {
					this.updateRoomModeratorByUserId(roomModeratorsId,
							isSuperModerator);
				}

			}

			// Check for items to delete
			List<RoomModerator> roomModeratorsToDelete = new LinkedList<RoomModerator>();

			if (remoteRoomModeratorList != null) {

				for (RoomModerator roomModerator : remoteRoomModeratorList) {

					boolean found = false;

					for (Iterator<Map<String, Object>> iter = roomModerators
							.iterator(); iter.hasNext();) {
						Map<String, Object> roomModeratorObj = iter.next();

						Long roomModeratorsId = Long.parseLong(roomModeratorObj
								.get("roomModeratorsId").toString());

						if (roomModerator.getRoomModeratorsId() == roomModeratorsId
								.longValue()) {
							found = true;
							break;
						}

					}

					if (!found) {
						roomModeratorsToDelete.add(roomModerator);
					}

				}

			}

			for (RoomModerator rModerator : roomModeratorsToDelete) {
				System.out.println("Remove Map "
						+ rModerator.getRoomModeratorsId());
				this.removeRoomModeratorByUserId(rModerator
						.getRoomModeratorsId());
			}

		} catch (Exception ex2) {
			log.error("[updateRoomModeratorByUserList] ", ex2);
			ex2.printStackTrace();
		}
	}

}
