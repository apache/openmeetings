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
package org.apache.openmeetings.web.mail.template;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class InvitationTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	public InvitationTemplate(Long langId, String invitorName, String message, String link) {
		super(TemplatePage.COMP_ID);
		ensureApplication(langId);

		FieldLanguagesValuesDao dao = getBean(FieldLanguagesValuesDao.class);
		add(new Label("titleLbl", dao.getString(500, langId).replaceAll("\\$APP_NAME", getBean(ConfigurationDao.class).getAppName())));
		add(new Label("userLbl", dao.getString(501, langId)));
		add(new Label("user", invitorName));
		add(new Label("messageLbl", dao.getString(502, langId)));
		add(new Label("message", message).setEscapeModelStrings(false));
		
		add(new WebMarkupContainer("links")
			.add(new Label("comment_for_link1", dao.getString(503, langId)))
			.add(new ExternalLink("invitation_link1", link).add(new Label("clickMe", dao.getString(504, langId))))
			.add(new Label("comment_for_link2", dao.getString(505, langId)))
			.add(new Label("invitation_link2", link))
			.setVisible(link != null)
			);
	}
	
	public String getEmail() {
		return renderPanel(this).toString();
	}
}
