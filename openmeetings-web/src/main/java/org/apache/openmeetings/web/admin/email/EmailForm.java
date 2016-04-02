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

import static org.apache.openmeetings.util.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;

import java.util.Date;

import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class EmailForm extends Form<MailMessage> {
	private static final long serialVersionUID = 1L;
	private final DateLabel inserted;
	private final DateLabel updated;
	private final Label status;

	public EmailForm(String id, MailMessage m) {
		super(id, new CompoundPropertyModel<>(m));
		add(status = new Label("status", Model.of("")));
		add(new Label("subject"));
		add(new Label("recipients"));
		add(new Label("body").setEscapeModelStrings(false));
		add(inserted = forDatePattern("inserted", Model.of((Date)null), WEB_DATE_PATTERN));
		add(updated = forDatePattern("updated", Model.of((Date)null), WEB_DATE_PATTERN));
		add(new Label("errorCount"));
		add(new Label("lastError"));
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		MailMessage m = getModelObject();
		status.setDefaultModelObject(getString("admin.email.status." + m.getStatus().name()));
		inserted.setModelObject(m.getInserted().getTime());
		updated.setModelObject(m.getInserted().getTime());
	}
}
