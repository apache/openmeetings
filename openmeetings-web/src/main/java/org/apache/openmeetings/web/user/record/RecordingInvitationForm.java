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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getInvitationLink;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.InvitationForm;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RecordingInvitationForm extends InvitationForm {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingInvitationForm.class, webAppRootKey);
	private Long recordingId;
	final UserMultiChoice recipients = new UserMultiChoice("recipients", new CollectionModel<User>(new ArrayList<User>()));

	public RecordingInvitationForm(String id) {
		super(id);
		add(recipients.setLabel(Model.of(Application.getString(216))).setRequired(true).add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				url.setModelObject(null);
				updateButtons(target);
			}
		}).setOutputMarkupId(true));
	}

	private void updateButtons(AjaxRequestTarget target) {
		Collection<User> to = recipients.getModelObject();
		dialog.send.setEnabled(to.size() > 0, target);
		dialog.generate.setEnabled(to.size() == 1, target);
	}

	@Override
	public void updateModel(AjaxRequestTarget target) {
		super.updateModel(target);
		//Invitation i = getModelObject();
		//i.setReco
		recipients.setModelObject(new ArrayList<User>());
		recipients.setEnabled(true);
	}

	@Override
	public boolean onSubmit(AjaxRequestTarget target, boolean generate, boolean send) {
		//TODO need to be reviewed
		if (generate) {
			Invitation i = create(recipients.getModelObject().iterator().next());
			setModelObject(i);
			url.setModelObject(getInvitationLink(getBean(ConfigurationDao.class).getBaseUrl(), i));
			target.add(url);
			return true;
		} else if (send) {
			if (Strings.isEmpty(url.getModelObject())) {
				for (User u : recipients.getModelObject()) {
					Invitation i = create(u);
					try {
						getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getModelObject(), message.getModelObject(), false);
					} catch (Exception e) {
						log.error("error while sending invitation by User ", e);
					}
				}
			} else {
				Invitation i = getModelObject();
				try {
					getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getModelObject(), message.getModelObject(), false);
				} catch (Exception e) {
					log.error("error while sending invitation by URL ", e);
				}
			}
		}
		return false;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}
}
