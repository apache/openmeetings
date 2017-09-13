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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Transactional
public class FileItemDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FileItemDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	public FileItem add(String fileName, Long parentId, Long ownerId, Long roomId, Long insertedBy,
			Type type, String externalId, String externalType) {
		log.debug(".add(): adding file " + fileName + " roomID: " + roomId);
		try {
			FileItem fileItem = new FileItem();
			fileItem.setName(fileName);
			fileItem.setHash(UUID.randomUUID().toString());
			fileItem.setDeleted(false);
			fileItem.setParentId(parentId);
			fileItem.setOwnerId(ownerId);
			fileItem.setRoomId(roomId);
			fileItem.setInserted(new Date());
			fileItem.setInsertedBy(insertedBy);
			fileItem.setType(type);
			fileItem.setUpdated(new Date());
			fileItem.setExternalId(externalId);
			fileItem.setExternalType(externalType);

			fileItem = em.merge(fileItem);

			log.debug(".add(): file " + fileName + " added as " + fileItem.getId());
			return fileItem;
		} catch (Exception ex2) {
			log.error(".add(): ", ex2);
		}
		return null;
	}

	public List<FileItem> getByRoomAndOwner(Long roomId, Long ownerId) {
		log.debug(".getByRoomAndOwner() started");
		try {
			TypedQuery<FileItem> query = em.createNamedQuery("getFilesByRoomAndOwner", FileItem.class);
			query.setParameter("roomId", roomId);
			query.setParameter("ownerId", ownerId);

			List<FileItem> fileExplorerList = query.getResultList();

			return fileExplorerList;
		} catch (Exception ex2) {
			log.error("[getByRoomAndOwner]: ", ex2);
		}
		return null;
	}

	public List<FileItem> getByRoom(Long roomId) {
		log.debug("getByRoom roomId :: " + roomId);
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

	public FileItem getByHash(String hash) {
		log.debug("getByHash() started");
		FileItem f = null;
		TypedQuery<FileItem> query = em.createNamedQuery("getFileByHash", FileItem.class);
		query.setParameter("hash", hash);

		try {
			f = query.getSingleResult();
		} catch (NoResultException ex) {
			//no-op
		}
		return f;
	}

	public FileItem get(Long id) {
		FileItem f = null;
		if (id != null && id > 0) {
			TypedQuery<FileItem> query = em.createNamedQuery("getFileById", FileItem.class)
					.setParameter("id", id);

			try {
				f = query.getSingleResult();
			} catch (NoResultException ex) {
			}
		} else {
			log.info("[get] " + "Info: No id given");
		}
		return f;
	}

	public FileItem get(String externalId, String externalType) {
		FileItem f = null;
		log.debug("get started");

		try {
			TypedQuery<FileItem> query = em.createNamedQuery("getFileExternal", FileItem.class)
					.setParameter("externalFileId", externalId).setParameter("externalType", externalType);

			try {
				f = query.getSingleResult();
			} catch (NoResultException ex) {
			}

		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return f;
	}

	public List<FileItem> get() {
		log.debug("get started");

		return em.createNamedQuery("getAllFiles", FileItem.class).getResultList();
	}

	public void delete(FileItem f) {
		f.setDeleted(true);
		f.setUpdated(new Date());

		update(f);
	}

	public void delete(String externalId, String externalType) {
		log.debug("delete started");

		delete(get(externalId, externalType));
	}

	/**
	 * @param id
	 * @param name
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
		if (f.getId() == null) {
			f.setInserted(new Date());
			em.persist(f);
		} else {
			f.setUpdated(new Date());
			f = em.merge(f);
		}
		return f;
	}

	private void updateChilds(FileItem f) {
		for (FileItem child : getByParent(f.getId())) {
			child.setOwnerId(f.getOwnerId());
			child.setRoomId(f.getRoomId());
			update(child);
			if (Type.Folder == f.getType()) {
				updateChilds(child);
			}
		}
	}

	/**
	 * @param id
	 * @param parentId
	 * @param isOwner
	 * @param roomId
	 */
	public FileItem move(long id, long parentId, long ownerId, long roomId) {
		log.debug(".move() started");

		FileItem f = get(id);
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
		if (Type.Folder == f.getType()) {
			updateChilds(f);
		}
		return update(f);
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
				if (Type.Image == f.getType()) {
					size += f.getFile().length();
					File thumbFile = new File(base, String.format("%s%s.%s", thumbImagePrefix, f.getHash(), EXTENSION_JPG));
					if (thumbFile.exists()) {
						size += thumbFile.length();
					}
				}
				if (Type.Presentation == f.getType()) {
					File tFolder = new File(base, f.getHash());

					if (tFolder.exists()) {
						size += OmFileHelper.getSize(tFolder);
					}
				}
				if (Type.Video == f.getType()) {
					size += f.getFile().length();
					File thumb = f.getFile(EXTENSION_JPG);
					if (thumb.exists()) {
						size += thumb.length();
					}
				}
			}
			if (Type.Folder == f.getType()) {
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
