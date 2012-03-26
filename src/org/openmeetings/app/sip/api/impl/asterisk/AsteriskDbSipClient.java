package org.openmeetings.app.sip.api.impl.asterisk;

import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.UserSipData;
import org.openmeetings.app.sip.api.ISIPClient;
import org.openmeetings.app.sip.api.impl.asterisk.dao.AsteriskDAOImpl;
import org.openmeetings.app.sip.api.request.SIPCreateConferenceRequest;
import org.openmeetings.app.sip.api.request.SIPCreateUserRequest;
import org.openmeetings.app.sip.api.result.SIPCreateUserRequestResult;
import org.openmeetings.app.sip.api.result.SipCreateConferenceRequestResult;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 */
public class AsteriskDbSipClient implements ISIPClient {

    @Autowired
	private AsteriskDAOImpl asteriskDAO;

    public SIPCreateUserRequestResult createSIPUser(final SIPCreateUserRequest request) {
        asteriskDAO.addSipUser(request.getLogin(), request.getPassword());
        return new SIPCreateUserRequestResult() {
            @Override
            public UserSipData getUserSipData() {
                UserSipData sipData = new UserSipData();
                sipData.setAuthId(request.getLogin());
                sipData.setUserpass(request.getPassword());
                sipData.setUsername(request.getLogin());
                return sipData;
            }

            public boolean hasError() {
                return false;
            }

            public String getErrorMessage() {
                return null;
            }
        };
    }

    public SipCreateConferenceRequestResult createSIPConference(SIPCreateConferenceRequest request) {
        String confno = asteriskDAO.addMeetMeConference();
        return new SipCreateConferenceRequestResult(confno, "") {
            public boolean hasError() {
                return false;
            }

            public String getErrorMessage() {
                return null;
            }
        };
    }

    public Integer getConferenceMembersNumber(String sipNumber) {
        return asteriskDAO.getConferensMembersNumber(sipNumber);
    }
}