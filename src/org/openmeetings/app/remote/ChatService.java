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
package org.openmeetings.app.remote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.conference.session.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.EmoticonsManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.stringhandlers.ChatString;
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
 * @author sebastianwagner
 *
 */
public class ChatService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(ChatService.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private ClientListManager clientListManager = null;
	@Autowired
	private EmoticonsManager emoticonsManager = null;
	
	//the overall chatroom is jsut another room
	private static final Long overallChatRoomName = new Long(-1);
	
	//number of items in the Chatroom history
	private static final int chatRoomHistory = 50;
	
	private static LinkedHashMap<Long,List<HashMap<String,Object>>> myChats = new LinkedHashMap<Long,List<HashMap<String,Object>>>();
	
	private String parseDateAsTimeString() {
		Calendar cal=Calendar.getInstance();
		
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);

		String str_h,str_m;
		if (h<10) 
			str_h = "0"+Integer.toString(h);
		else
			str_h = Integer.toString(h);

		if (m<10) 
			str_m = "0"+Integer.toString(m);
		else
			str_m = Integer.toString(m);
		
		return str_h+':'+str_m;
	}

	/**
	 * sends a Chat-Message
	 * to all members of the Chatroom
	 * and all additional users (waitng for a free entry for example)
	 * @param newMessage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int sendMessageWithClient(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();
			
			log.debug("room_id: "+room_id);
			log.debug("currentClient.getIsChatNotification(): "+currentClient.getIsChatNotification());
			if (currentClient.getIsChatNotification()){
				room_id = currentClient.getChatUserRoomId();
			}
			
			//log.error(newMessage.getClass().getName());
			ArrayList messageMap = (ArrayList) newMessage;
			//adding delimiter space, cause otherwise an emoticon in the last string would not be found
			String messageText = messageMap.get(4).toString()+" ";
			//log.error("messageText"+messageText);
			//add server time
			messageMap.set(1,parseDateAsTimeString());
			LinkedList<String[]> parsedStringObjects = ChatString.getInstance().parseChatString(messageText);
			//log.error("parsedStringObjects"+parsedStringObjects.size());
			log.debug("size:"+messageMap.size());
			messageMap.add(parsedStringObjects);
			newMessage = messageMap;			
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			List<HashMap<String,Object>> myChatList = myChats.get(room_id);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();
			
			if (myChatList.size()==chatRoomHistory) myChatList.remove(0);
			myChatList.add(hsm);
			myChats.put(room_id,myChatList);
			
			log.debug("SET CHATROOM: "+room_id);
			
			//broadcast to everybody in the room/domain
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
    			for (IConnection conn : conset) {
    				if (conn != null) {
    					if (conn instanceof IServiceCapableConnection) {
    						
    						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
    						
    						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
	    						log.debug("*..*idremote room_id: " + room_id);
	    						log.debug("*..*my idstreamid room_id: " + rcl.getRoom_id());
	    						if (room_id!=null && room_id.equals(rcl.getRoom_id())) {
	    							((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
	    							log.debug("sending sendVarsToMessageWithClient to " + conn);
	    						} else if (rcl.getIsChatNotification()) {
	    							if (room_id.equals(rcl.getChatUserRoomId()) && room_id != null) {
	    								((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
	    							}
	    						}
	    					}
    						
	    			 	}
	    			}
    			}
			}
    					
		} catch (Exception err) {
			log.error("[ChatService sendMessageWithClient] ",err);
			return -1;
		}
		return 1;
	}
	
	public List<HashMap<String,Object>> clearChat() {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();
			
			Long chatroom = room_id;
			log.debug("### GET CHATROOM: "+chatroom);
			
			List<HashMap<String,Object>> myChatList = myChats.get(chatroom);
			myChatList = new LinkedList<HashMap<String,Object>>();
			
			myChats.put(chatroom,myChatList);
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			
			//broadcast to everybody in the room/domain
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
    			for (IConnection conn : conset) {
    				if (conn != null) {
    					if (conn instanceof IServiceCapableConnection) {
			
    						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
    						if (!rcl.getIsScreenClient()) {
	    						log.debug("*..*idremote: " + rcl.getStreamid());
	    						log.debug("*..*my idstreamid: " + currentClient.getStreamid());
	    						((IServiceCapableConnection) conn).invoke("clearChatContent",new Object[] { hsm }, this);
    						}
    					}
    				}
				}
			}		
			
			return myChatList;
			
		} catch (Exception err) {
			log.error("[clearChat] ",err);
			return null;
		}
	}
	
	public List<HashMap<String,Object>> getRoomChatHistory() {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();
			
			Long chatroom = room_id;
			log.debug("GET CHATROOM: "+chatroom);
			
			List<HashMap<String,Object>> myChatList = myChats.get(chatroom);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();	
			
			return myChatList;
		} catch (Exception err) {
			log.error("[getRoomChatHistory] ",err);
			return null;
		}
	}
	
	/**
	 * gets the chat history by string for non-conference-clients
	 * @param roomname
	 * @param orgdomain
	 * @return
	 */
	public List<HashMap<String,Object>> getRoomChatHistoryByString(Long room_id) {
		try {
			
			Long chatroom = room_id;
			log.debug("GET CHATROOM: "+chatroom);
			
			List<HashMap<String,Object>> myChatList = myChats.get(chatroom);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();	
			
			return myChatList;
		} catch (Exception err) {
			log.error("[getRoomChatHistory] ",err);
			return null;
		}
	}	
	
	/**
	 * adds a Client to the additional List of Users to Chat
	 * @param userroom
	 * @param room_id
	 * @param orgdomain
	 * @return
	 */
	public Long addClientToChatNotification(Long room_id){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());					
			String streamid = currentClient.getStreamid();
			
			currentClient.setIsChatNotification(true);
			currentClient.setChatUserRoomId(room_id);
			
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
		} catch (Exception err) {
			log.error("[addClientToCahtNotification]",err);
		}
		return new Long(-1);
	} 
	
	/**
	 * remove's a Client from the additional List of users to chat
	 * @param userroom
	 * @param room_id
	 * @param orgdomain
	 * @return
	 */
	public Long removeClientFromChatNotification(){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());					
			String streamid = currentClient.getStreamid();
			
			currentClient.setIsChatNotification(false);
			currentClient.setChatUserRoomId(null);
			
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
		} catch (Exception err) {
			log.error("[addClientToCahtNotification]",err);
		}
		return new Long(-1);
	}
	

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		log.error("resultReceived ChatService "+arg0);
	}
	
	/**
	 * sends a message to all connected users
	 * @param SID
	 * @param newMessage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int sendMessageToOverallChat(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			//log.error(newMessage.getClass().getName());
			ArrayList messageMap = (ArrayList) newMessage;
			//adding delimiter space, cause otherwise an emoticon in the last string would not be found
			String messageText = messageMap.get(4).toString()+" ";
			//log.error("messageText"+messageText);
			//add server time
			messageMap.set(1,parseDateAsTimeString());
			LinkedList<String[]> parsedStringObjects = ChatString.getInstance().parseChatString(messageText);
			//log.error("parsedStringObjects"+parsedStringObjects.size());
			log.debug("size:" + messageMap.size());
			messageMap.add(parsedStringObjects);
			newMessage = messageMap;
			
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			List<HashMap<String,Object>> myChatList = myChats.get(overallChatRoomName);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();
			
			if (myChatList.size()==chatRoomHistory) myChatList.remove(0);
			myChatList.add(hsm);
			myChats.put(overallChatRoomName,myChatList);
			
			log.debug("SET CHATROOM: "+overallChatRoomName);
			
			//broadcast to everybody in the room/domain
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
    			for (IConnection conn : conset) {
    				if (conn != null) {
    					if (conn instanceof IServiceCapableConnection) {
			
    						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
    						if (!rcl.getIsScreenClient()) {
	    						log.debug("*..*idremote: " + rcl.getStreamid());
	    						log.debug("*..*my idstreamid: " + currentClient.getStreamid());
	    						((IServiceCapableConnection) conn).invoke("sendVarsToOverallChat",new Object[] { hsm }, this);
    						}
    					}
    				}
				}
			}		
			
		} catch (Exception err) {
			log.error("[ChatService sendMessageToOverallChat] ",err);
			return -1;
		}
		return 1;
	}
	
	
	/**
	 * gets the chat history of overallChat
	 * @param roomname
	 * @param orgdomain
	 * @return
	 */
	public List<HashMap<String,Object>> getOverallChatHistory() {
		try {
			
			List<HashMap<String,Object>> myChatList = myChats.get(overallChatRoomName);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();	
			
			return myChatList;
		} catch (Exception err) {
			log.error("[getRoomChatHistory] ",err);
			return null;
		}
	}	
	
	/**
	 * clear the overallChat history
	 * @return
	 */
	public List<HashMap<String,Object>> clearOverallChat() {
		try {
			
			List<HashMap<String,Object>> myChatList = myChats.get(overallChatRoomName);
			myChatList = new LinkedList<HashMap<String,Object>>();
			
			myChats.put(overallChatRoomName,myChatList);
			
			//Send event to clear to all participants
			Map<Integer,String> newMessage = new HashMap<Integer,String>();
			newMessage.put(0, "clearOverallChatHistory");
			scopeApplicationAdapter.sendMessageToMembers(newMessage);
			
			return myChatList;
			
		} catch (Exception err) {
			log.error("[clearChat] ",err);
			return null;
		}
	}	
	
	public LinkedList<LinkedList<String>> getAllPublicEmoticons(){
		try {
			LinkedList<LinkedList<String>> publicemotes = new LinkedList<LinkedList<String>>();
			LinkedList<LinkedList<String>> allEmotes = EmoticonsManager.getEmotfilesList();
			for (Iterator<LinkedList<String>> iter = allEmotes.iterator();iter.hasNext();){
				LinkedList<String> emot = iter.next();
				LinkedList<String> emotPub = new LinkedList<String>();
				if (emot.get((emot.size()-1)).equals("y")){
					emotPub.add(emot.get(0));
					emotPub.add(emot.get(1).replace("\\", ""));
					if (emot.size()>4) {
						emotPub.add(emot.get(2).replace("\\", ""));
						emotPub.add(emot.get(3));
						emotPub.add(emot.get(4));
					} else {
						emotPub.add(emot.get(2));
						emotPub.add(emot.get(3));
					}
					publicemotes.add(emotPub);
				}
			}
			return publicemotes;
		} catch (Exception err) {
			log.error("[getAllPublicEmoticons] ",err);
			return null;
		}
	}
	
	public LinkedHashMap<String,LinkedList<RoomClient>> getChatOverallUsers(){
		try {
			LinkedHashMap<String,LinkedList<RoomClient>> clientList = new LinkedHashMap<String,LinkedList<RoomClient>>();
			LinkedList<RoomClient> guestList = new LinkedList<RoomClient>();
			LinkedList<RoomClient> overallList = new LinkedList<RoomClient>();
			
			for (RoomClient rcl : clientListManager.getAllClients()) {
				if (rcl.getUser_id()==null || rcl.getUser_id()<=0) {
					guestList.add(rcl);
				} else {
					overallList.add(rcl);
				}
			}
			
			clientList.put("guestList", guestList); 
			clientList.put("overallList", overallList); 
			return clientList;
		} catch (Exception err) {
			log.error("[getChatOverallUsers]",err);
		}
		return null;
	}
	
}
