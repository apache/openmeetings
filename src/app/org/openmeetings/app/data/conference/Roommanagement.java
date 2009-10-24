package org.openmeetings.app.data.conference;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.*;
import org.openmeetings.app.hibernate.beans.user.*;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.remote.red5.ClientListManager;

/**
 * 
 * @author swagner
 *
 */
public class Roommanagement {

	private static final Logger log = Logger.getLogger(Roommanagement.class);

	private static Roommanagement instance;

	private Roommanagement() {}

	public static synchronized Roommanagement getInstance() {
		if (instance == null) {
			instance = new Roommanagement();
		}
		return instance;
	}
	
	/**
	 * add a new Record to the table roomtypes
	 * @param name
	 * @return ID of new created roomtype or null
	 */
	public Long addRoomType(String name){
		try {
			RoomTypes rtype = new RoomTypes();
			rtype.setName(name);
			rtype.setStarttime(new Date());
			rtype.setDeleted("false");
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			long returnId = (Long) session.save(rtype);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return returnId;
		} catch (HibernateException ex) {
			log.error("[addRoomType] ",ex);
		} catch (Exception ex2) {
			log.error("[addRoomType] ",ex2);
		}
		return null;
	}
	
	/**
	 * get all availible RoomTypes
	 * @return List of RoomTypes
	 */
	public List<RoomTypes> getAllRoomTypes(Long user_level){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from RoomTypes as c where c.deleted != :deleted");
				query.setString("deleted", "true");
				List<RoomTypes> ll = query.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getAllRoomTypes] ", ex);
		} catch (Exception ex2) {
			log.error("[getAllRoomTypes] ", ex2);
		}
		return null;
	}
	
	/**
	 * Get a RoomTypes-Object by its id
	 * @param roomtypes_id
	 * @return RoomTypes-Object or NULL
	 */
	public RoomTypes getRoomTypesById(long roomtypes_id){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select c from RoomTypes as c where c.roomtypes_id = :roomtypes_id AND c.deleted != :deleted");
			query.setLong("roomtypes_id",roomtypes_id);
			query.setString("deleted", "true");
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (ll.size()>0){
				return (RoomTypes) ll.get(0);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomTypesById] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomTypesById] ", ex2);
		}
		return null;
	}	
	
	/**
	 * get a room object if user level
	 * @param user_level
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomById(long user_level, long rooms_id){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				return this.getRoomById(rooms_id);
			}
			else
				log.error("getRoombyId : Userlevel" +  user_level + " not allowed");
		} catch (Exception ex2) {
			log.error("[getRoomById] ", ex2);
		}
		return null;
	}
	
	public Rooms getRoomWithCurrentUsersById(long user_level, long rooms_id){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Rooms room = this.getRoomById(rooms_id);
				
				if (room != null) {
					HashMap<String,RoomClient> map = ClientListManager.getInstance().getClientListByRoom(room.getRooms_id());
					
					room.setCurrentusers(new LinkedList<RoomClient>());
					
					for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
						room.getCurrentusers().add(map.get(iter.next()));
					}
					
					return room;
				}
			}
		} catch (Exception ex2) {
			log.error("[getRoomWithCurrentUsersById] ", ex2);
		}
		return null;
	}
	
	/**
	 * Get a Rooms-Object or NULL
	 * @param rooms_id
	 * @return Rooms-Object or NULL
	 */
	public Rooms getRoomById(long rooms_id){
		log.debug("getRoombyId : " + rooms_id);
		try {
			String hql = "select c from Rooms as c where c.rooms_id = :rooms_id AND c.deleted != :deleted";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("rooms_id", rooms_id);
			query.setString("deleted", "true");
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (ll.size()>0){
				return (Rooms) ll.get(0);
			}
			else{
				log.error("Could not find room " + rooms_id);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomById] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomById] ", ex2);
		}
		return null;
	}	
	
	public SearchResult getRooms(long user_level, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				SearchResult sResult = new SearchResult();
				sResult.setRecords(this.selectMaxFromRooms());
				sResult.setObjectName(Rooms.class.getName());
				sResult.setResult(this.getRoomsInternatl(start, max, orderby, asc));
				return sResult;
			}
		} catch (HibernateException ex) {
			log.error("[getRooms] ", ex);
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}
	
	public SearchResult getRoomsWithCurrentUsers(long user_level, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				SearchResult sResult = new SearchResult();
				sResult.setRecords(this.selectMaxFromRooms());
				sResult.setObjectName(Rooms.class.getName());
				
				List<Rooms> rooms = this.getRoomsInternatl(start, max, orderby, asc);
				
				for (Rooms room : rooms) {
					
					HashMap<String,RoomClient> map = ClientListManager.getInstance().getClientListByRoom(room.getRooms_id());
					
					room.setCurrentusers(new LinkedList<RoomClient>());
					
					for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
						room.getCurrentusers().add(map.get(iter.next()));
					}
					
				}
				
				sResult.setResult(rooms);
				return sResult;
			}
		} catch (HibernateException ex) {
			log.error("[getRooms] ", ex);
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}
	
	public Long selectMaxFromRooms(){
		try {
			String hql = "select count(c.rooms_id) from Rooms c " +
						"where c.deleted <> 'true'";
			
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("err"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (HibernateException ex) {
			log.error("[selectMaxFromRooms] ", ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}	
	
	/**
	 * gets a list of all availible rooms
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Rooms> getRoomsInternatl(int start, int max, String orderby, boolean asc){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Rooms.class);
			crit.setFirstResult(start);
			crit.setMaxResults(max);
			crit.add(Restrictions.ne("deleted", "true"));
			if (asc) crit.addOrder(Order.asc(orderby));
			else crit.addOrder(Order.desc(orderby));
			List<Rooms> ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (HibernateException ex) {
			log.error("[getRooms ] ", ex);
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}
	
	public List<Rooms_Organisation> getOrganisationsByRoom(long user_level, long rooms_id){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Criteria crit = session.createCriteria(Rooms_Organisation.class);
				crit.add(Restrictions.ne("deleted", "true"));
				Criteria subcrit = crit.createCriteria("room");
				subcrit.add(Restrictions.eq("rooms_id", rooms_id));
				List<Rooms_Organisation> ll = crit.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getOrganisationsByRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[getOrganisationsByRoom] ", ex2);
		}
		return null;
	}
	
	/**
	 * get all rooms which are availible for public
	 * @param user_level
	 * @param roomtypes_id
	 * @return
	 */
	public List<Rooms> getPublicRooms(long user_level, long roomtypes_id){
		try {
			log.error("getPublicRooms: roomtypes_id "+roomtypes_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				log.error("### getPublicRooms: create Query "+roomtypes_id);
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
//				Criteria crit = session.createCriteria(Rooms.class);
//				Criteria subcriteriaRoomType = crit.createCriteria("roomtype");
//				subcriteriaRoomType.add(Restrictions.eq("roomtypes_id", roomtypes_id));
//				crit.add(Restrictions.eq("ispublic", true));
//				crit.add(Restrictions.ne("deleted", "true"));			
//				List ll = crit.list();
				String queryString = "SELECT r from Rooms r " +
						"JOIN r.roomtype as rt " +
						"WHERE " +
						"r.ispublic=:ispublic and r.deleted=:deleted and rt.roomtypes_id=:roomtypes_id";
				Query q = session.createQuery(queryString);
				//
				q.setBoolean("ispublic", true);
				q.setString("deleted", "false");
				q.setLong("roomtypes_id", new Long(roomtypes_id));
				
				List ll = q.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				log.error("### getPublicRooms: size Room List "+ll.size());
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	public List<Rooms> getPublicRoomsWithoutType(long user_level){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
//				Criteria crit = session.createCriteria(Rooms.class);
//				Criteria subcriteriaRoomType = crit.createCriteria("roomtype");
//				subcriteriaRoomType.add(Restrictions.eq("roomtypes_id", roomtypes_id));
//				crit.add(Restrictions.eq("ispublic", true));
//				crit.add(Restrictions.ne("deleted", "true"));			
//				List ll = crit.list();
				String queryString = "SELECT r from Rooms r " +
						"JOIN r.roomtype as rt " +
						"WHERE " +
						"r.ispublic=:ispublic and r.deleted=:deleted";
				Query q = session.createQuery(queryString);
				//
				q.setBoolean("ispublic", true);
				q.setString("deleted", "false");
				
				List<Rooms> ll = q.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				log.error("### getPublicRooms: size Room List "+ll.size());
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getPublicRoomsWithoutType] ", ex);
		} catch (Exception ex2) {
			log.error("[getPublicRoomsWithoutType] ", ex2);
		}
		return null;
	}	
	
	
	/**
	 * Get Appointed Meetings
	 */
	//---------------------------------------------------------------------------------------------
	public List<Rooms> getAppointedMeetings(Long userid, Long user_level){
		log.debug("Roommanagement.getAppointedMeetings");
		
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				
				String queryString = "SELECT r from Rooms r " +
						"JOIN r.roomtype as rt " +
						"WHERE " +
						"r.deleted=:deleted and r.appointment=:appointed";
				Query q = session.createQuery(queryString);
				//
				q.setBoolean("appointed", true);
				q.setString("deleted", "false");
				
				List ll = q.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getAppointedMeetings] ", ex);
		} catch (Exception ex2) {
			log.error("[getAppointedMeetings] ", ex2);
		}
		return null;
		
	}
	//---------------------------------------------------------------------------------------------
	
	/**
	 * get all rooms which are availible for public
	 * @param user_level
	 * @param roomtypes_id
	 * @return
	 */
	public List<Rooms> getPublicRooms(long user_level){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				//log.error("### getPublicRooms: create Query "+roomtypes_id);
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
//				Criteria crit = session.createCriteria(Rooms.class);
//				Criteria subcriteriaRoomType = crit.createCriteria("roomtype");
//				subcriteriaRoomType.add(Restrictions.eq("roomtypes_id", roomtypes_id));
//				crit.add(Restrictions.eq("ispublic", true));
//				crit.add(Restrictions.ne("deleted", "true"));			
//				List ll = crit.list();
				String queryString = "SELECT r from Rooms r " +
						"JOIN r.roomtype as rt " +
						"WHERE " +
						"r.ispublic=:ispublic and r.deleted=:deleted";
				Query q = session.createQuery(queryString);
				//
				q.setBoolean("ispublic", true);
				q.setString("deleted", "false");
				
				List ll = q.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				log.error("### getPublicRooms: size Room List "+ll.size());
				return ll;
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	
	/**
	 * adds a new Record to the table rooms
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @return id of the newly created room or NULL
	 */
	public Long addRoom(long user_level,String name, long roomtypes_id, String comment, Long numberOfPartizipants,
			boolean ispublic, List organisations,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom){
		
		log.debug("addRoom");
		
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				Rooms r = new Rooms();
				r.setName(name);
				r.setComment(comment);
				r.setStarttime(new Date());
				r.setNumberOfPartizipants(numberOfPartizipants);
				r.setRoomtype(this.getRoomTypesById(roomtypes_id));
				r.setIspublic(ispublic);
				
				r.setAppointment(appointment);
				
				r.setIsDemoRoom(isDemoRoom);
				r.setDemoTime(demoTime);
				
				r.setIsModeratedRoom(isModeratedRoom);
				
				r.setDeleted("false");
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				long returnId = (Long) session.save(r);
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				if (organisations!=null){
					Long t = this.updateRoomOrganisations(organisations, r);
					if (t==null) return null;
				}
				
				return returnId;
			}
		} catch (HibernateException ex) {
			log.error("[addRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[addRoom] ", ex2);
		}
		return null;
	}
	
	/**
	 * adds a new record to the table rooms_organisation
	 * @param rooms_id
	 * @param organisation_id
	 * @return the id of the newly created Rooms_Organisation or NULL
	 */
	public Long addRoomToOrganisation(long user_level, long rooms_id, long organisation_id){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				Rooms_Organisation rOrganisation = new Rooms_Organisation();
				rOrganisation.setRoom(this.getRoomById(rooms_id));
				log.error("addRoomToOrganisation rooms "+rOrganisation.getRoom().getName());
				rOrganisation.setStarttime(new Date());
				rOrganisation.setOrganisation(Organisationmanagement.getInstance().getOrganisationById(organisation_id));
				rOrganisation.setDeleted("false");
				
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				long returnId = (Long) session.save(rOrganisation);
				tx.commit();
				HibernateUtil.closeSession(idf);
				return returnId;
			}
		} catch (HibernateException ex) {
			log.error("[addRoomToOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[addRoomToOrganisation] ", ex2);
		}
		return null;
	}
	
	/**
	 * 
	 * @param rooms_organisation_id
	 * @return
	 */
	public Rooms_Organisation getRoomsOrganisationById(long rooms_organisation_id){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Criteria crit = session.createCriteria(Rooms_Organisation.class);
			crit.add(Restrictions.eq("rooms_organisation_id", rooms_organisation_id));
			List ll = crit.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (ll.size()>0){
				return (Rooms_Organisation)ll.get(0);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	/**
	 * get List of Rooms_Organisation by organisation and roomtype
	 * @param user_level
	 * @param organisation_id
	 * @param roomtypes_id
	 * @return
	 */
	public List<Rooms_Organisation> getRoomsOrganisationByOrganisationIdAndRoomType(long user_level,long organisation_id, long roomtypes_id){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				
				Criteria crit = session.createCriteria(Rooms_Organisation.class);
				Criteria subcrit = crit.createCriteria("organisation");
				subcrit.add(Restrictions.eq("organisation_id", organisation_id));
				Criteria subcritRoom = crit.createCriteria("room");
				Criteria subCritRoomType = subcritRoom.createCriteria("roomtype");
				subCritRoomType.add(Restrictions.eq("roomtypes_id", roomtypes_id));
				crit.add(Restrictions.eq("deleted", "false"));
				List ll = crit.list();
				
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return ll;
			} else {
				log.error("[notauthentificated] "+ user_level);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	/**
	 * Gets all rooms by an organisation
	 * @param organisation_id
	 * @return list of Rooms_Organisation with Rooms as Sub-Objects or null
	 */
	public List<Rooms_Organisation> getRoomsOrganisationByOrganisationId(long user_level,long organisation_id){
		try {
			if (AuthLevelmanagement.getInstance().checkModLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				
				Criteria crit = session.createCriteria(Rooms_Organisation.class);
				Criteria subcrit = crit.createCriteria("organisation");
				subcrit.add(Restrictions.eq("organisation_id", organisation_id));
				crit.add(Restrictions.ne("deleted", "true"));
				List<Rooms_Organisation> ll = crit.list();
				
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return ll;
			} else {
				log.error("[notauthentificated] "+ user_level);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}
	
	public SearchResult getRoomsOrganisationByOrganisationId(long user_level,long organisation_id, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkModLevel(user_level)){
				
				SearchResult sResult = new SearchResult();
				sResult.setObjectName(Rooms_Organisation.class.getName());
				sResult.setRecords(this.selectMaxFromRoomsByOrganisation(organisation_id).longValue());
				sResult.setResult(this.getRoomsOrganisationByOrganisationId(organisation_id, start, max, orderby, asc));
				return sResult;
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	public Integer selectMaxFromRoomsByOrganisation(long organisation_id){
		try {
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Rooms_Organisation.class);
			Criteria subcrit = crit.createCriteria("organisation");
			subcrit.add(Restrictions.eq("organisation_id", organisation_id));
			crit.add(Restrictions.ne("deleted", "true"));
			List ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);

			return ll.size();			
		} catch (HibernateException ex) {
			log.error("[selectMaxFromRooms] ", ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}		
	
	/**
	 * TODO: add sortings
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	private List getRoomsOrganisationByOrganisationId(long organisation_id, int start, int max, String orderby, boolean asc){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Criteria crit = session.createCriteria(Rooms_Organisation.class);
			Criteria subcrit = crit.createCriteria("organisation");
			subcrit.add(Restrictions.eq("organisation_id", organisation_id));
			crit.add(Restrictions.ne("deleted", "true"));
			crit.setFirstResult(start);
			crit.setMaxResults(max);
			List ll = crit.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}	
	
	private Rooms_Organisation getRoomsOrganisationByOrganisationIdAndRoomId(long organisation_id, long rooms_id){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Criteria crit = session.createCriteria(Rooms_Organisation.class);
			Criteria subcrit = crit.createCriteria("organisation");
			subcrit.add(Restrictions.eq("organisation_id", organisation_id));
			Criteria subcrit2 = crit.createCriteria("room");
			subcrit2.add(Restrictions.eq("rooms_id", rooms_id));				
			crit.add(Restrictions.ne("deleted", "true"));
			List ll = crit.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (ll.size()>0){
				return (Rooms_Organisation)ll.get(0);
			}
		} catch (HibernateException ex) {
			log.error("[getRoomsOrganisationByOrganisationIdAndRoomId] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsOrganisationByOrganisationIdAndRoomId] ", ex2);
		}
		return null;
	}	
	
	/**
	 * 
	 * @param organisation_id
	 * @return
	 */
	public List getRoomsOrganisationByRoomsId(long rooms_id){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Rooms_Organisation.class);
			Criteria subcrit = crit.createCriteria("room");
			subcrit.add(Restrictions.eq("rooms_id", rooms_id));
			crit.add(Restrictions.ne("deleted", "true"));
			List ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (HibernateException ex) {
			log.error("[getRoomsByOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}
	
	/**
	 * 
	 * @param user_id
	 * @param rooms_id
	 * @return
	 */
	private boolean checkUserOrgRoom(long user_id, long rooms_id){
		try {
			
			Users us = UsersDaoImpl.getInstance().getUser(user_id);
			Set s = us.getOrganisation_users();
		
			for (Iterator it = s.iterator(); it.hasNext();){
				Organisation_Users orgUsers = (Organisation_Users) it.next();
				long organisation_id = orgUsers.getOrganisation().getOrganisation_id();
				List ll = this.getRoomsOrganisationByOrganisationId(3, organisation_id);
				for (Iterator it2 = ll.iterator(); it2.hasNext(); ){
					Rooms_Organisation roomOrg = (Rooms_Organisation) it2.next();
					if(roomOrg.getRoom().getRooms_id()==rooms_id){
						return true;
					}
				}
			}
			
		} catch (HibernateException ex) {
			log.error("[checkUserOrgRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[checkUserOrgRoom] ", ex2);
		}
		return false;
	}
	
	/**
	 * 
	 * @param user_id
	 * @param user_level
	 * @param rooms_id
	 * @param roomtypes_id
	 * @param name
	 * @param ispublic
	 * @param comment
	 * @return
	 */
	public Rooms updateRoomsSelf(long user_id, long user_level, long rooms_id, long roomtypes_id, String name, boolean ispublic, String comment){
		try {
			if (AuthLevelmanagement.getInstance().checkModLevel(user_level)){
				
				//TODO: testen
				if (this.checkUserOrgRoom(user_id, rooms_id)){
					
					Rooms r = this.getRoomById(rooms_id);
					r.setComment(comment);
					r.setIspublic(ispublic);
					r.setName(name);
					r.setRoomtype(this.getRoomTypesById(roomtypes_id));
					r.setUpdatetime(new Date());
					Object idf = HibernateUtil.createSession();
					Session session = HibernateUtil.getSession();
					Transaction tx = session.beginTransaction();
					session.update(r);
					tx.commit();
					HibernateUtil.closeSession(idf);
				}
			}
		} catch (HibernateException ex) {
			log.error("[updateRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}
	
	/**
	 * Update a Record in the rooms table
	 * @param rooms_id
	 * @param roomtypes_id
	 * @param name
	 * @param ispublic
	 * @param comment
	 * @return
	 */
	public Long updateRoom(long user_level, long rooms_id, long roomtypes_id, String name,
			boolean ispublic, String comment, Long numberOfPartizipants, List organisations,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom){
		try {
			log.debug("*** updateRoom numberOfPartizipants: "+numberOfPartizipants);
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				Rooms r = this.getRoomById(rooms_id);
				r.setComment(comment);
				
				r.setIspublic(ispublic);
				r.setNumberOfPartizipants(numberOfPartizipants);
				r.setName(name);
				r.setRoomtype(this.getRoomTypesById(roomtypes_id));
				r.setUpdatetime(new Date());
				

				r.setIsDemoRoom(isDemoRoom);
				r.setDemoTime(demoTime);
				
				r.setAppointment(appointment);
				
				r.setIsModeratedRoom(isModeratedRoom);
				
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.update(r);
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				if (organisations!=null){
					Long t = this.updateRoomOrganisations(organisations, r);
					if (t==null) return null;
				}
				
				return r.getRooms_id();
			}
		} catch (HibernateException ex) {
			log.error("[updateRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}
	
	private boolean checkRoomAlreadyInOrg(Long orgid, List organisations) throws Exception{
		for (Iterator it = organisations.iterator();it.hasNext();){
			Rooms_Organisation rOrganisation = (Rooms_Organisation) it.next();
			if (rOrganisation.getOrganisation().getOrganisation_id().equals(orgid)) return true;
		}
		return false;
	}
	
	private boolean checkRoomShouldByDeleted(long orgId, List organisations) throws Exception{
		for (Iterator it = organisations.iterator();it.hasNext();){
			Integer key = (Integer) it.next();
			Long storedOrgId = key.longValue();
			if (storedOrgId.equals(orgId)) return true;
		}
		return false;
	}
	
	private Long updateRoomOrganisations(List organisations, Rooms room) throws Exception{
		List roomOrganisations = this.getOrganisationsByRoom(3, room.getRooms_id());
		
		List<Long> roomsToAdd = new LinkedList<Long>();
		List<Long> roomsToDel = new LinkedList<Long>();
		
		for (Iterator it = organisations.iterator();it.hasNext();){
			Integer key = (Integer) it.next();
			Long orgIdToAdd = key.longValue();
			if (!this.checkRoomAlreadyInOrg(orgIdToAdd, roomOrganisations)) roomsToAdd.add(orgIdToAdd);
		}
		
		for (Iterator it = roomOrganisations.iterator();it.hasNext();){
			Rooms_Organisation rOrganisation = (Rooms_Organisation) it.next();
			Long orgIdToDel = rOrganisation.getOrganisation().getOrganisation_id();
			if (!this.checkRoomShouldByDeleted(orgIdToDel, organisations)) roomsToDel.add(orgIdToDel);
		}
		
//		log.error("updateRoomOrganisations roomsToAdd: "+roomsToAdd.size());
//		log.error("updateRoomOrganisations roomsToDel: "+roomsToDel.size());
		
		for (Iterator<Long> it = roomsToAdd.iterator();it.hasNext();){
			Long orgIdToAdd = it.next();
			this.addRoomToOrganisation(3, room.getRooms_id(), orgIdToAdd);
		}
		for (Iterator<Long> it = roomsToDel.iterator();it.hasNext();){
			Long orgToDel = it.next();
			this.deleteRoomFromOrganisationByRoomAndOrganisation(room.getRooms_id(), orgToDel);
		}
		
		return new Long(1);
	}
	
	/**
	 * delete all Rooms_Organisations and Room by a given room_id
	 * @param rooms_id
	 */
	public Long deleteRoomById(long user_level, long rooms_id){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				this.deleteAllRoomsOrganisationOfRoom(rooms_id);
				return this.deleteRoom(this.getRoomById(rooms_id));
			}
		} catch (HibernateException ex) {
			log.error("[deleteRoomById] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteRoomById] ", ex2);
		}
		return null;
	}
	
	/**
	 * deletes a Room by given Room-Object
	 * @param r
	 */
	public Long deleteRoom(Rooms r){
		log.debug("deleteRoom");
		try {			
			r.setDeleted("true");
			r.setUpdatetime(new Date());
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(r);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return r.getRooms_id();
		} catch (HibernateException ex) {
			log.error("[deleteRoomsOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisation] ", ex2);
		}
		return null;
	}
	
	/**
	 * delete all Rooms_Organisation by a rooms_id
	 * @param rooms_id
	 */
	public void deleteAllRoomsOrganisationOfRoom(long rooms_id){
		try {
			List ll = this.getRoomsOrganisationByRoomsId(rooms_id);
			for (Iterator it = ll.iterator(); it.hasNext();){
				Rooms_Organisation rOrg = (Rooms_Organisation) it.next();
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (HibernateException ex) {
			log.error("[deleteAllRoomsOrganisationOfRoom] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOrganisationOfRoom] ", ex2);
		}
	}	
	
	/**
	 * Delete all room of a given Organisation
	 * @param organisation_id
	 */
	public void deleteAllRoomsOrganisationOfOrganisation(long organisation_id){
		try {
			List ll = this.getRoomsOrganisationByOrganisationId(3,organisation_id);
			for (Iterator it = ll.iterator(); it.hasNext();){
				Rooms_Organisation rOrg = (Rooms_Organisation) it.next();
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (HibernateException ex) {
			log.error("[deleteAllRoomsOfOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOfOrganisation] ", ex2);
		}
	}
	
	/**
	 * Delete a Rooms_Organisation by its id
	 * @param rooms_organisation_id
	 */
	public Long deleteRoomsOrganisationByID(long rooms_organisation_id){
		try {
			Rooms_Organisation rOrg = this.getRoomsOrganisationById(rooms_organisation_id);
			return this.deleteRoomsOrganisation(rOrg);
		} catch (HibernateException ex) {
			log.error("[deleteRoomsOrganisationByID] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisationByID] ", ex2);
		}
		return null;
	}
	
	private Long deleteRoomFromOrganisationByRoomAndOrganisation(long rooms_id, long organisation_id){
		try {
			Rooms_Organisation rOrganisation = this.getRoomsOrganisationByOrganisationIdAndRoomId(organisation_id, rooms_id);
			return this.deleteRoomsOrganisation(rOrganisation);
		} catch (HibernateException ex) {
			log.error("[deleteRoomFromOrganisationByRoomAndOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteRoomFromOrganisationByRoomAndOrganisation] ", ex2);
		}
		return null;
	}
	
	/**
	 * delete a Rooms_Organisation-Object
	 * @param rOrg
	 */
	public Long deleteRoomsOrganisation(Rooms_Organisation rOrg){
		try {			
			rOrg.setDeleted("true");
			rOrg.setUpdatetime(new Date());
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(rOrg);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return rOrg.getRooms_organisation_id();
		} catch (HibernateException ex) {
			log.error("[deleteRoomsOrganisation] ", ex);
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisation] ", ex2);
		}
		return null;
	}
	
	
	/**
	 * Update Room Object
	 */
	//--------------------------------------------------------------------------------------------
	public void updateRoomObject(Rooms room){
		log.debug("updateRoomObject");
		
		try{
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(room);
			tx.commit();
			HibernateUtil.closeSession(idf);
		}catch(Exception e){
			log.error("Error updateRoomObject : " + e.getMessage());
		}
	}
	//--------------------------------------------------------------------------------------------
	
}
