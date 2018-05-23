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
package org.apache.openmeetings.web.user;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class InviteUserMessageDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private DialogButton send;
	private DialogButton cancel;
	private final Form<Void> form = new Form<>("form");
	private final TextArea<String> message = new TextArea<>("message", Model.of(""));
	private final CheckBox enterRoom = new CheckBox("enterRoom", Model.of(false));

	public InviteUserMessageDialog(String id) {
		super(id, "");
		add(form.add(message.setRequired(true), enterRoom.setOutputMarkupId(true)).setOutputMarkupId(true));
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("1138"));
		send = new DialogButton("send", getString("213"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		super.onInitialize();
	}

	public void open(IPartialPageRequestHandler handler, Long roomId, Long userId) {
		Room r = getBean(RoomDao.class).get(roomId);
		User u = getBean(UserDao.class).get(userId);
		message.setModelObject(String.format("%s %s %s %s", u.getFirstname(), u.getLastname(), getString("1137"), r.getName()));
		enterRoom.setModelObject(false);
		handler.add(form);
		open(handler);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(send, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return send;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		// no-op
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		// no-op
	}
}
