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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getInvitationLink;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.UserMultiChoice;
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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public abstract class InvitationForm extends Form<Invitation> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationForm.class, getWebAppRootKey());
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final PasswordTextField passwd = new PasswordTextField("password");
	private final DropDownChoice<String> timeZoneId = new DropDownChoice<>("timeZoneId", Model.of((String)null), AVAILABLE_TIMEZONES);
	private final OmDateTimePicker from = new OmDateTimePicker("from", Model.of(LocalDateTime.now()));
	private final OmDateTimePicker to = new OmDateTimePicker("to", Model.of(LocalDateTime.now()));
	private final LanguageDropDown lang = new LanguageDropDown("language", Model.of((Long)null));
	protected final TextField<String> subject = new TextField<>("subject", Model.of((String)null));
	protected final TextArea<String> message = new TextArea<>("message", Model.of((String)null));
	protected final TextField<String> url = new TextField<>("url", Model.of((String)null));
	protected final UserMultiChoice recipients = new UserMultiChoice("recipients", new CollectionModel<>(new ArrayList<User>()));
	protected InvitationDialog dialog;

	public InvitationForm(String id) {
		super(id, new CompoundPropertyModel<>(new Invitation()));
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		add(subject, message);
		recipients.setLabel(new ResourceModel("216")).setRequired(true).add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				url.setModelObject(null);
				updateButtons(target);
			}
		}).setOutputMarkupId(true);
		add(new AjaxCheckBox("passwordProtected") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				InvitationForm.this.getModelObject().setPasswordProtected(getConvertedInput());
				passwd.setEnabled(getConvertedInput());
				target.add(passwd);
			}
		});
		RadioGroup<Valid> valid = new RadioGroup<>("valid");
		valid.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean dateEnabled = InvitationForm.this.getModelObject().getValid() == Valid.Period;
				target.add(from.setEnabled(dateEnabled), to.setEnabled(dateEnabled), timeZoneId.setEnabled(dateEnabled));
			}
		});
		add(valid.add(new Radio<>("one", Model.of(Valid.OneTime))
				, new Radio<>("period", Model.of(Valid.Period))
				, new Radio<>("endless", Model.of(Valid.Endless))));
		add(passwd);
		Invitation i = getModelObject();
		passwd.setLabel(new ResourceModel("110")).setOutputMarkupId(true).setEnabled(i.isPasswordProtected());
		add(from.setOutputMarkupId(true), to.setOutputMarkupId(true), timeZoneId.setOutputMarkupId(true));
		timeZoneId.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//no-op added to preserve selection
			}
		});
		add(url.setOutputMarkupId(true));
		add(lang, feedback);
		super.onInitialize();
	}

	protected void updateButtons(AjaxRequestTarget target) {
		Collection<User> recpnts = recipients.getModelObject();
		dialog.getSend().setEnabled(!recpnts.isEmpty(), target);
		dialog.getGenerate().setEnabled(recpnts.size() == 1, target);
	}

	@Override
	protected void onValidate() {
		if (from.getConvertedInput() != null && to.getConvertedInput() != null && from.getConvertedInput().isAfter(to.getConvertedInput())) {
			error(getString("1592"));
		}
	}

	protected Invitation create(User u) {
		Invitation i = new Invitation(getModelObject());
		i.setInvitedBy(getBean(UserDao.class).get(getUserId()));
		i.setId(null);
		i.setUpdated(null);
		i.setUsed(false);

		i.setPassword(CryptProvider.get().hash(i.getPassword()));
		i.setValidFrom(getDate(from.getModelObject().minusMinutes(5), timeZoneId.getModelObject()));
		i.setValidTo(getDate(to.getModelObject(), timeZoneId.getModelObject()));

		i.setInvitee(u);
		i.setHash(UUID.randomUUID().toString());
		if (Type.contact == u.getType()) {
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
		recipients.setModelObject(new ArrayList<User>());
		recipients.setEnabled(true);
		passwd.setEnabled(false);
		final boolean isPeriod = i.getValid() == Valid.Period;
		from.setEnabled(isPeriod);
		to.setEnabled(isPeriod);
		timeZoneId.setEnabled(isPeriod);
		target.add(this);
	}

	public void setDialog(InvitationDialog dialog) {
		this.dialog = dialog;
	}

	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (button.equals(dialog.getCancel())) {
			dialog.onSuperClick(target, button);
		} else if (button.equals(dialog.getGenerate())) {
			Invitation i = create(recipients.getModelObject().iterator().next());
			setModelObject(i);
			url.setModelObject(getInvitationLink(i, WebSession.get().getExtendedProperties().getBaseUrl()));
			target.add(url);
		} else if (button.equals(dialog.getSend())) {
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
			dialog.onSuperClick(target, button);
		}
	}
}
