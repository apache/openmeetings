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
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class OmTreeProvider implements ITreeProvider<FileItem> {
	private static final long serialVersionUID = 1L;
	public static String RECORDINGS_MY = "recordings-my";
	public static String RECORDINGS_PUBLIC = "recordings-public";
	public static String RECORDINGS_GROUP = "recordings-group-%s";
	public static String FILES_MY = "files-my";
	public static String FILES_ROOM = "files-room";
	private Long roomId = null;
	private final List<FileItem> roots = new ArrayList<>();

	public OmTreeProvider(Long roomId) {
		this.roomId = roomId;
		if (roomId != null) {
			roots.add(createFileRoot(null));
			roots.add(createFileRoot(roomId));
		}
		final String PUBLIC = Application.getString(861);
		{
			Recording r = createRecRoot(Application.getString(860), RECORDINGS_MY);
			r.setOwnerId(getUserId());
			roots.add(r);
		}
		{
			Recording r = createRecRoot(PUBLIC, RECORDINGS_PUBLIC);
			roots.add(r);
		}
		for (GroupUser gu : getBean(UserDao.class).get(getUserId()).getGroupUsers()) {
			Group g = gu.getGroup();
			
			Recording r = createRecRoot(String.format("%s (%s)", PUBLIC, g.getName()), String.format(RECORDINGS_GROUP, g.getId()));
			r.setGroupId(g.getId());
			roots.add(r);
		}
	}
	
	static Recording createRecRoot(String name, String hash) {
		Recording r = new Recording();
		r.setType(Type.Folder);
		r.setName(name);
		r.setHash(hash);
		return r;
	}
	
	static FileExplorerItem createFileRoot(Long roomId) {
		FileExplorerItem f = new FileExplorerItem();
		f.setRoomId(roomId);
		f.setType(Type.Folder);
		if (roomId == null) {
			f.setOwnerId(getUserId());
			f.setName(Application.getString(706));
			f.setHash(FILES_MY);
		} else {
			f.setName(Application.getString(707));
			f.setHash(FILES_ROOM);
		}
		return f;
	}
	
	public FileItem getRoot() {
		return roots.get(0);
	}
	
	@Override
	public Iterator<FileItem> getRoots() {
		return roots.iterator();
	}

	@Override
	public Iterator<FileItem> getChildren(FileItem node) {
		List<FileItem> list = new ArrayList<>();
		if (node instanceof Recording) {
			Recording rec = (Recording)node;
			RecordingDao dao = getBean(RecordingDao.class);
			List<Recording> _list;
			if (node.getId() == null) {
				if (node.getOwnerId() == null) {
					_list = dao.getRootByPublic(rec.getGroupId());
				} else {
					_list = dao.getRootByOwner(node.getOwnerId());
				}
			} else {
				_list = dao.getByParent(node.getId());
			}
			for (Recording r : _list) {
				list.add(r);
			}
		} else {
			FileExplorerItemDao dao = getBean(FileExplorerItemDao.class);
			List<FileExplorerItem> _list;
			if (node.getId() == null) {
				if (roomId == null) {
					_list = dao.getByRoom(roomId);
				} else {
					_list = dao.getByOwner(node.getOwnerId());
				}
			} else {
				_list = dao.getByParent(node.getId());
			}
			for (FileExplorerItem r : _list) {
				list.add(r);
			}
		}
		return list.iterator();
	}

	@Override
	public boolean hasChildren(FileItem node) {
		return node.getId() == null || Type.Folder == node.getType();
	}

	@Override
	public IModel<FileItem> model(FileItem object) {
		// TODO LDM should be used
		return Model.of(object);
	}

	@Override
	public void detach() {
		// TODO LDM should be used
	}
}
