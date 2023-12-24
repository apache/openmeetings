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
package org.apache.openmeetings.web.admin.email;

import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import org.apache.openmeetings.db.dao.basic.MailMessageDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import jakarta.inject.Inject;

public class EmailForm extends Form<MailMessage> {
	private static final long serialVersionUID = 1L;
	private final Label status = new Label("status", Model.of(""));
	private BootstrapAjaxButton reset;
	private AjaxLink<Void> delBtn;
	private final WebMarkupContainer list;

	@Inject
	private MailMessageDao emailDao;

	public EmailForm(String id, final WebMarkupContainer list, MailMessage m) {
		super(id, new CompoundPropertyModel<>(m));
		this.list = list;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(status);
		add(new Label("subject"));
		add(new Label("recipients"));
		add(new Label("body").setEscapeModelStrings(false));
		add(new DateLabel("inserted"));
		add(new DateLabel("updated"));
		add(new Label("errorCount"));
		add(new Label("lastError"));
		add(reset = new BootstrapAjaxButton("reset", new ResourceModel("admin.email.reset.status"), Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				emailDao.resetSendingStatus(EmailForm.this.getModelObject().getId());
				target.add(list);
			}
		});
		reset.setEnabled(false);
		// add a cancel button that can be used to submit the form via ajax
		delBtn = new AjaxLink<>("btn-delete") {
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) {
				emailDao.delete(EmailForm.this.getModelObject().getId());
				EmailForm.this.setModelObject(new MailMessage());
				target.add(list, EmailForm.this);
			}
		};
		delBtn.add(newOkCancelDangerConfirm(this, getString("833")));
		add(delBtn.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true).setVisible(false));
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		MailMessage m = getModelObject();
		delBtn.setVisible(m.getId() != null);
		status.setDefaultModelObject(getString("admin.email.status." + m.getStatus().name()));
		reset.setEnabled(m.getId() != null && MailMessage.Status.ERROR == m.getStatus());
	}
}
