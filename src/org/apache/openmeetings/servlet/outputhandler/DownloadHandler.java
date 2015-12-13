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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.servlet.BaseHttpServlet;
import org.apache.openmeetings.servlet.ServerNotInitializedException;
import org.apache.openmeetings.utils.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class DownloadHandler extends BaseHttpServlet {
	
	private static final long serialVersionUID = 7243653203578587544L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			DownloadHandler.class, OpenmeetingsVariables.webAppRootKey);

	private static final String defaultImageName = "deleted.jpg";
	private static final String defaultProfileImageName = "profile_pic.jpg";
	private static final String defaultProfileImageNameBig = "_big_profile_pic.jpg";
	private static final String defaultChatImageName = "_chat_profile_pic.jpg";
	private static final String defaultSWFName = "deleted.swf";

	private void logNonExistentFolder(File f) {
		if (!f.exists()) {
			boolean c = f.mkdir();
			if (!c) {
				log.error("cannot write to directory");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {
			httpServletRequest.setCharacterEncoding("UTF-8");

			log.debug("\nquery = " + httpServletRequest.getQueryString());
			log.debug("\n\nfileName = "
					+ httpServletRequest.getParameter("fileName"));
			log.debug("\n\nparentPath = "
					+ httpServletRequest.getParameter("parentPath"));

			String queryString = httpServletRequest.getQueryString();
			if (queryString == null) {
				queryString = "";
			}

			String sid = httpServletRequest.getParameter("sid");

			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			Long users_id = getBean(SessiondataDao.class).checkSession(sid);
			Long user_level = getBean(UserManager.class).getUserLevelByID(users_id);

			if (user_level != null && user_level > 0) {
				String room_id = httpServletRequest.getParameter("room_id");
				if (room_id == null) {
					room_id = "default";
				}

				String moduleName = httpServletRequest
						.getParameter("moduleName");
				if (moduleName == null) {
					moduleName = "nomodule";
				}

				String parentPath = httpServletRequest
						.getParameter("parentPath");
				if (parentPath == null) {
					parentPath = "nomodule";
				}

				String requestedFile = httpServletRequest
						.getParameter("fileName");
				if (requestedFile == null) {
					requestedFile = "";
				}
				
				String fileExplorerItemIdParam = httpServletRequest
						.getParameter("fileExplorerItemId");
				Long fileExplorerItemId = null;
				if (fileExplorerItemIdParam != null) {
					fileExplorerItemId = Long.parseLong(fileExplorerItemIdParam);
				}
				
				

				// make a complete name out of domain(organisation) + roomname
				String roomName = room_id;
				// trim whitespaces cause it is a directory name
				roomName = StringUtils.deleteWhitespace(roomName);

				// Get the current User-Directory

				File working_dir;

				// Add the Folder for the Room
				if (moduleName.equals("lzRecorderApp")) {
					working_dir = OmFileHelper.getStreamsHibernateDir();
				} else if (moduleName.equals("videoconf1")) {
					working_dir = OmFileHelper.getUploadRoomDir(roomName);
					if (parentPath.length() != 0 && !parentPath.equals("/")) {
						working_dir = new File(working_dir, parentPath);
					}
				} else if (moduleName.equals("userprofile")) {
					working_dir = OmFileHelper.getUploadProfilesUserDir(users_id);
					logNonExistentFolder(working_dir);
				} else if (moduleName.equals("remoteuserprofile")) {
					String remoteUser_id = httpServletRequest.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);
				} else if (moduleName.equals("remoteuserprofilebig")) {
					String remoteUser_id = httpServletRequest.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);
					
					requestedFile = getBigProfileUserName(working_dir);
				} else if (moduleName.equals("chat")) {
					String remoteUser_id = httpServletRequest.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);

					requestedFile = getChatUserName(working_dir);
				} else {
					working_dir = OmFileHelper.getUploadRoomDir(roomName);
				}

				if (!moduleName.equals("nomodule")) {

					log.debug("requestedFile: " + requestedFile
							+ " current_dir: " + working_dir);

					File full_path = new File(working_dir, requestedFile);

					// If the File does not exist or is not readable show/load a
					// place-holder picture

					if (!full_path.exists() || !full_path.canRead()) {
						if (!full_path.canRead()) {
							log.debug("LOG DownloadHandler: The request file is not readable ");
						} else {
							log.debug("LOG DownloadHandler: The request file does not exist / has already been deleted");
						}
						log.debug("LOG ERROR requestedFile: " + requestedFile);
						// replace the path with the default picture/document

						if (requestedFile.endsWith(".jpg")) {
							log.debug("LOG endsWith d.jpg");

							log.debug("LOG moduleName: " + moduleName);

							requestedFile = DownloadHandler.defaultImageName;
							if (moduleName.equals("remoteuserprofile")) {
								requestedFile = DownloadHandler.defaultProfileImageName;
							} else if (moduleName.equals("remoteuserprofilebig")) {
								requestedFile = DownloadHandler.defaultProfileImageNameBig;
							} else if (moduleName.equals("userprofile")) {
								requestedFile = DownloadHandler.defaultProfileImageName;
							} else if (moduleName.equals("chat")) {
								requestedFile = DownloadHandler.defaultChatImageName;
							}
						} else if (requestedFile.endsWith(".swf")) {
							requestedFile = DownloadHandler.defaultSWFName;
						} else {
							requestedFile = DownloadHandler.defaultImageName;
						}
						full_path = new File(OmFileHelper.getDefaultDir(), requestedFile);
					}

					log.debug("full_path: " + full_path);

					if (!full_path.exists() || !full_path.canRead()) {
						if (!full_path.canRead()) {
							log.debug("DownloadHandler: The request DEFAULT-file does not exist / has already been deleted");
						} else {
							log.debug("DownloadHandler: The request DEFAULT-file does not exist / has already been deleted");
						}
						// no file to handle abort processing
						return;
					}
					// Requested file is outside OM webapp folder
					File curDirFile = OmFileHelper.getOmHome();
					if (!full_path.getCanonicalPath()
							.startsWith(curDirFile.getCanonicalPath())) {
						throw new Exception("Invalid file requested: f2.cp == "
								+ full_path.getCanonicalPath() + "; curDir.cp == "
								+ curDirFile.getCanonicalPath());
					}

					// Default type - Explorer, Chrome and others
					int browserType = 0;

					// Firefox and Opera browsers
					if (httpServletRequest.getHeader("User-Agent") != null) {
						if ((httpServletRequest.getHeader("User-Agent")
								.contains("Firefox"))
								|| (httpServletRequest.getHeader("User-Agent")
										.contains("Opera"))) {
							browserType = 1;
						}
					}

					log.debug("Detected browser type:" + browserType);

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					OutputStream out = httpServletResponse.getOutputStream();

					if (requestedFile.endsWith(".swf")) {
						// trigger download to SWF => THIS is a workaround for
						// Flash Player 10, FP 10 does not seem
						// to accept SWF-Downloads with the Content-Disposition
						// in the Header
						httpServletResponse
								.setContentType("application/x-shockwave-flash");
						httpServletResponse.setHeader("Content-Length",
								"" + full_path.length());
					} else {
						httpServletResponse
								.setContentType("APPLICATION/OCTET-STREAM");
						
						String fileNameResult = requestedFile;
						if (fileExplorerItemId != null && fileExplorerItemId > 0) {
							FileExplorerItem fileExplorerItem = getBean(FileExplorerItemDao.class).getFileExplorerItemsById(fileExplorerItemId);
							if (fileExplorerItem != null) {
								
								fileNameResult = fileExplorerItem.getFileName().substring(0, fileExplorerItem.getFileName().length()-4)
													+ fileNameResult.substring(fileNameResult.length()-4, fileNameResult.length());
								
							}
						}
						
						if (browserType == 0) {
							httpServletResponse.setHeader(
									"Content-Disposition",
									"attachment; filename="
											+ java.net.URLEncoder.encode(
													fileNameResult, "UTF-8"));
						} else {
							httpServletResponse.setHeader(
									"Content-Disposition",
									"attachment; filename*=UTF-8'en'"
											+ java.net.URLEncoder.encode(
													fileNameResult, "UTF-8"));
						}

						httpServletResponse.setHeader("Content-Length",
								"" + full_path.length());
					}

					OmFileHelper.copyFile(full_path, out);
					out.flush();
					out.close();
				}
			} else {
				log.error("ERROR DownloadHandler: not authorized FileDownload ");
			}

		} catch (ServerNotInitializedException e) {
			return;
		} catch (Exception er) {
			log.error("Error downloading: ", er);
		}
	}

	private String getChatUserName(File f) throws Exception {
		if (f.exists() && f.isDirectory()) {
			String filesString[] = f.list();
			for (int i = 0; i < filesString.length; i++) {
				String fileName = filesString[i];
				if (fileName.startsWith("_chat_"))
					return fileName;
			}
		}
		return "_no.jpg";
	}

	private String getBigProfileUserName(File f) throws Exception {
		if (f.exists() && f.isDirectory()) {
			String filesString[] = f.list();
			for (int i = 0; i < filesString.length; i++) {
				String fileName = filesString[i];
				if (fileName.startsWith("_big_"))
					return fileName;
			}
		}
		return "_no.jpg";
	}

}
