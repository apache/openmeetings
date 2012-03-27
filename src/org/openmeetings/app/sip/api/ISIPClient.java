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
