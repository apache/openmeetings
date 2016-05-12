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
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.LanguageDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.datetime.AjaxDateTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.DateTimePicker;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class InvitationDialog extends AbstractFormDialog<Invitation> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationDialog.class, webAppRootKey);
	private final DialogButton generate = new DialogButton("generate", Application.getString(1526));
	private final DialogButton send = new DialogButton("send", Application.getString(218));
	private final DialogButton cancel = new DialogButton("cancel", Application.getString(219));
	private final InvitationForm form;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final Long roomId;
	private final IModel<String> subject = Model.of((String)null);
	private final IModel<String> message = Model.of((String)null);
	private final IModel<String> tzId = Model.of((String)null);
	private final IModel<InviteeType> inviteeType = Model.of(InviteeType.user);
	private long lang;
	private final TextField<String> url = new TextField<String>("url", Model.of((String)null));
	enum InviteeType {
		user
		, group
	}
	private final UserMultiChoice recipients = new UserMultiChoice("recipients", new CollectionModel<User>(new ArrayList<User>()));
	private final Select2MultiChoice<Group> groups = new Select2MultiChoice<Group>("groups"
			, new CollectionModel<Group>(new ArrayList<Group>())
			, new ChoiceProvider<Group>() {
				private static final long serialVersionUID = 1L;

				@Override
				public void query(String term, int page, Response<Group> response) {
					if (WebSession.getRights().contains(User.Right.Admin)) {
						List<Group> groups = getBean(GroupDao.class).get(0, Integer.MAX_VALUE);
						for (Group g : groups) {
							if (Strings.isEmpty(term) || g.getName().toLowerCase().contains(term.toLowerCase())) {
								response.add(g);
							}
						}
					} else {
						User u = getBean(UserDao.class).get(getUserId());
						for (GroupUser ou : u.getGroupUsers()) {
							if (Strings.isEmpty(term) || ou.getGroup().getName().toLowerCase().contains(term.toLowerCase())) {
								response.add(ou.getGroup());
							}
						}
					}
				}

				@Override
				public Collection<Group> toChoices(Collection<String> ids) {
					Collection<Group> c = new ArrayList<>();
					for (String id : ids) {
						c.add(getBean(GroupDao.class).get(Long.valueOf(id)));
					}
					return c;
				}

				@Override
				public String getDisplayValue(Group choice) {
					return choice.getName();
				}

				@Override
				public String getIdValue(Group choice) {
					Long id = choice.getId();
					return id == null ? null : "" + id;
				}
			});
	private final WebMarkupContainer sipContainer = new WebMarkupContainer("sip-container");

	public InvitationDialog(String id, Long roomId) {
		super(id, Application.getString(214), new CompoundPropertyModel<Invitation>(new Invitation()));
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
		if (i.getRoom() != null) {
			target.add(sipContainer.replace(new Label("room.confno", i.getRoom().getConfno())).setVisible(i.getRoom().isSipEnabled()));
		}
		Calendar d = Calendar.getInstance();
		i.setValidFrom(d.getTime());
		d.add(Calendar.DATE, 1);
		i.setValidTo(d.getTime());
		i.setPassword(null);
		i.setHash(null);
		subject.setObject(null);
		message.setObject(null);
		recipients.setModelObject(new ArrayList<User>());
		recipients.setEnabled(true);
		groups.setModelObject(new ArrayList<Group>());
		groups.setEnabled(false);
		tzId.setObject(u.getTimeZoneId());
		lang = u.getLanguageId();
		url.setModelObject(null);
		inviteeType.setObject(InviteeType.user);
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
	public DialogButton getSubmitButton() {
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
			Invitation i = create(recipients.getModelObject().iterator().next());
			form.setModelObject(i);
			url.setModelObject(getInvitationLink(getBean(ConfigurationDao.class).getBaseUrl(), i));
			target.add(url);
		} else if (button.equals(send)) {
			if (Strings.isEmpty(url.getModelObject())) {
				if (inviteeType.getObject() == InviteeType.user) {
					for (User u : recipients.getModelObject()) {
						Invitation i = create(u);
						try {
							getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getObject(), message.getObject(), false);
						} catch (Exception e) {
							log.error("error while sending invitation by User ", e);
						}
					}
				} else {
					for (Group g : groups.getModelObject()) {
						for (GroupUser ou : getBean(GroupUserDao.class).get(g.getId(), 0, Integer.MAX_VALUE)) {
							Invitation i = create(ou.getUser());
							try {
								getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getObject(), message.getObject(), false);
							} catch (Exception e) {
								log.error("error while sending invitation by Group ", e);
							}
						}
					}
				}
			} else {
				Invitation i = form.getModelObject();
				try {
					getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getObject(), message.getObject(), false);
				} catch (Exception e) {
					log.error("error while sending invitation by URL ", e);
				}
			}
			super.onClick(target, button);
		}
	}
	
	private Invitation create(User u) {
		Invitation i = new Invitation(form.getModelObject());
		i.setId(null);
		i.setUpdated(null);
		i.setUsed(false);
		
		i.setPassword(CryptProvider.get().hash(i.getPassword())); //FIXME should be hidden
		//FIXME another HACK
		Calendar d = Calendar.getInstance();
		d.setTime(i.getValidFrom());
		d.add(Calendar.MINUTE, -5);
		i.setValidFrom(d.getTime());
		
		i.setInvitee(u);
		i.setHash(UUID.randomUUID().toString());
		if (Type.contact == u.getType()) {
			//TODO not sure it is right
			u.setLanguageId(lang);
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
		private final WebMarkupContainer groupContainer = new WebMarkupContainer("groupContainer");
		
		public InvitationForm(String id, IModel<Invitation> model) {
			super(id, model);
			boolean showGroups = AuthLevelUtil.hasAdminLevel(getRights());
			RadioGroup<InviteeType> rdi = new RadioGroup<>("inviteeType", inviteeType);
			add(rdi.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					boolean groupsEnabled = InviteeType.group == inviteeType.getObject();
					updateButtons(target);
					target.add(groups.setEnabled(groupsEnabled), recipients.setEnabled(!groupsEnabled));
				}
			}));
			//TODO list should be updated on open
			rdi.add(recipients.setLabel(Model.of(Application.getString(216))).setRequired(true).add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					url.setModelObject(null);
					updateButtons(target);
				}
			}).setOutputMarkupId(true));
			groupContainer.add(
				groups.setLabel(Model.of(Application.getString(126))).setRequired(true).add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						url.setModelObject(null);
						updateButtons(target);
					}
				}).setOutputMarkupId(true)
				, new Radio<InviteeType>("group", Model.of(InviteeType.group))
			);
			rdi.add(groupContainer.setVisible(showGroups));
			rdi.add(new Radio<InviteeType>("user", Model.of(InviteeType.user)));

			rdi.add(new TextField<String>("subject", subject));
			rdi.add(new TextArea<String>("message", message));
			rdi.add(new AjaxCheckBox("passwordProtected") {
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
			rdi.add(valid.add(new Radio<Valid>("one", Model.of(Valid.OneTime))
					, new Radio<Valid>("period", Model.of(Valid.Period))
					, new Radio<Valid>("endless", Model.of(Valid.Endless))));
			rdi.add(passwd = new PasswordTextField("password"));
			Invitation i = getModelObject();
			passwd.setLabel(Model.of(Application.getString(525))).setOutputMarkupId(true).setEnabled(i.isPasswordProtected());
			rdi.add(from = new AjaxDateTimePicker("validFrom", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			rdi.add(to = new AjaxDateTimePicker("validTo", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			rdi.add(timeZoneId = new DropDownChoice<String>("timeZoneId", tzId, AVAILABLE_TIMEZONES));
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
			rdi.add(new LanguageDropDown("language", new PropertyModel<Long>(InvitationDialog.this, "lang")));
			rdi.add(url.setOutputMarkupId(true));
			rdi.add(sipContainer.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
			sipContainer.add(new Label("room.confno", "")).setVisible(false);
			add(feedback);
		}
		
		private void updateButtons(AjaxRequestTarget target) {
			if (inviteeType.getObject() == InviteeType.user) {
				Collection<User> to = recipients.getModelObject();
				send.setEnabled(to.size() > 0, target);
				generate.setEnabled(to.size() == 1, target);
			} else {
				Collection<Group> to = groups.getModelObject();
				send.setEnabled(to.size() > 0, target);
				generate.setEnabled(false, target);
			}
		}
		
		@Override
		protected void onValidate() {
			if (from.getConvertedInput() != null && to.getConvertedInput() != null && from.getConvertedInput().after(to.getConvertedInput())) {
				error(Application.getString(1592));
			}
		}
	}
	
	@Override
	protected void onDetach() {
		subject.detach();
		message.detach();
		tzId.detach();
		inviteeType.detach();
		super.onDetach();
	}
}
