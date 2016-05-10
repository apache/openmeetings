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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getSid;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomBroadcaster.getClient;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.core.session.SessionManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.poll.CreatePollDialog;
import org.apache.openmeetings.web.room.poll.PollResultsDialog;
import org.apache.openmeetings.web.room.poll.VoteDialog;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final String WICKET_ROOM_ID = "wicketroomid";
	public static final String PARAM_PUBLIC_SID = "publicSid";
	public static final String PARAM_URL = "url";
	public static final String SWF_TYPE_NETWORK = "network";
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, webAppRootKey);
	private final InvitationDialog invite;
	private final CreatePollDialog createPoll;
	private final VoteDialog vote;
	private final PollResultsDialog pollResults;
	private final StartSharingEventBehavior startSharing;
	private Long roomId = null;
	
	public RoomPanel(String id) {
		this(id, new PageParameters());
	}
	
	private String getFlashFile(StringValue type) {
		String fmt = SWF_TYPE_NETWORK.equals(type.toString()) ? "networktesting%s.swf10.swf" : "main%s.swf11.swf";
		return String.format(fmt, DEVELOPMENT == getApplication().getConfigurationType() ? "debug" : "");
	}
	
	private static PageParameters addServer(PageParameters pp, Server s) {
		return pp.add("protocol", s.getProtocol()).add("host", s.getAddress()).add("port", s.getPort()).add("context", s.getWebapp());
	}
	
	public static PageParameters addServer(Long roomId, boolean addBasic) {
		PageParameters pp = new PageParameters();
		if (addBasic) {
			pp.add("wicketsid", getSid()).add(WICKET_ROOM_ID, roomId).add("language", getLanguage());
		}
		List<Server> serverList = getBean(ServerDao.class).getActiveServers();

		long minimum = -1;
		Server result = null;
		HashMap<Server, List<Long>> activeRoomsMap = new HashMap<Server, List<Long>>();
		for (Server server : serverList) {
			List<Long> roomIds = getBean(SessionManager.class).getActiveRoomIdsByServer(server);
			if (roomIds.contains(roomId)) {
				// if the room is already opened on a server, redirect the user to that one,
				log.debug("Room is already opened on a server " + server.getAddress());
				return addServer(pp, server);
			}
			activeRoomsMap.put(server, roomIds);
		}
		for (Map.Entry<Server, List<Long>> entry : activeRoomsMap.entrySet()) {
			List<Long> roomIds = entry.getValue();
			long capacity = getBean(RoomDao.class).getRoomsCapacityByIds(roomIds);
			if (minimum < 0 || capacity < minimum) {
				minimum = capacity;
				result = entry.getKey();
			}
			log.debug("Checking server: " + entry.getKey() + " Number of rooms " + roomIds.size() + " RoomIds: "
					+ roomIds + " max(Sum): " + capacity);
		}
		return result == null ? pp : addServer(pp, result);
	}

	public RoomPanel(String id, Long roomId) {
		this(id, addServer(roomId, true));
	}
	
	public RoomPanel(String id, PageParameters pp) {
		super(id);
		//OK let's find the room
		try {
			StringValue room = pp.get(WICKET_ROOM_ID);
			StringValue secureHash = pp.get(WebSession.SECURE_HASH);
			StringValue invitationHash = pp.get(WebSession.INVITATION_HASH);
			if (!room.isEmpty()) {
				roomId = room.toLongObject();
			} else if (!secureHash.isEmpty()) {
				if (WebSession.get().signIn(secureHash.toString(), false)) {
					SOAPLogin soapLogin = getBean(SOAPLoginDao.class).get(secureHash.toString());
					roomId = soapLogin.getRoomId();
					pp = pp.mergeWith(RoomPanel.addServer(roomId, false));
				}
				//TODO access denied
			} else if (!invitationHash.isEmpty()) {
				roomId = getBean(InvitationDao.class).getInvitationByHashCode(invitationHash.toString(), true).getRoom().getId();
			}
		} catch (Exception e) {
			//no-op
		}
		PageParameters spp = new PageParameters(pp);
		if (roomId != null) {
			spp.mergeWith(new PageParameters().add(WICKET_ROOM_ID, roomId));
		}
		String swf = getFlashFile(pp.get("swf")) + new PageParametersEncoder().encodePageParameters(spp);
		add(new Label("init", String.format("initSwf('%s');", swf)).setEscapeModelStrings(false));
		add(new AbstractAjaxTimerBehavior(Duration.minutes(5)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				getBean(SessiondataDao.class).checkSession(WebSession.getSid()); //keep SID alive
			}
		});
		add(invite = new InvitationDialog("invite", roomId));
		add(createPoll = new CreatePollDialog("createPoll", roomId));
		add(vote = new VoteDialog("vote", roomId));
		add(pollResults = new PollResultsDialog("pollResults", roomId));
		add(startSharing = new StartSharingEventBehavior(roomId));
		if (roomId != null && roomId.longValue() > 0) {
			add(new AbstractDefaultAjaxBehavior() {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void respond(AjaxRequestTarget target) {
					invite.updateModel(target);
					invite.open(target);
				}
				
				@Override
				public void renderHead(Component component, IHeaderResponse response) {
					super.renderHead(component, response);
					response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("openInvitation", this), "openInvitation")));
				}
			});
			add(new AbstractDefaultAjaxBehavior() {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void respond(AjaxRequestTarget target) {
					String publicSid = getPublicSid();
					Client c = getClient(publicSid);
					if (c != null && c.getIsMod()) {
						createPoll.updateModel(target, publicSid);
						createPoll.open(target);
					}
				}
				
				@Override
				public void renderHead(Component component, IHeaderResponse response) {
					super.renderHead(component, response);
					response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("createPoll", this, explicit(PARAM_PUBLIC_SID)), "createPoll")));
				}
			});
			add(new AbstractDefaultAjaxBehavior() {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void respond(AjaxRequestTarget target) {
					Client c = getClient(getPublicSid());
					if (c != null) {
						pollResults.updateModel(target, c.getIsMod());
						pollResults.open(target);
					}
				}
				
				@Override
				public void renderHead(Component component, IHeaderResponse response) {
					super.renderHead(component, response);
					response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("pollResults", this, explicit(PARAM_PUBLIC_SID)), "pollResults")));
				}
			});
			add(new AbstractDefaultAjaxBehavior() {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void respond(AjaxRequestTarget target) {
					if (getBean(PollDao.class).hasPoll(roomId) && !getBean(PollDao.class).hasVoted(roomId, getUserId()) && getClient(getPublicSid()) != null) {
						vote.updateModel(target);
						vote.open(target);
					}
				}
				
				@Override
				public void renderHead(Component component, IHeaderResponse response) {
					super.renderHead(component, response);
					response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("vote", this, explicit(PARAM_PUBLIC_SID)), "vote")));
				}
			});
			add(new AbstractDefaultAjaxBehavior() {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void respond(AjaxRequestTarget target) {
					startSharing.respond(target);
				}
				
				@Override
				public void renderHead(Component component, IHeaderResponse response) {
					super.renderHead(component, response);
					response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("startSharing", this, explicit(PARAM_PUBLIC_SID), explicit(PARAM_URL)), "startSharing")));
				}
			});
		}
	}

	public static boolean isModerator(long userId, long roomId) {
		for (org.apache.openmeetings.web.app.Client c : getRoomUsers(roomId)) {
			if (c.getUserId() == userId && c.hasRight(org.apache.openmeetings.web.app.Client.Right.moderator)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		handler.add(getMainPage().getHeader().setVisible(false), getMainPage().getMenu().setVisible(false)
				, getMainPage().getTopLinks().setVisible(false));
		//handler.appendJavaScript("roomLoad();");
	}
	
	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(RoomPanel.class, "swf-functions.js");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/history.js")));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/openmeetings_functions.js")));
		response.render(new PriorityHeaderItem(CssHeaderItem.forUrl("css/history.css")));
		//FIXME TODO ugly HACK
		if (WebSession.get().getClientInfo().getProperties().isBrowserMozillaFirefox()) {
			response.render(new PriorityHeaderItem(CssHeaderItem.forCSS(".ui-widget-overlay{opacity: 1 !important;}", "linux-ff-veil-hack")));
		}
	}
	
	private String getPublicSid() {
		return getRequest().getRequestParameters().getParameterValue(PARAM_PUBLIC_SID).toString();
	}
}
