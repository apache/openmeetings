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
package org.apache.openmeetings.web.room.sidebar;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.tree.FileItemTree;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.openmeetings.web.common.tree.MyRecordingTreeProvider;
import org.apache.openmeetings.web.common.tree.PublicRecordingTreeProvider;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class RoomFilePanel extends FileTreePanel {
	private static final long serialVersionUID = 1L;
	private final RoomPanel room;

	public RoomFilePanel(String id, RoomPanel room) {
		super(id);
		this.room = room;
	}
	
	@Override
	public void updateSizes() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(AjaxRequestTarget target, FileItem f) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Component getUpload(String id) {
		Component u = super.getUpload(id);
		u.setVisible(true);
		u.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				room.getSidebar().showUpload(target);
			}
		});
		return u;
	}
	
	@Override
	public void defineTrees() {
		FileExplorerItem f = new FileExplorerItem();
		f.setOwnerId(getUserId());
		selectedFile.setObject(f);
		treesView.add(selected = new FileItemTree<FileExplorerItem>(treesView.newChildId(), this, new FilesTreeProvider(null)));
		treesView.add(new FileItemTree<FileExplorerItem>(treesView.newChildId(), this, new FilesTreeProvider(room.getRoom().getId())));
		treesView.add(new FileItemTree<Recording>(treesView.newChildId(), this, new MyRecordingTreeProvider()));
		treesView.add(new FileItemTree<Recording>(treesView.newChildId(), this, new PublicRecordingTreeProvider(null, null)));
		for (GroupUser ou : getBean(UserDao.class).get(getUserId()).getGroupUsers()) {
			Group o = ou.getGroup();
			treesView.add(new FileItemTree<Recording>(treesView.newChildId(), this, new PublicRecordingTreeProvider(o.getId(), o.getName())));
		}
	}
	
	@Override
	public void createFolder(String name) {
		if (selectedFile.getObject() instanceof Recording) {
			createRecordingFolder(name);
		} else {
			FileExplorerItem f = new FileExplorerItem();
			f.setName(name);
			f.setInsertedBy(getUserId());
			f.setInserted(new Date());
			f.setType(Type.Folder);;
			FileItem p = selectedFile.getObject();
			long parentId = p.getId();
			f.setParentId(Type.Folder == p.getType() && parentId > 0 ? parentId : null);
			f.setOwnerId(p.getOwnerId());
			f.setRoomId(p.getRoomId());
			getBean(FileExplorerItemDao.class).update(f);
		}
	}

	static class FilesTreeProvider implements ITreeProvider<FileExplorerItem> {
		private static final long serialVersionUID = 1L;
		Long roomId = null;

		FilesTreeProvider(Long roomId) {
			this.roomId = roomId;
		}
		
		@Override
		public void detach() {
			// TODO LDM should be used
		}

		@Override
		public boolean hasChildren(FileExplorerItem node) {
			return node.getId() <= 0 || Type.Folder == node.getType();
		}

		@Override
		public Iterator<? extends FileExplorerItem> getChildren(FileExplorerItem node) {
			FileExplorerItemDao dao = getBean(FileExplorerItemDao.class);
			List<FileExplorerItem> list = null;
			if (node.getId() == 0) {
				list = dao.getByOwner(node.getOwnerId());
			} else if (node.getId() < 0) {
				list = dao.getByRoom(roomId);
			} else {
				list = dao.getByParent(node.getId());
			}
			return list.iterator();
		}

		@Override
		public IModel<FileExplorerItem> model(FileExplorerItem object) {
			// TODO LDM should be used
			return Model.of(object);
		}

		@Override
		public Iterator<? extends FileExplorerItem> getRoots() {
			FileExplorerItem f = new FileExplorerItem();
			f.setRoomId(roomId);
			f.setType(Type.Folder);
			if (roomId == null) {
				f.setId(0L);
				f.setOwnerId(getUserId());
				f.setName(Application.getString(706));
			} else {
				f.setId(-roomId);
				f.setName(Application.getString(707));
			}
			return Arrays.asList(f).iterator();
		}
	}
}
