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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.PrivateMessage;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.renderer.ITextRenderer;
import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteTextField;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessageDialog extends AbstractFormDialog<PrivateMessage> {
	private static final long serialVersionUID = 1L;
	private final Form<PrivateMessage> form;
	private DialogButton send = new DialogButton(WebSession.getString(218));
	private DialogButton cancel = new DialogButton(WebSession.getString(219));
	private final WebMarkupContainer roomParamsBlock = new WebMarkupContainer("roomParamsBlock");
	private final WebMarkupContainer roomParams = new WebMarkupContainer("roomParams");

	@Override
	public int getWidth() {
		return 650;
	}
	
	public MessageDialog(String id, IModel<PrivateMessage> model) {
		super(id, WebSession.getString(1209), model);
		form = new Form<PrivateMessage>("form", getModel());
		
		form.add(new AutoCompleteTextField<User>("to", new UserTextRenderer()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<User> getChoices(String input) {
				return getBean(UsersDao.class).get(input, 0, 10, null);
			}
		});
		form.add(new TextField<String>("subject"));
		form.add(new TextArea<String>("message"));
		form.add(roomParamsBlock.setOutputMarkupId(true));
		form.add(new CheckBox("room", Model.of(getModelObject().getRoom() != null)).add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			boolean val = false;
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				roomParams.setVisible(val = !val);
				target.add(roomParamsBlock);
			}
		}));
		roomParamsBlock.add(roomParams);
		roomParams.add(new RoomTypeDropDown("roomType"));
		roomParams.add(new WebMarkupContainer("start"));
		roomParams.add(new WebMarkupContainer("end"));
		add(form);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(send, cancel);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return send;
	}

	@Override
	public Form<PrivateMessage> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		// TODO Auto-generated method stub

	}

	private static class UserTextRenderer implements ITextRenderer<User> {
		private static final long serialVersionUID = 1L;

		public String getText(User u) {
			return u == null ? "" : String.format("%s %s <%s>", u.getFirstname(), u.getLastname(), u.getAdresses().getEmail());
		}
		
		public String getText(User u, String expression) {
			return getText(u);
		}
	}
}
