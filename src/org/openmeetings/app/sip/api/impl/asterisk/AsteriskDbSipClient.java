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