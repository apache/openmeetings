package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BackupImportFacade extends HttpServlet {

	private static final long serialVersionUID = 2786696080712127872L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			BackupImportFacade.class, ScopeApplicationAdapter.webAppRootKey);

	private BackupImport getBackupImport() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (BackupImport) context.getBean("backupImport");
		} catch (Exception err) {
			log.error("[getBackupImport]", err);
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

			if (getBackupImport() == null) {
				OutputStream out = httpServletResponse.getOutputStream();

				String msg = "Server is not booted yet";

				out.write(msg.getBytes());

				out.flush();
				out.close();

				return;
			}

			getBackupImport().service(httpServletRequest, httpServletResponse,
					getServletContext());

		} catch (Exception er) {
			log.error("ERROR ", er);
			log.debug("Error importing: " + er);
			er.printStackTrace();
		}
	}

}
