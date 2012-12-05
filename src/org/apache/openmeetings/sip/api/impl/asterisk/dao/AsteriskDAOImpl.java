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
package org.apache.openmeetings.sip.api.impl.asterisk.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.sip.asterisk.AsteriskSipUsers;
import org.apache.openmeetings.persistence.beans.sip.asterisk.Extensions;
import org.apache.openmeetings.persistence.beans.sip.asterisk.MeetMe;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AsteriskDAOImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(AsteriskDAOImpl.class, OpenmeetingsVariables.webAppRootKey);
    private static final String DEFAULT_SIP_CONTEXT = "rooms";
	@PersistenceContext
	private EntityManager em;
    @Autowired
	private ConfigurationDao configurationDaoImpl;

    public void addSipUser(String username, String secret) {
        AsteriskSipUsers u = new AsteriskSipUsers();
        String defaultRoomContext = configurationDaoImpl
        		.getConfValue("red5sip.exten_context", String.class, "rooms");
        u.setName(username);
        u.setSecret(secret);
        u.setContext(defaultRoomContext);
        u.setHost("dynamic");
        Extensions e = new Extensions();
        e.setApp("Dial");
        e.setAppdata("SIP/" + username);
        e.setExten(username);
        e.setContext(defaultRoomContext);
        e.setPriority(1);
        em.persist(u);
        em.persist(e);
        log.debug("addSipUser successfull");
    }

    public String addMeetMeConference() {
        int count = (em.createQuery("SELECT COUNT(m) FROM MeetMe AS m", Long.class).getSingleResult()).intValue();
        String prefix = configurationDaoImpl.getConfValue("red5sip.room_prefix", String.class, DEFAULT_SIP_CONTEXT);
        String confno;
        int count1;
        do { // FIXME ugly
        	confno = prefix + (++count);
        	count1 = (em.createQuery("SELECT COUNT(m) FROM MeetMe AS m WHERE m.confno = :confno", Long.class)
                .setParameter("confno", confno)
        		.getSingleResult()).intValue();
        } while (count1 > 0);
        addMeetMeConference(confno);
        return confno;
    }

    public String addMeetMeConference(String sipNumber) {
        MeetMe mm = new MeetMe();
        mm.setConfno(sipNumber);
        Extensions e = new Extensions();
        e.setApp("MeetMe");
        e.setAppdata(sipNumber + ",dM");
        e.setExten(sipNumber);
        e.setContext("rooms");
        e.setPriority(1);
        em.persist(mm);
        em.persist(e);
        return sipNumber;
    }

    public Integer getConferenceMembersNumber(String confNo) {
        return ((Number)em.createQuery("SELECT m.members FROM MeetMe AS m WHERE m.confno=:confno")
                .setParameter("confno", confNo)
                .getSingleResult()).intValue();
    }
    
    public int saveAsteriskSipUsers(AsteriskSipUsers asteriskSipUsers) {
    	em.persist(asteriskSipUsers);
    	return asteriskSipUsers.getId();
    }
    
    public List<AsteriskSipUsers> getAsteriskSipUsers() {
    	return em.createQuery("select c from AsteriskSipUsers as c", AsteriskSipUsers.class)
                .getResultList();
    }
    
    public int saveExtensions(Extensions extensions) {
    	em.persist(extensions);
    	return extensions.getId();
    }
    
    public List<Extensions> getExtensions() {
    	return em.createQuery("select c from Extensions as c", Extensions.class)
                .getResultList();
    }
    
    public String saveMeetMe(MeetMe meetMe) {
    	em.persist(meetMe);
    	return meetMe.getConfno();
    }
    
    public List<MeetMe> getMembers() {
        return em.createQuery("select c from MeetMe as c", MeetMe.class)
                .getResultList();
    }
    
}