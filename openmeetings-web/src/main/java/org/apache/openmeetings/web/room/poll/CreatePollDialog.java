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

import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class CreatePollDialog extends Modal<RoomPoll> {
	private static final long serialVersionUID = 1L;
	private final Long roomId;
	private PollForm form;
	private final NotificationPanel feedback = new NotificationPanel("feedback");

	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;
	@Inject
	private PollDao pollDao;

	public CreatePollDialog(String id, Long roomId) {
		super(id, new CompoundPropertyModel<>(new RoomPoll()));
		this.roomId = roomId;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("18"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		add(form = new PollForm("form", getModel()));
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("22"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				pollDao.close(roomId);
				pollDao.update(form.getModelObject());
				sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.POLL_CREATED));
				close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}
		}); // create
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	public void updateModel(AjaxRequestTarget target) {
		RoomPoll p = new RoomPoll();
		User u = userDao.get(getUserId());
		p.setCreator(u);
		p.setRoom(roomDao.get(roomId));
		p.setType(RoomPoll.Type.YES_NO);
		setModelObject(p);
		form.setModelObject(getModelObject());
		target.add(form);
	}

	PollForm getForm() {
		return form;
	}

	class PollForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;

		public PollForm(String id, IModel<RoomPoll> model) {
			super(id, model);
		}

		@Override
		protected void onInitialize() {
			add(new RequiredTextField<String>("name").setLabel(new ResourceModel("1410")));
			add(new TextArea<String>("question"));
			add(new DropDownChoice<>("type", List.of(RoomPoll.Type.values())
					, new LambdaChoiceRenderer<>(pt -> getString("poll.type." + pt.name()), RoomPoll.Type::name))
					.setRequired(true).setLabel(new ResourceModel("21")));
			add(feedback.setOutputMarkupId(true));
			super.onInitialize();
		}
	}
}
