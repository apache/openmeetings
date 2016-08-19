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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.util.CalendarHelper.getDate;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.UUID;

import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.threeten.bp.LocalDateTime;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public abstract class InvitationForm extends Form<Invitation> {
	private static final long serialVersionUID = 1L;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final PasswordTextField passwd;
	private final DropDownChoice<String> timeZoneId = new DropDownChoice<String>("timeZoneId", Model.of((String)null), AVAILABLE_TIMEZONES);
	private final OmDateTimePicker from = new OmDateTimePicker("from", Model.of(LocalDateTime.now()));
	private final OmDateTimePicker to = new OmDateTimePicker("to", Model.of(LocalDateTime.now()));
	private final LanguageDropDown lang = new LanguageDropDown("language", Model.of((Long)null));
	protected final TextField<String> subject = new TextField<String>("subject", Model.of((String)null));
	protected final TextArea<String> message = new TextArea<String>("message", Model.of((String)null));
	protected final TextField<String> url = new TextField<String>("url", Model.of((String)null));
	protected InvitationDialog dialog;

	public InvitationForm(String id) {
		super(id, new CompoundPropertyModel<Invitation>(new Invitation()));
		setOutputMarkupId(true);

		add(subject, message);
		add(new AjaxCheckBox("passwordProtected") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				InvitationForm.this.getModelObject().setPasswordProtected(getConvertedInput());
				passwd.setEnabled(getConvertedInput());
				target.add(passwd);
			}
		});
		RadioGroup<Valid> valid = new RadioGroup<Valid>("valid");
		valid.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean dateEnabled = InvitationForm.this.getModelObject().getValid() == Valid.Period;
				target.add(from.setEnabled(dateEnabled), to.setEnabled(dateEnabled), timeZoneId.setEnabled(dateEnabled));
			}
		});
		add(valid.add(new Radio<Valid>("one", Model.of(Valid.OneTime))
				, new Radio<Valid>("period", Model.of(Valid.Period))
				, new Radio<Valid>("endless", Model.of(Valid.Endless))));
		add(passwd = new PasswordTextField("password"));
		Invitation i = getModelObject();
		passwd.setLabel(Model.of(Application.getString(525))).setOutputMarkupId(true).setEnabled(i.isPasswordProtected());
		add(from, to, timeZoneId);
		from.setEnabled(i.getValid() == Valid.Period).setOutputMarkupId(true);
		to.setEnabled(i.getValid() == Valid.Period).setOutputMarkupId(true);
		timeZoneId.setEnabled(i.getValid() == Valid.Period).setOutputMarkupId(true)
			.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					//no-op added to preserve selection
				}
			});
		add(url.setOutputMarkupId(true));
		add(lang, feedback);
	}
	
	@Override
	protected void onValidate() {
		if (from.getConvertedInput() != null && to.getConvertedInput() != null && from.getConvertedInput().isAfter(to.getConvertedInput())) {
			error(Application.getString(1592));
		}
	}

	protected Invitation create(User u) {
		Invitation i = new Invitation(getModelObject());
		i.setId(null);
		i.setUpdated(null);
		i.setUsed(false);
		
		i.setPassword(CryptProvider.get().hash(i.getPassword())); //FIXME should be hidden
		i.setValidFrom(getDate(from.getModelObject().minusMinutes(5), timeZoneId.getModelObject()));
		i.setValidTo(getDate(to.getModelObject(), timeZoneId.getModelObject()));
		
		i.setInvitee(u);
		i.setHash(UUID.randomUUID().toString());
		if (Type.contact == u.getType()) {
			//TODO not sure it is right
			u.setLanguageId(lang.getModelObject());
		}
		return getBean(InvitationDao.class).update(i);
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}

	public void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	public void updateModel(AjaxRequestTarget target) {
		Invitation i = new Invitation();
		User u = getBean(UserDao.class).get(getUserId());
		i.setInvitedBy(u);
		i.setRoom(null);
		from.setModelObject(LocalDateTime.now());
		to.setModelObject(LocalDateTime.now().plusDays(1));
		i.setPassword(null);
		i.setHash(null);
		subject.setModelObject(null);
		message.setModelObject(null);
		timeZoneId.setModelObject(u.getTimeZoneId());
		lang.setModelObject(u.getLanguageId());
		url.setModelObject(null);
		setModelObject(i);
		target.add(this);
	}

	public void setDialog(InvitationDialog dialog) {
		this.dialog = dialog;
	}

	public abstract boolean onSubmit(AjaxRequestTarget target, boolean generate, boolean send);
}