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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomTypeDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomTypeDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private FieldLanguagesValuesDao fieldLangValueDao;
	
	/**
	 * get all availible RoomTypes
	 * 
	 * @return List of RoomTypes
	 */
	public List<RoomType> getAll(long langId) {
		TypedQuery<RoomType> query = em.createNamedQuery("getAllRoomTypes", RoomType.class);
		query.setParameter("deleted", true);
		List<RoomType> ll = query.getResultList();
		for (RoomType ti : ll) {
			ti.setLabel(fieldLangValueDao.get(ti.getFieldvalues_id(), langId));
		}
		return ll;
	}
	
	public RoomType get(long id) {
		TypedQuery<RoomType> query = em.createNamedQuery("getRoomTypesById", RoomType.class);
		query.setParameter("roomtypes_id", id);
		query.setParameter("deleted", true);
		List<?> ll = query.getResultList();
		if (ll.size() > 0) {
			return (RoomType) ll.get(0);
		}
		return null;
	}

	/**
	 * add a new Record to the table roomtypes
	 * 
	 * @param name
	 * @return ID of new created roomtype or null
	 */
	public Long addRoomType(String name, long fieldvalues_id, boolean deleted) {
		try {
			RoomType rtype = new RoomType();
			rtype.setName(name);
			rtype.setStarttime(new Date());
			rtype.setDeleted(deleted);
			rtype.setFieldvalues_id(fieldvalues_id);
			rtype = em.merge(rtype);
			long returnId = rtype.getRoomtypes_id();
			return returnId;
		} catch (Exception ex2) {
			log.error("[addRoomType] ", ex2);
		}
		return null;
	}
}
