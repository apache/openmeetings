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
package org.apache.openmeetings.remote;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.conference.PollManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.poll.PollType;
import org.apache.openmeetings.persistence.beans.poll.RoomPoll;
import org.apache.openmeetings.persistence.beans.poll.RoomPollAnswers;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.session.ISessionManager;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class PollService implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(
			PollService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private PollManager pollManager;

	public String createPoll(String pollName, String pollQuestion, int pollTypeId) {
		String returnValue = "";
		try {
			log.debug("createPoll: " + pollQuestion);

			IConnection currentcon = Red5.getConnectionLocal();
			Client rc = sessionManager.getClientByStreamId(currentcon
					.getClient().getId(), null);

			log.debug("rc: " + rc.getStreamid() + " " + rc.getUsername() + " "
					+ rc.getIsMod());

			if (rc.getIsMod()) {
				// will move all existing polls to the archive
				pollManager.closePoll(rc.getRoom_id());
				
				sendNotification(currentcon, "newPoll",
						new Object[] { pollManager.createPoll(rc,
								pollName, pollQuestion, (long) pollTypeId) });
				returnValue = "200";
			} else {
				returnValue = "202";
			}

		} catch (Exception err) {
			returnValue = "203";
			log.error("[createPoll]", err);
		}
		return returnValue;
	}

	public boolean closePoll() {
		try {
			log.debug("closePoll: ");

			IConnection currentcon = Red5.getConnectionLocal();
			Client rc = sessionManager.getClientByStreamId(currentcon
					.getClient().getId(), null);

			log.debug("rc: " + rc.getStreamid() + " " + rc.getUsername() + " "
					+ rc.getIsMod());

			if (rc.getIsMod()) {
				// will move all existing polls to the archive
				return pollManager.closePoll(rc.getRoom_id());
			}

		} catch (Exception err) {
			log.error("[closePoll]", err);
		}
		return false;
	}

	public boolean deletePoll(Long poll_id) {
		try {
			log.debug("closePoll: ");

			IConnection currentcon = Red5.getConnectionLocal();
			Client rc = sessionManager.getClientByStreamId(currentcon
					.getClient().getId(), null);

			log.debug("rc: " + rc.getStreamid() + " " + rc.getUsername() + " "
					+ rc.getIsMod());

			if (rc.getIsMod()) {
				// will move all existing polls to the archive
				return pollManager.deletePoll(poll_id);
			}

		} catch (Exception err) {
			log.error("[closePoll]", err);
		}
		return false;
	}

	public void sendNotification(IConnection current, String clientFunction,
			Object[] obj) throws Exception {
		// Notify all clients of the same scope (room)
		Client rc = this.sessionManager.getClientByStreamId(current
				.getClient().getId(), null);
		Collection<Set<IConnection>> conCollection = current.getScope()
				.getConnections();
		for (Set<IConnection> conset : conCollection) {
			for (IConnection conn : conset) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						Client rcl = this.sessionManager
								.getClientByStreamId(conn.getClient().getId(), null);
						if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							// continue;
						} else {
							if (rcl.getRoom_id() != null && rcl.getRoom_id().equals(rc.getRoom_id())) {
								((IServiceCapableConnection) conn).invoke(
										clientFunction, obj,
										scopeApplicationAdapter);
								log.debug("sending " + clientFunction + " to "
										+ conn + " " + conn.getClient().getId());
							}
						}
					}
				}
			}
		}
	}

	public List<PollType> getPollTypeList() {
		return pollManager.getPollTypes();
	}

	public int vote(int pollvalue, int pollTypeId) {
		int returnVal = 0;
		try {
			log.debug("PollService::vote: Enter");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client rc = sessionManager.getClientByStreamId(streamid, null);

			if (rc == null) {
				log.error("RoomClient IS NULL for ClientID: "
						+ current.getClient().getId());
				return -1;
			}
			long roomId = rc.getRoom_id();
			if (!pollManager.hasPoll(roomId)) {
				log.error("POLL IS NULL for RoomId: " + rc.getRoom_id());
				return -1;
			}
			if (pollManager.hasVoted(roomId, rc.getUser_id())) {
				log.debug("hasVoted: true");
				return -1;
			}
			// get Poll
			RoomPoll roomP = pollManager.getPoll(roomId);

			log.debug("vote: " + pollvalue + " " + pollTypeId + " "
					+ roomP.getPollQuestion());

			log.debug("hasVoted: false");
			RoomPollAnswers rpA = new RoomPollAnswers();
			if (roomP.getPollType().getIsNumericAnswer()) {
				log.debug("numeric");
				rpA.setPointList(pollvalue);
			} else {
				log.debug("boolean");
				// Is boolean Question
				rpA.setAnswer(new Boolean(pollvalue == 1));
			}
			rpA.setVotedUser(userManager.getUserById(rc.getUser_id()));
			rpA.setVoteDate(new Date());
			rpA.setRoomPoll(roomP);
			roomP.getRoomPollAnswerList().add(rpA);
			pollManager.updatePoll(roomP);
			return 1;
		} catch (Exception err) {
			log.error("[vote]", err);
		}
		return returnVal;
	}

	public RoomPoll getVotes() {
		return getPoll();
	}

	public RoomPoll getPoll() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client rc = this.sessionManager.getClientByStreamId(current
					.getClient().getId(), null);

			// get Poll
			return pollManager.getPoll(rc.getRoom_id());
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}

	public int checkHasVoted() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client rc = this.sessionManager
					.getClientByStreamId(streamid, null);

			long roomId = rc.getRoom_id();
			if (pollManager.hasPoll(roomId)) {
				return pollManager.hasVoted(roomId, rc.getUser_id()) ? -1 : 1;
			} else {
				return -2;
			}
		} catch (Exception err) {
			log.error("[checkHasVoted]", err);
		}
		return 0;
	}

	public List<RoomPoll> getArchivedPollList() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client rc = this.sessionManager.getClientByStreamId(current
					.getClient().getId(), null);

			// get Poll
			return pollManager.getArchivedPollList(rc.getRoom_id());
		} catch (Exception err) {
			log.error("[getArchivedPollList]", err);
		}
		return null;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		
	}
}
