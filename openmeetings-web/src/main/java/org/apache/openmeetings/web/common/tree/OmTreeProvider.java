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
import java.util.Arrays;
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
	private static final List<Type> VIDEO_TYPES = Arrays.asList(Type.Folder, Type.Video);
	public static String RECORDINGS_MY = "recordings-my";
	public static String RECORDINGS_PUBLIC = "recordings-public";
	public static String RECORDINGS_GROUP = "recordings-group-%s";
	public static String FILES_MY = "files-my";
	public static String FILES_ROOM = "files-room";
	public static String FILES_GROUP = "files-group-%s";
	private final Long roomId;
	private final List<FileItem> roots = new ArrayList<>();
	private final String PUBLIC, GROUP;

	public OmTreeProvider(Long roomId) {
		this.roomId = roomId;
		PUBLIC = Application.getString(861);
		GROUP = Application.getString("files.root.group");
		refreshRoots(true);
	}

	public void refreshRoots(boolean all) {
		List<FileItem> fRoot = new ArrayList<>(), rRoot = new ArrayList<>();
		if (all) {
			if (roomId != null) {
				FileItem r = createRoot(Application.getString(706), FILES_MY, false);
				r.setOwnerId(getUserId());
				fRoot.add(r);
			}
		}
		if (roomId != null) {
			FileItem r = createRoot(Application.getString(707), FILES_ROOM, false);
			r.setRoomId(roomId);
			roots.add(r);
		}
		if (all) {
			{
				FileItem r = createRoot(Application.getString(860), RECORDINGS_MY, true);
				r.setOwnerId(getUserId());
				rRoot.add(r);
			}
			{
				FileItem r = createRoot(PUBLIC, RECORDINGS_PUBLIC, true);
				rRoot.add(r);
			}
		}
		for (GroupUser gu : getBean(UserDao.class).get(getUserId()).getGroupUsers()) {
			Group g = gu.getGroup();
			if (all) {
				FileItem r = createRoot(String.format("%s (%s)", PUBLIC, g.getName()), String.format(RECORDINGS_GROUP, g.getId()), true);
				r.setGroupId(g.getId());
				rRoot.add(r);
			}
			/*FileItem r = createRoot(String.format("%s (%s)", GROUP, g.getName()), String.format(FILES_GROUP, g.getId()), false);
			r.setGroupId(g.getId());
			r.setReadOnly(roomId == null); //group videos are read-only in recordings tree
			fRoot.add(r);*/
		}
		roots.clear();
		if (roomId == null) {
			roots.addAll(rRoot);
			roots.addAll(fRoot);
		} else {
			roots.addAll(fRoot);
			roots.addAll(rRoot);
		}
	}

	static FileItem createRoot(String name, String hash, boolean rec) {
		FileItem f = rec ? new Recording() : new FileExplorerItem();
		f.setType(Type.Folder);
		f.setName(name);
		f.setHash(hash);
		return f;
	}

	public FileItem getRoot() {
		return roots.get(0);
	}

	@Override
	public Iterator<FileItem> getRoots() {
		return roots.iterator();
	}

	public List<FileItem> getByParent(FileItem node, Long id) {
		List<FileItem> list = new ArrayList<>();
		if (node instanceof Recording) {
			Recording rec = (Recording)node;
			RecordingDao dao = getBean(RecordingDao.class);
			List<Recording> _list;
			if (id == null) {
				if (node.getOwnerId() == null) {
					_list = dao.getRootByPublic(rec.getGroupId());
				} else {
					_list = dao.getRootByOwner(node.getOwnerId());
				}
			} else {
				_list = dao.getByParent(id);
			}
			list.addAll(_list);
		} else {
			FileExplorerItemDao dao = getBean(FileExplorerItemDao.class);
			List<FileExplorerItem> _list;
			if (id == null) {
				if (node.getRoomId() != null) {
					_list = dao.getByRoom(node.getRoomId());
				} else if (node.getGroupId() != null) {
					_list = dao.getByGroup(node.getGroupId(), roomId == null ? VIDEO_TYPES : null);
				} else {
					_list = dao.getByOwner(node.getOwnerId());
				}
			} else {
				_list = dao.getByParent(id, roomId == null ? VIDEO_TYPES : null);
			}
			list.addAll(_list);
		}
		if (node.isReadOnly()) {
			for (FileItem f : list) {
				f.setReadOnly(true);
			}
		}
		return list;
	}

	@Override
	public Iterator<FileItem> getChildren(FileItem node) {
		return getByParent(node, node.getId()).iterator();
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
