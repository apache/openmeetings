package org.openmeetings.servlet.outputhandler;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.utils.ImportHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public abstract class AbstractUploadController implements ServletContextAware {
	private static final Logger log = Red5LoggerFactory.getLogger(
			AbstractUploadController.class, OpenmeetingsVariables.webAppRootKey);
	protected ServletContext context;
	@Autowired
	protected Sessionmanagement sessionManagement;
	@Autowired
	protected Usermanagement userManagement;
	@Autowired
	protected AuthLevelmanagement authLevelManagement;
	@Autowired
	protected Configurationmanagement cfgManagement;
	
	public void setServletContext(ServletContext arg0) {
		context = arg0;
	}
	
	protected enum UploadParams {
		pFile
		, pUserId
		, pSID
		, pPublicSID
	}

	protected <T> T getParam(HashMap<UploadParams, Object> params, UploadParams param, Class<T> type) {
		return getParam(params, param);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getParam(HashMap<UploadParams, Object> params, UploadParams param) {
		return params.containsKey(param) ? (T)params.get(param) : null;
	}
	
    protected HashMap<UploadParams, Object> validate(HttpServletRequest request, boolean admin) throws ServletException {
    	HashMap<UploadParams, Object> params = new HashMap<UploadParams, Object>();
		log.debug("Starting validate");
		try {

			String sid = request.getParameter("sid");
			if (sid == null) {
				throw new ServletException("SID Missing");
			}
			params.put(UploadParams.pSID, sid);
			log.debug("sid: " + sid);

			Long userId = sessionManagement.checkSession(sid);
			Long userLevel = userManagement.getUserLevelByID(userId);
			log.debug("userId = " + userId + ", userLevel = " + userLevel);
			params.put(UploadParams.pUserId, userId);

			if ((admin && !authLevelManagement.checkAdminLevel(userLevel))
					|| (!admin && userLevel <= 0)) {
				throw new ServletException("Insufficient permissions "
						+ userLevel);
			}

			String publicSID = request.getParameter("publicSID");
			if (publicSID == null) {
				// Always ask for Public SID
				throw new ServletException("Missing publicSID");
			}
			log.debug("publicSID: " + publicSID);
			params.put(UploadParams.pPublicSID, publicSID);

			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
			MultipartFile multipartFile = multipartRequest.getFile("Filedata");
			long fileSize = multipartFile.getSize();
			long maxSize = ImportHelper.getMaxUploadSize(cfgManagement);
			log.debug("uploading " + fileSize + " bytes");
			if (fileSize > maxSize) {
				throw new ServletException("Maximum upload size: " + maxSize + " exceeded: " + fileSize);
			}
			params.put(UploadParams.pFile, multipartFile);
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
		}
		return params;
    }
}
