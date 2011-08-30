package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmeetings.app.data.file.FileProcessor;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FileExplorerUploadHandler extends UploadHandler {
	private static final long serialVersionUID = 2848421357849982426L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			FileExplorerUploadHandler.class,
			ScopeApplicationAdapter.webAppRootKey);

	public FileExplorerItemDaoImpl getFileExplorerItemDao() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (FileExplorerItemDaoImpl) context
						.getBean("fileExplorerItemDao");
			}
		} catch (Exception err) {
			log.error("[getFileExplorerItemDao]", err);
		}
		return null;
	}

	public FileProcessor getFileProcessor() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (FileProcessor) context.getBean("fileProcessor");
			}
		} catch (Exception err) {
			log.error("[getFileProcessor]", err);
		}
		return null;
	}

	@Override
	protected void fileService(HttpServletRequest httpServletRequest,
			String sid, Long userId, Map<String, Object> hs)
			throws ServletException, Exception {

		if (getFileExplorerItemDao() == null || getFileProcessor() == null) {
			return;
		}

		String room_idAsString = httpServletRequest.getParameter("room_id");
		if (room_idAsString == null) {
			throw new ServletException("Missing Room ID");
		}

		Long room_id_to_Store = Long.parseLong(room_idAsString);

		String isOwnerAsString = httpServletRequest.getParameter("isOwner");
		if (isOwnerAsString == null) {
			throw new ServletException("Missing isOwnerAsString");
		}
		boolean isOwner = false;
		if (isOwnerAsString.equals("1")) {
			isOwner = true;
		}

		String parentFolderIdAsString = httpServletRequest
				.getParameter("parentFolderId");
		if (parentFolderIdAsString == null) {
			throw new ServletException("Missing parentFolderId ID");
		}
		Long parentFolderId = Long.parseLong(parentFolderIdAsString);

		String current_dir = getServletContext().getRealPath("/");

		ServletMultipartRequest upload = new ServletMultipartRequest(
				httpServletRequest, 104857600 * 5, // max 500 mb
				"utf-8");
		InputStream is = upload.getFileContents("Filedata");
		String fileSystemName = upload.getBaseFilename("Filedata");
		log.debug("fileSystemName: " + fileSystemName);

		HashMap<String, HashMap<String, Object>> returnError = getFileProcessor()
				.processFile(userId, room_id_to_Store, isOwner, is,
						parentFolderId, fileSystemName, current_dir, hs, 0L, ""); // externalFilesId,
																					// externalType

		HashMap<String, Object> returnAttributes = returnError
				.get("returnAttributes");

		// Flash cannot read the response of an upload
		// httpServletResponse.getWriter().print(returnError);
		hs.put("message", "library");
		hs.put("action", "newFile");
		hs.put("fileExplorerItem",
				getFileExplorerItemDao().getFileExplorerItemsById(
						Long.parseLong(returnAttributes.get(
								"fileExplorerItemId").toString())));
		hs.put("error", returnError);
		hs.put("fileName", returnAttributes.get("completeName"));

	}

}
