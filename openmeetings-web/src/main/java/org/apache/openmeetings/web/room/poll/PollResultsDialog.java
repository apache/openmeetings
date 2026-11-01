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
package org.apache.openmeetings.web.room.poll;

import static org.apache.openmeetings.core.util.WebSocketHelper.sendRoom;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome7IconType;
import jakarta.inject.Inject;

public class PollResultsDialog extends Modal<RoomPoll> {
	private static final long serialVersionUID = 1L;
	private static final String NUM_TICKS = getValList(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
	private String boolTicks;

	private final Long roomId;
	private PollSelectForm selForm;
	private PollResultsForm dispForm;
	private BootstrapAjaxLink<String> close;
	private BootstrapAjaxLink<String> delete;
	private BootstrapAjaxLink<String> clone;
	private boolean moderator = false;
	private boolean opened = false;
	private String chartSimple;
	private String chartPie;
	private String setLabel;
	private final CreatePollDialog createPoll;
	private DropDownChoice<String> chartType;

	@Inject
	private PollDao pollDao;
	@Inject
	private UserDao userDao;

	public PollResultsDialog(String id, CreatePollDialog createPoll, Long roomId) {
		super(id);
		this.roomId = roomId;
		this.createPoll = createPoll;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("37"));
		setUseCloseHandler(true);

		chartSimple = getString("1414");
		chartPie = getString("1415");
		setLabel = getString("40");
		boolTicks = getValList(List.of(getString("35"), getString("34")));
		add(selForm = new PollSelectForm("selForm"));
		add(dispForm = new PollResultsForm("dispForm"));
		addButton(close = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("1418")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				Long id = dispForm.getModelObject().getId();
				pollDao.close(roomId);
				selForm.updateModel(target);

				RoomPoll p = pollDao.get(id);
				selForm.select.setModelObject(p);
				dispForm.updateModel(p, target);
				sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.POLL_UPDATED));
				close(target);
			}
		});
		close.setIconType(FontAwesome7IconType.xmark_s).add(newOkCancelDangerConfirm(this, getString("1419")));
		close.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(delete = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("1420")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pollDao.delete(dispForm.getModelObject());
				selForm.updateModel(target);
				dispForm.updateModel(selForm.select.getModelObject(), target);
				sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.POLL_UPDATED));
				close(target);
			}
		});
		delete.setIconType(FontAwesome7IconType.trash_s).add(newOkCancelDangerConfirm(this, getString("1421")));
		delete.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(clone = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("poll.clone")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				RoomPoll rp = dispForm.getModelObject();
				RoomPoll nrp = new RoomPoll();
				nrp.setCreator(userDao.get(getUserId()));
				nrp.setName(rp.getName());
				nrp.setQuestion(rp.getQuestion());
				nrp.setType(rp.getType());
				nrp.setRoom(rp.getRoom());
				createPoll.setModelObject(nrp);
				createPoll.setModelObject(nrp);
				target.add(createPoll.getForm());
				createPoll.show(target);
				close(target);
			}
		});
		clone.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	private static <T> String getValList(List<T> data) {
		return data.stream()
			.map(v -> "'" + v + "'")
			.collect(Collectors.joining(", "));
	}
	public void updateModel(IPartialPageRequestHandler target, boolean moderator) {
		selForm.updateModel(target);
		this.moderator = moderator;
		RoomPoll p = selForm.select.getModelObject();
		dispForm.updateModel(p, target);
	}

	@Override
	public Modal<RoomPoll> show(IPartialPageRequestHandler handler) {
		opened = true;
		super.show(handler);
		redraw(handler);
		return this;
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler) {
		opened = false;
	}

	public boolean isOpened() {
		return opened;
	}

	private String getTicks(RoomPoll p) {
		return p != null && RoomPoll.Type.NUMERIC == p.getType()
				? NUM_TICKS
				: boolTicks;
	}

	private static String getValues(RoomPoll p) {
		List<AtomicInteger> values = Stream
			.generate(() -> new AtomicInteger(0))
			.limit(p != null && RoomPoll.Type.NUMERIC == p.getType() ? 10 : 2)
			.toList();
		if (p != null) {
			for (RoomPollAnswer a : p.getAnswers()) {
				int idx = RoomPoll.Type.NUMERIC == p.getType() ? a.getPointList() - 1 : (Boolean.FALSE.equals(a.getAnswer()) ? 0 : 1);
				values.get(idx).incrementAndGet();
			}
		}
		return values.stream().map(String::valueOf).collect(Collectors.joining(", "));
	}

	private String getScript() {
		RoomPoll p = dispForm.getModelObject();
		return "OmCharts.updateChart('room-poll-chart', '%s', '%s', [%s], [%s]);"
			.formatted(
				chartSimple.equals(chartType.getModelObject()) ? "bar" : "pie"
				, setLabel
				, getTicks(p)
				, getValues(p)
			);
	}
	private void redraw(IPartialPageRequestHandler handler) {
		handler.appendJavaScript(getScript());
	}

	private class PollSelectForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private DropDownChoice<RoomPoll> select;

		PollSelectForm(String id) {
			super(id);
			setOutputMarkupId(true);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add((select = new DropDownChoice<>("polls", Model.of((RoomPoll)null), new ArrayList<>(), new ChoiceRenderer<RoomPoll>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getDisplayValue(RoomPoll poll) {
					return poll == null ? ""
						: poll.getName() + (poll.isArchived() ? "" : " (" + getString("1413") + ")");
				}

				@Override
				public String getIdValue(RoomPoll poll, int index) {
					return poll == null ? "" : "" + poll.getId();
				}
			})).add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> dispForm.updateModel(select.getModelObject(), target))));
			updateModel(null);
		}

		public void updateModel(IPartialPageRequestHandler handler) {
			List<RoomPoll> list = new ArrayList<>();
			RoomPoll p = pollDao.getByRoom(roomId);
			if (p != null) {
				list.add(p);
			}
			list.addAll(pollDao.getArchived(roomId));
			select.setChoices(list);
			select.setModelObject(list.isEmpty() ? null : list.get(0));
			if (handler != null) {
				handler.add(this);
			}
		}
	}

	private class PollResultsForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private final Label name = new Label("name", Model.of((String)null));
		private final Label question = new Label("question", Model.of((String)null));
		private final Label count = new Label("count", Model.of(0));

		PollResultsForm(String id) {
			super(id, Model.of((RoomPoll)null));
			setOutputMarkupId(true);
		}

		@Override
		protected void onInitialize() {
			add(name, question, count);
			chartType = new DropDownChoice<>("chartType", Model.of(chartSimple), List.of(chartSimple, chartPie));
			add(chartType.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> redraw(target))));
			super.onInitialize();
		}

		public void updateModel(RoomPoll poll, IPartialPageRequestHandler handler) {
			setModelObject(poll);
			name.setDefaultModelObject(poll == null ? "" : VoteDialog.getName(this, poll.getCreator()));
			question.setDefaultModelObject(poll == null ? "" : poll.getQuestion());
			count.setDefaultModelObject(poll == null ? 0 : poll.getAnswers().size());
			handler.add(this);
			close.setVisible(moderator && (poll != null && !poll.isArchived()));
			clone.setVisible(moderator && (poll != null && poll.isArchived()));
			delete.setVisible(moderator);
			handler.add(close, clone, delete);
			redraw(handler);
		}
	}
}
