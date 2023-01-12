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
package org.apache.openmeetings.web.room.menu;

import org.apache.openmeetings.core.sip.SipManager;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class SipDialerDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Form<String> form = new Form<>("form", Model.of(""));
	private final TextField<String> number = new TextField<>("number", Model.of(""));
	private final RoomPanel room;

	@Inject
	private SipManager sipDao;

	public SipDialerDialog(String id, RoomPanel room) {
		super(id);
		this.room = room;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1003"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		AjaxButton ab = new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				SipDialerDialog.this.onSubmit(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				SipDialerDialog.this.onError(target);
			}
		};
		form.setDefaultButton(ab);
		add(feedback.setOutputMarkupId(true), form.add(number, ab));
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("label.hangup"), form, Buttons.Type.Outline_Danger) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				sipDao.hangup(room.getRoom());
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				SipDialerDialog.this.onError(target);
			}
		});
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("1448"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				SipDialerDialog.this.onSubmit(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				SipDialerDialog.this.onError(target);
			}
		}); // call
		addButton(OmModalCloseButton.of("85"));
		super.onInitialize();
	}

	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	/**
	 * handler for "Call" button
	 *
	 * @param target - {@link AjaxRequestTarget} to perform additional actions(s) if any
	 */
	protected void onSubmit(AjaxRequestTarget target) {
		sipDao.callExternalNumber(number.getModelObject(), room.getRoom());
	}
}
