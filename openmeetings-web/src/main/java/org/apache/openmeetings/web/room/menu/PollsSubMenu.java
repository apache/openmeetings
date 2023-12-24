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
import org.apache.openmeetings.web.common.menu.OmMenuItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.poll.CreatePollDialog;
import org.apache.openmeetings.web.room.poll.PollResultsDialog;
import org.apache.openmeetings.web.room.poll.VoteDialog;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class PollsSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PollsSubMenu.class);
	private static final String FUNC_QPOLL_ACTION = "quickPollAction";
	private static final String PARAM_VOTE = "vote";
	private static final String ACTION_CLOSE = "close";
	private static final String ACTION_OPEN = "open";
	private final RoomPanel room;
	private final RoomMenuPanel mp;
	private final CreatePollDialog createPoll;
	private final VoteDialog vote;
	private final PollResultsDialog pollResults;
	private OmMenuItem pollsMenu;
	private OmMenuItem pollQuickMenuItem;
	private OmMenuItem pollCreateMenuItem;
	private OmMenuItem pollVoteMenuItem;
	private OmMenuItem pollResultMenuItem;
	private final AbstractDefaultAjaxBehavior quickPollAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				if (room.getRoom().isHidden(RoomElement.POLL_MENU)) {
					return;
				}
				String action = mp.getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString();
				Client c = room.getClient();
				if (ACTION_OPEN.equals(action)) {
					qpollManager.start(room.getClient());
				} else if (ACTION_CLOSE.equals(action)) {
					qpollManager.close(c);
				} else if (PARAM_VOTE.equals(action)) {
					final boolean curVote = mp.getRequest().getRequestParameters().getParameterValue(PARAM_VOTE).toBoolean();
					qpollManager.vote(c, curVote);
				}
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'quickPollAction'", e);
			}
		}
	};
	private final boolean visible;

	@Inject
	private QuickPollManager qpollManager;
	@Inject
	private PollDao pollDao;

	public PollsSubMenu(final RoomPanel room, final RoomMenuPanel mp) {
		Injector.get().inject(this);
		this.room = room;
		this.mp = mp;
		mp.add(createPoll = new CreatePollDialog("createPoll", room.getRoom().getId()));
		mp.add(vote = new VoteDialog("vote"));
		mp.add(pollResults = new PollResultsDialog("pollResults", createPoll, room.getRoom().getId()));
		visible = !room.getRoom().isHidden(RoomElement.POLL_MENU);
	}

	public void init() {
		pollsMenu = new OmMenuItem(mp.getString("menu.polls"), null, false);
		pollQuickMenuItem = new OmMenuItem(mp.getString("menu.polls.quick.title"), mp.getString("menu.polls.quick.descr"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				qpollManager.start(room.getClient());
			}
		};
		pollCreateMenuItem = new OmMenuItem(mp.getString("24"), mp.getString("1483"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				createPoll.updateModel(target);
				createPoll.show(target);
			}
		};
		pollVoteMenuItem = new OmMenuItem(mp.getString("32"), mp.getString("1485"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				RoomPoll rp = pollDao.getByRoom(room.getRoom().getId());
				if (rp != null) {
					vote.updateModel(target, rp);
					vote.show(target);
				}
			}
		};
		pollResultMenuItem = new OmMenuItem(mp.getString("37"), mp.getString("1484"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				pollResults.updateModel(target, true, room.getClient().hasRight(Room.Right.MODERATOR));
				pollResults.show(target);
			}
		};
		mp.add(quickPollAction);
	}

	OmMenuItem getMenu() {
		pollsMenu
			.add(pollQuickMenuItem)
			.add(pollCreateMenuItem)
			.add(pollResultMenuItem)
			.add(pollVoteMenuItem);
		return pollsMenu;
	}

	public void update(final boolean moder, final boolean notExternalUser, final Room r) {
		if (!visible) {
			return;
		}
		boolean pollExists = pollDao.hasPoll(r.getId());
		pollsMenu.setVisible(moder || r.isAllowUserQuestions());
		pollQuickMenuItem.setVisible(room.getClient().hasRight(Room.Right.PRESENTER) && !qpollManager.isStarted(r.getId()));
		pollCreateMenuItem.setVisible(moder);
		pollVoteMenuItem.setVisible(pollExists && notExternalUser && pollDao.notVoted(r.getId(), getUserId()));
		pollResultMenuItem.setVisible(pollExists || !pollDao.getArchived(r.getId()).isEmpty());
	}

	public void updatePoll(IPartialPageRequestHandler handler, Long createdBy) {
		RoomPoll rp = pollDao.getByRoom(room.getRoom().getId());
		if (rp != null) {
			vote.updateModel(handler, rp);
		} else {
			vote.close(handler);
		}
		if (createdBy != null && !getUserId().equals(createdBy)) {
			vote.show(handler);
		}
		if (pollResults.isOpened()) {
			pollResults.updateModel(handler, false, room.getClient().hasRight(Room.Right.MODERATOR));
		}
	}

	public void renderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_QPOLL_ACTION, quickPollAction, explicit(PARAM_ACTION), explicit(PARAM_VOTE))));
	}

	public boolean isVisible() {
		return visible;
	}
}
