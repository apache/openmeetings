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
package org.apache.openmeetings.web.admin.configurations;

import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.wicket.validation.validator.StringValidator.maximumLength;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.basic.Configuration.Type;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import jakarta.inject.Inject;

/**
 * Handle {@link Configuration} items as list and form
 *
 * @author swagner
 *
 */
public class ConfigForm extends AdminBaseForm<Configuration> {
	private static final long serialVersionUID = 1L;
	private static final Set<String> PATHS = Set.of(CONFIG_PATH_FFMPEG, CONFIG_PATH_IMAGEMAGIC, CONFIG_PATH_OFFICE, CONFIG_PATH_SOX);
	private final WebMarkupContainer listContainer;
	private final WebMarkupContainer stringBox = new WebMarkupContainer("string-box");
	private final WebMarkupContainer numberBox = new WebMarkupContainer("number-box");
	private final WebMarkupContainer booleanBox = new WebMarkupContainer("boolean-box");
	private final WebMarkupContainer hotkeyBox = new WebMarkupContainer("hotkey-box");
	private final TextArea<String> valueS = new TextArea<>("valueS");
	private final TextField<Long> valueN = new TextField<>("valueN") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String[] getInputTypes() {
			return new String[] {"number"};
		}
	};
	private final CheckBox valueB = new CheckBox("valueB");
	private final TextField<String> valueH = new TextField<>("value");

	@Inject
	private ConfigurationDao cfgDao;

	public ConfigForm(String id, WebMarkupContainer listContainer, Configuration configuration) {
		super(id, new CompoundPropertyModel<>(configuration));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
	}

	private void refresh(AjaxRequestTarget target) {
		target.add(this);
	}

	private void update(AjaxRequestTarget target) {
		Configuration c = getModelObject();
		stringBox.setVisible(Type.PATH == c.getType() || Type.STRING == c.getType());
		numberBox.setVisible(Type.NUMBER == c.getType());
		booleanBox.setVisible(Type.BOOL == c.getType());
		hotkeyBox.setVisible(Type.HOTKEY == c.getType());
		if (target != null) {
			target.add(stringBox, numberBox, booleanBox, hotkeyBox);
			if (Type.HOTKEY == c.getType()) {
				target.appendJavaScript("addOmAdminConfigHandlers()");
			}
		}
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		update(null);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new DateLabel("updated"));
		add(new Label("user.login"));
		add(new TextArea<String>("comment"));
		update(null);

		add(new DropDownChoice<>("type", List.of(Type.values()), new LambdaChoiceRenderer<>(Type::name, Type::name))
				.setLabel(new ResourceModel("45"))
				.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, this::update)));
		add(new RequiredTextField<String>("key").setLabel(new ResourceModel("265")).add(new IValidator<String>(){
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				Configuration c = cfgDao.forceGet(validatable.getValue());
				if (c != null && !c.isDeleted() && !c.getId().equals(ConfigForm.this.getModelObject().getId())) {
					validatable.error(new ValidationError(getString("error.cfg.exist")));
				}
			}
		}).add(maximumLength(255)));
		valueS.add(maximumLength(255));
		valueS.add(new IValidator<String>(){
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				Configuration c = getModelFixType();
				if (Type.PATH == c.getType()) {
					try {
						Path.of(validatable.getValue());
					} catch (InvalidPathException e) {
						validatable.error(new ValidationError(e.getMessage()));
					}
				}
			}
		});
		stringBox.add(valueS.setLabel(new ResourceModel("271"))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		numberBox.add(valueN.setLabel(new ResourceModel("271"))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		booleanBox.add(valueB.setLabel(new ResourceModel("271"))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		hotkeyBox.add(valueH.setLabel(new ResourceModel("271"))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		add(stringBox, numberBox, booleanBox, hotkeyBox);
		setNewRecordVisible(true);
	}

	private Configuration getModelFixType() {
		Configuration c = ConfigForm.this.getModelObject();
		if (c.getKey() != null && PATHS.contains(c.getKey())) {
			c.setType(Type.PATH);
		}
		return c;
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Configuration c = cfgDao.forceGet(getModelObject().getKey());
		if (c != null && c.isDeleted() && !c.getId().equals(getModelObject().getId())) {
			getModelObject().setId(c.getId());
		}
		setModelObject(cfgDao.update(getModelFixType(), WebSession.getUserId()));
		setNewRecordVisible(false);
		target.add(listContainer);
		refresh(target);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new Configuration());
		refresh(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Configuration conf = getModelObject();
		if (conf.getId() != null) {
			conf = cfgDao.get(conf.getId());
		} else {
			conf = new Configuration();
		}
		setModelObject(conf);
		refresh(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		cfgDao.delete(getModelObject(), WebSession.getUserId());
		setModelObject(new Configuration());
		target.add(listContainer);
		refresh(target);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ConfigForm.class, "admin-config.js")));
	}
}
