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

import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 * 
 */
@Transactional
public class FileExplorerItemDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FileExplorerItemDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	public FileExplorerItem add(String fileName, Long parentId, Long ownerId, Long roomId, Long insertedBy,
			Type type, String externalId, String externalType) {
		log.debug(".add(): adding file " + fileName + " roomID: " + roomId);
		try {
			FileExplorerItem fileItem = new FileExplorerItem();
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

	public List<FileExplorerItem> getFileExplorerItemsByRoomAndOwner(Long roomId, Long ownerId) {
		log.debug(".getFileExplorerItemsByRoomAndOwner() started");
		try {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByRoomAndOwner", FileExplorerItem.class);
			query.setParameter("roomId", roomId);
			query.setParameter("ownerId", ownerId);

			List<FileExplorerItem> fileExplorerList = query.getResultList();

			return fileExplorerList;
		} catch (Exception ex2) {
			log.error("[getFileExplorerItemsByRoomAndOwner]: ", ex2);
		}
		return null;
	}

	public List<FileExplorerItem> getByRoom(Long roomId) {
		log.debug("getFileExplorerItemsByRoom roomId :: " + roomId);
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByRoom", FileExplorerItem.class);
		query.setParameter("roomId", roomId);

		return query.getResultList();
	}

	public List<FileExplorerItem> getByOwner(Long ownerId) {
		log.debug("getByOwner() started");
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByOwner", FileExplorerItem.class);
		query.setParameter("ownerId", ownerId);

		return query.getResultList();
	}

	public List<FileExplorerItem> getByParent(Long parentId) {
		log.debug("getByParent() started");
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByParent", FileExplorerItem.class);
		query.setParameter("parentId", parentId);

		return query.getResultList();
	}

	public FileExplorerItem getByHash(String hash) {
		log.debug("getByHash() started");
		FileExplorerItem f = null;
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileByHash", FileExplorerItem.class);
		query.setParameter("hash", hash);

		try {
			f = query.getSingleResult();
		} catch (NoResultException ex) {
			//no-op
		}
		return f;
	}

	public FileExplorerItem get(Long id) {
		FileExplorerItem f = null;
		if (id != null && id > 0) {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileById", FileExplorerItem.class)
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

	public FileExplorerItem get(String externalId, String externalType) {
		FileExplorerItem f = null;
		log.debug("get started");

		try {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileExternal", FileExplorerItem.class)
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

	public List<FileExplorerItem> get() {
		log.debug("get started");

		return em.createNamedQuery("getAllFiles", FileExplorerItem.class).getResultList();
	}

	public void delete(FileExplorerItem f) {
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
	public FileExplorerItem rename(Long id, String name) {
		log.debug("rename started");

		FileExplorerItem f = get(id);
		if (f == null) {
			return null;
		}
		f.setName(name);
		return update(f);
	}

	public FileExplorerItem update(FileExplorerItem f) {
		if (f.getId() == null) {
			f.setInserted(new Date());
			em.persist(f);
		} else {
			f.setUpdated(new Date());
			f = em.merge(f);
		}
		return f;
	}

	private void updateChilds(FileExplorerItem f) {
		for (FileExplorerItem child : getByParent(f.getId())) {
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
	 * @param newParentFileExplorerItemId
	 * @param isOwner
	 */
	public FileExplorerItem move(long id, long parentId, long ownerId, long roomId) {
		log.debug(".move() started");

		FileExplorerItem f = get(id);
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

	public long getSize(List<FileExplorerItem> list) {
		long size = 0;
		for (FileExplorerItem f : list) {
			log.debug("FileExplorerItem fList " + f.getName());
			size += getSize(f);
		}
		return size;
	}
	
	public long getSize(FileExplorerItem f) {
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
				for (FileExplorerItem child : getByParent(f.getId())) {
					size += getSize(child);
				}
			}
		} catch (Exception err) {
			log.error("[getSize] ", err);
		}
		return size;
	}
}
