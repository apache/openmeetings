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

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class InviteUserMessageDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<>("form");
	private final TextArea<String> message = new TextArea<>("message", Model.of(""));
	private final CheckBox enterRoom = new CheckBox("enterRoom", Model.of(false));

	@Inject
	private UserDao userDao;
	@Inject
	private RoomDao roomDao;

	public InviteUserMessageDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1138"));

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("213"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				// no-op
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				InviteUserMessageDialog.this.close(target);
			}
		}); //send
		addButton(OmModalCloseButton.of());
		super.onInitialize();
		add(form.add(message.setRequired(true), enterRoom.setOutputMarkupId(true)).setOutputMarkupId(true));
	}

	public void show(IPartialPageRequestHandler handler, Long roomId, Long userId) {
		Room r = roomDao.get(roomId);
		User u = userDao.get(userId);
		message.setModelObject(String.format("%s %s %s %s", u.getFirstname(), u.getLastname(), getString("1137"), r.getName()));
		enterRoom.setModelObject(false);
		handler.add(form);
		super.show(handler);
	}
}
