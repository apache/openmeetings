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
package org.apache.openmeetings.web.room.poll;

import static org.apache.openmeetings.core.util.WebSocketHelper.sendRoom;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class VoteDialog extends Modal<RoomPollAnswer> {
	private static final long serialVersionUID = 1L;
	private static final List<Integer> answers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	private PollAnswerForm form;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Label userLbl = new Label("user", Model.of(""));

	@Inject
	private UserDao userDao;
	@Inject
	private PollDao pollDao;

	public VoteDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("18"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		add(form = new PollAnswerForm("form", new CompoundPropertyModel<>(new RoomPollAnswer())));
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("32"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				RoomPollAnswer a = form.getModelObject();
				Long roomId = a.getRoomPoll().getRoom().getId();
				if (pollDao.notVoted(roomId, getUserId())) {
					a.setVoteDate(new Date());
					a.getRoomPoll().getAnswers().add(a);
					pollDao.update(a.getRoomPoll());
				}
				sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.POLL_UPDATED));
				close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}
		}); // vote
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	static String getName(Component c, User u) {
		if (u == null) {
			return  "";
		}
		return getUserId().equals(u.getId()) ? c.getString("1411") : u.getDisplayName();
	}

	public void updateModel(IPartialPageRequestHandler target, RoomPoll rp) {
		RoomPollAnswer a = new RoomPollAnswer();
		a.setRoomPoll(rp);
		User u = userDao.get(getUserId());
		a.setVotedUser(u);
		userLbl.setDefaultModelObject(getName(this, a.getRoomPoll().getCreator()));
		form.setModelObject(a);
		boolean typeNum = a.getRoomPoll() != null && RoomPoll.Type.NUMERIC == a.getRoomPoll().getType();
		form.typeBool.setVisible(!typeNum);
		form.typeInt.setVisible(typeNum);
		target.add(form);
	}

	private class PollAnswerForm extends Form<RoomPollAnswer> {
		private static final long serialVersionUID = 1L;
		private final WebMarkupContainer typeBool = new WebMarkupContainer("typeBool");
		private final WebMarkupContainer typeInt = new WebMarkupContainer("typeInt");

		PollAnswerForm(String id, IModel<RoomPollAnswer> model) {
			super(id, model);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			add(userLbl);
			add(new Label("roomPoll.question"));
			add(typeBool.add(new RadioGroup<Boolean>("answer").setRequired(true)
						.add(new Radio<>("true", Model.of(Boolean.TRUE))).add(new Radio<>("false", Model.of(Boolean.FALSE)))
				).setOutputMarkupPlaceholderTag(true).setVisible(false));
			add(typeInt.add(new DropDownChoice<>("pointList", answers).setRequired(true))
					.setOutputMarkupPlaceholderTag(true).setVisible(false));
		}
	}
}
