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

import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class NicknameDialog extends AbstractFormDialog<User> {
	private static final long serialVersionUID = 1L;
	private static final FastDateFormat TIME_DF = FastDateFormat.getInstance("HH:mm:ss");
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final DialogButton ok = new DialogButton("ok", Application.getString(54));
	private final RoomPanel room;
	private final Form<User> form;

	public NicknameDialog(String id, final RoomPanel room) {
		super(id, Application.getString(1287));
		this.room = room;
		User u = room.getClient().getUser();
		if (isVisible(u)) {
			u.setFirstname(Application.getString(433));
			u.setLastname(String.format("%s %s", u.getFirstname(), TIME_DF.format(new Date())));
		}
		add(form = new Form<>("form", new CompoundPropertyModel<>(u)));
		form.add(feedback);
		form.add(new RequiredTextField<String>("firstname").setLabel(Model.of(Application.getString(135))).add(minimumLength(4)));
		form.add(new RequiredTextField<String>("lastname").setLabel(Model.of(Application.getString(136))).add(minimumLength(4)));
		form.add(new RequiredTextField<String>("address.email").setLabel(Model.of(Application.getString(137))).add(RfcCompliantEmailAddressValidator.getInstance()));
	}

	private static boolean isVisible(User u) {
		return User.Type.external == u.getType() && Strings.isEmpty(u.getFirstname()) && Strings.isEmpty(u.getLastname());
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", isVisible(form.getModelObject()));
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("dialogClass", Options.asString("no-close"));
		behavior.setOption("resizable", false);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(ok);
	}

	@Override
	public DialogButton getSubmitButton() {
		return ok;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		room.broadcast(room.getClient());
	}
}
