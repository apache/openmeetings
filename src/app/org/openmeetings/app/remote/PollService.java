package org.openmeetings.app.remote;

import java.util.Collection;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;

import org.openmeetings.app.conference.videobeans.RoomPoll;
import org.openmeetings.app.conference.videobeans.RoomPollAnswers;
import org.openmeetings.app.conference.videobeans.PollType;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

/**
 * 
 * @author swagner
 *
 */
public class PollService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(PollService.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static HashMap<Long,RoomPoll> pollList = new HashMap<Long,RoomPoll>();
	
	//Beans, see red5-web.xml
	@Autowired
	private ClientListManager clientListManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	
	public String createPoll(String pollQuestion,int pollTypeId){
		String returnValue="";
		try {
			log.debug("createPoll: "+pollQuestion);
			
			IConnection currentcon = Red5.getConnectionLocal();

			
			HashMap<String,RoomClient> ClientList = this.clientListManager.getClientList();
			RoomClient rc = ClientList.get(currentcon.getClient().getId());
			
			Long uniqueRoomPollName = rc.getRoom_id();
			
			log.debug("rc: "+rc.getStreamid()+" "+rc.getUsername()+" "+rc.getIsMod());
			
			if (rc.getIsMod()){
				RoomPoll roomP = new RoomPoll();
				
				roomP.setCreatedBy(rc);
				roomP.setPollDate(new Date());
				roomP.setPollQuestion(pollQuestion);
				roomP.setPollTypeId(pollTypeId);
				roomP.setRoom_id(rc.getRoom_id());
				List<RoomPollAnswers> rpA = new LinkedList<RoomPollAnswers>();
				roomP.setRoomPollAnswerList(rpA);
				
				pollList.put(uniqueRoomPollName, roomP);
				
				sendNotification(currentcon,"newPoll",new Object[] { roomP });
				returnValue="200";
			} else {
				returnValue="202";
			}

			
		} catch (Exception err){
			returnValue="203";
			log.error("[createPoll]",err);
		}
		return returnValue;
	}	

	public static void clearRoomPollList(Long room_id){
		try {
			log.debug("clearRoomPollList: "+room_id);
			if(pollList.get(room_id)!=null){
				pollList.remove(room_id);
			}
		} catch (Exception err){
			log.error("[clearRoomPollList]",err);
		}
	}
	
	public void sendNotification(IConnection current,String clientFunction, Object[]obj) throws Exception{
		//Notify all clients of the same scope (room)
		RoomClient rc = this.clientListManager.getClientByStreamId(current.getClient().getId());
		Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
		for (Set<IConnection> conset : conCollection) {
			for (IConnection conn : conset) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
    						//continue;
    					} else {
							if (rcl.getRoom_id().equals(rc.getRoom_id()) && rcl.getRoom_id()!=null){
								((IServiceCapableConnection) conn).invoke(clientFunction,obj, scopeApplicationAdapter);
								log.debug("sending "+clientFunction+" to " + conn+" "+conn.getClient().getId());
							}
						}
					}
				}		
			}
		}
	}
	
	public List<PollType> getPollTypeList(){
		List<PollType> pollTypesList = new LinkedList<PollType>();
		try {
			
			PollType pt = new PollType();
			pt.setPollTypeLabelid(26);
			pt.setIsNumericAnswer("no");
			pt.setPollTypesId(1);
			pollTypesList.add(pt);
			
			pt = new PollType();
			pt.setPollTypeLabelid(27);
			pt.setIsNumericAnswer("yes");
			pt.setPollTypesId(2);
			pollTypesList.add(pt);
			
		} catch (Exception err){
			log.error("[getPollTypeList]",err);
		}
		return pollTypesList;
	}
	
	public int vote(int pollvalue,int pollTypeId){
		int returnVal=0;
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient rc = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			if (rc==null){
				log.error("RoomClient IS NULL for ClientID: "+current.getClient().getId());
				return -1;
			}
			//get Poll
			RoomPoll roomP = pollList.get(rc.getRoom_id());
			
			if (roomP==null){
				log.error("POLL IS NULL for RoomId: "+rc.getRoom_id());
				return -1;
			}
			
			log.debug("vote: "+pollvalue+" "+pollTypeId+" "+roomP.getPollQuestion());
			
			//Check if this user has already voted
			if(this.hasVoted(roomP,rc.getStreamid())){
				log.debug("hasVoted: true");
				return -1;
			} else {
				log.debug("hasVoted: false");
				RoomPollAnswers rpA = new RoomPollAnswers();
				if (pollTypeId==1){
					log.debug("boolean");
					//Is boolean Question
					if (pollvalue==1){
						rpA.setAnswer(new Boolean(true));
					} else {
						rpA.setAnswer(new Boolean(false));
					}
				} else if(pollTypeId==2){
					log.debug("numeric");
					rpA.setPointList(pollvalue);
				}
				rpA.setVotedClients(rc);
				rpA.setVoteDate(new Date());
				roomP.getRoomPollAnswerList().add(rpA);
				return 1;
			}		
		} catch (Exception err){
			log.error("vote");
			log.error("[vote]",err);
		}
		return returnVal;
	}
	
	private boolean hasVoted(RoomPoll roomP,String streamid) throws Exception{
		List<RoomPollAnswers> answerList = roomP.getRoomPollAnswerList();
		Iterator<RoomPollAnswers> iter = answerList.iterator();
		log.debug("hasVoted: "+streamid);
		while (iter.hasNext()){
			RoomPollAnswers rpA = iter.next();
			RoomClient rc = rpA.getVotedClients();
			if (rc.getStreamid().equals(streamid)){
				return true;
			}
		}
		return false;
	}
	
	public RoomPoll getVotes(){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient rc =this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			//get Poll
			return pollList.get(rc.getRoom_id());	
		} catch (Exception err) {
			log.error("[getVotes]",err);
		}
		return null;
	}
	
	public RoomPoll getPoll(){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient rc = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			//get Poll
			return pollList.get(rc.getRoom_id());
		} catch (Exception err) {
			log.error("[getPoll]",err);
		}
		return null;
	}
	
	public int checkHasVoted(){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient rc = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			//get Poll
			RoomPoll roomP = pollList.get(rc.getRoom_id());
			
			if (roomP!=null){
				log.debug("checkHasVoted: "+roomP.getPollQuestion());
				//Check if this user has already voted
				if(this.hasVoted(roomP,rc.getStreamid())){
					return -1;
				} else {
					return 1;
				}
			} else {
				return -2;
			}
		} catch (Exception err){
			log.error("checkHasVoted");
			log.error("[checkHasVoted]",err);
		}
		return 0;
	}
	
}
