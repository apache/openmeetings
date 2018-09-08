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
package org.apache.openmeetings.webservice;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.file.FileExplorerObject;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * Contains methods to import and upload files into the Files section of the
 * conference room and the personal drive of any user
 *
 * @author sebawagner
 *
 */
@Service("fileWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.FileWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/file")
public class FileWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(FileWebService.class, getWebAppRootKey());

	/**
	 * deletes files or folders based on it id
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param id
	 *            the id of the file or folder
	 * @return {@link ServiceResult} with result type
	 */
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) {
		FileItemDao dao = getFileDao();
		FileItem f = dao.get(id);
		return performCall(sid, sd -> {
				Long userId = sd.getUserId();
				Set<Right> rights = getRights(userId);
				return AuthLevelUtil.hasWebServiceLevel(rights)
					|| (AuthLevelUtil.hasUserLevel(rights) && userId.equals(f.getOwnerId()));
			}
			, sd -> {
				if (f == null) {
					return new ServiceResult("Bad id", Type.ERROR);
				}
				dao.delete(f);
				return new ServiceResult("Deleted", Type.SUCCESS);
			});
	}

	/**
	 *
	 * deletes a file by its external Id and type
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param externalId
	 *            the od of the file or folder
	 * @param externalType
	 *            the externalType
	 * @return {@link ServiceResult} with result type
	 */
	@DELETE
	@Path("/{externaltype}/{externalid}")
	public ServiceResult deleteExternal(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @WebParam(name="externalid") @PathParam("externalid") String externalId
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			FileItemDao dao = getFileDao();
			FileItem f = dao.get(externalId, externalType);
			dao.delete(f);
			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}

	/**
	 * to add a folder to the private drive, set parentId = 0 and isOwner to 1/true and
	 * externalUserId/externalUserType to a valid user
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param file
	 *            the The file to be added
	 * @param stream
	 *            the The file to be added
	 * @return - Object created
	 */
	@WebMethod
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/")
	public FileItemDTO add(@WebParam(name="sid") @QueryParam("sid") String sid
			, @Multipart(value = "file", type = MediaType.APPLICATION_JSON) @WebParam(name="file") FileItemDTO file
			, @Multipart(value = "stream", type = MediaType.APPLICATION_OCTET_STREAM, required = false) @WebParam(name="stream") InputStream stream
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			FileItem f = file == null ? null : file.get();
			if (f == null || f.getId() != null) {
				throw new ServiceException("Bad id");
			}
			f.setInsertedBy(sd.getUserId());
			if (stream != null) {
				try {
					ProcessResultList result = getBean(FileProcessor.class).processFile(f, stream);
					if (result.hasError()) {
						throw new ServiceException(result.getLogMessage());
					}
				} catch (Exception e) {
					throw new ServiceException(e.getMessage());
				}
			} else {
				f = getFileDao().update(f);
			}
			return new FileItemDTO(f);
		});
	}

	/**
	 * Get all files by external type
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param externalType
	 *            External type for file listing
	 * @return - the list of file for given external type
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}")
	public List<FileItemDTO> getAllExternal(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			)
	{
		log.debug("getAllExternal::externalType {}", externalType);
		return performCall(sid, User.Right.Soap, sd -> {
			FileItemDao dao = getFileDao();
			return FileItemDTO.list(dao.getExternal(externalType));
		});
	}

	/**
	 * Get a File Explorer Object by a given Room
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param roomId
	 *            Room Id
	 * @return - File Explorer Object by a given Room
	 */
	@WebMethod
	@GET
	@Path("/room/{id}")
	public FileExplorerObject getRoom(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long roomId
			)
	{
		log.debug("getRoom::roomId {}", roomId);
		return performCall(sid, User.Right.Soap, sd -> {
			FileItemDao dao = getFileDao();
			FileExplorerObject fileExplorerObject = new FileExplorerObject();

			// Home File List
			List<FileItem> fList = dao.getByOwner(sd.getUserId());
			fileExplorerObject.setUser(fList, dao.getSize(fList));

			// Public File List
			List<FileItem> rList = dao.getByRoom(roomId);
			fileExplorerObject.setRoom(rList, dao.getSize(rList));

			return fileExplorerObject;
		});
	}

	/**
	 *
	 * Get list of {@link FileItemDTO} by parent
	 *
	 * @param sid
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param parentId
	 *            the parent folder id
	 * @param roomId
	 *            the room id
	 * @return - list of file explorer items
	 */
	@WebMethod
	@GET
	@Path("/room/{id}/{parent}")
	public List<FileItemDTO> getRoomByParent(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long roomId
			, @WebParam(name="parent") @PathParam("parent") long parentId
			)
	{
		log.debug("getRoomByParent {}", parentId);
		return performCall(sid, User.Right.Room, sd -> {
			FileItemDao dao = getFileDao();
			List<FileItem> list;
			if (parentId < 0) {
				if (parentId == -1) {
					list = dao.getByOwner(sd.getUserId());
				} else {
					list = dao.getByRoom(roomId);
				}
			} else {
				list = dao.getByParent(parentId);
			}
			return FileItemDTO.list(list);
		});
	}

	/**
	 *
	 * update a file or folder name
	 *
	 * @param sid
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param id
	 *            file or folder id
	 * @param name
	 *            new file or folder name
	 * @return - resulting file object
	 */
	@WebMethod
	@POST
	@Path("/rename/{id}/{name}")
	public FileItemDTO rename(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			, @WebParam(name="name") @PathParam("name") String name)
	{
		log.debug("rename {}", id);
		return performCall(sid, User.Right.Soap, sd -> {
			FileItem f = getFileDao().rename(id, name);
			return f == null ? null : new FileItemDTO(f);
		});
	}

	/**
	 * move a file or folder
	 *
	 * @param sid
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param id
	 *            current file or folder id to be moved
	 * @param roomId
	 *            room this file need to be moved
	 * @param parentId
	 *            new parent folder id
	 * @return - resulting file object
	 */
	@WebMethod
	@POST
	@Path("/move/{roomid}/{id}/{parentid}")
	public FileItemDTO move(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			, @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @WebParam(name="parentid") @PathParam("parentid") long parentId)
	{
		log.debug("move {}", id);
		return performCall(sid, User.Right.Soap, sd -> {
			FileItem f = getFileDao().move(id, parentId, sd.getUserId(), roomId);
			return f == null ? null : new FileItemDTO(f);
		});
	}
}
