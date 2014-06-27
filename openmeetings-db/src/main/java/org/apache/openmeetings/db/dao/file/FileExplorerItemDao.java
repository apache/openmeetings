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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

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

    public Long add(String fileName, String fileHash,
            Long parentFileExplorerItemId, Long ownerId, Long room_id,
            Long insertedBy, Boolean isFolder, Boolean isImage,
            Boolean isPresentation, String wmlFilePath,
            Boolean isStoredWmlFile, Boolean isChart,
            Long externalFileId, String externalType) {
        log.debug(".add(): adding file " + fileName+ " roomID: "+room_id);
        try {
            FileExplorerItem fileItem = new FileExplorerItem();
            fileItem.setFileName(fileName);
            fileItem.setFileHash(fileHash);
            fileItem.setDeleted(false);
            fileItem.setParentItemId(parentFileExplorerItemId);
            fileItem.setOwnerId(ownerId);
            fileItem.setRoomId(room_id);
            fileItem.setInserted(new Date());
            fileItem.setInsertedBy(insertedBy);
            Type t = null;
            if (isStoredWmlFile) {
            	t = Type.WmlFile;
            }
            if (isChart) {
            	t = Type.PollChart;
            }
            if (isImage) {
            	t = Type.Image;
            }
            if (isPresentation) {
            	t = Type.Presentation;
            }
            if (isFolder) {
            	t = Type.Folder;
            }
            fileItem.setType(t);
            fileItem.setUpdated(new Date());
            fileItem.setWmlFilePath(wmlFilePath);
            fileItem.setExternalFileId(externalFileId);
            fileItem.setExternalType(externalType);

			fileItem = em.merge(fileItem);
			Long fileItemId = fileItem.getId();

            log.debug(".add(): file " + fileName + " added as " + fileItemId);
            return fileItemId;
        } catch (Exception ex2) {
            log.error(".add(): ", ex2);
        }
        return null;
    }
    
    public Long addFileExplorerItem(FileExplorerItem fileItem) {
        try {

			fileItem = em.merge(fileItem);
			Long fileItemId = fileItem.getId();

            return fileItemId;
        } catch (Exception ex2) {
            log.error("[addFileExplorerItem]", ex2);
        }
        return null;
    }

	public List<FileExplorerItem> getFileExplorerItemsByRoomAndOwner(Long room_id, Long ownerId) {
        log.debug(".getFileExplorerItemsByRoomAndOwner() started");
        try {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByRoomAndOwner", FileExplorerItem.class);
			query.setParameter("room_id",room_id);
			query.setParameter("ownerId",ownerId);
			
			List<FileExplorerItem> fileExplorerList = query.getResultList();

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsByRoomAndOwner]: ", ex2);
        }
        return null;
    }

    public List<FileExplorerItem> getByRoom(Long room_id) {
        log.debug("getFileExplorerItemsByRoom room_id :: "+room_id);
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByRoom", FileExplorerItem.class);
		query.setParameter("roomId",room_id);
		
		return query.getResultList();
    }

    public List<FileExplorerItem> getByOwner(Long ownerId) {
        log.debug(".getFileExplorerItemsByOwner() started");
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByOwner", FileExplorerItem.class);
		query.setParameter("ownerId",ownerId);
		
        return query.getResultList();
    }

    public List<FileExplorerItem> getByParent(Long parentId) {
        log.debug(".getFileExplorerItemsByParent() started");
		TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFilesByParent", FileExplorerItem.class);
		query.setParameter("parentItemId", parentId);
		
        return query.getResultList();
    }

    public FileExplorerItem getFileExplorerItemsByHash(String hash) {
        try {

			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileByHash", FileExplorerItem.class);
			query.setParameter("fileHash", hash);
			
			FileExplorerItem fileExplorerList = null;
			try {
				fileExplorerList = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsById]: ", ex2);
        }
        return null;
    }
    
    public FileExplorerItem get(Long fileId) {
        try {

			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileById", FileExplorerItem.class);
			query.setParameter("id", fileId);
			
			FileExplorerItem fileExplorerList = null;
			try {
				fileExplorerList = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsById]: ", ex2);
        }
        return null;
    }
    
    public FileExplorerItem getFileExplorerItemsByExternalIdAndType(Long externalFileId, String externalType) {
        log.debug(".getFileExplorerItemsByExternalIdAndType() started");

        try {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getFileExternal", FileExplorerItem.class);
			query.setParameter("externalFileId", externalFileId);
			query.setParameter("externalType", externalType);
			
			FileExplorerItem fileExplorerList = null;
			try {
				fileExplorerList = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsByExternalIdAndType]: ", ex2);
        }
        return null;
    }

    public List<FileExplorerItem> getFileExplorerItems() {
        log.debug(".getFileExplorerItems() started");

        try {
			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getAllFiles", FileExplorerItem.class);

            List<FileExplorerItem> fileExplorerList = query.getResultList();

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItems]: ", ex2);
        }
        return null;
    }    

    /**
     * @param fileId
     */
    public void delete(Long fileId) {
        log.debug(".delete() started");
        delete(get(fileId));
    }
    
    public void delete(FileExplorerItem f) {
        f.setDeleted(true);
        f.setUpdated(new Date());

        update(f);
    }
    
    public void deleteFileExplorerItemByExternalIdAndType(Long externalFilesid, String externalType) {
        log.debug(".deleteFileExplorerItemByExternalIdAndType() started");

        try {

            FileExplorerItem fId = getFileExplorerItemsByExternalIdAndType(externalFilesid, externalType);

            if (fId == null) {
            	throw new Exception("externalFilesid: "+externalFilesid+" and externalType: "+externalType+" Not found");
            }
            
            fId.setDeleted(true);
            fId.setUpdated(new Date());

            update(fId);
        } catch (Exception ex2) {
            log.error("[deleteFileExplorerItemByExternalIdAndType]: ", ex2);
        }
    }

    /**
     * @param fileId
     * @param fileName
     */
    public void updateFileOrFolderName(Long fileId, String fileName) {
        log.debug(".updateFileOrFolderName() started");

        try {

            FileExplorerItem fId = get(fileId);

            fId.setFileName(fileName);
            fId.setUpdated(new Date());

            update(fId);
        } catch (Exception ex2) {
            log.error("[updateFileOrFolderName]: ", ex2);
        }
    }

    public FileExplorerItem update(FileExplorerItem f) {
        // fId.setUpdated(new Date());

		if (f.getId() == null) {
			em.persist(f);
	    } else {
	    	if (!em.contains(f)) {
	    		f = em.merge(f);
		    }
		}
		return f;
    }

    /**
     * @param fileId
     * @param newParentFileExplorerItemId
     * @param isOwner
     */
    public void moveFile(Long fileId, Long parentId, Long room_id, Boolean isOwner, Long ownerId) {
        log.debug(".moveFile() started");
        try {

            FileExplorerItem fId = get(fileId);

            fId.setParentItemId(parentId);

            if (parentId == 0) {
                if (isOwner) {
                    // move to personal Folder
                    fId.setOwnerId(ownerId);
                } else {
                    // move to public room folder
                    fId.setOwnerId(null);
                    fId.setRoomId(room_id);
                }
            } else {
                fId.setOwnerId(null);
            }

            fId.setUpdated(new Date());

			if (fId.getId() == null) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
        } catch (Exception ex2) {
            log.error("[updateFileOrFolderName]: ", ex2);
        }
    }

}
