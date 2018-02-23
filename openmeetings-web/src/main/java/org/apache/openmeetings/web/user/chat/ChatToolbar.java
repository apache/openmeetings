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
package org.apache.openmeetings.web.user.chat;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.core.util.WebSocketHelper.ID_ALL;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.wicket.util.time.Duration.NONE;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar;

/**
 * Provides a custom implementation for com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar suitable
 * for chat}
 */
public class ChatToolbar extends Panel implements IWysiwygToolbar {
	private static final long serialVersionUID = 1L;
	private static final String CHAT_FNAME_TMPL = "chatlog_%s.txt";
	private final WebMarkupContainer toolbar = new WebMarkupContainer("toolbar");
	private final WebMarkupContainer save = new WebMarkupContainer("save");
	private final ChatForm chatForm;
	private ConfirmableAjaxBorder delBtn;
	private final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new ResourceStreamResource() {
		private static final long serialVersionUID = 1L;
		private static final char DELIMITER = ',';
		private static final char QUOTE_CHAR = '"';
		private final String quoteReplacement = new StringBuilder().append(QUOTE_CHAR).append(QUOTE_CHAR).toString();

		{
			setCacheDuration(NONE);
		}

		@Override
		protected ResourceResponse newResourceResponse(Attributes attributes) {
			ResourceResponse rr = super.newResourceResponse(attributes);
			final boolean admin = hasAdminLevel(getRights());
			if (!chatForm.process(
					() -> admin
					, r -> admin || isModerator(getUserId(), r.getId())
					, u -> true))
			{
				rr.setError(HttpServletResponse.SC_FORBIDDEN);
			}
			return rr;
		}

		private String getName(User u) {
			return String.format("%s %s", u.getFirstname(), u.getLastname());
		}

		private StringBuilder appendQuoted(StringBuilder sb, String value) {
			return sb.append(QUOTE_CHAR).append(value == null ? "" : value.replace(String.valueOf(QUOTE_CHAR), quoteReplacement)).append(QUOTE_CHAR);
		}

		private void export(List<ChatMessage> list, StringBuilder sb) {
			String lineDelim = "";
			for (ChatMessage msg : list) {
				sb.append(lineDelim);
				appendQuoted(sb, getName(msg.getFromUser())).append(DELIMITER);
				appendQuoted(sb, getDateFormat().format(msg.getSent())).append(DELIMITER);
				appendQuoted(sb, msg.getMessage());
				lineDelim = "\r\n";
			}
		}

		@Override
		protected IResourceStream getResourceStream(Attributes attributes) {
			final ChatDao dao = getBean(ChatDao.class);
			final boolean admin = hasAdminLevel(getRights());
			final StringBuilder sb = new StringBuilder();
			chatForm.process(
					() -> {
						if (admin) {
							setFileName(String.format(CHAT_FNAME_TMPL, "global"));
							export(dao.getGlobal(0, Integer.MAX_VALUE), sb);
						}
						return true;
					}
					, r -> {
						if (admin || isModerator(getUserId(), r.getId())) {
							setFileName(String.format(CHAT_FNAME_TMPL, "room_" + r.getId()));
							export(dao.getRoom(r.getId(), 0, Integer.MAX_VALUE, true), sb);
						}
						return true;
					}, u -> {
						setFileName(String.format(CHAT_FNAME_TMPL, "user_" + u.getId()));
						export(dao.getUser(u.getId(), 0, Integer.MAX_VALUE), sb);
						return true;
					});
			StringResourceStream srs = new StringResourceStream(sb, "text/csv");
			srs.setCharset(UTF_8);
			return srs;
		}
	});

	/**
	 * Constructor
	 *
	 * @param id
	 *            the markup-id
	 * @param form
	 *            chat form
	 */
	public ChatToolbar(String id, ChatForm form) {
		this(id, form, null);
	}

	/**
	 * Constructor
	 *
	 * @param id
	 *            the markup-id
	 * @param form
	 *            chat form
	 * @param model
	 *            the {@link org.apache.wicket.model.IModel}
	 */
	public ChatToolbar(String id, ChatForm form, IModel<String> model) {
		super(id, model);
		this.chatForm = form;
	}

	@Override
	public void attachToEditor(Component editor) {
		toolbar.add(AttributeModifier.replace("data-target", JQueryWidget.getSelector(editor)));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		BasePage page = (BasePage)getPage();
		add(toolbar.add(new WebMarkupContainer("hyperlink").add(AttributeModifier.append("class", page.isRtl() ? "dropdown-menu-left" : "dropdown-menu-right"))));
		add(download);
		delBtn = new ConfirmableAjaxBorder("delete", getString("80"), getString("832"), chatForm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				final ChatDao dao = getBean(ChatDao.class);
				final String scope = chatForm.getScope();
				final boolean admin = hasAdminLevel(getRights());
				chatForm.process(
					() -> {
						if (admin) {
							dao.deleteGlobal();
							clean(target, ID_ALL);
						}
						return true;
					}
					, r -> {
						if (admin || isModerator(getUserId(), r.getId())) {
							dao.deleteRoom(r.getId());
							clean(target, scope);
						}
						return true;
					}, u -> {
						dao.deleteUser(u.getId());
						clean(target, scope);
						return true;
					});
			}
		};
		toolbar.add(delBtn.setVisible(hasAdminLevel(getRights())).setOutputMarkupId(true)
				.setOutputMarkupPlaceholderTag(true));
		toolbar.add(save.setVisible(hasAdminLevel(getRights())).setOutputMarkupId(true)
				.setOutputMarkupPlaceholderTag(true).add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						download.initiate(target);
					}
				}));
	}

	private static void clean(AjaxRequestTarget target, String scope) {
		target.appendJavaScript("$('#" + scope + "').html('')");
	}

	void update(AjaxRequestTarget target) {
		final boolean admin = hasAdminLevel(getRights());
		chatForm.process(
			() -> {
				target.add(save.setVisible(admin), delBtn.setVisible(admin));
				return true;
			}
			, r -> {
				final boolean moder = admin || isModerator(getUserId(), r.getId());
				target.add(save.setVisible(moder), delBtn.setVisible(moder));
				return true;
			}, u -> {
				target.add(save.setVisible(true), delBtn.setVisible(true));
				return true;
			});
	}
}
