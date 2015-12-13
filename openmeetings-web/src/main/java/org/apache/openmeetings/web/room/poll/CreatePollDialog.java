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
import static org.apache.openmeetings.web.room.RoomBroadcaster.broadcast;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class CreatePollDialog extends AbstractFormDialog<RoomPoll> {
	private static final long serialVersionUID = 1L;
	private final DialogButton create = new DialogButton("create", Application.getString(22));
	private final DialogButton cancel = new DialogButton("cancel", Application.getString(25));
	private final long roomId;
	private final PollForm form;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private String publicSid = null;

	public CreatePollDialog(String id, long roomId) {
		super(id, Application.getString(18), new CompoundPropertyModel<RoomPoll>(new RoomPoll()));
		this.roomId = roomId;
		add(form = new PollForm("form", getModel()));
	}

	public void updateModel(AjaxRequestTarget target, String publicSid) {
		this.publicSid = publicSid;
		RoomPoll p = new RoomPoll();
		User u = getBean(UserDao.class).get(getUserId());
		p.setCreator(u);
		p.setRoom(getBean(RoomDao.class).get(roomId));
		p.setType(RoomPoll.Type.yesNo);
		form.setModelObject(p);
		target.add(form);
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(create, cancel);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return create;
	}

	@Override
	public PollForm getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		getBean(PollDao.class).close(roomId);
		RoomPoll p = getBean(PollDao.class).update(form.getModelObject());
		broadcast(publicSid, "newPoll", p);
	}

	private class PollForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		
		public PollForm(String id, IModel<RoomPoll> model) {
			super(id, model);
			add(new RequiredTextField<String>("name").setLabel(Model.of(Application.getString(1410))));
			add(new TextArea<String>("question"));
			add(new DropDownChoice<RoomPoll.Type>("type", Arrays.asList(RoomPoll.Type.values())
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
					.setRequired(true).setLabel(Model.of(Application.getString(21))));
			add(feedback);
		}
	}
}
