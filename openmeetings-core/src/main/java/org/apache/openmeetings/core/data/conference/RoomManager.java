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
package org.apache.openmeetings.core.data.conference;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class RoomManager {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomManager.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ISessionManager sessionManager;
    @Autowired
	private RoomDao roomDao;
    @Autowired
	private SipDao sipDao;

	public SearchResult<Room> getRooms(int start, int max, String orderby, boolean asc, String search) {
		try {
			SearchResult<Room> sResult = new SearchResult<Room>();
			sResult.setRecords(this.selectMaxFromRooms(search));
			sResult.setObjectName(Room.class.getName());
			sResult.setResult(this.getRoomsInternatlByHQL(start, max,
					orderby, asc, search));
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}
	
	public SearchResult<Room> getRoomsWithCurrentUsers(int start, int max, String orderby, boolean asc) {
		try {
			SearchResult<Room> sResult = new SearchResult<Room>();
			sResult.setRecords(this.selectMaxFromRooms(""));
			sResult.setObjectName(Room.class.getName());

			List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
					asc);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			sResult.setResult(rooms);
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByList(int start, int max, String orderby, boolean asc) {
		try {
			List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
					asc);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			return rooms;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(int start, int max, String orderby, boolean asc, String externalType) {
		try {
			List<Room> rooms = this.getRoomsInternatlbyType(start, max, orderby, asc, externalType);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			return rooms;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public Long selectMaxFromRooms(String search) {
		try {
			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			// get all users
			TypedQuery<Long> query = em.createNamedQuery("selectMaxFromRooms", Long.class);
			query.setParameter("search", search);
			List<Long> ll = query.getResultList();
			log.debug("Number of records" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatl(int start, int max, String orderby, boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatlByHQL(int start, int max,
			String orderby, boolean asc, String search) {
		try {

			String hql = "select c from Room c where c.deleted = false AND c.name LIKE :search ";

			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			if (orderby != null) {
				hql += " ORDER BY " + (orderby.startsWith("c.") ? "" : "c.") + orderby;
	
				if (asc) {
					hql += " ASC";
				} else {
					hql += " DESC";
				}
			}
			TypedQuery<Room> query = em.createQuery(hql, Room.class);
			query.setParameter("search", search);
			query.setFirstResult(start);
			query.setMaxResults(max);

			return query.getResultList();

		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<Room> getAllRooms() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			TypedQuery<Room> q = em.createQuery(cq);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getAllRooms]", ex2);
		}
		return null;
	}

	public List<Room> getRoomsInternatlbyType(int start, int max, String orderby, boolean asc, String externalType) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			Predicate subCondition = cb.equal(c.get("externalType"), externalType);
			cq.where(condition, subCondition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<RoomGroup> getGroupsByRoom(long roomId) {
		try {
			String hql = "select c from RoomGroup as c "
					+ "where c.room.id = :id "
					+ "AND c.deleted = false";
			TypedQuery<RoomGroup> q = em.createQuery(hql, RoomGroup.class);

			q.setParameter("id", roomId);
			List<RoomGroup> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getGroupsByRoom] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsByIds(List<Integer> roomIds) {
		try {
			if (roomIds == null || roomIds.size() == 0) {
				return new LinkedList<Room>();
			}

			String queryString = "SELECT r from Room r " + "WHERE ";

			queryString += "(";

			int i = 0;
			for (Integer roomId : roomIds) {
				if (i != 0) {
					queryString += " OR ";
				}
				queryString += " r.id = " + roomId;
				i++;
			}

			queryString += ")";

			TypedQuery<Room> q = em.createQuery(queryString, Room.class);

			List<Room> ll = q.getResultList();

			return ll;

		} catch (Exception ex2) {
			log.error("[getRoomsByIds] ", ex2);
		}
		return null;
	}

	// ---------------------------------------------------------------------------------------------

    /**
     * Returns number of SIP conference participants
     * @param roomId id of room
     * @return number of participants
     */
    public Integer getSipConferenceMembersNumber(Long roomId) {
    	Room r = roomDao.get(roomId);
    	return r == null || r.getConfno() == null ? null : sipDao.countUsers(r.getConfno());
    }

	/**
	 * get List of RoomGroup by group and roomtype
	 * 
	 * @param groupId
	 * @param typeId
	 * @return
	 */
	public List<RoomGroup> getRoomGroupByGroupIdAndRoomType(long groupId, long typeId) {
		try {
			TypedQuery<RoomGroup> q = em.createNamedQuery("getRoomGroupByGroupIdAndRoomType", RoomGroup.class);
			q.setParameter("type", Room.Type.get(typeId));
			q.setParameter("groupId", groupId);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomGroupByGroupIdAndRoomType] ", ex2);
		}
		return null;
	}

	/**
	 * Gets all rooms by an group
	 * 
	 * @param groupId
	 * @return list of RoomGroup with Rooms as Sub-Objects or null
	 */
	public List<RoomGroup> getRoomGroupByGroupId(long groupId) {
		try {
			TypedQuery<RoomGroup> query = em.
					createNamedQuery("getRoomGroupByGroupId", RoomGroup.class);

			query.setParameter("groupId", groupId);

			List<RoomGroup> ll = query.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getPublicRoomsWithoutType] ", ex2);
		}
		return null;
	}

	public SearchResult<RoomGroup> getRoomGroupByGroupId(long groupId, int start, int max, String orderby,
			boolean asc) {
		try {
			SearchResult<RoomGroup> sResult = new SearchResult<RoomGroup>();
			sResult.setObjectName(RoomGroup.class.getName());
			sResult.setRecords(this.selectMaxFromRoomsByGroup(groupId).longValue());
			sResult.setResult(getRoomGroupsByGroupId(groupId, start, max, orderby, asc));
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRoomGroupByGroupId] ", ex2);
		}
		return null;
	}

	public Integer selectMaxFromRoomsByGroup(long groupId) {
		try {
			// get all users
			TypedQuery<RoomGroup> q = em.createNamedQuery("selectMaxFromRoomsByGroup", RoomGroup.class);

			q.setParameter("groupId", groupId);
			List<RoomGroup> ll = q.getResultList();

			return ll.size();
		} catch (Exception ex2) {
			log.error("[selectMaxFromRoomsByGroup] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param groupId
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	private List<RoomGroup> getRoomGroupsByGroupId(long groupId, int start, int max, String orderby, boolean asc) {
		try {
			String hql = "select c from RoomGroup as c where c.group.id = :groupId AND c.deleted = false";
			if (orderby.startsWith("c.")) {
				hql += "ORDER BY " + orderby;
			} else {
				hql += "ORDER BY " + "c." + orderby;
			}
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			TypedQuery<RoomGroup> q = em.createQuery(hql, RoomGroup.class);

			q.setParameter("groupId", groupId);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<RoomGroup> ll = q.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getRoomsByGroup] ", ex2);
		}
		return null;
	}
}
