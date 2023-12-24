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
package org.apache.openmeetings.db.bind.adapter;

import static org.apache.commons.lang3.math.NumberUtils.toLong;

import java.util.Map;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

public abstract class EntityAdapter<E extends IDataProviderEntity> extends XmlAdapter<String, E> {
	private final IDataProviderDao<E> dao;
	private final Map<Long, Long> idMap;

	protected EntityAdapter() {
		this(null, null);
	}

	protected EntityAdapter(IDataProviderDao<E> dao, Map<Long, Long> idMap) {
		this.dao = dao;
		this.idMap = idMap;
	}

	@Override
	public String marshal(E v) throws Exception {
		return "" + v.getId();
	}

	@Override
	public E unmarshal(String v) throws Exception {
		long oldId = toLong(v);
		Long newId = idMap.containsKey(oldId) ? idMap.get(oldId) : oldId;

		E r = dao.get(newId);
		return r == null ? newEntity() : r;
	}

	protected abstract E newEntity();
}
