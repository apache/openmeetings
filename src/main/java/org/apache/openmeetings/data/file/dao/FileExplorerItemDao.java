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
package org.apache.openmeetings.data.file.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 * 
 */
@Transactional
public class FileExplorerItemDao {

    private static final Logger log = Red5LoggerFactory.getLogger(
            FileExplorerItemDao.class,
            OpenmeetingsVariables.webAppRootKey);
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
            fileItem.setParentFileExplorerItemId(parentFileExplorerItemId);
            fileItem.setOwnerId(ownerId);
            fileItem.setRoom_id(room_id);
            fileItem.setInserted(new Date());
            fileItem.setInsertedBy(insertedBy);
            fileItem.setIsFolder(isFolder);
            fileItem.setIsImage(isImage);
            fileItem.setIsPresentation(isPresentation);
            fileItem.setUpdated(new Date());
            fileItem.setWmlFilePath(wmlFilePath);
            fileItem.setIsStoredWmlFile(isStoredWmlFile);
            fileItem.setIsChart(isChart);
            fileItem.setExternalFileId(externalFileId);
            fileItem.setExternalType(externalType);

			fileItem = em.merge(fileItem);
			Long fileItemId = fileItem.getFileExplorerItemId();

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
			Long fileItemId = fileItem.getFileExplorerItemId();

