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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.manager.IWhiteboardManager;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.github.openjson.JSONArray;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * WbService contains methods to manipulate whiteboard contents
 *
 */
@Service("wbWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.WbWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "WbService")
@Path("/wb")
public class WbWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(WbWebService.class);

	@Inject
	private IWhiteboardManager wbManager;
	@Inject
	private IClientManager cm;

	/**
	 * This method will remove all whiteboards from given room
	 * and create empty one(s) for room files specified
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - id of the room to clean
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/resetwb/{id}")
	@Operation(
			description = "This method will remove all whiteboards from given room\n"
					+ " and create empty one(s) for room files specified",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult resetWb(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "id of the room to clean") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		log.debug("[resetWb] room id {}", id);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.reset(id, sd.getUserId());
			return new ServiceResult("", Type.SUCCESS);
		});
	}

	/**
	 * This method will do the same as clean WB in the room (except for there will be no UNDO)
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - id of the room to clean
	 * @param wbId - id of the white board to clean
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/cleanwb/{roomid}/{wbid}")
	@Operation(
			description = "This method will do the same as clean WB in the room (except for there will be no UNDO)",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult cleanWb(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "id of the room to clean") @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @Parameter(required = true, description = "id of the white board to clean") @WebParam(name="wbid") @PathParam("wbid") long wbId
			) throws ServiceException
	{
		log.debug("[cleanWb] room id {}, wb id {}", roomId, wbId);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.clearAll(roomId, wbId, false, null);
			return new ServiceResult("", Type.SUCCESS);
		});
	}

	/**
	 * This method will do the same as clean slide in the room (except for there will be no UNDO)
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - id of the room to clean
	 * @param wbId - id of the white board to clean
	 * @param slide - slide number (zero based)
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/cleanslide/{roomid}/{wbid}/{slide}")
	@Operation(
			description = "This method will do the same as clean slide in the room (except for there will be no UNDO)",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult cleanSlide(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "id of the room to clean") @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @Parameter(required = true, description = "id of the white board to clean") @WebParam(name="wbid") @PathParam("wbid") long wbId
			, @Parameter(required = true, description = "slide number (zero based)") @WebParam(name="slide") @PathParam("slide") int slide
			) throws ServiceException
	{
		log.debug("[cleanSlide] room id {}, wb id {}, slide {}", roomId, wbId, slide);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.cleanSlide(roomId, wbId, slide, null);
			return new ServiceResult("", Type.SUCCESS);
		});
	}

	/**
	 * This method will receive WB as binary data (png) and store it to temporary PDF/PNG file
	 *
	 * unlike other web service methods this one uses internal client sid
	 * NOT web service sid
	 *
	 * @param sid - internal client sid
	 * @param type - the type of document being saved PNG/PDF
	 * @param data - binary data
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/uploadwb/{type}")
	@Operation(
			description = "This method will receive WB as binary data (png) and store it to temporary PDF/PNG file",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult uploadWb(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the type of document being saved PNG/PDF") @WebParam(name="type") @PathParam("type") String type
			, @Parameter(required = true, description = "binary data") @WebParam(name="data") @FormParam("data") String data
			) throws ServiceException
	{
		log.debug("[uploadwb] type {}", type);
		Client c = cm.getBySid(sid);
		final boolean allowed = c != null
				&& c.getRoom() != null
				&& c.hasRight(Room.Right.MODERATOR)
				&& !c.getRoom().isHidden(RoomElement.ACTION_MENU);
		return performCall(null, sd -> allowed, sd -> {
			try {
				String tDir = System.getProperty("java.io.tmpdir");
				String fuid = randomUUID().toString();
				if (EXTENSION_PDF.equals(type)) {
					try (PDDocument doc = new PDDocument(); OutputStream os = new FileOutputStream(Paths.get(tDir, fuid).toFile())) {
						JSONArray arr = new JSONArray(data);
						for (int i = 0; i < arr.length(); ++i) {
							String base64Image = arr.getString(i).split(",")[1];
							byte[] bb = Base64.decodeBase64(base64Image);
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(bb));
							float width = img.getWidth();
							float height = img.getHeight();
							PDPage page = new PDPage(new PDRectangle(width, height));
							PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, img);
							try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, false)) {
								contentStream.drawImage(pdImageXObject, 0, 0, width, height);
							}
							doc.addPage(page);
						}
						doc.save(os);
					}
				} else {
					JSONArray arr = new JSONArray(data);
					String base64Image = arr.getString(0).split(",")[1];
					byte[] bb = Base64.decodeBase64(base64Image);
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bb), Paths.get(tDir, fuid).toFile());
				}
				return new ServiceResult(fuid, Type.SUCCESS);
			} catch (Exception e) {
				return new ServiceResult(e.getMessage(), Type.ERROR);
			}
		});
	}
}
