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
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
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

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

/**
 * @author solomax
 *
 */
public class VoteDialog extends AbstractFormDialog<RoomPollAnswer> {
	private static final long serialVersionUID = 1L;
	private static final List<Integer> answers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	private PollAnswerForm form;
	private DialogButton vote;
	private DialogButton cancel;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final IModel<String> user = Model.of((String)null);

	public VoteDialog(String id) {
		super(id, "");
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("18"));
		add(form = new PollAnswerForm("form", new CompoundPropertyModel<>(new RoomPollAnswer())));
		vote = new DialogButton("vote", getString("32"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		super.onInitialize();
	}

	static String getName(Component c, User u) {
		return u == null ? "" : (getUserId().equals(u.getId()) ? c.getString("1411") : u.getFirstname() + " " + u.getLastname());
	}

	public void updateModel(IPartialPageRequestHandler target, RoomPoll rp) {
		RoomPollAnswer a = new RoomPollAnswer();
		a.setRoomPoll(rp);
		User u = getBean(UserDao.class).get(getUserId());
		a.setVotedUser(u);
		user.setObject(getName(this, a.getRoomPoll().getCreator()));
		form.setModelObject(a);
		boolean typeNum = a.getRoomPoll() != null && RoomPoll.Type.numeric == a.getRoomPoll().getType();
		form.typeBool.setVisible(!typeNum);
		form.typeInt.setVisible(typeNum);
		target.add(form);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(vote, cancel);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog#getSubmitButton()
	 */
	@Override
	public DialogButton getSubmitButton() {
		return vote;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog#getForm()
	 */
	@Override
	public PollAnswerForm getForm() {
		return form;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog#onError(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog#onSubmit(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		RoomPollAnswer a = form.getModelObject();
		PollDao dao = getBean(PollDao.class);
		Long roomId = a.getRoomPoll().getRoom().getId();
		if (!dao.hasVoted(roomId, getUserId())) {
			a.setVoteDate(new Date());
			a.getRoomPoll().getAnswers().add(a);
			dao.update(a.getRoomPoll());
		}
		sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.pollUpdated));
	}

	@Override
	protected void onDetach() {
		user.detach();
		super.onDetach();
	}

	private class PollAnswerForm extends Form<RoomPollAnswer> {
		private static final long serialVersionUID = 1L;
		private final WebMarkupContainer typeBool = new WebMarkupContainer("typeBool");
		private final WebMarkupContainer typeInt = new WebMarkupContainer("typeInt");

		PollAnswerForm(String id, IModel<RoomPollAnswer> model) {
			super(id, model);
			add(feedback);
			add(new Label("user", user));
			add(new Label("roomPoll.question"));
			add(typeBool.add(new RadioGroup<Boolean>("answer").setRequired(true)
						.add(new Radio<>("true", Model.of(Boolean.TRUE))).add(new Radio<>("false", Model.of(Boolean.FALSE)))
				).setOutputMarkupPlaceholderTag(true).setVisible(false));
			add(typeInt.add(new DropDownChoice<>("pointList", answers).setRequired(true))
					.setOutputMarkupPlaceholderTag(true).setVisible(false));
		}
	}
}
