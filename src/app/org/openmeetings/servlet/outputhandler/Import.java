package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.xmlimport.LanguageImport;
import org.openmeetings.app.xmlimport.UserImport;
import org.openmeetings.utils.ImportHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class Import extends HttpServlet {
	private static final long serialVersionUID = 582610358088411294L;
	private static final Logger log = Red5LoggerFactory.getLogger(Import.class,
			ScopeApplicationAdapter.webAppRootKey);

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

	public Configurationmanagement getCfgManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Configurationmanagement) context.getBean("cfgManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	public UsersDaoImpl getUsersDao() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (UsersDaoImpl) context.getBean("usersDao");
			}
		} catch (Exception err) {
			log.error("[getUsersDao]", err);
		}
		return null;
	}

	public ScopeApplicationAdapter getScopeApplicationAdapter() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (ScopeApplicationAdapter) context
						.getBean("web.handler");
			}
		} catch (Exception err) {
			log.error("[getScopeApplicationAdapter]", err);
		}
		return null;
	}
	
	public UserImport getUserImport() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (UserImport) context
						.getBean("userImport");
			}
		} catch (Exception err) {
			log.error("[getScopeApplicationAdapter]", err);
		}
		return null;
	}
	
	public AuthLevelmanagement getAuthLevelManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (AuthLevelmanagement) context
						.getBean("authLevelManagement");
			}
		} catch (Exception err) {
			log.error("[getAuthLevelManagement]", err);
		}
		return null;
	}
	
	public LanguageImport getLanguageImport() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (LanguageImport) context
						.getBean("languageImport");
			}
		} catch (Exception err) {
			log.error("[getLanguageImport]", err);
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

			if (getUserManagement() == null || getAuthLevelManagement() == null
					|| getScopeApplicationAdapter() == null || getUserImport() == null
					|| getSessionManagement() == null || getUsersDao() == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			String moduleName = httpServletRequest.getParameter("moduleName");
			if (moduleName == null) {
				moduleName = "moduleName";
			}
			log.debug("moduleName: " + moduleName);
			Long users_id = getSessionManagement().checkSession(sid);
			Long user_level = getUserManagement().getUserLevelByID(users_id);

			String publicSID = httpServletRequest.getParameter("publicSID");
			if (publicSID == null) {
				// Always ask for Public SID
				return;
			}

			log.debug("users_id: " + users_id);
			log.debug("user_level: " + user_level);
			log.debug("moduleName: " + moduleName);

			// if (user_level!=null && user_level > 0) {
			if (getAuthLevelManagement().checkAdminLevel(user_level)) {
				ServletMultipartRequest upload = new ServletMultipartRequest(
						httpServletRequest, ImportHelper.getMaxUploadSize(getCfgManagement(), user_level), "UTF8");
				InputStream is = upload.getFileContents("Filedata");

				if (moduleName.equals("users")) {
					log.error("Import Users");
					getUserImport().addUsersByDocument(is);

				} else if (moduleName.equals("language")) {
					log.error("Import Language");
					String language = httpServletRequest
							.getParameter("secondid");
					if (language == null) {
						language = "0";
					}
					Long language_id = Long.valueOf(language).longValue();
					log.debug("language_id: " + language_id);

					getLanguageImport().addLanguageByDocument(
							language_id, is);
				}
			} else {
				log.error("ERROR LangExport: not authorized FileDownload");
			}

			log.debug("Return And Close");

			LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
			hs.put("user", getUsersDao().getUser(users_id));
			hs.put("message", "library");
			hs.put("action", "import");

			log.debug("moduleName.equals(userprofile) ? " + moduleName);

			log.debug("moduleName.equals(userprofile) ! ");

			getScopeApplicationAdapter().sendMessageWithClientByPublicSID(hs,
					publicSID);

			return;

		} catch (Exception er) {
			log.error("ERROR exporting:", er);
		}
	}

}
