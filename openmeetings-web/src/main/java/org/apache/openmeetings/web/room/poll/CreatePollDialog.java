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
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class CreatePollDialog extends AbstractFormDialog<RoomPoll> {
	private static final long serialVersionUID = 1L;
	private DialogButton create;
	private DialogButton cancel;
	private final Long roomId;
	private final PollForm form;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

	public CreatePollDialog(String id, Long roomId) {
		super(id, "", new CompoundPropertyModel<>(new RoomPoll()));
		this.roomId = roomId;
		add(form = new PollForm("form", getModel()));
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("18"));
		create = new DialogButton("create", getString("22"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		super.onInitialize();
	}

	public void updateModel(AjaxRequestTarget target) {
		RoomPoll p = new RoomPoll();
		User u = getBean(UserDao.class).get(getUserId());
		p.setCreator(u);
		p.setRoom(getBean(RoomDao.class).get(roomId));
		p.setType(RoomPoll.Type.yesNo);
		setModelObject(p);
		form.setModelObject(getModelObject());
		target.add(form);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(create, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return create;
	}

	@Override
	public PollForm getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		PollDao dao = getBean(PollDao.class);
		dao.close(roomId);
		dao.update(form.getModelObject());
		sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.pollCreated));
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
			add(new DropDownChoice<>("type", Arrays.asList(RoomPoll.Type.values())
					, new ChoiceRenderer<RoomPoll.Type>() {
						private static final long serialVersionUID = 1L;

						@Override
						public Object getDisplayValue(RoomPoll.Type pt) {
							return getString("poll.type." + pt.name());
						}

						@Override
						public String getIdValue(RoomPoll.Type pt, int index) {
							return pt.name();
						}
					})
					.setRequired(true).setLabel(new ResourceModel("21")));
			add(feedback);
			super.onInitialize();
		}
	}
}
