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
package org.apache.openmeetings.db.dao.calendar;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

@Transactional
public class OmCalendarDao {
    private static final Logger log = Red5LoggerFactory.getLogger(OmCalendarDao.class, webAppRootKey);

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AppointmentDao appointmentDao;

    public List<OmCalendar> get(){
        return em.createNamedQuery("getCalendars", OmCalendar.class).getResultList();
    }

    public OmCalendar update(OmCalendar c){
        if(c.getId() == null){
            em.persist(c);
        } else
            c = em.merge(c);

        return c;
    }

    public OmCalendar getCalendarbyId(Long calId){
        return em.createNamedQuery("getCalendarbyId", OmCalendar.class)
                .setParameter("calId", calId)
                .getSingleResult();
    }

    public void delete(OmCalendar c){
        c.setDeleted(true);

        //Delete all appointments in calendar.
        List<Appointment> appointments = appointmentDao.getAppointmentsinCalendar(c.getId());
        for(Appointment appointment: appointments)
            appointmentDao.delete(appointment, appointment.getOwner().getId());

        update(c);
    }
}
