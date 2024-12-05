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
package org.apache.openmeetings.web.common.datetime;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.ChainingModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.AbstractTempusDominusWithIcon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;

public abstract class AbstractOmDateTimePicker<T extends Serializable> extends FormComponentPanel<T> {
	private static final long serialVersionUID = 1L;
	private static final ResourceReference FUNCJS = new JavaScriptResourceReference(AbstractOmDateTimePicker.class, "datepicker-functions.js");
	private final HiddenField<T> date;
	private AbstractTempusDominusWithIcon<T> picker;
	private String markupId;

	AbstractOmDateTimePicker(String id, IModel<T> model) {
		super(id, model);
		date = newHidden("date");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		TempusDominusConfig config = new TempusDominusConfig()
				.withLocalization(cfg -> cfg
						.withDateFormats(null)
				)
				.withIcons(cfg -> cfg
						.withDateIcon(FontAwesome6IconType.calendar_s)
						.withTimeIcon(FontAwesome6IconType.clock_s)
						.withUpIcon(FontAwesome6IconType.arrow_up_s)
						.withDownIcon(FontAwesome6IconType.arrow_down_s)
						.withPreviousIcon(FontAwesome6IconType.arrow_left_s)
						.withNextIcon(FontAwesome6IconType.arrow_right_s)
						.withTodayIcon(FontAwesome6IconType.calendar_check_s)
						.withClearIcon(FontAwesome6IconType.eraser_s)
						.withCloseIcon(FontAwesome6IconType.xmark_s));
		picker = new AbstractTempusDominusWithIcon<>("picker", new Model<>(), patch(config)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected FormComponent<T> newInput(String wicketId, String dateFormat) {
				return AbstractOmDateTimePicker.this.newInput(wicketId, dateFormat);
			}
		};
		picker.setOutputMarkupId(true);
		markupId = picker.getMarkupId();
		date.setOutputMarkupId(true);
		date.setModel(new ChainingModel<>(getModel()));
		add(date, picker.setRenderBodyOnly(false));
	}

	protected abstract FormComponent<T> newInput(String wicketId, String dateFormat);

	protected abstract HiddenField<T> newHidden(String wicketId);

	protected TempusDominusConfig patch(TempusDominusConfig cfg) {
		return cfg;
	}

	private CharSequence initScript() {
		return "omDateTimeInit('" + markupId + "', '" + date.getMarkupId() + "');";
	}

	@Override
	public void convertInput() {
		date.convertInput();
		if (date.isValid()) {
			setConvertedInput(date.getConvertedInput());
		} else {
			date.getFeedbackMessages().forEach(msg -> getFeedbackMessages().add(msg));
			date.getFeedbackMessages().clear();
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(FUNCJS));
		response.render(new OnDomReadyHeaderItem(initScript()));
	}

	@Override
	public boolean processChildren() {
		return false;
	}

	@Override
	public FormComponent<T> setLabel(IModel<String> labelModel) {
		date.setLabel(labelModel);
		return this;
	}

	@Override
	protected void onModelChanged() {
		RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> {
			target.add(date);
			target.appendJavaScript(initScript());
		});
	}
}
