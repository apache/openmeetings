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

import static org.apache.openmeetings.webservice.Constants.TNS;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.file.FileExplorerObject;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.FileExplorerObjectWrapper;
import org.apache.openmeetings.webservice.schema.FileItemDTOListWrapper;
import org.apache.openmeetings.webservice.schema.FileItemDTOWrapper;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 *
 * Contains methods to import and upload files into the Files section of the
 * conference room and the personal drive of any USER
 *
 * @author sebawagner
 *
 */
@Service("fileWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.FileWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "FileService")
@Path("/file")
public class FileWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(FileWebService.class);

	@Inject
	private FileProcessor fileProcessor;

	/**
	 * deletes files or folders based on it id
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param id
	 *            the id of the file or folder
	 * @return {@link ServiceResult} with result type
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{id}")
	@Operation(
			description = "deletes files or folders based on it id",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the id of the file or folder") @PathParam("id") @WebParam(name="id") Long id
			) throws ServiceException
	{
		FileItem f = fileDao.get(id);
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
				fileDao.delete(f);
				return new ServiceResult("Deleted", Type.SUCCESS);
			});
	}

	/**
	 *
	 * deletes a file by its external Id and type
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param externalType
	 *            the externalType
	 * @param externalId
	 *            the id of the file or folder
	 * @return {@link ServiceResult} with result type
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{externaltype}/{externalid}")
	@Operation(
			description = "deletes a file by its external Id and type",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult deleteExternal(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the externalType") @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @Parameter(required = true, description = "the id of the file or folder") @WebParam(name="externalid") @PathParam("externalid") String externalId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			FileItem f = fileDao.get(externalId, externalType);
			fileDao.delete(f);
			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}

	/**
	 * to add a folder to the private drive, set parentId = 0 and isOwner to 1/true and
	 * externalUserId/externalUserType to a valid USER
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param file
	 *            the The file attributes to be added
	 * @param stream
	 *            the The file to be added
	 * @return - Object created
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/")
	@Operation(
			description = "to add a folder to the private drive, set parentId = 0 and isOwner to 1/true and\n"
					+ " externalUserId/externalUserType to a valid USER",
			responses = {
					@ApiResponse(responseCode = "200", description = "Object created",
						content = @Content(schema = @Schema(implementation = FileItemDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public FileItemDTO add(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the The file attributes to be added") @Multipart(value = "file", type = MediaType.APPLICATION_JSON) @WebParam(name="file") FileItemDTO file
			, @Parameter(required = true, description = "the The file to be added") @Multipart(value = "stream", type = MediaType.APPLICATION_OCTET_STREAM, required = false) @WebParam(name="stream") InputStream stream
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			FileItem f = file == null ? null : file.get();
			if (f == null || f.getId() != null) {
				throw new InternalServiceException("Bad id");
			}
			f.setInsertedBy(sd.getUserId());
			if (stream != null) {
				try {
					ProcessResultList result = fileProcessor.processFile(f, stream, Optional.empty());
					if (result.hasError()) {
						throw new ServiceException(result.getLogMessage());
					}
				} catch (Exception e) {
					throw new InternalServiceException(e.getMessage());
				}
			} else {
				f = fileDao.update(f);
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
	 * @return - the list of files for given external type
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}")
	@Operation(
			description = "Get all files by external type",
			responses = {
					@ApiResponse(responseCode = "200", description = "the list of files for given external type",
						content = @Content(schema = @Schema(implementation = FileItemDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<FileItemDTO> getAllExternal(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "External type for file listing") @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			) throws ServiceException
	{
		log.debug("getAllExternal::externalType {}", externalType);
		return performCall(sid, User.Right.SOAP, sd -> FileItemDTO.list(fileDao.getExternal(externalType)));
	}

	/**
	 * Get a File Explorer Object by a given ROOM
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as logged in
	 * @param roomId
	 *            ROOM Id
	 * @return - File Explorer Object by a given ROOM
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/room/{id}")
	@Operation(
			description = "Get a File Explorer Object by a given ROOM",
			responses = {
					@ApiResponse(responseCode = "200", description = "File Explorer Object by a given ROOM",
							content = @Content(schema = @Schema(implementation = FileExplorerObjectWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public FileExplorerObject getRoom(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "ROOM Id") @WebParam(name="id") @PathParam("id") long roomId
			) throws ServiceException
	{
		log.debug("getRoom::roomId {}", roomId);
		return performCall(sid, User.Right.SOAP, sd -> {
			FileExplorerObject fileExplorerObject = new FileExplorerObject();

			// Home File List
			List<FileItem> fList = fileDao.getByOwner(sd.getUserId());
			fileExplorerObject.setUser(fList, fileDao.getSize(fList));

			// Public File List
			List<FileItem> rList = fileDao.getByRoom(roomId);
			fileExplorerObject.setRoom(rList, fileDao.getSize(rList));

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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/room/{id}/{parent}")
	@Operation(
			description = "Get list of FileItemDTO by parent",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of file explorer items",
							content = @Content(schema = @Schema(implementation = FileItemDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<FileItemDTO> getRoomByParent(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the parent folder id") @WebParam(name="id") @PathParam("id") long roomId
			, @Parameter(required = true, description = "the room id") @WebParam(name="parent") @PathParam("parent") long parentId
			) throws ServiceException
	{
		log.debug("getRoomByParent {}", parentId);
		return performCall(sid, User.Right.ROOM, sd -> {
			List<FileItem> list;
			if (parentId < 0) {
				if (parentId == -1) {
					list = fileDao.getByOwner(sd.getUserId());
				} else {
					list = fileDao.getByRoom(roomId);
				}
			} else {
				list = fileDao.getByParent(parentId);
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/rename/{id}/{name}")
	@Operation(
			description = "update a file or folder name",
			responses = {
					@ApiResponse(responseCode = "200", description = "resulting file object",
							content = @Content(schema = @Schema(implementation = FileItemDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public FileItemDTO rename(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "file or folder id") @WebParam(name="id") @PathParam("id") long id
			, @Parameter(required = true, description = "new file or folder name") @WebParam(name="name") @PathParam("name") String name
			) throws ServiceException
	{
		log.debug("rename {}", id);
		return performCall(sid, User.Right.SOAP, sd -> {
			FileItem f = fileDao.rename(id, name);
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/move/{roomid}/{id}/{parentid}")
	@Operation(
			description = "move a file or folder",
			responses = {
					@ApiResponse(responseCode = "200", description = "resulting file object",
							content = @Content(schema = @Schema(implementation = FileItemDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public FileItemDTO move(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "current file or folder id to be moved") @WebParam(name="id") @PathParam("id") long id
			, @Parameter(required = true, description = "room this file need to be moved") @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @Parameter(required = true, description = "new parent folder id") @WebParam(name="parentid") @PathParam("parentid") long parentId
			) throws ServiceException
	{
		log.debug("move {}", id);
		return performCall(sid, User.Right.SOAP, sd -> {
			FileItem f = fileDao.move(id, parentId, sd.getUserId(), roomId);
			return f == null ? null : new FileItemDTO(f);
		});
	}
}
