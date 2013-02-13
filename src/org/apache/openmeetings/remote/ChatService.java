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

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.whiteboard.EmoticonsManager;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.stringhandlers.ChatString;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebawagner
 *
 */
public class ChatService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(ChatService.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private ISessionManager sessionManager = null;
	@Autowired
	private EmoticonsManager emoticonsManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private UserManager userManager;
	
	//the overall chat room is just another room
	private static final Long overallChatRoomName = new Long(-1);
	
	//number of items in the chat room history
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
	 * 
	 * @param newMessage
	 * @return - 1 in case of success, -1 otherwise
	 */
	@SuppressWarnings("unchecked")
	public int sendMessageWithClient(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();			
			log.debug("room_id: " + room_id);
			
			if (room_id == null) {
				return 1; //TODO weird
			}
			Long user_level = userManager.getUserLevelByID(currentClient.getUser_id());
			Room room = roomManager.getRoomById(user_level, room_id);
			@SuppressWarnings("rawtypes")
			ArrayList messageMap = (ArrayList) newMessage;
			// adding delimiter space, cause otherwise an emoticon in the last
			// string would not be found
			String messageText = messageMap.get(4) + " ";
			LinkedList<String[]> parsedStringObjects = ChatString.parseChatString(messageText, emoticonsManager.getEmotfilesList(), room.getAllowFontStyles());
			// log.error("parsedStringObjects"+parsedStringObjects.size());
			log.debug("size:" + messageMap.size());
			messageMap.add(parsedStringObjects);
			newMessage = messageMap;			

			boolean needModeration = Boolean.valueOf("" + messageMap.get(9));
			List<HashMap<String, Object>> myChatList = myChats.get(room_id);
			if (myChatList == null) myChatList = new LinkedList<HashMap<String, Object>>();
			
			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("message", newMessage);
			String publicSID = "" + messageMap.get(6);
			if (!publicSID.equals(currentClient.getPublicSID())) {
				hsm.put("client", sessionManager.getClientByPublicSID("" + messageMap.get(6), false, null));
				//need to remove unconfirmed chat message if any
				for (int i = myChatList.size() - 1; i > -1; --i) {
					Client msgClient = (Client)myChatList.get(i).get("client");
					@SuppressWarnings("rawtypes")
					List msgList = (List)myChatList.get(i).get("message");
					if (publicSID.equals(msgClient.getPublicSID())
						&& ("" + msgList.get(4)).equals(messageMap.get(4))
						&& ("" + msgList.get(1)).equals(messageMap.get(1))
						&& Boolean.valueOf("" + msgList.get(9))) {
						myChatList.remove(i);
						break;
					}
				}
				
			} else {
				// add server time
				messageMap.set(1, parseDateAsTimeString());
				hsm.put("client", currentClient);
			}

			if (myChatList.size() == chatRoomHistory) myChatList.remove(0);
			myChatList.add(hsm);
			myChats.put(room_id, myChatList);
			log.debug("SET CHATROOM: " + room_id);

			//broadcast to everybody in the room/domain
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
    			for (IConnection conn : conset) {
    				if (conn != null) {
    					if (conn instanceof IServiceCapableConnection) {
    						
    						Client rcl = this.sessionManager.getClientByStreamId(conn.getClient().getId(), null);
    						
    						if (rcl == null) {
    							continue;
    						}
    						if (rcl.getIsAVClient()) {
    							continue;
    						}
    						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						continue;
	    					}
    						if (needModeration && Boolean.TRUE != rcl.getIsMod() && Boolean.TRUE != rcl.getIsSuperModerator()) {
    							continue;
    						}
							((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
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

	//FIXME copy/past need to be removed
	@SuppressWarnings("unchecked")
	public int sendMessageWithClientByPublicSID(Object newMessage, String publicSID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();
			Long user_level = userManager.getUserLevelByID(currentClient.getUser_id());
			Room room = roomManager.getRoomById(user_level, room_id);
			log.debug("room_id: " + room_id);

			@SuppressWarnings("rawtypes")
			ArrayList messageMap = (ArrayList) newMessage;
			// adding delimiter space, cause otherwise an emoticon in the last
			// string would not be found
			String messageText = messageMap.get(4).toString() + " ";
			// add server time
			messageMap.set(1, parseDateAsTimeString());
			LinkedList<String[]> parsedStringObjects = ChatString.parseChatString(messageText, emoticonsManager.getEmotfilesList(), room.getAllowFontStyles());
			// log.error("parsedStringObjects"+parsedStringObjects.size());
			log.debug("size:" + messageMap.size());
			messageMap.add(parsedStringObjects);
			newMessage = messageMap;

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			// broadcast to everybody in the room/domain
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
    						IClient client = conn.getClient();
							if (SessionVariablesUtil.isScreenClient(client)) {
								// screen sharing clients do not receive events
								continue;
							} else if (SessionVariablesUtil.isAVClient(client)) {
								// AVClients or potential AVClients do not receive events
								continue;
							}

							if (SessionVariablesUtil.getPublicSID(client).equals(publicSID)
									|| SessionVariablesUtil.getPublicSID(client).equals(
											currentClient.getPublicSID())) {
								((IServiceCapableConnection) conn).invoke(
									"sendVarsToMessageWithClient",
										new Object[] { hsm }, this);
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[ChatService sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	public List<HashMap<String,Object>> clearChat() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();
			
			Long chatroom = room_id;
			log.debug("### GET CHATROOM: "+chatroom);
			
			List<HashMap<String,Object>> myChatList = myChats.get(chatroom);
			myChatList = new LinkedList<HashMap<String,Object>>();
			
			myChats.put(chatroom,myChatList);
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			
			scopeApplicationAdapter.syncMessageToCurrentScope("clearChatContent", hsm, true);
			
			return myChatList;
			
		} catch (Exception err) {
			log.error("[clearChat] ",err);
			return null;
		}
	}
	
	public List<HashMap<String,Object>> getRoomChatHistory() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();
			
			log.debug("GET CHATROOM: " + room_id);
			
			List<HashMap<String,Object>> myChatList = myChats.get(room_id);
			if (myChatList==null) myChatList = new LinkedList<HashMap<String,Object>>();
			
			if (Boolean.TRUE != currentClient.getIsMod() && Boolean.TRUE != currentClient.getIsSuperModerator()) {
				//current user is not moderator, chat history need to be filtered
				List<HashMap<String,Object>> tmpChatList = new LinkedList<HashMap<String,Object>>(myChatList);
				for (int i = tmpChatList.size() - 1; i > -1; --i) {
					@SuppressWarnings("rawtypes")
					List msgList = (List)tmpChatList.get(i).get("message");
					if (Boolean.valueOf("" + msgList.get(9))) { //needModeration
						tmpChatList.remove(i);
					}
				}
				myChatList = tmpChatList;
			}
			
			return myChatList;
		} catch (Exception err) {
			log.error("[getRoomChatHistory] ",err);
			return null;
		}
	}
	
	/**
	 * gets the chat history by string for non-conference-clients
	 * 
	 * @param room_id
	 * @return - chat history of the room given, null in case of exception
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
	
	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		log.error("resultReceived ChatService "+arg0);
	}
	
	/**
	 * sends a message to all connected users
	 * 
	 * @param newMessage
	 * @return - 1 in case of success, -1 otherwise
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int sendMessageToOverallChat(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager.getClientByStreamId(current.getClient().getId(), null);
			
			//log.error(newMessage.getClass().getName());
			ArrayList messageMap = (ArrayList) newMessage;
			//adding delimiter space, cause otherwise an emoticon in the last string would not be found
			String messageText = messageMap.get(4).toString()+" ";
			//log.error("messageText"+messageText);
			//add server time
			messageMap.set(1,parseDateAsTimeString());
			LinkedList<String[]> parsedStringObjects = ChatString.parseChatString(messageText, emoticonsManager.getEmotfilesList(), true);
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
			
			scopeApplicationAdapter.syncMessageToCurrentScope("sendVarsToOverallChat", hsm, true);
			
		} catch (Exception err) {
			log.error("[ChatService sendMessageToOverallChat] ",err);
			return -1;
		}
		return 1;
	}
	
	
	/**
	 * gets the chat history of overallChat
	 * 
	 * @return - overall chat history
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
	 * @return - all messages being cleaned, null in case of error
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
			LinkedList<LinkedList<String>> allEmotes = emoticonsManager.getEmotfilesList();
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
	
	public LinkedHashMap<String,LinkedList<Client>> getChatOverallUsers(){
		try {
			LinkedHashMap<String,LinkedList<Client>> clientList = new LinkedHashMap<String,LinkedList<Client>>();
			LinkedList<Client> guestList = new LinkedList<Client>();
			LinkedList<Client> overallList = new LinkedList<Client>();
			
			for (Client rcl : sessionManager.getClients()) {
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
