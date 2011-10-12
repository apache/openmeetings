package org.openmeetings.app.remote;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.openmeetings.app.persistence.beans.poll.RoomPoll;
import org.openmeetings.app.persistence.beans.poll.RoomPollAnswers;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class PollService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			PollService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private ClientListManager clientListManager;
	@Autowired
	private Usermanagement usermanagement;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private PollManagement pollManagement;

	public String createPoll(String pollName, String pollQuestion, int pollTypeId) {
		String returnValue = "";
		try {
			log.debug("createPoll: " + pollQuestion);

			IConnection currentcon = Red5.getConnectionLocal();
			RoomClient rc = clientListManager.getClientByStreamId(currentcon
					.getClient().getId());

			log.debug("rc: " + rc.getStreamid() + " " + rc.getUsername() + " "
					+ rc.getIsMod());

			if (rc.getIsMod()) {
				// will move all existing polls to the archive
				pollManagement.clearRoomPollList(rc.getRoom_id());
				
				sendNotification(currentcon, "newPoll",
						new Object[] { pollManagement.createPoll(rc,
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

	public void clearRoomPollList(Long room_id) {
		pollManagement.clearRoomPollList(room_id);
	}

	public void sendNotification(IConnection current, String clientFunction,
			Object[] obj) throws Exception {
		// Notify all clients of the same scope (room)
		RoomClient rc = this.clientListManager.getClientByStreamId(current
				.getClient().getId());
		Collection<Set<IConnection>> conCollection = current.getScope()
				.getConnections();
		for (Set<IConnection> conset : conCollection) {
			for (IConnection conn : conset) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							// continue;
						} else {
							if (rcl.getRoom_id().equals(rc.getRoom_id())
									&& rcl.getRoom_id() != null) {
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
		return pollManagement.getPollTypes();
	}

	public int vote(int pollvalue, int pollTypeId) {
		int returnVal = 0;
		try {
			log.debug("PollService::vote: Enter");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient rc = clientListManager.getClientByStreamId(streamid);

			if (rc == null) {
				log.error("RoomClient IS NULL for ClientID: "
						+ current.getClient().getId());
				return -1;
			}
			long roomId = rc.getRoom_id();
			if (!pollManagement.hasPoll(roomId)) {
				log.error("POLL IS NULL for RoomId: " + rc.getRoom_id());
				return -1;
			}
			if (pollManagement.hasVoted(roomId, rc.getUser_id())) {
				log.debug("hasVoted: true");
				return -1;
			}
			// get Poll
			RoomPoll roomP = pollManagement.getPoll(roomId);

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
			rpA.setVotedUser(usermanagement.getUserById(rc.getUser_id()));
			rpA.setVoteDate(new Date());
			rpA.setRoomPoll(roomP);
			roomP.getRoomPollAnswerList().add(rpA);
			pollManagement.updatePoll(roomP);
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
			RoomClient rc = this.clientListManager.getClientByStreamId(current
					.getClient().getId());

			// get Poll
			return pollManagement.getPoll(rc.getRoom_id());
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}

	public int checkHasVoted() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient rc = this.clientListManager
					.getClientByStreamId(streamid);

			long roomId = rc.getRoom_id();
			if (pollManagement.hasPoll(roomId)) {
				return pollManagement.hasVoted(roomId, rc.getUser_id()) ? -1 : 1;
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
			RoomClient rc = this.clientListManager.getClientByStreamId(current
					.getClient().getId());

			// get Poll
			return pollManagement.getArchivedPollList(rc.getRoom_id());
		} catch (Exception err) {
			log.error("[getArchivedPollList]", err);
		}
		return null;
	}
}
