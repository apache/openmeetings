package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.methodgateway.MethodGatewayResponse;
import org.openmeetings.app.persistence.beans.basic.RemoteSessionObject;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class MethodGateway extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MethodGateway.class, ScopeApplicationAdapter.webAppRootKey);

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

			String service = httpServletRequest.getParameter("service");

			if (service == null) {
				MethodGatewayResponse mGateway = new MethodGatewayResponse();
				mGateway.setMessage("No Service given");

				XStream xStream = new XStream(new XppDriver());
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(mGateway);

				OutputStream out = httpServletResponse.getOutputStream();

				out.write(xmlString.getBytes());
				return;
			}

			String method = httpServletRequest.getParameter("method");

			if (method == null) {
				MethodGatewayResponse mGateway = new MethodGatewayResponse();
				mGateway.setMessage("No Method given");

				XStream xStream = new XStream(new XppDriver());
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(mGateway);

				OutputStream out = httpServletResponse.getOutputStream();

				out.write(xmlString.getBytes());
				return;
			}

			log.debug("service: " + service + " method: " + method);

			// Services

			if (service.equals("userservice")) {

				if (method.equals("getSession")) {

					Sessiondata sessionData = MainService.getInstance()
							.getsessiondata();

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(sessionData);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					return;
				} else if (method.equals("loginUser")) {

					String SID = httpServletRequest.getParameter("SID");
					String username = httpServletRequest
							.getParameter("username");
					String userpass = httpServletRequest
							.getParameter("userpass");

					Long returnVal = new Long(-1);

					Object obj = Usermanagement.getInstance().loginUser(SID,
							username, userpass, null, false);
					if (obj == null) {
						returnVal = new Long(-1);
					}
					String objName = obj.getClass().getName();
					log.debug("objName: " + objName);
					if (objName.equals("java.lang.Long")) {
						returnVal = (Long) obj;
					} else {
						returnVal = new Long(1);
					}

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					return;
				} else if (method.equals("setUserObject")) {

					String SID = httpServletRequest.getParameter("SID");
					String username = httpServletRequest
							.getParameter("username");
					String firstname = httpServletRequest
							.getParameter("firstname");
					String lastname = httpServletRequest
							.getParameter("lastname");
					String profilePictureUrl = httpServletRequest
							.getParameter("profilePictureUrl");
					String email = httpServletRequest.getParameter("email");

					Long returnVal = new Long(-1);

					Long users_id = Sessionmanagement.getInstance()
							.checkSession(SID);
					Long user_level = Usermanagement.getInstance()
							.getUserLevelByID(users_id);
					if (AuthLevelmanagement.getInstance().checkAdminLevel(
							user_level)) {

						RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
								username, firstname, lastname,
								profilePictureUrl, email);

						XStream xStream = new XStream(new XppDriver());
						xStream.setMode(XStream.NO_REFERENCES);
						String xmlString = xStream.toXML(remoteSessionObject);

						Sessionmanagement.getInstance()
								.updateUserRemoteSession(SID, xmlString);

						returnVal = new Long(1);
					} else {
						returnVal = new Long(-36);
					}

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					return;
				}

			} else if (service.equals("roomservice")) {

				// public Long addRoom(String SID,
				// String name,
				// Long roomtypes_id ,
				// String comment, Long numberOfPartizipants,
				// Boolean ispublic,
				// Integer videoPodWidth,
				// Integer videoPodHeight,
				// Integer videoPodXPosition,
				// Integer videoPodYPosition,
				// Integer moderationPanelXPosition,
				// Boolean showWhiteBoard,
				// Integer whiteBoardPanelXPosition,
				// Integer whiteBoardPanelYPosition,
				// Integer whiteBoardPanelHeight,
				// Integer whiteBoardPanelWidth,
				// Boolean showFilesPanel,
				// Integer filesPanelXPosition,
				// Integer filesPanelYPosition,
				// Integer filesPanelHeight,
				// Integer filesPanelWidth) {

				if (method.equals("addRoom")) {

					String SID = httpServletRequest.getParameter("SID");
					String name = httpServletRequest.getParameter("name");
					Long roomtypes_id = Long.valueOf(
							httpServletRequest.getParameter("roomtypes_id"))
							.longValue();
					String comment = httpServletRequest.getParameter("comment");
					Long numberOfPartizipants = Long.valueOf(
							httpServletRequest
									.getParameter("numberOfPartizipants"))
							.longValue();
					Boolean ispublic = Boolean.valueOf(
							httpServletRequest.getParameter("ispublic"))
							.booleanValue();

					Long users_id = Sessionmanagement.getInstance()
							.checkSession(SID);
					Long user_level = Usermanagement.getInstance()
							.getUserLevelByID(users_id);

					Long returnVal = Roommanagement.getInstance().addRoom(
							user_level, name, roomtypes_id, comment,
							numberOfPartizipants, ispublic, null, false, false,
							null, false, null, true, false, false, "", "", "",
							null, null, null, false);

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					return;
				}

			}

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

}
