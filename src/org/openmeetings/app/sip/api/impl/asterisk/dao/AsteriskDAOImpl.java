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
package org.openmeetings.app.sip.api.impl.asterisk.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.sip.asterisk.AsteriskSipUsers;
import org.openmeetings.app.persistence.beans.sip.asterisk.Extensions;
import org.openmeetings.app.persistence.beans.sip.asterisk.MeetMe;
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
	private Configurationmanagement cfgManagement;

    public void addSipUser(String username, String secret) {
        AsteriskSipUsers u = new AsteriskSipUsers();
        Configuration conf = cfgManagement.getConfKey(3L,"red5sip.exten_context");
        String defaultRoomContext = "rooms";
        if(conf != null && !conf.getConf_value().isEmpty()) {
            defaultRoomContext = conf.getConf_value();
        }
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
        int count = ((Number)em.createQuery("SELECT COUNT(m.confno) FROM MeetMe AS m").getSingleResult()).intValue();
        Configuration conf = cfgManagement.getConfKey(3L,"red5sip.room_prefix");
        String prefix = DEFAULT_SIP_CONTEXT;
        if(conf != null) {
            prefix = conf.getConf_value();
        }
        String confno = prefix+(count+1);
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

    public Integer getConferensMembersNumber(String confNo) {
        return ((Number)em.createQuery("SELECT m.members FROM MeetMe AS m WHERE m.confno=:confno")
                .setParameter("confno", confNo)
                .getSingleResult()).intValue();
    }
}