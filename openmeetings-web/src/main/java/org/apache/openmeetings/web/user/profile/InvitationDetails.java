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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.db.util.FormatHelper.formatUser;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Date;

import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class InvitationDetails extends Form<Invitation>{
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer list;
	private ConfirmableAjaxBorder delBtn;
	private final Label valid = new Label("valid", Model.of(""));
	private final Label invitee = new Label("invitee", Model.of(""));
	private final DateLabel from = new DateLabel("validFrom", Model.of((Date)null));
	private final DateLabel to = new DateLabel("validTo", Model.of((Date)null));
	@SpringBean
	private InvitationDao inviteDao;

	public InvitationDetails(String id, final WebMarkupContainer list, Invitation i) {
		super(id, new CompoundPropertyModel<>(i));
		this.list = list;
		setOutputMarkupId(true);
		add(new Label("id"));
		add(valid);
		add(invitee);
		add(from);
		add(to);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// add a cancel button that can be used to submit the form via ajax
		delBtn = new ConfirmableAjaxBorder("ajax-cancel-button", getString("80"), getString("833"), this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				inviteDao.delete(getModelObject(), getUserId());
				setModelObject(new Invitation());
				target.add(list, InvitationDetails.this);
			}
		};
		add(delBtn.setOutputMarkupId(true).setEnabled(false));
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		Invitation i = getModelObject();
		Valid v = i.getValid();
		valid.setDefaultModelObject(v.name());
		invitee.setDefaultModelObject(formatUser(i.getInvitee()));
		from.setDefaultModelObject(Valid.Period == v ? i.getValidFrom() : null);
		to.setDefaultModelObject(Valid.Period == v ? i.getValidTo() : null);
		delBtn.setEnabled(i.getId() != null);
	}
}
