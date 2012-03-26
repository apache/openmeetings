package org.openmeetings.app.sip.api;

import org.openmeetings.app.sip.api.request.SIPCreateConferenceRequest;
import org.openmeetings.app.sip.api.request.SIPCreateUserRequest;
import org.openmeetings.app.sip.api.result.SIPCreateUserRequestResult;
import org.openmeetings.app.sip.api.result.SipCreateConferenceRequestResult;

/**
 * SIP client interface
 */
public interface ISIPClient {

    SIPCreateUserRequestResult createSIPUser(SIPCreateUserRequest request);

    SipCreateConferenceRequestResult createSIPConference(SIPCreateConferenceRequest request);

    Integer getConferenceMembersNumber(String sipNumber);

}
