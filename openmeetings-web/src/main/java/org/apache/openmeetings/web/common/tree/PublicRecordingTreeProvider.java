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
package org.apache.openmeetings.web.common.tree;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.app.Application;

public class PublicRecordingTreeProvider extends RecordingTreeProvider {
	private static final long serialVersionUID = 1L;
	private final Long groupId;
	private final String name;

	public PublicRecordingTreeProvider(Long groupId, String name) {
		this.groupId = groupId;
		this.name = name;
	}
	
	public Iterator<? extends Recording> getRoots() {
		Recording r = new Recording();
		r.setId(groupId == null ? -1 : -groupId);
		r.setGroupId(groupId);
		r.setOwnerId(null);
		r.setType(Type.Folder);
		String pub = Application.getString(861);
		r.setName(groupId == null ? pub : String.format("%s (%s)", pub, name));
		return Arrays.asList(r).iterator();
	}
	
	public Iterator<? extends Recording> getChildren(Recording node) {
		if (node.getId() < 0) {
			return getBean(RecordingDao.class).getRootByPublic(groupId).iterator();
		} else {
			return super.getChildren(node);
		}
	}
}