            return fileItemId;
        } catch (Exception ex2) {
            log.error("[addFileExplorerItem]", ex2);
        }
        return null;
    }

	public List<FileExplorerItem> getFileExplorerItemsByRoomAndOwner(
            Long room_id, Long ownerId) {
        log.debug(".getFileExplorerItemsByRoomAndOwner() started");
        try {
            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.deleted <> :deleted "
                    + "AND c.room_id = :room_id " + "AND c.ownerId = :ownerId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

			TypedQuery<FileExplorerItem> query = em.createQuery(hql, FileExplorerItem.class);
			query.setParameter("deleted", true);
			query.setParameter("room_id",room_id);
			query.setParameter("ownerId",ownerId);
			
			List<FileExplorerItem> fileExplorerList = query.getResultList();

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsByRoomAndOwner]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem[] getFileExplorerItemsByRoom(Long room_id,
            Long parentFileExplorerItemId) {
        log.debug("getFileExplorerItemsByRoom room_id :: "+room_id);
        try {

			String hql = "SELECT c FROM FileExplorerItem c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.room_id = :room_id " +
					"AND c.ownerId IS NULL " +
					"AND c.parentFileExplorerItemId = :parentFileExplorerItemId " +
					"ORDER BY c.isFolder DESC, c.fileName ";
			
			TypedQuery<FileExplorerItem> query = em.createQuery(hql, FileExplorerItem.class);
			query.setParameter("deleted", true);
			query.setParameter("room_id",room_id);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
	        FileExplorerItem[] fileExplorerList = query.getResultList().toArray(new FileExplorerItem[0]);
			
			return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerRootItemsByRoom]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem[] getFileExplorerItemsByOwner(Long ownerId,
            Long parentFileExplorerItemId) {
        log.debug(".getFileExplorerItemsByOwner() started");
        try {

            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.deleted <> :deleted "
                    + "AND c.ownerId = :ownerId "
                    + "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

			TypedQuery<FileExplorerItem> query = em.createQuery(hql, FileExplorerItem.class);
			query.setParameter("deleted", true);
			query.setParameter("ownerId",ownerId);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
            FileExplorerItem[] fileExplorerList = query.getResultList().toArray(new FileExplorerItem[0]);

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerRootItemsByOwner]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem[] getFileExplorerItemsByParent(
            Long parentFileExplorerItemId) {
        log.debug(".getFileExplorerItemsByParent() started");
        try {

            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.deleted <> :deleted "
                    + "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

			TypedQuery<FileExplorerItem> query = em.createQuery(hql, FileExplorerItem.class);
			query.setParameter("deleted", true);
			query.setParameter("parentFileExplorerItemId", parentFileExplorerItemId);
			
            FileExplorerItem[] fileExplorerList = query.getResultList().toArray(new FileExplorerItem[0]);

            return fileExplorerList;
        } catch (Exception ex2) {
            log.error("[getFileExplorerRootItemsByOwner]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem getFileExplorerItemsByHash(String hash) {
        try {

			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getByHash", FileExplorerItem.class);
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
    
    public FileExplorerItem getFileExplorerItemsById(Long fileExplorerItemId) {
        try {

			TypedQuery<FileExplorerItem> query = em.createNamedQuery("getById", FileExplorerItem.class);
			query.setParameter("fileExplorerItemId", fileExplorerItemId);
			
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

            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.externalFileId = :externalFileId " +
            		"AND c.externalType LIKE :externalType";

			TypedQuery<FileExplorerItem> query = em.createQuery(hql, FileExplorerItem.class);
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
     * @param fileExplorerItemId
     */
    public void deleteFileExplorerItem(Long fileExplorerItemId) {
        log.debug(".deleteFileExplorerItem() started");

        try {

            FileExplorerItem fId = this
                    .getFileExplorerItemsById(fileExplorerItemId);

            fId.setDeleted(true);
            fId.setUpdated(new Date());

			if (fId.getFileExplorerItemId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
        } catch (Exception ex2) {
            log.error("[deleteFileExplorerItem]: ", ex2);
        }
    }
    
    public void deleteFileExplorerItemByExternalIdAndType(Long externalFilesid, String externalType) {
        log.debug(".deleteFileExplorerItemByExternalIdAndType() started");

        try {

            FileExplorerItem fId = this
                    .getFileExplorerItemsByExternalIdAndType(externalFilesid, externalType);

            if (fId == null) {
            	throw new Exception("externalFilesid: "+externalFilesid+" and externalType: "+externalType+" Not found");
            }
            
            fId.setDeleted(true);
            fId.setUpdated(new Date());

			if (fId.getFileExplorerItemId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
        } catch (Exception ex2) {
            log.error("[deleteFileExplorerItemByExternalIdAndType]: ", ex2);
        }
    }

    /**
     * @param fileExplorerItemId
     * @param fileName
     */
    public void updateFileOrFolderName(Long fileExplorerItemId, String fileName) {
        log.debug(".updateFileOrFolderName() started");

        try {

            FileExplorerItem fId = this
                    .getFileExplorerItemsById(fileExplorerItemId);

            fId.setFileName(fileName);
            fId.setUpdated(new Date());

			if (fId.getFileExplorerItemId() == 0) {
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

    public void updateFileOrFolder(FileExplorerItem fId) {
        log.debug(".updateFileOrFolder() started");
        try {
            // fId.setUpdated(new Date());

			if (fId.getFileExplorerItemId() == 0) {
				em.persist(fId);
		    } else {
		    	if (!em.contains(fId)) {
		    		em.merge(fId);
			    }
			}
        } catch (Exception ex2) {
            log.error("[updateFileOrFolder]: ", ex2);
        }
    }

    /**
     * @param fileExplorerItemId
     * @param newParentFileExplorerItemId
     * @param isOwner
     */
    public void moveFile(Long fileExplorerItemId,
            Long parentFileExplorerItemId, Long room_id, Boolean isOwner,
            Long ownerId) {
        log.debug(".moveFile() started");
        try {

            FileExplorerItem fId = this
                    .getFileExplorerItemsById(fileExplorerItemId);

            fId.setParentFileExplorerItemId(parentFileExplorerItemId);

            if (parentFileExplorerItemId == 0) {
                if (isOwner) {
                    // move to personal Folder
                    fId.setOwnerId(ownerId);
                } else {
                    // move to public room folder
                    fId.setOwnerId(null);
                    fId.setRoom_id(room_id);
                }
            } else {
                fId.setOwnerId(null);
            }

            fId.setUpdated(new Date());

			if (fId.getFileExplorerItemId() == 0) {
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
