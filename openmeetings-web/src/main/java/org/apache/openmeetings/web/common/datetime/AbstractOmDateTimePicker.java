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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.ChainingModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.AbstractDateTimePickerWithIcon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerIconConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;

public abstract class AbstractOmDateTimePicker<T extends Serializable> extends FormComponentPanel<T> {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
	private static final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + " HH:mm:ss";
	private static final ResourceReference FUNCJS = new JavaScriptResourceReference(AbstractOmDateTimePicker.class, "datepicker-functions.js");
	private final boolean dateOnly;
	private final HiddenField<T> date;
	private AbstractDateTimePickerWithIcon<T> picker;
	private String markupId;

	AbstractOmDateTimePicker(String id, IModel<T> model, boolean dateOnly) {
		super(id, model);
		this.dateOnly = dateOnly;
		date = newHidden("date", new ChainingModel<>(model));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final String format = dateOnly ? getDateFormat() : getDateTimeFormat();
		DatetimePickerConfig config = new DatetimePickerConfig()
				.useLocale(WebSession.get().getLocale().toLanguageTag())
				.withFormat(patch(format))
				.withKeepInvalid(true)
				.with(new DatetimePickerIconConfig().useDateIcon(FontAwesome6IconType.calendar_s)
						.useTimeIcon(FontAwesome6IconType.clock_s).useUpIcon(FontAwesome6IconType.arrow_up_s)
						.useDownIcon(FontAwesome6IconType.arrow_down_s)
						.usePreviousIcon(FontAwesome6IconType.arrow_left_s)
						.useNextIcon(FontAwesome6IconType.arrow_right_s)
						.useTodayIcon(FontAwesome6IconType.calendar_check_s).useClearIcon(FontAwesome6IconType.eraser_s)
						.useCloseIcon(FontAwesome6IconType.xmark_s));
		picker = new AbstractDateTimePickerWithIcon<>("picker", new Model<>(getModelObject()), config) {
			private static final long serialVersionUID = 1L;

			@Override
			protected FormComponent<T> newInput(String wicketId, String dateFormat) {
				FormComponent<T> input = AbstractOmDateTimePicker.this.newInput(wicketId, dateFormat);
				markupId = input.getMarkupId();
				return input;
			}
		};
		date.setOutputMarkupId(true);
		add(date, picker.setRenderBodyOnly(false));
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

	protected abstract FormComponent<T> newInput(String wicketId, String dateFormat);

	protected abstract HiddenField<T> newHidden(String wicketId, IModel<T> model);

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(FUNCJS));
		response.render(new OnDomReadyHeaderItem("$('#" + markupId + "').on('change.datetimepicker', function (e) {omDateTimeInputHasChanged(e, '" + date.getMarkupId() + "', " + dateOnly + ");});"));
	}

	@Override
	public boolean processChildren() {
		return false;
	}

	public static String patch(String format) {
		// in Java free text is escaped with single-quotes
		// moment.js uses []
		return format.replaceAll("'([^']*)'", "\\[$1\\]");
	}

	public static String getDateTimeFormat() {
		return getDateTimeFormat(WebSession.get().getLocale());
	}

	@Override
	public FormComponent<T> setLabel(IModel<String> labelModel) {
		date.setLabel(labelModel);
		return this;
	}

	@Override
	protected void onModelChanged() {
		picker.setModelObject(getModelObject());
	}

	public static String getDateTimeFormat(Locale loc) {
		DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, loc);
		if (fmt instanceof SimpleDateFormat sfmt) {
			return sfmt.toPattern();
		}
		return DEFAULT_DATE_TIME_FORMAT;
	}

	public static String getDateFormat() {
		DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT, Session.get().getLocale());
		if (fmt instanceof SimpleDateFormat sfmt) {
			return sfmt.toPattern();
		}
		return DEFAULT_DATE_FORMAT;
	}
}
