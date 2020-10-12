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
package org.apache.openmeetings.db.dao.room;

public class SipConfig {
	private String sipHostname;
	private int managerPort;
	private String managerUser;
	private String managerPass;
	private long managerTimeout;

	private int localWsPort = 6666;
	private String localWsHost;
	private int wsPort;
	private String omSipUser;
	private String omSipPasswd;

	private String uid; //FIXME TODO is this still required ?!

	public String getSipHostname() {
		return sipHostname;
	}

	public void setSipHostname(String sipHostname) {
		this.sipHostname = sipHostname;
	}

	public int getManagerPort() {
		return managerPort;
	}

	public void setManagerPort(int managerPort) {
		this.managerPort = managerPort;
	}

	public String getManagerUser() {
		return managerUser;
	}

	public void setManagerUser(String managerUser) {
		this.managerUser = managerUser;
	}

	public String getManagerPass() {
		return managerPass;
	}

	public void setManagerPass(String managerPass) {
		this.managerPass = managerPass;
	}

	public long getManagerTimeout() {
		return managerTimeout;
	}

	public void setManagerTimeout(long managerTimeout) {
		this.managerTimeout = managerTimeout;
	}

	public int getLocalWsPort() {
		return localWsPort;
	}

	public void setLocalWsPort(int localWsPort) {
		this.localWsPort = localWsPort;
	}

	public String getLocalWsHost() {
		return localWsHost;
	}

	public void setLocalWsHost(String localWsHost) {
		this.localWsHost = localWsHost;
	}

	public int getWsPort() {
		return wsPort;
	}

	public void setWsPort(int wsPort) {
		this.wsPort = wsPort;
	}

	public String getOmSipUser() {
		return omSipUser;
	}

	public void setOmSipUser(String omSipUser) {
		this.omSipUser = omSipUser;
	}

	public String getOmSipPasswd() {
		return omSipPasswd;
	}

	public void setOmSipPasswd(String omSipPasswd) {
		this.omSipPasswd = omSipPasswd;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
