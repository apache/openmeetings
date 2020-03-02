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
import static org.apache.openmeetings.db.bind.adapter.CDATAAdapter.CDATA_BEGIN;
import static org.apache.openmeetings.db.bind.adapter.CDATAAdapter.CDATA_END;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GroupAdapter extends XmlAdapter<String, Group> {
	private final GroupDao groupDao;
	private final Map<Long, Long> idMap;

	public GroupAdapter() {
		this(null, null);
	}

	public GroupAdapter(GroupDao groupDao, Map<Long, Long> idMap) {
		this.groupDao = groupDao;
		this.idMap = idMap;
	}

	@Override
	public String marshal(Group v) throws Exception {
		return CDATA_BEGIN + v.getId() + CDATA_END;
	}

	@Override
	public Group unmarshal(String v) throws Exception {
		long oldId = toLong(v);
		Long newId = idMap.containsKey(oldId) ? idMap.get(oldId) : oldId;

		Group g = groupDao.get(newId);
		return g == null ? new Group() : g;
	}
}
