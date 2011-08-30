package org.openmeetings.axis.services;

import javax.servlet.ServletContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class UserWebServiceFacade {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserWebServiceFacade.class, ScopeApplicationAdapter.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}

	private UserWebService getUserServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (UserWebService) context.getBean("userWebService");
		} catch (Exception err) {
			log.error("[getUserServiceProxy]", err);
		}
		return null;
	}

	/**
	 * load this session id before doing anything else
	 * 
	 * @return Sessiondata-Object
	 */
	public Sessiondata getSession() throws AxisFault {
		return getUserServiceProxy().getSession();
	}

	/**
	 * auth function, use the SID you get by getSession
	 * 
	 * @param SID
	 * @param Username
	 * @param Userpass
	 * @return positive means Loggedin, if negativ its an ErrorCode, you have to
	 *         invoke the Method getErrorByCode to get the Text-Description of
	 *         that ErrorCode
	 */
	public Long loginUser(String SID, String username, String userpass)
			throws AxisFault {
		return getUserServiceProxy().loginUser(SID, username, userpass);
	}

	/**
	 * Gets the Error-Object
	 * 
	 * @param SID
	 * @param errorid
	 * @param language_id
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		return getUserServiceProxy().getErrorByCode(SID, errorid, language_id);
	}

	public Long addNewUser(String SID, String username, String userpass,
			String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL)
			throws AxisFault {
		return getUserServiceProxy().addNewUser(SID, username, userpass,
				lastname, firstname, email, additionalname, street, zip, fax,
				states_id, town, language_id, baseURL);
	}

	public Long addNewUserWithTimeZone(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL,
			String jNameTimeZone) throws AxisFault {
		return getUserServiceProxy().addNewUserWithTimeZone(SID, username,
				userpass, lastname, firstname, email, additionalname, street,
				zip, fax, states_id, town, language_id, baseURL, jNameTimeZone);

	}

	/**
	 * 
	 * Adds a user with an externalUserId and type, but checks if the user/type
	 * does already exist
	 * 
	 * @param SID
	 * @param username
	 * @param userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param additionalname
	 * @param street
	 * @param zip
	 * @param fax
	 * @param states_id
	 * @param town
	 * @param language_id
	 * @param jNameTimeZone
	 * @param externalUserId
	 * @param externalUserType
	 * @return
	 * @throws AxisFault
	 */
	public Long addNewUserWithExternalType(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id,
			String jNameTimeZone, Long externalUserId, String externalUserType)
			throws AxisFault {
		return getUserServiceProxy().addNewUserWithExternalType(SID, username,
				userpass, lastname, firstname, email, additionalname, street,
				zip, fax, states_id, town, language_id, jNameTimeZone,
				externalUserId, externalUserType);

	}

	/**
	 * 
	 * delete a user by its id
	 * 
	 * @param SID
	 * @param userId
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserById(String SID, Long userId) throws AxisFault {
		return getUserServiceProxy().deleteUserById(SID, userId);
	}

	/**
	 * 
	 * delete a user by its external user id and type
	 * 
	 * @param SID
	 * @param externalUserId
	 * @param externalUserType
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserByExternalUserIdAndType(String SID,
			Long externalUserId, String externalUserType) throws AxisFault {
		return getUserServiceProxy().deleteUserByExternalUserIdAndType(SID,
				externalUserId, externalUserType);
	}

	/**
	 * 
	 * @param SID
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @return
	 * @throws AxisFault
	 */
	public Long setUserObject(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email)
			throws AxisFault {
		return getUserServiceProxy().setUserObject(SID, username, firstname,
				lastname, profilePictureUrl, email);
	}

	/**
	 * This is the advanced technique to set the User Object + simulate a User
	 * from the external system, this is needed cause you can that always
	 * simulate to same user in openmeetings
	 * 
	 * @param SID
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @param externalUserId
	 *            the User Id of the external System
	 * @param externalUserType
	 *            the Name of the external system, for example you can run
	 *            several external system and one meeting server
	 * @return
	 * @throws AxisFault
	 */
	public Long setUserObjectWithExternalUser(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType)
			throws AxisFault {
		return getUserServiceProxy().setUserObjectWithExternalUser(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType);
	}

	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
			throws AxisFault {
		return getUserServiceProxy().setUserObjectAndGenerateRoomHash(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType, room_id,
				becomeModeratorAsInt, showAudioVideoTestAsInt);
	}

	public String setUserObjectAndGenerateRoomHashByURL(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, Long externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt) throws AxisFault {
		return getUserServiceProxy().setUserObjectAndGenerateRoomHashByURL(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType, room_id,
				becomeModeratorAsInt, showAudioVideoTestAsInt);
	}

	public String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, Long externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) throws AxisFault {
		return getUserServiceProxy()
				.setUserObjectAndGenerateRoomHashByURLAndRecFlag(SID, username,
						firstname, lastname, profilePictureUrl, email,
						externalUserId, externalUserType, room_id,
						becomeModeratorAsInt, showAudioVideoTestAsInt,
						allowRecording);
	}

	public String setUserObjectMainLandingZone(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType)
			throws AxisFault {
		return getUserServiceProxy().setUserObjectMainLandingZone(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType);
	}

	public String setUserAndNickName(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt)
			throws AxisFault {
		return getUserServiceProxy().setUserAndNickName(SID, username,
				firstname, lastname, profilePictureUrl, email, externalUserId,
				externalUserType, room_id, becomeModeratorAsInt,
				showAudioVideoTestAsInt, showNickNameDialogAsInt);
	}

	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			Long externalUserId, String externalUserType, Long recording_id)
			throws AxisFault {
		return getUserServiceProxy()
				.setUserObjectAndGenerateRecordingHashByURL(SID, username,
						firstname, lastname, externalUserId, externalUserType,
						recording_id);
	}

	public Long addUserToOrganisation(String SID, Long user_id,
			Long organisation_id, Long insertedby, String comment)
			throws AxisFault {
		return getUserServiceProxy().addUserToOrganisation(SID, user_id,
				organisation_id, insertedby, comment);
	}

	public SearchResult getUsersByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) throws AxisFault {
		return getUserServiceProxy().getUsersByOrganisation(SID,
				organisation_id, start, max, orderby, asc);
	}

	public Boolean kickUserByPublicSID(String SID, String publicSID)
			throws AxisFault {
		return getUserServiceProxy().kickUserByPublicSID(SID, publicSID);
	}

}
