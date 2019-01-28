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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class SipDialerDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private DialogButton call;
	private DialogButton close;
	private final Form<String> form = new Form<>("form", Model.of(""));
	private final TextField<String> number = new TextField<>("number", Model.of(""));
	private final RoomPanel room;

	public SipDialerDialog(String id, RoomPanel room) {
		super(id, "");
		this.room = room;
		AjaxButton ab = new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				SipDialerDialog.this.onSubmit(target, call);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				SipDialerDialog.this.onError(target, call);
			}
		};
		form.setDefaultButton(ab);
		add(feedback.setOutputMarkupId(true), form.add(number, ab));
	}

	@Override
	protected void onInitialize() {
		setTitle(new ResourceModel("1003"));
		call = new DialogButton("call", getString("1448"));
		close = new DialogButton("close", getString("85"));
		super.onInitialize();
	}

	@Override
	public int getWidth() {
		return 300;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(call, close);
	}

	@Override
	public DialogButton getSubmitButton() {
		return call;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		getBean(SipDao.class).joinToConfCall(number.getModelObject(), room.getRoom());
	}
}
