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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinFnameLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLnameLength;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class NicknameDialog extends Modal<User> {
	private static final long serialVersionUID = 1L;
	private static final FastDateFormat TIME_DF = FastDateFormat.getInstance("HH:mm:ss");
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final RoomPanel room;
	private Form<User> form;

	@Inject
	private ClientManager cm;

	public NicknameDialog(String id, final RoomPanel room) {
		super(id);
		this.room = room;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1287"));
		setBackdrop(Backdrop.STATIC);

		add(form = new Form<>("form", new CompoundPropertyModel<>(room.getClient().getUser())));
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("54"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				final User u = form.getModelObject();
				final Client c = room.getClient();
				c.getUser().setFirstname(u.getFirstname()).setLastname(u.getLastname());
				room.broadcast(cm.update(c));
				NicknameDialog.this.close(target);
			}
		}); //set nickname
		form.add(feedback.setOutputMarkupId(true));
		form.add(new RequiredTextField<String>("firstname").setLabel(new ResourceModel("135")).add(minimumLength(getMinFnameLength())));
		form.add(new RequiredTextField<String>("lastname").setLabel(new ResourceModel("136")).add(minimumLength(getMinLnameLength())));
		form.add(new RequiredTextField<String>("address.email").setLabel(new ResourceModel("119")).add(RfcCompliantEmailAddressValidator.getInstance()));
		super.onInitialize();

		User u = form.getModelObject();
		boolean visible = isVisible(u);
		if (visible) {
			u.setFirstname(getString("433"));
			u.setLastname(String.format("%s %s", u.getFirstname(), TIME_DF.format(new Date())));
		}
		show(visible);
	}

	private static boolean isVisible(User u) {
		return (User.Type.CONTACT == u.getType() || User.Type.EXTERNAL == u.getType())
				&& Strings.isEmpty(u.getFirstname()) && Strings.isEmpty(u.getLastname());
	}
}
