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
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.wicketstuff.jqplot.behavior.JqPlotBehavior;
import org.wicketstuff.jqplot.behavior.JqPlotCssResourceReference;
import org.wicketstuff.jqplot.behavior.JqPlotJavascriptResourceReference;
import org.wicketstuff.jqplot.lib.Chart;
import org.wicketstuff.jqplot.lib.ChartConfiguration;
import org.wicketstuff.jqplot.lib.JqPlotResources;
import org.wicketstuff.jqplot.lib.JqPlotUtils;
import org.wicketstuff.jqplot.lib.chart.BarChart;
import org.wicketstuff.jqplot.lib.chart.PieChart;
import org.wicketstuff.jqplot.lib.elements.Highlighter;
import org.wicketstuff.jqplot.lib.elements.Location;
import org.wicketstuff.jqplot.lib.elements.RendererOptions;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

/**
 * @author solomax
 *
 */
public class PollResultsDialog extends AbstractDialog<RoomPoll> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer chartDiv = new WebMarkupContainer("chart");
	private final Long roomId;
	private final PollSelectForm selForm;
	private final PollResultsForm dispForm;
	private DialogButton cancel;
	private DialogButton close;
	private DialogButton delete;
	private DialogButton clone;
	private boolean moderator = false;
	private MessageDialog closeConfirm;
	private MessageDialog deleteConfirm;
	private boolean opened = false;
	private final CreatePollDialog createPoll;

	public PollResultsDialog(String id, CreatePollDialog createPoll, Long _roomId) {
		super(id, "");
		this.roomId = _roomId;
		this.createPoll = createPoll;
		add(selForm = new PollSelectForm("selForm"));
		add(dispForm = new PollResultsForm("dispForm"));
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("37"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		close = new DialogButton("close", getString("1418"));
		delete = new DialogButton("delete", getString("1420"));
		clone = new DialogButton("clone", getString("poll.clone"));
		add(closeConfirm = new MessageDialog("closeConfirm", getString("1418"), getString("1419"), DialogButtons.YES_NO, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				if(button != null && button.match(YES)) {
					Long id = dispForm.getModelObject().getId();
					getBean(PollDao.class).close(roomId);
					selForm.updateModel(handler);

					RoomPoll p = getBean(PollDao.class).get(id);
					selForm.select.setModelObject(p);
					dispForm.updateModel(p, true, handler);
					sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.pollUpdated));
				}
			}
		});
		add(deleteConfirm = new MessageDialog("deleteConfirm", getString("1420"), getString("1421"), DialogButtons.YES_NO, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				if(button != null && button.match(YES)) {
					getBean(PollDao.class).delete(dispForm.getModelObject());
					selForm.updateModel(handler);
					dispForm.updateModel(selForm.select.getModelObject(), true, handler);
					sendRoom(new RoomMessage(roomId, findParent(MainPanel.class).getClient(), RoomMessage.Type.pollUpdated));
				}
			}
		});
		super.onInitialize();
	}

	@Override
	public int getWidth() {
		return 500;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(clone, delete, close, cancel);
	}

	public void updateModel(IPartialPageRequestHandler target, boolean moderator) {
		selForm.updateModel(target);
		this.moderator = moderator;
		RoomPoll p = selForm.select.getModelObject();
		dispForm.updateModel(p, false, target);
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(PollResultsDialog.this.getMarkupId()).append("').on('dialogopen', function( event, ui ) {");
		builder.append(getScript(barChart(p)));
		builder.append("});");

		target.appendJavaScript(builder.toString());
	}

	private StringBuilder getScript(Chart<?> chart) {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(chartDiv.getMarkupId()).append("').html(''); ");
		builder.append("$.jqplot('").append(chartDiv.getMarkupId()).append("', ");
		builder.append(chart.getChartData().toJsonString());
		builder.append(", ");
		builder.append(JqPlotUtils.jqPlotToJson(chart.getChartConfiguration()));
		builder.append(");");
		return builder;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(JqPlotJavascriptResourceReference.get()));
		response.render(CssHeaderItem.forReference(JqPlotCssResourceReference.get()));
		Chart<?> c1 = new PieChart<>(null);
		c1.getChartConfiguration().setHighlighter(new Highlighter());
		for (Chart<?> chart : new Chart<?>[]{c1, new BarChart<Integer>(null)}) {
			for (String resource : JqPlotUtils.retriveJavaScriptResources(chart)) {
				response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(JqPlotBehavior.class, removeMinified(resource))));
			}
		}
	}

	private static String removeMinified(String _name) {
		String name = _name;
		int idxOfExtension = name.lastIndexOf('.');
		if (idxOfExtension > -1) {
			String extension = name.substring(idxOfExtension);
			name = name.substring(0, name.length() - extension.length() + 1);
			if (name.endsWith(".min.")) {
				name = name.substring(0, name.length() - 5) + extension;
			}
		}
		return name;
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (close.equals(button)) {
			closeConfirm.open(target);
			return;
		} else if (delete.equals(button)) {
			deleteConfirm.open(target);
			return;
		} else if (moderator && clone.equals(button)) {
			RoomPoll rp = dispForm.getModelObject();
			RoomPoll nrp = new RoomPoll();
			User u = getBean(UserDao.class).get(getUserId());
			nrp.setCreator(u);
			nrp.setName(rp.getName());
			nrp.setQuestion(rp.getQuestion());
			nrp.setType(rp.getType());
			nrp.setRoom(rp.getRoom());
			createPoll.setModelObject(nrp);
			createPoll.getForm().setModelObject(nrp);
			target.add(createPoll.getForm());
			createPoll.open(target);
		}
		super.onClick(target, button);
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		super.onOpen(handler);
		opened = true;
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		opened = false;
	}

	public boolean isOpened() {
		return opened;
	}

	private String[] getTicks(RoomPoll p) {
		return p != null && RoomPoll.Type.numeric == p.getType()
				? new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
				: new String[] {getString("35"), getString("34")};
	}

	private static Integer[] initValues(int size) {
		Integer[] values = new Integer[size];
		for (int i = 0; i < size; ++i) {
			values[i] = 0;
		}
		return values;
	}

	private static Integer[] getValues(RoomPoll p) {
		Integer[] values = initValues(p != null && RoomPoll.Type.numeric == p.getType() ? 10 : 2);
		if (p != null) {
			for (RoomPollAnswer a : p.getAnswers()) {
				if (RoomPoll.Type.numeric == p.getType()) {
					values[a.getPointList() - 1] ++;
				} else {
					values[Boolean.FALSE.equals(a.getAnswer()) ? 0 : 1] ++;
				}
			}
		}
		return values;
	}

	private BarChart<Integer> barChart(RoomPoll p) {
		String[] ticks = getTicks(p);
		BarChart<Integer> barChart = new BarChart<>(null);
		barChart.addValue(Arrays.asList(getValues(p)));

		barChart.getSeriesDefaults()
				.setRendererOptions(new RendererOptions().setHighlightMouseDown(true).setShowDataLabels(true)
						.setFill(false).setSliceMargin(4).setLineWidth(5).setBarDirection("horizontal"));

		Highlighter h = new Highlighter();
		h.setShow(true);
		h.setFormatString("%s, %P");
		h.setTooltipLocation(Location.ne);
		h.setShowTooltip(true);
		h.setUseAxesFormatters(false);

		ChartConfiguration<Long> cfg = barChart.getChartConfiguration();
		cfg.setLegend(null).setHighlighter(h);
		cfg.axesInstance().setXaxis(null);
		cfg.axesInstance().yAxisInstance().setTicks(ticks).setRenderer(JqPlotResources.CategoryAxisRenderer);
		return barChart;
	}

	private class PollSelectForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private final DropDownChoice<RoomPoll> select;

		PollSelectForm(String id) {
			super(id);
			add((select = new DropDownChoice<>("polls", Model.of((RoomPoll)null), new ArrayList<RoomPoll>(), new ChoiceRenderer<RoomPoll>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getDisplayValue(RoomPoll object) {
					return object == null ? "" : String.format("%s%s", object.getName(), object.isArchived() ? "" : String.format(" (%s)", getString("1413")));
				}

				@Override
				public String getIdValue(RoomPoll object, int index) {
					return object == null ? "" : "" + object.getId();
				}
			})).add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					dispForm.updateModel(select.getModelObject(), true, target);
				}
			}));
			updateModel(null);
		}

		public void updateModel(IPartialPageRequestHandler handler) {
			List<RoomPoll> list = new ArrayList<>();
			RoomPoll p = getBean(PollDao.class).getByRoom(roomId);
			if (p != null) {
				list.add(p);
			}
			list.addAll(getBean(PollDao.class).getArchived(roomId));
			select.setChoices(list);
			select.setModelObject(list.isEmpty() ? null : list.get(0));
			if (handler != null) {
				handler.add(this);
			}
		}
	}

	private class PollResultsForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private String chartSimple;
		private String chartPie;
		private final Label name = new Label("name", Model.of((String)null));
		private final Label question = new Label("question", Model.of((String)null));
		private final Label count = new Label("count", Model.of(0));
		private DropDownChoice<String> chartType;

		PollResultsForm(String id) {
			super(id, Model.of((RoomPoll)null));
			setOutputMarkupId(true);
			add(chartDiv.setOutputMarkupId(true));
		}

		@Override
		protected void onInitialize() {
			chartSimple = getString("1414");
			chartPie = getString("1415");
			add(name, question, count);
			chartType = new DropDownChoice<>("chartType", Model.of(chartSimple), Arrays.asList(chartSimple, chartPie));
			add(chartType.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					redraw(target);
				}
			}));
			super.onInitialize();
		}

		public void updateModel(RoomPoll poll, boolean redraw, IPartialPageRequestHandler handler) {
			setModelObject(poll);
			name.setDefaultModelObject(poll == null ? "" : VoteDialog.getName(this, poll.getCreator()));
			question.setDefaultModelObject(poll == null ? "" : poll.getQuestion());
			count.setDefaultModelObject(poll == null ? 0 : poll.getAnswers().size());
			handler.add(this);
			close.setVisible(moderator && (poll != null && !poll.isArchived()), handler);
			clone.setVisible(moderator && (poll != null && poll.isArchived()), handler);
			delete.setVisible(moderator, handler);
			if (redraw) {
				redraw(handler);
			}
		}

		private void redraw(IPartialPageRequestHandler handler) {
			RoomPoll poll = getModelObject();
			Chart<?> chart = chartSimple.equals(chartType.getModelObject()) ? barChart(poll) : pieChart(poll);
			handler.appendJavaScript(getScript(chart));
		}

		private PieChart<Integer> pieChart(RoomPoll p) {
			PieChart<Integer> pieChart = new PieChart<>(null);
			String[] ticks = getTicks(p);
			Integer[] values = getValues(p);
			for (int i = 0; i < values.length; ++i) {
				pieChart.addValue(ticks[i], values[i]);
			}

			pieChart.getSeriesDefaults().setRendererOptions(new RendererOptions().setHighlightMouseDown(true)
					.setShowDataLabels(true).setFill(false).setSliceMargin(4).setLineWidth(5));

			Highlighter h = new Highlighter();
			h.setShow(true);
			h.setFormatString("%s, %P");
			h.setTooltipLocation(Location.ne);
			h.setShowTooltip(true);
			h.setUseAxesFormatters(false);

			pieChart.getChartConfiguration().setLegend(null).setHighlighter(h);
			return pieChart;
		}
	}
}
