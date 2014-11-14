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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getInvitationLink;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.util.crypt.MD5;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.datetime.AjaxDateTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.DateTimePicker;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class InvitationDialog extends AbstractFormDialog<Invitation> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationDialog.class, webAppRootKey);
	private final DialogButton generate = new DialogButton(WebSession.getString(1526));
	private final DialogButton send = new DialogButton(WebSession.getString(218));
	private final DialogButton cancel = new DialogButton(WebSession.getString(219));
	private final InvitationForm form;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final long roomId;
	private final IModel<String> subject = Model.of((String)null);
	private final IModel<String> message = Model.of((String)null);
	private final IModel<String> tzId = Model.of((String)null);
	private final IModel<FieldLanguage> lang = Model.of((FieldLanguage)null);
	private final IModel<Collection<User>> modelTo = new CollectionModel<User>(new ArrayList<User>());
	private final TextField<String> url = new TextField<String>("url", Model.of((String)null));

	public InvitationDialog(String id, long roomId) {
		super(id, WebSession.getString(214), new CompoundPropertyModel<Invitation>(new Invitation()));
		this.roomId = roomId;
		add(form = new InvitationForm("form", getModel()));
	}

	@Override
	public int getWidth() {
		return 500;
	}

	public void updateModel(AjaxRequestTarget target) {
		Invitation i = new Invitation();
		User u = getBean(UserDao.class).get(getUserId());
		i.setInvitedBy(u);
		i.setRoom(getBean(RoomDao.class).get(roomId));
		Calendar d = Calendar.getInstance();
		i.setValidFrom(d.getTime());
		d.add(Calendar.DATE, 1);
		i.setValidTo(d.getTime());
		i.setPassword("ASDDF");
		String hashRaw = "HASH" + (System.currentTimeMillis());
		try {
			i.setHash(MD5.do_checksum(hashRaw));
		} catch (NoSuchAlgorithmException e) {
			log.error("Unexpected error while creating invitation", e);
			throw new RuntimeException(e);
		}
		subject.setObject(null);
		message.setObject(null);
		modelTo.setObject(new ArrayList<User>());
		tzId.setObject(u.getTimeZoneId());
		lang.setObject(getBean(FieldLanguageDao.class).get(u.getLanguageId()));
		url.setModelObject(null);
		form.setModelObject(i);
		send.setEnabled(false, target);
		generate.setEnabled(false, target);
		target.add(form);
	}

	@Override
	protected Form<?> getForm(DialogButton button) {
		if (button.equals(generate) || button.equals(send)) {
			return form;
		}
		return super.getForm(button);
	}
	
	@Override
	public InvitationForm getForm() {
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
		target.add(feedback);
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		//TODO need to be reviewed
		if (button.equals(cancel)) {
			super.onClick(target, button);
		} else if (button.equals(generate)) {
			Invitation i = create(modelTo.getObject().iterator().next());
			form.setModelObject(i);
			url.setModelObject(getInvitationLink(getBean(ConfigurationDao.class).getBaseUrl(), i));
			target.add(url);
		} else if (button.equals(send)) {
			if (Strings.isEmpty(url.getModelObject())) {
				for (User u : modelTo.getObject()) {
					Invitation i = create(u);
					try {
						getBean(InvitationManager.class).sendInvitionLink(i, MessageType.Create, subject.getObject(), message.getObject(), false);
					} catch (Exception e) {
						log.error("error while sending invitation ", e);
					}
				}
			} else {
				//FIXME To might be changed and it would'n be reflected, might lead to misunderstandings
				Invitation i = form.getModelObject();
				try {
					getBean(InvitationManager.class).sendInvitionLink(i, MessageType.Create, subject.getObject(), message.getObject(), false);
				} catch (Exception e) {
					log.error("error while sending invitation ", e);
				}
			}
			super.onClick(target, button);
		}
	}
	
	private Invitation create(User u) {
		Invitation i = form.getModelObject();
		
		i.setPassword(ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(i.getPassword())); //FIXME should be hidden
		//FIXME another HACK
		Calendar d = Calendar.getInstance();
		d.setTime(i.getValidFrom());
		d.add(Calendar.MINUTE, -5);
		i.setValidFrom(d.getTime());
		
		i.setInvitee(u);
		if (Type.contact == u.getType()) {
			//TODO not sure it is right
			u.setLanguageId(lang.getObject().getId());
		}
		return getBean(InvitationDao.class).update(i);
	}
	
	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		//designed to be empty because of multiple submit buttons
	}
	
	private class InvitationForm extends Form<Invitation> {
		private static final long serialVersionUID = 1L;
		private final PasswordTextField passwd;
		private final DateTimePicker from;
		private final DateTimePicker to;
		private final DropDownChoice<String> timeZoneId;
		
		public InvitationForm(String id, IModel<Invitation> model) {
			super(id, model);
			add(new UserMultiChoice("recipients", modelTo).setLabel(Model.of(WebSession.getString(216))).setRequired(true)
					.add(new AjaxFormComponentUpdatingBehavior("change") {
						private static final long serialVersionUID = 1L;
		
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							Collection<User> to = modelTo.getObject();
							send.setEnabled(to.size() > 0, target);
							generate.setEnabled(to.size() == 1, target);
						}
					}));
			add(new TextField<String>("subject", subject));
			add(new TextArea<String>("message", message));
			add(new AjaxCheckBox("passwordProtected") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					InvitationForm.this.getModelObject().setPasswordProtected(getConvertedInput());
					passwd.setEnabled(getConvertedInput());
					target.add(passwd);
				}
			});
			add(new RadioGroup<Valid>("valid").add(
					new Radio<Valid>("one", Model.of(Valid.OneTime)).add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
							target.add(from.setEnabled(false), to.setEnabled(false), timeZoneId.setEnabled(false));
						}
					})
					, new Radio<Valid>("period", Model.of(Valid.Period)).add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
							target.add(from.setEnabled(true), to.setEnabled(true), timeZoneId.setEnabled(true));
						}
					})
					, new Radio<Valid>("endless", Model.of(Valid.Endless)).add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
							target.add(from.setEnabled(false), to.setEnabled(false), timeZoneId.setEnabled(false));
						}
					})
					));
			add(passwd = new PasswordTextField("password"));
			Invitation i = getModelObject();
			passwd.setLabel(Model.of(WebSession.getString(525))).setOutputMarkupId(true).setEnabled(i.isPasswordProtected());
			add(from = new AjaxDateTimePicker("validFrom", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			add(to = new AjaxDateTimePicker("validTo", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			add(timeZoneId = new DropDownChoice<String>("timeZoneId", tzId, AVAILABLE_TIMEZONES));
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
			add(new DropDownChoice<FieldLanguage>("language", lang, getBean(FieldLanguageDao.class).get(), new ChoiceRenderer<FieldLanguage>("name", "id")));
			add(url.setOutputMarkupId(true));
			add(feedback);
		}
		
		@Override
		protected void onValidate() {
			if (from.getConvertedInput() != null && to.getConvertedInput() != null && from.getConvertedInput().after(to.getConvertedInput())) {
				error(WebSession.getString(1592));
			}
		}
	}
}
