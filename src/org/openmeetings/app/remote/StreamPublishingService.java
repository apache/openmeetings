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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.conference.session.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.red5.client.net.rtmp.ClientExceptionHandler;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IBasicScope;
import org.red5.server.api.scope.IBroadcastScope;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IBroadcastStreamService;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.stream.StreamingProxy;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class StreamPublishingService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(
			StreamPublishingService.class, OpenmeetingsVariables.webAppRootKey);
	//we supposing only rooms streams are published. So the key is room_id, the value is the list of the proxies
    private Map<Long, Map<String, StreamingProxy>> streamingProxyMap = new HashMap<Long, Map<String, StreamingProxy>>();

	@Autowired
	private ClientListManager clientListManager;
    
	public IBroadcastScope getBroadcastScope(IScope scope, String name) {
		IBasicScope basicScope = scope.getBasicScope(ScopeType.BROADCAST, name);
		if (!(basicScope instanceof IBroadcastScope)) {
			return null;
		} else {
			return (IBroadcastScope) basicScope;
		}
	}

	public boolean streamPublishingStart(IBroadcastStreamService application, String host, String app, String id) {
		boolean result = false;
		try {
			log.debug(":: streamPublishingStart ::");

			IConnection current = Red5.getConnectionLocal();
			RoomClient rc = clientListManager.getClientByStreamId(current.getClient().getId());
			Long roomId = rc.getRoom_id();
			
			// Update Client and set Flag
			rc.setStreamPublishStarted(true);
			clientListManager.updateClientByStreamId(current.getClient().getId(), rc);

			streamingProxyMap.put(roomId, new HashMap<String, StreamingProxy>());
			// get all stream and start publish them
			for (Set<IConnection> conset : current.getScope().getConnections()) {
				for (IConnection conn : conset) {
					if (conn != null && conn instanceof IServiceCapableConnection) {
						RoomClient rcl = clientListManager.getClientByStreamId(conn.getClient().getId());

						// Send every user a notification that the stream publishing is started
						if (!rcl.getIsAVClient()) {
							((IServiceCapableConnection)conn).invoke(
								"startedPublishing",
								new Object[] { rc, host, app, id }, this);
						}

						String bcId = getBroadcastId(rcl);
						if (bcId != null) {
							result &= startProxy(application, roomId, bcId, conn, host, app, id);
							//clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[streamPublishingStart]", err);
		}
		return result;
	}

	private String getBroadcastId(RoomClient rc) {
		String result = null;
		if (rc.getIsScreenClient() && rc.getFlvRecordingId() != null && rc.isScreenPublishStarted()) {
			result = rc.getStreamPublishName();
		} else if (rc.getIsAVClient() && !rc.getAvsettings().equals("n")) {
			result = "" + rc.getBroadCastID();
		}
		return result;
	}
	
	private boolean startProxy(IBroadcastStreamService application, Long roomId, String streamName, final IConnection conn, String host, String app, String id) {
    	final boolean[] result = {true};
    	
        IScope scope = conn.getScope();
        IBroadcastStream stream = application.getBroadcastStream(scope, streamName);
        IBroadcastScope bsScope = getBroadcastScope(scope, streamName);
        final StreamingProxy proxy = new StreamingProxy();
        proxy.setHost(host);
        proxy.setApp(app);
        proxy.setPort(1935);
        proxy.init();
        proxy.setExceptionHandler(new ClientExceptionHandler() {
			public void handleException(Throwable throwable) {
				result[0] = false;
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("stopPublishing", true);
				params.put("error", throwable.getMessage());
				((IServiceCapableConnection)conn).invoke(
					"screenSharerAction"
					, new Object[] { params }
					, StreamPublishingService.this);
			}
		});
        bsScope.subscribe(proxy, null);
        proxy.start(id, "live", null);
        streamingProxyMap.get(roomId).put(streamName, proxy);
        stream.addStreamListener(new IStreamListener() {
			public void packetReceived(IBroadcastStream stream, IStreamPacket packet) {
				try {
					RTMPMessage m = RTMPMessage.build((IRTMPEvent)packet, packet.getTimestamp());
			        proxy.pushMessage(null, m);
				} catch (Exception e) {
					log.error("Exception while sending proxy message", e);
				}
			}
		});
		return result[0];
    }
    
    public void streamPublishingStop(IBroadcastStreamService application) {
		IConnection current = Red5.getConnectionLocal();
		RoomClient rc = clientListManager.getClientByStreamId(current.getClient().getId());
		Long roomId = rc.getRoom_id();
		
		if (roomId != null && streamingProxyMap.containsKey(roomId)) {
			Map<String, StreamingProxy> proxies = streamingProxyMap.remove(roomId);
			
			for (Set<IConnection> conset : current.getScope().getConnections()) {
				for (IConnection conn : conset) {
					if (conn != null && conn instanceof IServiceCapableConnection) {
						RoomClient rcl = clientListManager.getClientByStreamId(conn.getClient().getId());
						
						String bcId = getBroadcastId(rcl);
						if (bcId != null) {
					        IScope scope = conn.getScope();
					        IBroadcastStream stream = application.getBroadcastStream(scope, bcId);
							StreamingProxy proxy = proxies.remove(bcId);
							if (proxy != null) {
								proxy.stop();
								IBroadcastScope bsScope = getBroadcastScope(scope, stream.getPublishedName());
								if (bsScope != null) {
									bsScope.unsubscribe(proxy);
								}
							}
						}
					}
				}
			}
			if (!proxies.isEmpty()) {
				//FIXME check if proxies is not empty !!!!!
				log.error("!!!!!!!!!!!!!!!!!!!!!!!!! dongling StreamingProxy !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}
    }

	public void resultReceived(IPendingServiceCall call) {
		// TODO Auto-generated method stub
	}
}
