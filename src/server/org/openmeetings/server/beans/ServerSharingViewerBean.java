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
package org.openmeetings.server.beans;

/**
 * @author sebastianwagner
 *
 */
public class ServerSharingViewerBean {
	
	public Long sessionId;
	public String publicSID;
	
	/**
	 * @param sessionId
	 * @param publicSID
	 *
	 * 20.09.2009 15:48:55
	 * sebastianwagner
	 * 
	 * 
	 */
	public ServerSharingViewerBean(long sessionId, String publicSID) {
		super();
		this.sessionId = sessionId;
		this.publicSID = publicSID;
	}
	
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	

}
