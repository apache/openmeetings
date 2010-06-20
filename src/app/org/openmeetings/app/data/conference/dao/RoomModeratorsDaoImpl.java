package org.openmeetings.app.data.conference.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.rooms.RoomModerators;
import org.openmeetings.app.hibernate.beans.rooms.RoomTypes;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomModeratorsDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(RoomModeratorsDaoImpl.class);

	private static RoomModeratorsDaoImpl instance;

	private RoomModeratorsDaoImpl() {}

	public static synchronized RoomModeratorsDaoImpl getInstance() {
		if (instance == null) {
			instance = new RoomModeratorsDaoImpl();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param us
	 * @param isSuperModerator
	 * @return
	 */
	public Long addRoomModeratorByUserId(Users us, Boolean isSuperModerator, Long roomId){
		try {
			RoomModerators rModerator = new RoomModerators();
			rModerator.setUser(us);
			rModerator.setIsSuperModerator(isSuperModerator);
			rModerator.setStarttime(new Date());
			rModerator.setDeleted("false");
			rModerator.setRoomId(roomId);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			long rModeratorId = (Long) session.save(rModerator);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return rModeratorId;
		} catch (HibernateException ex) {
			log.error("[addRoomModeratorByUserId] ",ex);
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserId] ",ex2);
		}
		return null;
	}
	
	public Long addRoomModeratorByObj(RoomModerators rModerator){
		try {
			rModerator.setStarttime(new Date());
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			long rModeratorId = (Long) session.save(rModerator);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return rModeratorId;
		} catch (HibernateException ex) {
			log.error("[addRoomModeratorByUserId] ",ex);
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserId] ",ex2);
		}
		return null;
	}
	
	/**
	 * get all available RoomTypes
	 * @return List of RoomTypes
	 */
	public RoomModerators getRoomModeratorById(Long roomModeratorsId){
		try {
			String hql = "select c from RoomModerators as c where c.roomModeratorsId = :roomModeratorsId";
		
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			
			query.setLong("roomModeratorsId", roomModeratorsId);
			
			RoomModerators roomModerators = (RoomModerators) query.uniqueResult();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomModerators;
			
		} catch (HibernateException ex) {
			log.error("[getRoomModeratorById] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomModeratorById] ", ex2);
		}
		return null;
	}
	
	public List<RoomModerators> getRoomModeratorByRoomId(Long roomId){
		try {
			
			String hql = "select c from RoomModerators as c " +
					"where c.roomId = :roomId AND c.deleted != :deleted";
		
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			
			query.setString("deleted", "true");
			query.setLong("roomId", roomId);
			
			List<RoomModerators> roomModerators = query.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomModerators;
			
		} catch (HibernateException ex) {
			log.error("[getRoomModeratorByRoomId] ", ex);
			ex.printStackTrace();
		} catch (Exception ex2) {
			log.error("[getRoomModeratorByRoomId] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}
	
	public List<RoomModerators> getRoomModeratorByUserAndRoomId(Long roomId, Long user_id){
		try {
			
			String hql = "select c from RoomModerators as c " +
					"where c.roomId = :roomId " +
					"AND c.deleted != :deleted " +
					"AND c.user.user_id = :user_id";
		
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			
			query.setString("deleted", "true");
			query.setLong("roomId", roomId);
			query.setLong("user_id", user_id);
			
			List<RoomModerators> roomModerators = query.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomModerators;
			
		} catch (HibernateException ex) {
			log.error("[getRoomModeratorByUserAndRoomId] ", ex);
			ex.printStackTrace();
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
	public void removeRoomModeratorByUserId(Long roomModeratorsId){
		try {
			RoomModerators rModerator = this.getRoomModeratorById(roomModeratorsId);
			
			if (rModerator == null) {
				return;
			}
			
			rModerator.setUpdatetime(new Date());
			rModerator.setDeleted("true");
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(rModerator);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (HibernateException ex) {
			log.error("[removeRoomModeratorByUserId] ",ex);
		} catch (Exception ex2) {
			log.error("[removeRoomModeratorByUserId] ",ex2);
		}
	}
	
	public void updateRoomModeratorByUserId(Long roomModeratorsId, Boolean isSuperModerator){
		try {
			RoomModerators rModerator = this.getRoomModeratorById(roomModeratorsId);
			
			if (rModerator == null) {
				return;
			}
			
			rModerator.setIsSuperModerator(isSuperModerator);
			rModerator.setUpdatetime(new Date());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(rModerator);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (HibernateException ex) {
			log.error("[updateRoomModeratorByUserId] ",ex);
		} catch (Exception ex2) {
			log.error("[updateRoomModeratorByUserId] ",ex2);
		}
	}

	public void addRoomModeratorByUserList(List roomModerators, Long roomId) {
		try {
			
			for (Iterator iter = roomModerators.iterator();iter.hasNext();) {
				
				Map roomModeratorObj = (Map) iter.next();
				
				Long userId = Long.parseLong(roomModeratorObj.get("userId").toString());
				Boolean isSuperModerator = Boolean.parseBoolean(roomModeratorObj.get("isSuperModerator").toString());
				
				this.addRoomModeratorByUserId(Usermanagement.getInstance().getUserById(userId), isSuperModerator, roomId);
				
			}
			
		} catch (Exception ex2) {
			log.error("[addRoomModeratorByUserList] ",ex2);
			ex2.printStackTrace();
		}
	}

	public void updateRoomModeratorByUserList(List roomModerators, Long roomId) {
		try {
			
			//getLsit of RoomModerators before you add new ones
			List<RoomModerators> remoteRoomModeratorList = this.getRoomModeratorByRoomId(roomId);
			
			for (Iterator iter = roomModerators.iterator();iter.hasNext();) {
				
				Map roomModeratorObj = (Map) iter.next();
				
				Long roomModeratorsId = Long.parseLong(roomModeratorObj.get("roomModeratorsId").toString());
				Long userId = Long.parseLong(roomModeratorObj.get("userId").toString());
				Boolean isSuperModerator = Boolean.parseBoolean(roomModeratorObj.get("isSuperModerator").toString());
				
				if (roomModeratorsId == null || roomModeratorsId == 0) {
					Long newRoomModeratorId = this.addRoomModeratorByUserId(Usermanagement.getInstance().getUserById(userId), isSuperModerator, roomId);
					
					roomModeratorObj.put("roomModeratorsId", newRoomModeratorId);
					
				} else {
					this.updateRoomModeratorByUserId(roomModeratorsId, isSuperModerator);
				}
				
			}
			
			//Check for items to delete
			List<RoomModerators> roomModeratorsToDelete = new LinkedList<RoomModerators>();
			
			if (remoteRoomModeratorList != null) {
			
				for (RoomModerators roomModerator : remoteRoomModeratorList) {
					
					boolean found = false;
					
					for (Iterator iter = roomModerators.iterator();iter.hasNext();) {
						Map roomModeratorObj = (Map) iter.next();
						
						Long roomModeratorsId = Long.parseLong(roomModeratorObj.get("roomModeratorsId").toString());
						
						if (roomModerator.getRoomModeratorsId() == roomModeratorsId.longValue()) {
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
				System.out.println("Remove Map "+rModerator.getRoomModeratorsId());
				this.removeRoomModeratorByUserId(rModerator.getRoomModeratorsId());
			}
			
		} catch (Exception ex2) {
			log.error("[updateRoomModeratorByUserList] ",ex2);
			ex2.printStackTrace();
		}
	}
	
	
	
	
}
