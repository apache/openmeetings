package org.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ScreenViewHandler extends HttpServlet {
	private static final long serialVersionUID = -2221780306064491855L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			ScreenViewHandler.class, ScopeApplicationAdapter.webAppRootKey);

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Usermanagement) context.getBean("userManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
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

			if (getUserManagement() == null || getSessionManagement() == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			System.out.println("sid: " + sid);

			Long users_id = getSessionManagement().checkSession(sid);
			long user_level = getUserManagement().getUserLevelByID(users_id);

			if (user_level > 0) {
				String room = httpServletRequest.getParameter("room");
				if (room == null) {
					room = "default";
				}
				String domain = httpServletRequest.getParameter("domain");
				if (domain == null)
					domain = "default";

				String filename = httpServletRequest.getParameter("fileName");
				if (filename == null)
					filename = "default";

				// make a complete name out of domain(organisation) + roomname
				String roomName = domain + "_" + room;
				// trim whitespaces cause it is a directory name
				roomName = StringUtils.deleteWhitespace(roomName);

				// Get the current User-Directory

				String current_dir = getServletContext().getRealPath("/");
				System.out.println("Current_dir: " + current_dir);

				String working_dir = current_dir + "desktop"
						+ File.separatorChar + roomName + File.separatorChar;

				// Add the Folder for the Room

				String full_path = working_dir + filename;
				System.out.println("full_path: " + full_path);

				RandomAccessFile rf = new RandomAccessFile(full_path, "r");

				httpServletResponse.reset();
				httpServletResponse.resetBuffer();
				OutputStream out = httpServletResponse.getOutputStream();
				httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
				httpServletResponse.setHeader("Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
				httpServletResponse.setHeader("Content-Length",
						"" + rf.length());
				httpServletResponse.setHeader("Cache-Control", "no-cache");
				httpServletResponse.setHeader("Pragma", "no-cache");

				byte[] buffer = new byte[1024];
				int readed = -1;

				while ((readed = rf.read(buffer, 0, buffer.length)) > -1) {
					out.write(buffer, 0, readed);
				}

				rf.close();

				out.flush();
				out.close();

			}

		} catch (Exception er) {
			System.out.println("Error downloading: " + er);
		}
	}

}
