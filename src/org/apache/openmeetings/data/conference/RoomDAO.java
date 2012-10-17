package org.apache.openmeetings.data.conference;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.persistence.beans.rooms.Rooms;
import org.apache.openmeetings.sip.api.impl.asterisk.AsteriskDbSipClient;
import org.apache.openmeetings.sip.api.request.SIPCreateConferenceRequest;
import org.apache.openmeetings.sip.api.result.SipCreateConferenceRequestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomDAO implements OmDAO<Rooms> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDaoImpl cfgDao;
	@Autowired
	private AsteriskDbSipClient asteriskDbSipClient;

	public Rooms get(long id) {
		TypedQuery<Rooms> q = em.createNamedQuery("getRoomById", Rooms.class);
		q.setParameter("id", id);
		List<Rooms> l = q.getResultList();
		return l.isEmpty() ? null : l.get(0);
	}

	public List<Rooms> get(int start, int count) {
		TypedQuery<Rooms> q = em.createNamedQuery("getNondeletedRooms", Rooms.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Rooms> get(String search, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countRooms", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void update(Rooms entity, long userId) {
		if (entity.getRooms_id() == null) {
	        /* Red5SIP integration *******************************************************************************/
			String sipEnabled = cfgDao.getConfValue("red5sip.enable", String.class, "no");
	        if("yes".equals(sipEnabled)) {
	            if(entity.getSipNumber() != null && !entity.getSipNumber().isEmpty()) {
	                asteriskDbSipClient.createSIPConference(new SIPCreateConferenceRequest(entity.getSipNumber()));
	            } else {
	                SipCreateConferenceRequestResult requestResult = asteriskDbSipClient.createSIPConference(new SIPCreateConferenceRequest());
	                if(!requestResult.hasError()) {
	                	entity.setSipNumber(requestResult.getConferenceNumber());
	                	entity.setConferencePin(requestResult.getConferencePin());
	                }
	            }
	        }
	        /*****************************************************************************************************/
			entity.setStarttime(new Date());
			entity = em.merge(entity);
		} else {
			entity.setUpdatetime(new Date());
			em.persist(entity);
		}
	}

	public void delete(Rooms entity, long userId) {
		entity.setDeleted(true);
		update(entity, userId);
	}

}
