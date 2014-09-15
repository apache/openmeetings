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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.message.RoomMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.wicketstuff.jqplot.behavior.JqPlotBehavior;
import org.wicketstuff.jqplot.behavior.JqPlotCssResourceReference;
import org.wicketstuff.jqplot.behavior.JqPlotJavascriptResourceReference;

import br.com.digilabs.jqplot.Chart;
import br.com.digilabs.jqplot.ChartConfiguration;
import br.com.digilabs.jqplot.JqPlotResources;
import br.com.digilabs.jqplot.JqPlotUtils;
import br.com.digilabs.jqplot.chart.BarChart;
import br.com.digilabs.jqplot.chart.PieChart;
import br.com.digilabs.jqplot.elements.Highlighter;
import br.com.digilabs.jqplot.elements.Location;
import br.com.digilabs.jqplot.elements.RendererOptions;

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
	private final long roomId;
	private final PollSelectForm selForm;
	private final PollResultsForm dispForm;
	private final DialogButton cancel = new DialogButton(WebSession.getString(25));
	private final DialogButton close = new DialogButton(WebSession.getString(1418));
	private final DialogButton delete = new DialogButton(WebSession.getString(1420));
	private boolean moderator = false;
	private final MessageDialog closeConfirm;
	private final MessageDialog deleteConfirm;

	public PollResultsDialog(String id, long _roomId) {
		super(id, WebSession.getString(37));
		this.roomId = _roomId;
		add(selForm = new PollSelectForm("selForm"));
		add(dispForm = new PollResultsForm("dispForm"));
		add(closeConfirm = new MessageDialog("closeConfirm", WebSession.getString(1418), WebSession.getString(1419), DialogButtons.YES_NO, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				// TODO should rights be additionally checked here????
				if(button != null && button.match(LBL_YES)) {
					Long id = dispForm.getModelObject().getId();
					getBean(PollDao.class).close(roomId);
					selForm.updateModel(target);

					RoomPoll p = getBean(PollDao.class).get(id);
					selForm.select.setModelObject(p);
					dispForm.updateModel(p, false, target);
					RoomPanel.broadcast(new RoomMessage(roomId, getUserId(), RoomMessage.Type.pollClosed));
					//TODO result dialogs of other users should also be updated
				}
			}
		});
		add(deleteConfirm = new MessageDialog("deleteConfirm", WebSession.getString(1420), WebSession.getString(1421), DialogButtons.YES_NO, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				// TODO should rights be additionally checked here????
				if(button != null && button.match(LBL_YES)) {
					getBean(PollDao.class).delete(dispForm.getModelObject());
					selForm.updateModel(target);
					dispForm.updateModel(selForm.select.getModelObject(), true, target);
					RoomPanel.broadcast(new RoomMessage(roomId, getUserId(), RoomMessage.Type.pollDeleted));
					//TODO result dialogs of other users should also be updated
				}
			}
		});
	}

	@Override
	public int getWidth() {
		return 500;
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(delete, close, cancel);
	}
	
	public void updateModel(AjaxRequestTarget target, boolean moderator) {
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
		Chart<?> c1 = new PieChart<Number>(null);
		c1.getChartConfiguration().setHighlighter(new Highlighter());
		for (Chart<?> chart : new Chart<?>[]{c1, new BarChart<Integer>(null)}) {
			for (String resource : JqPlotUtils.retriveJavaScriptResources(chart)) {
				response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(JqPlotBehavior.class, removeMinified(resource))));
			}
		}
	}
	
    private String removeMinified(String name_) {
        String name = name_;
        int idxOfExtension = name.lastIndexOf('.');
        if (idxOfExtension > -1) {
            String extension = name.substring(idxOfExtension);
            name = name.substring(0, name.length() - extension.length() + 1);
            if (name.endsWith(".min."))
            {
                name = name.substring(0, name.length() - 5) + extension;
            }
        }
        return name;
    }

    @Override
    public void onClick(AjaxRequestTarget target, DialogButton button) {
    	if (close.equals(button)) {
    		closeConfirm.open(target);
    	} else if (delete.equals(button)) {
    		deleteConfirm.open(target);
    	} else {
    		super.onClick(target, button);
    	}
    }
    
    @Override
    public void onClose(AjaxRequestTarget target, DialogButton button) {
    }
    
    private String[] getTicks(RoomPoll p) {
		return p != null && p.getType().isNumeric()
				? new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
				: new String[] {WebSession.getString(34), WebSession.getString(35)};
    }
    
	private Integer[] initValues(int size) {
		Integer[] values = new Integer[size];
		for (int i = 0; i < size; ++i) {
			values[i] = 0;
		}
		return values;
	}
	
	private Integer[] getValues(RoomPoll p) {
		Integer[] values = initValues(p != null && p.getType().isNumeric() ? 10 : 2);
		if (p != null && p.getType().isNumeric()) {
			for (RoomPollAnswer a : p.getAnswers()) {
				values[a.getPointList() - 1] ++;
			}
		} else if (p != null) {
			for (RoomPollAnswer a : p.getAnswers()) {
				values[a.getAnswer() ? 0 : 1] ++;
			}
		}
		return values;
	}
	
	private PieChart<Integer> pieChart(RoomPoll p) {
        PieChart<Integer> pieChart = new PieChart<Integer>(null);
		String[] ticks = getTicks(p);
		Integer[] values = getValues(p);
        for (int i = 0; i < values.length; ++i) {
            pieChart.addValue(ticks[i], values[i]);
        }
        
        pieChart.getSeriesDefaults().setRendererOptions(
        		new RendererOptions().setHighlightMouseDown(true).setShowDataLabels(true)
        			.setFill(false).setSliceMargin(4).setLineWidth(5));

        Highlighter h = new Highlighter();
        h.setShow(true);
        h.setFormatString("%s, %P");
        h.setTooltipLocation(Location.ne);
        h.setShowTooltip(true);
        h.setUseAxesFormatters(false);
        
        pieChart.getChartConfiguration().setLegend(null).setHighlighter(h);
        return pieChart;
	}
	
	private BarChart<Integer> barChart(RoomPoll p) {
		String[] ticks = getTicks(p);
		BarChart<Integer> barChart = new BarChart<Integer>(null);
		barChart.addValue(Arrays.asList(getValues(p)));
        
        barChart.getSeriesDefaults().setRendererOptions(
        		new RendererOptions().setHighlightMouseDown(true).setShowDataLabels(true)
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
        /*cfg.axesInstance().xAxisInstance().setRenderer(JqPlotResources.AxisTickRenderer);*/
        cfg.axesInstance()
        	.yAxisInstance()
        	.setTicks(ticks)
        	.setRenderer(JqPlotResources.CategoryAxisRenderer);
        return barChart;
	}
	
	private class PollSelectForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private final DropDownChoice<RoomPoll> select;

		PollSelectForm(String id) {
			super(id);
			add((select = new DropDownChoice<RoomPoll>("polls", Model.of((RoomPoll)null), new ArrayList<RoomPoll>(), new ChoiceRenderer<RoomPoll>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getDisplayValue(RoomPoll object) {
					return object == null ? "" : String.format("%s%s", object.getName(), object.isArchived() ? "" : String.format(" (%s)", WebSession.getString(1413)));
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

		public void updateModel(AjaxRequestTarget target) {
			List<RoomPoll> list = new ArrayList<RoomPoll>();
			RoomPoll p = getBean(PollDao.class).getPoll(roomId);
			if (p != null) {
				list.add(p);
			}
			list.addAll(getBean(PollDao.class).getArchived(roomId));
			select.setChoices(list);
			select.setModelObject(list.isEmpty() ? null : list.get(0));
			if (target != null) {
				target.add(this);
			}
		}
	}
	
	private class PollResultsForm extends Form<RoomPoll> {
		private static final long serialVersionUID = 1L;
		private final String SIMPLE_CHART = WebSession.getString(1414);
		private final String PIE_CHART = WebSession.getString(1415);
		private final IModel<String> name = Model.of((String)null);
		private final IModel<Integer> count = Model.of(0);
		private final DropDownChoice<String> chartType = new DropDownChoice<String>("chartType", Model.of(SIMPLE_CHART), Arrays.asList(SIMPLE_CHART, PIE_CHART));

		PollResultsForm(String id) {
			super(id, Model.of((RoomPoll)null));
			setOutputMarkupId(true);
			add(new Label("name", name));
			add(new Label("question"));
			add(new Label("count", count));
			add(chartType.add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					redraw(target);
				}
			}));
			add(chartDiv.setOutputMarkupId(true));
		}
		
		public void updateModel(RoomPoll poll, boolean redraw, AjaxRequestTarget target) {
			setModelObject(poll);
			name.setObject(poll == null ? "" : VoteDialog.getName(poll.getCreator()));
			count.setObject(poll == null ? 0 : poll.getAnswers().size());
			target.add(this);
			close.setVisible(moderator && (poll != null && !poll.isArchived()), target);
			delete.setVisible(moderator, target);
			if (redraw) {
				redraw(target);
			}
		}
		
		private void redraw(AjaxRequestTarget target) {
			RoomPoll poll = getModelObject();
			Chart<?> chart = SIMPLE_CHART.equals(chartType.getModelObject()) ? barChart(poll) : pieChart(poll);
			target.appendJavaScript(getScript(chart));
		}
	}
}
