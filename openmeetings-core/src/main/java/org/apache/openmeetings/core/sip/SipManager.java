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
package org.apache.openmeetings.core.sip;

import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;

import java.util.BitSet;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.ISipManager;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.util.string.Strings;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SipManager implements ISipManager {
	private static final Logger log = LoggerFactory.getLogger(SipManager.class);
	public static final String ASTERISK_OM_FAMILY = "openmeetings";
	public static final String ASTERISK_OM_KEY = "rooms";
	public static final String SIP_FIRST_NAME = "SIP Transport";
	public static final String SIP_USER_NAME = "--SIP--";

	@Value("${sip.hostname}")
	private String sipHostname;
	@Value("${sip.manager.port}")
	private int managerPort;
	@Value("${sip.manager.user}")
	private String managerUser;
	@Value("${sip.manager.password}")
	private String managerPass;
	@Value("${sip.manager.timeout}")
	private long managerTimeout;

	@Value("${sip.ws.remote.port}")
	private int wsPort;
	@Value("${sip.ws.remote.user}")
	private String omSipUser;
	@Value("${sip.ws.remote.password}")
	private String omSipPasswd;
	@Value("${sip.ws.local.host}")
	private String localWsHost;
	@Value("${sip.ws.local.port.min}")
	private int minLocalWsPort = 6666;
	@Value("${sip.ws.local.port.max}")
	private int maxLocalWsPort = 7666;

	private ManagerConnectionFactory factory;
	private String sipUserPicture;
	private BitSet ports;

	@PostConstruct
	public void init() {
		if (!Strings.isEmpty(sipHostname)) {
			factory = new ManagerConnectionFactory(
					sipHostname
					, managerPort
					, managerUser
					, managerPass);
			ports = new BitSet(maxLocalWsPort - minLocalWsPort);
		}
	}

	private ManagerConnection getConnection() {
		DefaultManagerConnection con = (DefaultManagerConnection)factory.createManagerConnection();
		con.setDefaultEventTimeout(managerTimeout);
		con.setDefaultResponseTimeout(managerTimeout);
		con.setSocketReadTimeout((int)managerTimeout);
		con.setSocketTimeout((int)managerTimeout);
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
				log.debug("{}", r);
			}
			return (r instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing ManagerAction: {}", action, e);
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
				log.debug("{}", r.getResponse());
			}
			return (r == null || r.getResponse() instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing EventGeneratingAction: {}", action, e);
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

	static String getSipNumber(Room r) {
		return (r != null && r.getConfno() != null) ? r.getConfno() : null;
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

	@Override
	public void update(String confno, String pin) {
		delete(confno);
		DbPutAction da = new DbPutAction(ASTERISK_OM_FAMILY, getKey(confno), pin);
		exec(da);
	}

	public void delete() {
		DbDelTreeAction da = new DbDelTreeAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY);
		exec(da);
	}

	@Override
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
			log.trace("SipManager::countUsers size == {}", r.getEvents().size());
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
		String sipNumber = getSipNumber(r);
		if (sipNumber == null) {
			log.warn("Failed to get SIP number for room: {}", r);
			return;
		}

		OriginateAction oa = new OriginateAction();
		oa.setChannel(String.format("Local/%s@rooms-originate", sipNumber));
		oa.setContext("rooms-out");
		oa.setExten(number);
		oa.setPriority(1);
		oa.setTimeout(managerTimeout);

		exec(oa);
	}

	public void setUserPicture(Function<User, String> pictureCreator) {
		User u = new User();
		u.setId(SIP_USER_ID);
		sipUserPicture = pictureCreator.apply(u);
	}

	public User getSipUser(Room r) {
		if (factory == null || !isSipEnabled() || !r.isSipEnabled()) {
			return null;
		}
		User u = new User();
		u.setId(OmFileHelper.SIP_USER_ID);
		u.setFirstname(SIP_FIRST_NAME);
		u.setLogin(SIP_USER_NAME);
		u.setPictureUri(sipUserPicture);
		return u;
	}

	String getSipHostname() {
		return sipHostname;
	}

	int getWsPort() {
		return wsPort;
	}

	String getOmSipUser() {
		return omSipUser;
	}

	String getOmSipPasswd() {
		return omSipPasswd;
	}

	String getLocalWsHost() {
		return localWsHost;
	}

	public Optional<SipStackProcessor> createSipStackProcessor(String name, Room r, ISipCallbacks callbacks) throws Exception {
		if (factory == null || !isSipEnabled() || !r.isSipEnabled()) {
			log.warn("Asterisk is not configured or denied in room #{}", r.getId());
			return Optional.empty();
		}
		int port;
		synchronized (ports) {
			int free = ports.nextClearBit(0);
			ports.flip(free);
			port = minLocalWsPort + free;
		}
		return Optional.of(new SipStackProcessor(this, name, port, callbacks));
	}

	void freePort(int port) {
		synchronized (ports) {
			ports.clear(port - minLocalWsPort);
		}
	}
}
