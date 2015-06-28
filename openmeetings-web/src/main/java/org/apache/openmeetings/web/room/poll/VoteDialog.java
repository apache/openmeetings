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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.message.RoomMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
	private final static List<Integer> answers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  //TODO max vote should be added 
	private final long roomId;
	private final PollAnswerForm form;
	private final DialogButton vote = new DialogButton("vote", Application.getString(32));
	private final DialogButton cancel = new DialogButton("cancel", Application.getString(25));
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final IModel<String> user = Model.of((String)null);

	public VoteDialog(String id, long roomId) {
		super(id, Application.getString(18));
		this.roomId = roomId;
		add(form = new PollAnswerForm("form", new CompoundPropertyModel<RoomPollAnswer>(new RoomPollAnswer())));
	}
	
	static String getName(User u) {
		return u == null ? "" : getUserId() == u.getId() ? Application.getString(1411) : u.getFirstname() + " " + u.getLastname();
	}
	
	public void updateModel(AjaxRequestTarget target) {
		RoomPollAnswer a = new RoomPollAnswer();
		a.setRoomPoll(getBean(PollDao.class).getPoll(roomId));
		User u = getBean(UserDao.class).get(getUserId());
		a.setVotedUser(u);
		user.setObject(getName(a.getRoomPoll().getCreator()));
		form.setModelObject(a);
		boolean typeNum = a.getRoomPoll() != null && a.getRoomPoll().getType() != null && a.getRoomPoll().getType().isNumeric();
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
	protected DialogButton getSubmitButton() {
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
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog#onSubmit(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		RoomPollAnswer a = form.getModelObject();
		a.setVoteDate(new Date());
		a.getRoomPoll().getAnswers().add(a);
		getBean(PollDao.class).update(a.getRoomPoll());
		RoomPanel.broadcast(new RoomMessage(roomId, getUserId(), RoomMessage.Type.voted));
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
						.add(new Radio<Boolean>("true", Model.of(Boolean.TRUE))).add(new Radio<Boolean>("false", Model.of(Boolean.FALSE)))
				).setOutputMarkupPlaceholderTag(true).setVisible(false));
			add(typeInt.add(new DropDownChoice<Integer>("pointList", answers).setRequired(true))
					.setOutputMarkupPlaceholderTag(true).setVisible(false));
		}
	}
}
