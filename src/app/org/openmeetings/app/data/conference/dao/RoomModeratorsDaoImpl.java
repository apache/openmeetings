package org.openmeetings.app.data.conference.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.rooms.RoomModerators;
import org.openmeetings.app.persistence.beans.user.Users;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomModeratorsDaoImpl {

	private static final Logger log = Red5LoggerFactory
			.getLogger(RoomModeratorsDaoImpl.class);
	@Autowired
	private Usermanagement userManagement;
	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @param us
	 * @param isSuperModerator
	 * @return
	 */
	public Long addRoomModeratorByUserId(Users us, Boolean isSuperModerator,
			Long roomId) {
		try {
			RoomModerators rModerator = new RoomModerators();
			rModerator.setUser(us);
			rModerator.setIsSuperModerator(isSuperModerator);
			rModerator.setStarttime(new Date());
			rModerator.setDeleted("false");
			rModerator.setRoomId(roomId);
			rModerator = em.merge(rModerator);
			long rModeratorId = rModerator.getRoomModeratorsId();
			return rModeratorId;
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserId] ", ex2);
		}
		return null;
	}

	public Long addRoomModeratorByObj(RoomModerators rModerator) {
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
	public RoomModerators getRoomModeratorById(Long roomModeratorsId) {
		try {
			String hql = "select c from RoomModerators as c where c.roomModeratorsId = :roomModeratorsId";

			Query query = em.createQuery(hql);

			query.setParameter("roomModeratorsId", roomModeratorsId);

			RoomModerators roomModerators = null;
			try {
				roomModerators = (RoomModerators) query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return roomModerators;

		} catch (Exception ex2) {
			log.error("[getRoomModeratorById] ", ex2);
		}
		return null;
	}

	public List<RoomModerators> getRoomModeratorByRoomId(Long roomId) {
		try {

			String hql = "select c from RoomModerators as c "
					+ "where c.roomId = :roomId AND c.deleted <> :deleted";

			Query query = em.createQuery(hql);

			query.setParameter("deleted", "true");
			query.setParameter("roomId", roomId);

			@SuppressWarnings("unchecked")
			List<RoomModerators> roomModerators = query.getResultList();

			return roomModerators;

		} catch (Exception ex2) {
			log.error("[getRoomModeratorByRoomId] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	public List<RoomModerators> getRoomModeratorByUserAndRoomId(Long roomId,
			Long user_id) {
		try {

			String hql = "select c from RoomModerators as c "
					+ "where c.roomId = :roomId "
					+ "AND c.deleted <> :deleted "
					+ "AND c.user.user_id = :user_id";

			Query query = em.createQuery(hql);

			query.setParameter("deleted", "true");
			query.setParameter("roomId", roomId);
			query.setParameter("user_id", user_id);

			@SuppressWarnings("unchecked")
			List<RoomModerators> roomModerators = query.getResultList();

			return roomModerators;

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
			RoomModerators rModerator = this
					.getRoomModeratorById(roomModeratorsId);

			if (rModerator == null) {
				return;
			}

			rModerator.setUpdatetime(new Date());
			rModerator.setDeleted("true");

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
			RoomModerators rModerator = this
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
			List<RoomModerators> remoteRoomModeratorList = this
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
			List<RoomModerators> roomModeratorsToDelete = new LinkedList<RoomModerators>();

			if (remoteRoomModeratorList != null) {

				for (RoomModerators roomModerator : remoteRoomModeratorList) {

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

			for (RoomModerators rModerator : roomModeratorsToDelete) {
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
