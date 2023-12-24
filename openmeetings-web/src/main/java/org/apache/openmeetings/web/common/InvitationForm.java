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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.app.Application.getInvitationLink;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.util.CalendarHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getZoneId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
import org.apache.openmeetings.web.common.datetime.OmDateTimePicker;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import jakarta.inject.Inject;

public abstract class InvitationForm extends Form<Invitation> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(InvitationForm.class);
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final PasswordTextField passwd = new PasswordTextField("password");
	private final DropDownChoice<String> timeZoneId = new DropDownChoice<>("timeZoneId", Model.of((String)null), AVAILABLE_TIMEZONES);
	private final OmDateTimePicker from = new OmDateTimePicker("from", Model.of(LocalDateTime.now()));
	private final OmDateTimePicker to = new OmDateTimePicker("to", Model.of(LocalDateTime.now()));
	private final LanguageDropDown lang = new LanguageDropDown("language", Model.of((Long)null));
	protected final TextField<String> subject = new TextField<>("subject", Model.of((String)null));
	protected final TextArea<String> message = new TextArea<>("message", Model.of((String)null));
	protected final TextField<String> url = new TextField<>("url", Model.of((String)null));
	protected final UserMultiChoice recipients = new UserMultiChoice("recipients", new CollectionModel<>(new ArrayList<>()));
	protected InvitationDialog dialog;
	protected String dropDownParentId;

	@Inject
	private InvitationDao inviteDao;
	@Inject
	private UserDao userDao;
	@Inject
	private InvitationManager inviteManager;

	public enum Action {
		GENERATE
		, SEND
	}

	protected InvitationForm(String id, String dropDownParentId) {
		super(id, new CompoundPropertyModel<>(new Invitation()));
		this.dropDownParentId = dropDownParentId;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		add(subject, message);
		recipients.setLabel(new ResourceModel("216")).setRequired(true).add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
			url.setModelObject(null);
			updateButtons(target);
		})).setOutputMarkupId(true);
		if (dropDownParentId != null) {
			recipients.getSettings().setDropdownParent(dropDownParentId);
		}
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
				boolean dateEnabled = InvitationForm.this.getModelObject().getValid() == Valid.PERIOD;
				target.add(from.setEnabled(dateEnabled), to.setEnabled(dateEnabled), timeZoneId.setEnabled(dateEnabled));
			}
		});
		add(valid.add(new Radio<>("one", Model.of(Valid.ONE_TIME))
				, new Radio<>("period", Model.of(Valid.PERIOD))
				, new Radio<>("endless", Model.of(Valid.ENDLESS))));
		add(passwd);
		Invitation i = getModelObject();
		passwd.setLabel(new ResourceModel("110")).setOutputMarkupId(true).setEnabled(i.isPasswordProtected());
		add(from.setLabel(new ResourceModel("530")).setOutputMarkupId(true)
				, to.setLabel(new ResourceModel("531")).setOutputMarkupId(true)
				, timeZoneId.setOutputMarkupId(true));
		timeZoneId.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
			//no-op added to preserve selection
		}));
		add(url.setOutputMarkupId(true));
		add(lang, feedback.setOutputMarkupId(true));
		super.onInitialize();
	}

	protected void updateButtons(AjaxRequestTarget target) {
		Collection<User> recpnts = recipients.getModelObject();
		boolean generateEnabled = false;
		if (recpnts.size() == 1) {
			User u = recpnts.iterator().next();
			generateEnabled = getUserId().equals(u.getId()) || User.Type.CONTACT == u.getType();
		}
		target.add(
				dialog.getSend().setEnabled(!recpnts.isEmpty())
				, dialog.getGenerate().setEnabled(generateEnabled)
				);
	}

	@Override
	protected void onValidate() {
		if (from.getConvertedInput() != null && to.getConvertedInput() != null && from.getConvertedInput().isAfter(to.getConvertedInput())) {
			error(getString("1592"));
		}
	}

	protected Invitation create(User u) {
		Invitation i = new Invitation(getModelObject());
		i.setInvitedBy(userDao.get(getUserId()));
		i.setId(null);
		i.setUsed(false);

		i.setPassword(CryptProvider.get().hash(i.getPassword()));
		i.setValidFrom(getDate(from.getModelObject().minusMinutes(5), timeZoneId.getModelObject()));
		i.setValidTo(getDate(to.getModelObject(), timeZoneId.getModelObject()));

		i.setInvitee(u);
		i.setHash(randomUUID().toString());
		if (Type.CONTACT == u.getType()) {
			u.setLanguageId(lang.getModelObject());
		}
		return inviteDao.update(i);
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
		User u = userDao.get(getUserId());
		i.setInvitedBy(u);
		i.setRoom(null);
		LocalDateTime now = ZonedDateTime.now(getZoneId()).toLocalDateTime();
		from.setModelObject(now);
		to.setModelObject(now.plusDays(1));
		i.setPassword(null);
		i.setHash(null);
		subject.setModelObject(null);
		message.setModelObject(null);
		timeZoneId.setModelObject(u.getTimeZoneId());
		lang.setModelObject(u.getLanguageId());
		url.setModelObject(null);
		setModelObject(i);
		recipients.setModelObject(new ArrayList<>());
		recipients.setEnabled(true);
		passwd.setEnabled(false);
		final boolean isPeriod = i.getValid() == Valid.PERIOD;
		from.setEnabled(isPeriod);
		to.setEnabled(isPeriod);
		timeZoneId.setEnabled(isPeriod);
		target.add(this);
	}

	public void setDialog(InvitationDialog dialog) {
		this.dialog = dialog;
	}

	public void onClick(AjaxRequestTarget target, Action action) {
		final String userbaseUrl = WebSession.get().getExtendedProperties().getBaseUrl();
		if (Action.GENERATE == action) {
			Invitation i = create(recipients.getModelObject().iterator().next());
			setModelObject(i);
			url.setModelObject(getInvitationLink(i, userbaseUrl));
			target.add(url);
		} else {
			if (Strings.isEmpty(url.getModelObject())) {
				for (User u : recipients.getModelObject()) {
					Invitation i = create(u);
					try {
						inviteManager.sendInvitationLink(i, MessageType.CREATE, subject.getModelObject(), message.getModelObject(), false, userbaseUrl);
					} catch (Exception e) {
						log.error("error while sending invitation by User ", e);
					}
				}
			} else {
				Invitation i = getModelObject();
				try {
					inviteManager.sendInvitationLink(i, MessageType.CREATE, subject.getModelObject(), message.getModelObject(), false, userbaseUrl);
				} catch (Exception e) {
					log.error("error while sending invitation by URL ", e);
				}
			}
			dialog.close(target);
		}
	}
}
