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
package org.apache.openmeetings.web.room.menu;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.removeUserFromRoom;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.InvitationDialog;
import org.apache.openmeetings.web.common.OmButton;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.room.OmRedirectTimerBehavior;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.poll.CreatePollDialog;
import org.apache.openmeetings.web.room.poll.PollResultsDialog;
import org.apache.openmeetings.web.room.poll.VoteDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

public class RoomMenuPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final InvitationDialog invite;
	private final CreatePollDialog createPoll;
	private final VoteDialog vote;
	private final PollResultsDialog pollResults;
	private final SipDialerDialog sipDialer;
	private final MenuPanel menuPanel;
	private final StartSharingButton shareBtn;
	private final Label roomName;
	private static final FastDateFormat df = FastDateFormat.getInstance("dd.MM.yyyy HH:mm");
	private final OmButton askBtn = new OmButton("ask") {
		private static final long serialVersionUID = 1L;
		{
			setOutputMarkupPlaceholderTag(true);
			setVisible(false);
		}
		@Override
		public void onClick(AjaxRequestTarget target) {
			room.requestRight(target, Room.Right.moderator);
		}
	};
	private final RoomPanel room;
	private final RoomMenuItem exitMenuItem = new RoomMenuItem(Application.getString(308), Application.getString(309), "room menu exit") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			exit(target);
		}
	};
	private final RoomMenuItem filesMenu = new RoomMenuItem(Application.getString(245), null, false);
	private final RoomMenuItem actionsMenu = new RoomMenuItem(Application.getString(635), null, false);
	private final RoomMenuItem inviteMenuItem = new RoomMenuItem(Application.getString(213), Application.getString(1489), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			invite.updateModel(target);
			invite.open(target);
		}
	};
	private final RoomMenuItem shareMenuItem = new RoomMenuItem(Application.getString(239), Application.getString(1480), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			shareBtn.onClick(target);
		}
	};
	private final RoomMenuItem applyModerMenuItem = new RoomMenuItem(Application.getString(784), Application.getString(1481), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			askBtn.onClick(target);
		}
	};
	private final RoomMenuItem applyWbMenuItem = new RoomMenuItem(Application.getString(785), Application.getString(1492), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			RoomPanel.broadcast(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.requestRightWb, room.getClient().getUid()));
		}
	};
	private final RoomMenuItem applyAvMenuItem = new RoomMenuItem(Application.getString(786), Application.getString(1482), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			RoomPanel.broadcast(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.requestRightAv, room.getClient().getUid()));
		}
	};
	private final RoomMenuItem pollCreateMenuItem = new RoomMenuItem(Application.getString(24), Application.getString(1483), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			createPoll.updateModel(target);
			createPoll.open(target);
		}
	};
	private final RoomMenuItem pollVoteMenuItem = new RoomMenuItem(Application.getString(42), Application.getString(1485), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			RoomPoll rp = getBean(PollDao.class).getByRoom(room.getRoom().getId());
			if (rp != null) {
				vote.updateModel(target, rp);
				vote.open(target);
			}
		}
	};
	private final RoomMenuItem pollResultMenuItem = new RoomMenuItem(Application.getString(37), Application.getString(1484), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			pollResults.updateModel(target, room.getClient().hasRight(Room.Right.moderator));
			pollResults.open(target);
		}
	};
	private final RoomMenuItem sipDialerMenuItem = new RoomMenuItem(Application.getString(1447), Application.getString(1488), false) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClick(AjaxRequestTarget target) {
			sipDialer.open(target);
		}
	};

	public RoomMenuPanel(String id, final RoomPanel room) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		this.room = room;
		Room r = room.getRoom();
		setVisible(!r.isHidden(RoomElement.TopBar));
		add((menuPanel = new MenuPanel("menu", getMenu())).setVisible(isVisible()));
		add(askBtn);
		add((roomName = new Label("roomName", r.getName())).setOutputMarkupId(true));
		add(shareBtn = new StartSharingButton("share", room.getClient()));
		RoomInvitationForm rif = new RoomInvitationForm("form", room.getRoom().getId());
		add(invite = new InvitationDialog("invite", rif));
		rif.setDialog(invite);
		add(createPoll = new CreatePollDialog("createPoll", room.getRoom().getId()));
		add(vote = new VoteDialog("vote"));
		add(pollResults = new PollResultsDialog("pollResults", room.getRoom().getId()));
		add(sipDialer = new SipDialerDialog("sipDialer", room));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		askBtn.add(new AttributeAppender("title", getString("906")));
		Label demo = new Label("demo", Model.of(""));
		Room r = room.getRoom();
		add(demo.setVisible(r.isDemoRoom() && r.getDemoTime() != null && room.getRoom().getDemoTime().intValue() > 0));
		if (demo.isVisible()) {
			demo.add(new OmRedirectTimerBehavior(room.getRoom().getDemoTime().intValue(), "637") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimer(int remain) {
					getComponent().add(AttributeAppender.replace("title", getText("639", remain)));
				}
				
				@Override
				protected void onFinish(AjaxRequestTarget target) {
					exit(target);
				}
			});
		}
	}
	
	private List<IMenuItem> getMenu() {
		List<IMenuItem> menu = new ArrayList<>();
		exitMenuItem.setEnabled(false);
		exitMenuItem.setTop(true);
		menu.add(exitMenuItem);
		
		filesMenu.getItems().add(new RoomMenuItem(Application.getString(15), Application.getString(1479)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				room.getSidebar().showUpload(target);
			}
		});
		filesMenu.setTop(true);
		menu.add(filesMenu);
		
		actionsMenu.setTop(true);
		actionsMenu.getItems().add(inviteMenuItem);
		actionsMenu.getItems().add(shareMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(applyModerMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(applyWbMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(applyAvMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(pollCreateMenuItem);
		actionsMenu.getItems().add(pollResultMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(pollVoteMenuItem); //FIXME enable/disable
		actionsMenu.getItems().add(sipDialerMenuItem);
		//TODO seems need to be removed actionsMenu.getItems().add(new RoomMenuItem(Application.getString(1126), Application.getString(1490)));
		menu.add(actionsMenu);
		return menu;
	}
	
	public void update(IPartialPageRequestHandler handler) {
		if (!isVisible()) {
			return;
		}
		Room r = room.getRoom();
		PollDao pollDao = getBean(PollDao.class);
		boolean pollExists = pollDao.hasPoll(r.getId());
		User u = getBean(UserDao.class).get(getUserId());
		boolean notExternalUser = u.getType() != User.Type.external && u.getType() != User.Type.contact;
		exitMenuItem.setEnabled(notExternalUser);//TODO check this
		filesMenu.setEnabled(room.getSidebar().isShowFiles());
		boolean moder = room.getClient().hasRight(Room.Right.moderator);
		actionsMenu.setEnabled((moder &&!r.isHidden(RoomElement.ActionMenu)) || (!moder && r.isAllowUserQuestions()));
		inviteMenuItem.setEnabled(notExternalUser && moder);
		//TODO add check "sharing started"
		boolean shareVisible = room.screenShareAllowed();
		shareMenuItem.setEnabled(shareVisible);
		//FIXME TODO apply* should be enabled if moder is in room
		applyModerMenuItem.setEnabled(!moder);
		applyWbMenuItem.setEnabled(!room.getClient().hasRight(Room.Right.whiteBoard));
		applyAvMenuItem.setEnabled(!room.getClient().hasRight(Room.Right.audio) || !room.getClient().hasRight(Room.Right.video));
		pollCreateMenuItem.setEnabled(moder);
		pollVoteMenuItem.setEnabled(pollExists && notExternalUser && !pollDao.hasVoted(r.getId(), getUserId()));
		pollResultMenuItem.setEnabled(pollExists || pollDao.getArchived(r.getId()).size() > 0);
		sipDialerMenuItem.setEnabled(r.isSipEnabled() && getBean(ConfigurationDao.class).isSipEnabled());
		//TODO sip menus
		menuPanel.update(handler);
		//FIXME TODO askBtn should be visible if moder is in room
		StringBuilder roomClass = new StringBuilder("room name");
		StringBuilder roomTitle = new StringBuilder();
		if (room.getRecordingUser() != null) {
			ISessionManager sessMngr = getBean(ISessionManager.class);
			org.apache.openmeetings.db.entity.room.Client recUser = sessMngr.getClientByPublicSID(room.getRecordingUser(), null); //TODO check server
			if (recUser != null) {
				roomTitle.append(String.format("%s %s %s %s %s", getString("419")
						, recUser.getUsername(), recUser.getFirstname(), recUser.getLastname(), df.format(recUser.getConnectedSince())));
				roomClass.append(" screen");
			}
			org.apache.openmeetings.db.entity.room.Client pubUser = sessMngr.getClientByPublicSID(room.getPublishingUser(), null); //TODO check server
			if (pubUser != null) {
				if (recUser != null) {
					roomTitle.append('\n');
				}
				roomTitle.append(String.format("%s %s %s %s %s", getString("1504")
						, recUser.getUsername(), pubUser.getFirstname(), pubUser.getLastname(), "URL")); //TODO add URL
				roomClass.append(" screen");
			}
		}
		handler.add(roomName.add(AttributeAppender.replace("class", roomClass), AttributeAppender.replace("title", roomTitle)));
		handler.add(askBtn.setVisible(!moder && r.isAllowUserQuestions()));
		handler.add(shareBtn.setVisible(shareVisible));
	}

	public void pollCreated(IPartialPageRequestHandler handler) {
		RoomPoll rp = getBean(PollDao.class).getByRoom(room.getRoom().getId());
		if (rp != null) {
			vote.updateModel(handler, rp);
			vote.open(handler);
			update(handler);
		}
	}

	public void exit(IPartialPageRequestHandler handler) {
		if (WebSession.getRights().contains(User.Right.Dashboard)) {
			roomExit(room.getClient(), false);
			room.getMainPanel().updateContents(ROOMS_PUBLIC, handler);
		} else {
			String url = getBean(ConfigurationDao.class).getConfValue(CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY, String.class, "");
			if (Strings.isEmpty(url)) {
				url = getBean(ConfigurationDao.class).getConfValue(CONFIG_APPLICATION_BASE_URL, String.class, "");
			}
			throw new RedirectToUrlException(url);
		}
	}

	public static void roomExit(Client c) {
		roomExit(c, true);
	}
	
	public static void roomExit(Client c, boolean broadcast) {
		Long roomId = c.getRoomId();
		removeUserFromRoom(c);
		if (broadcast && roomId != null) {
			RoomPanel.broadcast(new RoomMessage(roomId, c.getUserId(), RoomMessage.Type.roomExit));
		}
	}
}
