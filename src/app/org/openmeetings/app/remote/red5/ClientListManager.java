package org.openmeetings.app.remote.red5;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

//FIXME using of named queries can speed up things
@Transactional
public class ClientListManager {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientListManager.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ManageCryptStyle manageCryptStyle;

	public RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl) {
		try {

			// Store the Connection into a bean and add it to the HashMap
			RoomClient rcm = new RoomClient();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope(scopeName);
			long thistime = new Date().getTime();
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(thistime).toString()));

			rcm.setUserport(remotePort);
			rcm.setUserip(remoteAddress);
			rcm.setSwfurl(swfUrl);
			rcm.setIsMod(new Boolean(false));
			rcm.setCanDraw(new Boolean(false));

			if (getClientByStreamId(rcm.getStreamid()) != null) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			em.persist(rcm);
			
			log.debug(" :: addClientListItem :: " + rcm.getRoomClientId());

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	public RoomClient getClientByStreamId(String streamId) {
		try {
			log.debug(" :: getClientByStreamId :: " + streamId);
			Query q = em.createQuery("select rc from RoomClient rc where rc.streamid = :streamid");
			q.setParameter("streamid", streamId);
			return (RoomClient)q.getSingleResult();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public RoomClient getClientByPublicSID(String publicSID) {
		try {
			log.debug(" :: getClientByPublicSID :: " + publicSID);
			Query q = em.createQuery("select rc from RoomClient rc where rc.publicSID = :publicSID");
			q.setParameter("publicSID", publicSID);
			return (RoomClient)q.getSingleResult();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	public RoomClient getClientByUserId(Long userId) {
		try {
			log.debug(" :: getClientByUserId :: " + userId);
			Query q = em.createQuery("select rc from RoomClient rc where rc.user_id = :user_id");
			q.setParameter("user_id", userId);
			return (RoomClient)q.getSingleResult();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	public Boolean updateClientByStreamId(String streamId,
			RoomClient rcm) {
		try {
			if (getClientByStreamId(streamId) != null) { //FIXME too heavy
				em.merge(rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[updateClientByStreamId]", err);
		}
		return null;
	}

	public Boolean removeClient(String streamId) {
		try {
			RoomClient rc = getClientByStreamId(streamId);
			if (rc != null && em.contains(rc)) {
				em.remove(rc);
				// log.debug(":: removeClient ::"+clientList.size());
				return true;
			} else {
				log.debug("Tried to remove a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return null;
	}

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 * 
	 * @return
	 */
	//FIXME seems like there is no need to return HashMap
	@SuppressWarnings("unchecked")
	public HashMap<String, RoomClient> getClientListByRoom(
			Long room_id) {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		if (room_id == null) {
			return roomClientList;
		}
		try {
			Query q = em.createQuery("select rc from RoomClient rc where rc.room_id = :room_id and rc.isScreenClient = :isScreenClient");
			q.setParameter("room_id", room_id);
			q.setParameter("isScreenClient", false);
			for (RoomClient rcl : (List<RoomClient>)q.getResultList()) {
				roomClientList.put(rcl.getStreamid(), rcl);
			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	//FIXME seems to be copy/pasted with previous one
	@SuppressWarnings("unchecked")
	public HashMap<String, RoomClient> getClientListByRoomAll(
			Long room_id) {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		if (room_id == null) {
			return roomClientList;
		}
		try {
			Query q = em.createQuery("select rc from RoomClient rc where rc.room_id = :room_id");
			q.setParameter("room_id", room_id);
			for (RoomClient rcl : (List<RoomClient>)q.getResultList()) {
				roomClientList.put(rcl.getStreamid(), rcl);
			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	/**
	 * get the current Moderator in this room
	 * 
	 * @param roomname
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RoomClient> getCurrentModeratorByRoom(Long room_id) {

		List<RoomClient> rclList = new LinkedList<RoomClient>();
		if (room_id == null) {
			return rclList;
		}

		try {
			Query q = em.createQuery("select rc from RoomClient rc where rc.room_id = :room_id and rc.isMod = :isMod");
			q.setParameter("room_id", room_id);
			q.setParameter("isMod", true);
			rclList = (List<RoomClient>)q.getResultList();
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return rclList;
	}

	@SuppressWarnings("unchecked")
	public List<RoomClient> getAllClients() {
		Query q = em.createQuery("select rc from RoomClient rc");
		return q.getResultList();
	}
	
	public SearchResult getListByStartAndMax(int start, int max,
			String orderby, boolean asc) {
		String sq = "select rc from RoomClient rc";
		if (orderby != null && orderby.trim().length() > 0) {
			sq += " ORDER BY rc." + orderby + " " + (asc ? "ASC" : "DESC");
		}
		Query q = em.createQuery(sq);
		q.setFirstResult(start);
		q.setMaxResults(max);
		
		SearchResult sResult = new SearchResult();
		sResult.setObjectName(RoomClient.class.getName());
		sResult.setResult(q.getResultList());
		sResult.setRecords((long)sResult.getResult().size());

		return sResult;
	}

	public void removeAllClients() {
		try {
			log.debug(" :: removeAllClients :: ");
			Query q = em.createQuery("DELETE FROM RoomClient rc");
			q.executeUpdate();
		} catch (Exception err) {
			log.error("[removeAllClients]", err);
		}
	}
}
