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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.sidebar.RoomSidebar.PARAM_ACTION;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.io.Serializable;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.web.app.QuickPollManager;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.poll.CreatePollDialog;
import org.apache.openmeetings.web.room.poll.PollResultsDialog;
import org.apache.openmeetings.web.room.poll.VoteDialog;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PollsSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(PollsSubMenu.class, getWebAppRootKey());
	private static final String FUNC_QPOLL_ACTION = "quickPollAction";
	private static final String PARAM_VOTE = "vote";
	private static final String ACTION_CLOSE = "close";
	private final RoomPanel room;
	private final RoomMenuPanel mp;
	private final CreatePollDialog createPoll;
	private final VoteDialog vote;
	private final PollResultsDialog pollResults;
	private RoomMenuItem pollsMenu;
	private RoomMenuItem pollQuickMenuItem;
	private RoomMenuItem pollCreateMenuItem;
	private RoomMenuItem pollVoteMenuItem;
	private RoomMenuItem pollResultMenuItem;
	private final AbstractDefaultAjaxBehavior quickPollAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				if (room.getRoom().isHidden(RoomElement.PollMenu)) {
					return;
				}
				String action = mp.getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString();
				QuickPollManager qm = getBean(QuickPollManager.class);
				Client c = room.getClient();
				if (ACTION_CLOSE.equals(action)) {
					qm.close(c);
				} else if (PARAM_VOTE.equals(action)) {
					boolean vote = mp.getRequest().getRequestParameters().getParameterValue(PARAM_VOTE).toBoolean();
					qm.vote(c, vote);
				}
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'quickPollAction'", e);
			}
		}
	};
	private final boolean visible;

	public PollsSubMenu(final RoomPanel room, final RoomMenuPanel mp) {
		this.room = room;
		this.mp = mp;
		mp.add(createPoll = new CreatePollDialog("createPoll", room.getRoom().getId()));
		mp.add(vote = new VoteDialog("vote"));
		mp.add(pollResults = new PollResultsDialog("pollResults", createPoll, room.getRoom().getId()));
		visible = !room.getRoom().isHidden(RoomElement.PollMenu);
	}

	public void init() {
		pollsMenu = new RoomMenuItem(mp.getString("menu.polls"), null, false);
		pollQuickMenuItem = new RoomMenuItem(mp.getString("menu.polls.quick.title"), mp.getString("menu.polls.quick.descr"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getBean(QuickPollManager.class).start(room.getClient());
			}
		};
		pollCreateMenuItem = new RoomMenuItem(mp.getString("24"), mp.getString("1483"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				createPoll.updateModel(target);
				createPoll.open(target);
			}
		};
		pollVoteMenuItem = new RoomMenuItem(mp.getString("32"), mp.getString("1485"), false) {
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
		pollResultMenuItem = new RoomMenuItem(mp.getString("37"), mp.getString("1484"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pollResults.updateModel(target, room.getClient().hasRight(Room.Right.moderator));
				pollResults.open(target);
			}
		};
		mp.add(quickPollAction);
	}

	RoomMenuItem getMenu() {
		pollsMenu.setTop(true);
		pollsMenu.getItems().add(pollQuickMenuItem);
		pollsMenu.getItems().add(pollCreateMenuItem);
		pollsMenu.getItems().add(pollResultMenuItem);
		pollsMenu.getItems().add(pollVoteMenuItem);
		return pollsMenu;
	}

	public void update(final boolean moder, final boolean notExternalUser, final Room r) {
		if (!visible) {
			return;
		}
		PollDao pollDao = getBean(PollDao.class);
		boolean pollExists = pollDao.hasPoll(r.getId());
		pollsMenu.setEnabled((moder && visible) || (!moder && r.isAllowUserQuestions()));
		pollQuickMenuItem.setEnabled(room.getClient().hasRight(Room.Right.presenter) && !getBean(QuickPollManager.class).isStarted(r.getId()));
		pollCreateMenuItem.setEnabled(moder);
		pollVoteMenuItem.setEnabled(pollExists && notExternalUser && !pollDao.hasVoted(r.getId(), getUserId()));
		pollResultMenuItem.setEnabled(pollExists || !pollDao.getArchived(r.getId()).isEmpty());
	}

	public void updatePoll(IPartialPageRequestHandler handler, Long createdBy) {
		RoomPoll rp = getBean(PollDao.class).getByRoom(room.getRoom().getId());
		if (rp != null) {
			vote.updateModel(handler, rp);
		} else {
			vote.close(handler, null);
		}
		if (createdBy != null && !getUserId().equals(createdBy)) {
			vote.open(handler);
		}
		if (pollResults.isOpened()) {
			pollResults.updateModel(handler, room.getClient().hasRight(Room.Right.moderator));
		}
	}

	public void renderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_QPOLL_ACTION, quickPollAction, explicit(PARAM_ACTION), explicit(PARAM_VOTE))));
	}

	public boolean isVisible() {
		return visible;
	}
}
