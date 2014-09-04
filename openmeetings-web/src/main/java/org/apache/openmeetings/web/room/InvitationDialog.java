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

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class InvitationDialog extends AbstractFormDialog<Invitation> {
	private static final long serialVersionUID = 1L;
	private DialogButton generate = new DialogButton(WebSession.getString(1526));
	private DialogButton send = new DialogButton(WebSession.getString(218));
	private DialogButton cancel = new DialogButton(WebSession.getString(219));
	private Form<Invitation> form;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");

	public InvitationDialog(String id) {
		super(id, WebSession.getString(214));
		add(form = new Form<Invitation>("form") {
			private static final long serialVersionUID = 1L;
			
			{
				add(feedback);
			}
		});
	}

	@Override
	public Form<Invitation> getForm() {
		return form;
	}

	@Override
	protected DialogButton getSubmitButton() {
		return send;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(generate, send, cancel);
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
	}
}
