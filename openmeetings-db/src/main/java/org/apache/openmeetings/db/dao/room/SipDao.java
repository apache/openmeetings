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

import org.apache.openmeetings.db.entity.room.Room;
import org.asteriskjava.manager.DefaultManagerConnection;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.DbDelAction;
import org.asteriskjava.manager.action.DbDelTreeAction;
import org.asteriskjava.manager.action.DbGetAction;
import org.asteriskjava.manager.action.DbPutAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerError;
import org.asteriskjava.manager.response.ManagerResponse;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SipDao {
	private static final Logger log = Red5LoggerFactory.getLogger(SipDao.class);
	public static final String ASTERISK_OM_FAMILY = "openmeetings";
	public static final String ASTERISK_OM_KEY = "rooms";
	public static final String SIP_FIRST_NAME = "SIP Transport";
	public static final String SIP_USER_NAME = "--SIP--";
	private String sipHostname;
	private int sipPort;
	private String sipUsername;
	private String sipPassword;
	private String uid;
	private long timeout;
	private ManagerConnectionFactory factory;

	@SuppressWarnings("unused")
	private SipDao() {
		// prohibited default constructor
	}

	public SipDao(String sipHostname, int sipPort, String sipUsername, String sipPassword, long timeout) {
		this.sipHostname = sipHostname;
		this.sipPort = sipPort;
		this.sipUsername = sipUsername;
		this.sipPassword = sipPassword;
		this.timeout = timeout;
		factory = new ManagerConnectionFactory(this.sipHostname, this.sipPort, this.sipUsername, this.sipPassword);
	}

	private ManagerConnection getConnection() {
		DefaultManagerConnection con = (DefaultManagerConnection)factory.createManagerConnection();
		con.setDefaultEventTimeout(timeout);
		con.setDefaultResponseTimeout(timeout);
		con.setSocketReadTimeout((int)timeout);
		con.setSocketTimeout((int)timeout);
		return con;
	}

	private ManagerResponse exec(ManagerAction action) {
		if (factory == null) {
			log.warn("There is no Asterisk configured");
			return null;
		}
		ManagerConnection con = getConnection();
		try {
			con.login();
			ManagerResponse r = con.sendAction(action);
			if (r != null) {
				log.debug(r.toString());
			}
			return (r instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing ManagerAction: " + action, e);
		} finally {
			try {
				con.logoff();
			} catch (Exception e) {
				// no-op
			}
		}
		return null;
	}

	private ResponseEvents execEvent(EventGeneratingAction action) {
		if (factory == null) {
			log.warn("There is no Asterisk configured");
			return null;
		}
		ManagerConnection con = getConnection();
		try {
			con.login("on");
			ResponseEvents r = con.sendEventGeneratingAction(action);
			if (r != null) {
				log.debug(r.getResponse().toString());
			}
			return (r == null || r.getResponse() instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing EventGeneratingAction: " + action, e);
		} finally {
			try {
				con.logoff();
			} catch (Exception e) {
				// no-op
			}
		}
		return null;
	}

	private static String getKey(String confno) {
		return ASTERISK_OM_KEY + "/" + confno;
	}

	public String get(String confno) {
		String pin = null;
		DbGetAction da = new DbGetAction(ASTERISK_OM_FAMILY, getKey(confno));
		ManagerResponse r = exec(da);
		if (r != null) {
			pin = r.getResponse();
		}
		return pin;
	}

	public void update(String confno, String pin) {
		delete(confno);
		DbPutAction da = new DbPutAction(ASTERISK_OM_FAMILY, getKey(confno), pin);
		exec(da);
	}

	public void delete() {
		DbDelTreeAction da = new DbDelTreeAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY);
		exec(da);
	}

	public void delete(String confno) {
		DbDelAction da = new DbDelAction(ASTERISK_OM_FAMILY, getKey(confno));
		exec(da);
	}

	public Integer countUsers(String confno) {
		if (confno == null) {
			return null;
		}
		ConfbridgeListAction da = new ConfbridgeListAction(confno);
		ResponseEvents r = execEvent(da);
		if (r != null) {
			log.debug("SipDao::countUsers size == " + r.getEvents().size());
			// "- 1" here means: ListComplete event
			return r.getEvents().size() - 1;
		}
		return 0;
	}

	/**
	 * Perform call to specified phone number and join to conference
	 *
	 * @param number
	 *            number to call
	 * @param r
	 *            room to be connected to the call
	 */
	public void joinToConfCall(String number, Room r) {
		String sipNumber = (r != null && r.getConfno() != null) ? r.getConfno() : null;
		if (sipNumber == null) {
			log.warn("Failed to get SIP number for room: {}", r);
			return;
		}

		OriginateAction oa = new OriginateAction();
		oa.setChannel(String.format("Local/%s@rooms-originate", sipNumber));
		oa.setContext("rooms-out");
		oa.setExten(number);
		oa.setPriority(1);
		oa.setTimeout(timeout);

		exec(oa);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
