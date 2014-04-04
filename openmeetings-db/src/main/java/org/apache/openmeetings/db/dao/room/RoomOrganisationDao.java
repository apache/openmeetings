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
package org.apache.openmeetings.db.dao.room;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomOrganisationDao {
	@PersistenceContext
	private EntityManager em;

	public List<RoomOrganisation> get() {
		return em.createNamedQuery("getAllRoomsOrganisations", RoomOrganisation.class).getResultList();
	}

	public RoomOrganisation update(RoomOrganisation entity, Long userId) {
		if (entity.getRooms_organisation_id() == null) {
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity.setUpdatetime(new Date());
			entity = em.merge(entity);
		}
		return entity;
	}

}
