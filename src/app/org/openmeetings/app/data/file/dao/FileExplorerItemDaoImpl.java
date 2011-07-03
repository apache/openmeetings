package org.openmeetings.app.data.file.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 * 
 */
public class FileExplorerItemDaoImpl {

    private static final Logger log = Red5LoggerFactory.getLogger(
            FileExplorerItemDaoImpl.class,
            ScopeApplicationAdapter.webAppRootKey);

    private static FileExplorerItemDaoImpl instance;

    private FileExplorerItemDaoImpl() {
    }

    public static synchronized FileExplorerItemDaoImpl getInstance() {
        if (instance == null) {
            instance = new FileExplorerItemDaoImpl();
        }
        return instance;
    }

    public Long add(String fileName, String fileHash,
            Long parentFileExplorerItemId, Long ownerId, Long room_id,
            Long insertedBy, Boolean isFolder, Boolean isImage,
            Boolean isPresentation, String wmlFilePath,
            Boolean isStoredWmlFile, Boolean isChart,
            Long externalFileId, String externalType) {
        log.debug(".add(): adding file " + fileName);
        try {
            FileExplorerItem fileItem = new FileExplorerItem();
            fileItem.setFileName(fileName);
            fileItem.setFileHash(fileHash);
            fileItem.setDeleted("false");
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

            Object idf = HibernateUtil.createSession();

            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();

            Long fileItemId = (Long) session.save(fileItem);

            tx.commit();
            HibernateUtil.closeSession(idf);

            log.debug(".add(): file " + fileName + " added as " + fileItemId);
            return fileItemId;
        } catch (HibernateException ex) {
            log.error(".add(): ", ex);
        } catch (Exception ex2) {
            log.error(".add(): ", ex2);
        }
        return null;
    }
    
    public Long addFileExplorerItem(FileExplorerItem fileItem) {
        try {

            Object idf = HibernateUtil.createSession();

            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();

            Long fileItemId = (Long) session.save(fileItem);

            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileItemId;
        } catch (HibernateException ex) {
            log.error("[addFileExplorerItem]", ex);
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
                    + "WHERE c.deleted != :deleted "
                    + "AND c.room_id = :room_id " + "AND c.ownerId = :ownerId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setString("deleted", "true");
            query.setLong("room_id", room_id);
            query.setLong("ownerId", ownerId);

            List<FileExplorerItem> fileExplorerList = query.list();
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerItemsByRoomAndOwner]: ", ex);
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsByRoomAndOwner]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem[] getFileExplorerItemsByRoom(Long room_id,
            Long parentFileExplorerItemId) {
        log.debug(".getFileExplorerItemsByRoom() started");
        try {

            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.deleted != :deleted "
                    + "AND c.room_id = :room_id "
                    + "AND c.ownerId IS NULL "
                    + "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setString("deleted", "true");
            query.setLong("room_id", room_id);
            query.setLong("parentFileExplorerItemId", parentFileExplorerItemId);

            FileExplorerItem[] fileExplorerList = (FileExplorerItem[]) query.list().toArray(new FileExplorerItem[0]);
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerRootItemsByRoom]: ", ex);
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
                    + "WHERE c.deleted != :deleted "
                    + "AND c.ownerId = :ownerId "
                    + "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setString("deleted", "true");
            query.setLong("ownerId", ownerId);
            query.setLong("parentFileExplorerItemId", parentFileExplorerItemId);

            FileExplorerItem[] fileExplorerList = (FileExplorerItem[]) query.list().toArray(new FileExplorerItem[0]);
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerRootItemsByOwner]: ", ex);
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
                    + "WHERE c.deleted != :deleted "
                    + "AND c.parentFileExplorerItemId = :parentFileExplorerItemId "
                    + "ORDER BY c.isFolder DESC, c.fileName ";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setString("deleted", "true");
            query.setLong("parentFileExplorerItemId", parentFileExplorerItemId);

            FileExplorerItem[] fileExplorerList = (FileExplorerItem[]) query.list().toArray(new FileExplorerItem[0]);
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerRootItemsByOwner]: ", ex);
        } catch (Exception ex2) {
            log.error("[getFileExplorerRootItemsByOwner]: ", ex2);
        }
        return null;
    }

    public FileExplorerItem getFileExplorerItemsById(Long fileExplorerItemId) {
        log.debug(".getFileExplorerItemsById() started");

        try {

            String hql = "SELECT c FROM FileExplorerItem c "
                    + "WHERE c.fileExplorerItemId = :fileExplorerItemId";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setLong("fileExplorerItemId", fileExplorerItemId);

            FileExplorerItem fileExplorerList = (FileExplorerItem) query
                    .uniqueResult();
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerItemsById]: ", ex);
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

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setLong("externalFileId", externalFileId);
            query.setString("externalType", externalType);

            FileExplorerItem fileExplorerList = (FileExplorerItem) query
                    .uniqueResult();
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerItemsByExternalIdAndType]: ", ex);
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsByExternalIdAndType]: ", ex2);
        }
        return null;
    }

    public List<FileExplorerItem> getFileExplorerItems() {
        log.debug(".getFileExplorerItemsById() started");

        try {

            String hql = "SELECT c FROM FileExplorerItem c ";

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);

            List<FileExplorerItem> fileExplorerList = query.list();
            
            tx.commit();
            HibernateUtil.closeSession(idf);

            return fileExplorerList;
        } catch (HibernateException ex) {
            log.error("[getFileExplorerItemsById]: ", ex);
        } catch (Exception ex2) {
            log.error("[getFileExplorerItemsById]: ", ex2);
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

            fId.setDeleted("true");
            fId.setUpdated(new Date());

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            session.update(fId);
            session.flush();
            tx.commit();
            HibernateUtil.closeSession(idf);

        } catch (HibernateException ex) {
            log.error("[deleteFileExplorerItem]: ", ex);
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
            
            fId.setDeleted("true");
            fId.setUpdated(new Date());

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            session.update(fId);
            session.flush();
            tx.commit();
            HibernateUtil.closeSession(idf);

        } catch (HibernateException ex) {
            log.error("[deleteFileExplorerItemByExternalIdAndType]: ", ex);
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

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            session.update(fId);
            session.flush();
            tx.commit();
            HibernateUtil.closeSession(idf);

        } catch (HibernateException ex) {
            log.error("[updateFileOrFolderName]: ", ex);
        } catch (Exception ex2) {
            log.error("[updateFileOrFolderName]: ", ex2);
        }
    }

    public void updateFileOrFolder(FileExplorerItem fId) {
        log.debug(".updateFileOrFolder() started");
        try {
            // fId.setUpdated(new Date());

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            session.update(fId);
            session.flush();
            tx.commit();
            HibernateUtil.closeSession(idf);

        } catch (HibernateException ex) {
            log.error("[updateFileOrFolder]: ", ex);
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

            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            session.update(fId);
            session.flush();
            tx.commit();
            HibernateUtil.closeSession(idf);

        } catch (HibernateException ex) {
            log.error("[updateFileOrFolderName]: ", ex);
        } catch (Exception ex2) {
            log.error("[updateFileOrFolderName]: ", ex2);
        }
    }

}
