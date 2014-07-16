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
package org.apache.openmeetings.core.servlet.outputhandler;

import static org.apache.openmeetings.util.OmFileHelper.bigImagePrefix;
import static org.apache.openmeetings.util.OmFileHelper.chatImagePrefix;
import static org.apache.openmeetings.util.OmFileHelper.defaultProfileImageName;
import static org.apache.openmeetings.util.OmFileHelper.profileImagePrefix;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.core.servlet.BaseHttpServlet;
import org.apache.openmeetings.core.servlet.ServerNotInitializedException;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class DownloadHandler extends BaseHttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			DownloadHandler.class, OpenmeetingsVariables.webAppRootKey);

	private static final String defaultImageName = "deleted.jpg";
	private static final String defaultProfileImageNameBig = profileImagePrefix + defaultProfileImageName;
	private static final String defaultChatImageName = chatImagePrefix + defaultProfileImageName;
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
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");

			log.debug("\nquery = " + request.getQueryString());
			log.debug("\n\nfileName = " + request.getParameter("fileName"));
			log.debug("\n\nparentPath = " + request.getParameter("parentPath"));

			String queryString = request.getQueryString();
			if (queryString == null) {
				queryString = "";
			}

			String sid = request.getParameter("sid");

			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			Long users_id = getBean(SessiondataDao.class).checkSession(sid);
			Set<Right> rights = getBean(UserDao.class).getRights(users_id);

			if (rights != null && !rights.isEmpty()) {
				String room_id = request.getParameter("room_id");
				if (room_id == null) {
					room_id = "default";
				}

				String moduleName = request.getParameter("moduleName");
				if (moduleName == null) {
					moduleName = "nomodule";
				}

				String parentPath = request.getParameter("parentPath");
				if (parentPath == null) {
					parentPath = "nomodule";
				}

				String requestedFile = request.getParameter("fileName");
				if (requestedFile == null) {
					requestedFile = "";
				}
				
				String fileExplorerItemIdParam = request.getParameter("fileExplorerItemId");
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
					String remoteUser_id = request.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);
				} else if (moduleName.equals("remoteuserprofilebig")) {
					String remoteUser_id = request.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);
					
					requestedFile = getBigProfileUserName(working_dir);
				} else if (moduleName.equals("chat")) {
					String remoteUser_id = request.getParameter("remoteUserid");
					working_dir = OmFileHelper.getUploadProfilesUserDir(remoteUser_id == null ? "0" : remoteUser_id);
					logNonExistentFolder(working_dir);

					requestedFile = getChatUserName(working_dir);
				} else {
					working_dir = OmFileHelper.getUploadRoomDir(roomName);
				}

				if (!moduleName.equals("nomodule")) {
					log.debug("requestedFile: " + requestedFile + " current_dir: " + working_dir);

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

							requestedFile = defaultImageName;
							if (moduleName.equals("remoteuserprofile")) {
								requestedFile = defaultProfileImageName;
							} else if (moduleName.equals("remoteuserprofilebig")) {
								requestedFile = defaultProfileImageNameBig;
							} else if (moduleName.equals("userprofile")) {
								requestedFile = defaultProfileImageName;
							} else if (moduleName.equals("chat")) {
								requestedFile = defaultChatImageName;
							}
						} else if (requestedFile.endsWith(".swf")) {
							requestedFile = defaultSWFName;
						} else {
							requestedFile = defaultImageName;
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
					if (!full_path.getCanonicalPath().startsWith(curDirFile.getCanonicalPath())) {
						throw new Exception("Invalid file requested: f2.cp == "
								+ full_path.getCanonicalPath() + "; curDir.cp == "
								+ curDirFile.getCanonicalPath());
					}

					// Default type - Explorer, Chrome and others
					int browserType = 0;

					// Firefox and Opera browsers
					if (request.getHeader("User-Agent") != null) {
						if ((request.getHeader("User-Agent").contains("Firefox")) || (request.getHeader("User-Agent").contains("Opera"))) {
							browserType = 1;
						}
					}

					log.debug("Detected browser type:" + browserType);

					response.reset();
					response.resetBuffer();
					OutputStream out = response.getOutputStream();

					if (requestedFile.endsWith(".swf")) {
						// trigger download to SWF => THIS is a workaround for
						// Flash Player 10, FP 10 does not seem
						// to accept SWF-Downloads with the Content-Disposition
						// in the Header
						response.setContentType("application/x-shockwave-flash");
						response.setHeader("Content-Length", "" + full_path.length());
					} else {
						response.setContentType("APPLICATION/OCTET-STREAM");
						
						String fileNameResult = requestedFile;
						if (fileExplorerItemId != null && fileExplorerItemId > 0) {
							FileExplorerItem fileExplorerItem = getBean(FileExplorerItemDao.class).get(fileExplorerItemId);
							if (fileExplorerItem != null) {
								
								fileNameResult = fileExplorerItem.getFileName().substring(0, fileExplorerItem.getFileName().length()-4)
													+ fileNameResult.substring(fileNameResult.length()-4, fileNameResult.length());
								
							}
						}
						
						if (browserType == 0) {
							response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileNameResult, "UTF-8"));
						} else {
							response.setHeader("Content-Disposition", "attachment; filename*=UTF-8'en'"
											+ URLEncoder.encode(fileNameResult, "UTF-8"));
						}

						response.setHeader("Content-Length", "" + full_path.length());
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
				if (fileName.startsWith(chatImagePrefix))
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
				if (fileName.startsWith(bigImagePrefix))
					return fileName;
			}
		}
		return "_no.jpg";
	}

}
