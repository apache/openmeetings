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
package org.apache.openmeetings.db.dao.file;

import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;

import java.io.File;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.util.OmFileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Repository
@Transactional
public class FileItemDao extends BaseFileItemDao {
	private static final Logger log = LoggerFactory.getLogger(FileItemDao.class);

	public List<FileItem> getByRoom(Long roomId) {
		log.debug("getByRoom roomId :: {}", roomId);
		return em.createNamedQuery("getFilesByRoom", FileItem.class).setParameter("roomId", roomId).getResultList();
	}

	public List<FileItem> getByOwner(Long ownerId) {
		log.debug("getByOwner() started");
		TypedQuery<FileItem> query = em.createNamedQuery("getFilesByOwner", FileItem.class);
		query.setParameter("ownerId", ownerId);

		return query.getResultList();
	}

	public List<FileItem> getByGroup(Long groupId) {
		log.debug("getByGroup() started");
		return em.createNamedQuery("getFileByGroup", FileItem.class)
				.setParameter("groupId", groupId)
				.getResultList();
	}

	public List<FileItem> getByGroup(Long groupId, List<Type> filter) {
		if (filter == null) {
			return getByGroup(groupId);
		}
		log.debug("getByGroup() started");
		return em.createNamedQuery("getFileFilteredByGroup", FileItem.class)
				.setParameter("filter", filter)
				.setParameter("groupId", groupId)
				.getResultList();
	}

	public List<FileItem> getByParent(Long parentId) {
		log.debug("getByParent() started");
		return em.createNamedQuery("getFilesByParent", FileItem.class)
				.setParameter("parentId", parentId)
				.getResultList();
	}

	public List<FileItem> getByParent(Long parentId, List<Type> filter) {
		if (filter == null) {
			return getByParent(parentId);
		}
		log.debug("getByParent(filter) started");
		return em.createNamedQuery("getFilesFilteredByParent", FileItem.class)
				.setParameter("filter", filter)
				.setParameter("parentId", parentId)
				.getResultList();
	}

	@Override
	public FileItem get(Long id) {
		BaseFileItem bf = super.get(id);
		return bf instanceof FileItem fi ? fi : null;
	}

	public FileItem get(String externalId, String externalType) {
		log.debug("get started");

		return only(em.createNamedQuery("getFileExternal", FileItem.class)
				.setParameter("externalFileId", externalId).setParameter("externalType", externalType)
				.getResultList());
	}

	public List<FileItem> get() {
		log.debug("get started");

		return em.createNamedQuery("getAllFiles", FileItem.class).getResultList();
	}

	public List<FileItem> getExternal(String externalType) {
		log.debug("get external started");

		return em.createNamedQuery("getFileAllExternal", FileItem.class)
				.setParameter("externalType", externalType)
				.getResultList();
	}

	public void delete(String externalId, String externalType) {
		log.debug("delete started");

		delete(get(externalId, externalType));
	}

	/**
	 * @param id - id of file item to rename
	 * @param name - new name
	 *
	 * @return renamed item
	 */
	public FileItem rename(Long id, String name) {
		log.debug("rename started");

		FileItem f = get(id);
		if (f == null) {
			return null;
		}
		f.setName(name);
		return update(f);
	}

	public FileItem update(FileItem f) {
		return (FileItem)super.updateBase(f);
	}

	private void updateChilds(FileItem f) {
		for (FileItem child : getByParent(f.getId())) {
			child.setOwnerId(f.getOwnerId());
			child.setRoomId(f.getRoomId());
			update(child);
			if (Type.FOLDER == f.getType()) {
				updateChilds(child);
			}
		}
	}

	/**
	 * @param id - id of file item to move
	 * @param parentId - id of parent item
	 * @param ownerId - id of item owner
	 * @param roomId - id of room
	 *
	 * @return moved item
	 */
	public FileItem move(long id, long parentId, long ownerId, long roomId) {
		log.debug(".move() started");

		FileItem f = get(Long.valueOf(id));
		if (f == null) {
			return null;
		}

		if (parentId < 0) {
			if (parentId == -1) {
				// move to personal Folder
				f.setOwnerId(ownerId);
				f.setRoomId(null);
			} else {
				// move to public room folder
				f.setOwnerId(null);
				f.setRoomId(roomId);
			}
			f.setParentId(null);
		} else {
			f.setParentId(parentId);
			f.setOwnerId(null);
		}
		if (Type.FOLDER == f.getType()) {
			updateChilds(f);
		}
		return update(f);
	}

	public List<BaseFileItem> getAllRoomFiles(String search, long start, long count, Long roomId, List<Group> groups) {
		return setLimits(em.createNamedQuery("getAllFileItemsForRoom", BaseFileItem.class)
					.setParameter("folder", Type.FOLDER)
					.setParameter("roomId", roomId)
					.setParameter("groups", groups.parallelStream().map(Group::getId).toList())
					.setParameter("name", String.format("%%%s%%", search == null ? "" : search))
				, start, count).getResultList();
	}

	public List<BaseFileItem> get(Collection<String> ids) {
		return em.createNamedQuery("getFileItemsByIds", BaseFileItem.class)
				.setParameter("ids", ids.parallelStream().map(Long::valueOf).toList())
				.getResultList();
	}

	public long getOwnSize(Long userId) {
		return getSize(getByOwner(userId));
	}

	public long getRoomSize(Long roomId) {
		return getSize(getByRoom(roomId));
	}

	public long getSize(List<FileItem> list) {
		long size = 0;
		for (FileItem f : list) {
			size += getSize(f);
		}
		return size;
	}

	public long getSize(FileItem f) {
		long size = 0;
		try {
			if (f.exists()) {
				File base = OmFileHelper.getUploadFilesDir();
				switch (f.getType()) {
					case IMAGE, PRESENTATION, VIDEO:
						File tFolder = new File(base, f.getHash());

						if (tFolder.exists()) {
							size += OmFileHelper.getSize(tFolder);
						}
						break;
					default:
						break;
				}
			}
			if (Type.FOLDER == f.getType()) {
				for (FileItem child : getByParent(f.getId())) {
					size += getSize(child);
				}
			}
		} catch (Exception err) {
			log.error("[getSize] ", err);
		}
		return size;
	}
}
