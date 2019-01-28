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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.basic.Configuration.Type;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Handle {@link Configuration} items as list and form
 *
 * @author swagner
 *
 */
public class ConfigForm extends AdminBaseForm<Configuration> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer listContainer;
	private final TextArea<String> valueS;
	private final TextField<Long> valueN;
	private final CheckBox valueB;

	public ConfigForm(String id, WebMarkupContainer listContainer, Configuration configuration) {
		super(id, new CompoundPropertyModel<>(configuration));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		valueS = new TextArea<>("valueS");
		valueN = new TextField<Long>("valueN") {
			private static final long serialVersionUID = 1L;

			@Override
			protected String[] getInputTypes() {
				return new String[] {"number"};
			}
		};
		valueB = new CheckBox("valueB");
		add(new DateLabel("updated"));
		add(new Label("user.login"));
		add(new TextArea<String>("comment"));
		update(null);
	}

	private void refresh(AjaxRequestTarget target) {
		target.add(this);
		reinitJs(target);
	}

	private void update(AjaxRequestTarget target) {
		Configuration c = getModelObject();
		valueS.setVisible(Type.string == c.getType());
		valueN.setVisible(Type.number == c.getType());
		valueB.setVisible(Type.bool == c.getType());
		if (target != null) {
			target.add(valueS, valueN, valueB);
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
		add(new DropDownChoice<>("type", Arrays.asList(Type.values()), new IChoiceRenderer<Type>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getIdValue(Type rt, int index) {
				return rt.name();
			}

			@Override
			public Object getDisplayValue(Type rt) {
				return rt.name();
			}

			@Override
			public Type getObject(String id, IModel<? extends List<? extends Type>> choices) {
				for (Type rt : choices.getObject()) {
					if (rt.name().equals(id)) {
						return rt;
					}
				}
				return null;
			}
		}).setLabel(new ResourceModel("45")).add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				update(target);
			}
		}));
		add(new RequiredTextField<String>("key").setLabel(new ResourceModel("265")).add(new IValidator<String>(){
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				Configuration c = getBean(ConfigurationDao.class).forceGet(validatable.getValue());
				if (c != null && !c.isDeleted() && !c.getId().equals(ConfigForm.this.getModelObject().getId())) {
					validatable.error(new ValidationError(getString("error.cfg.exist")));
				}
			}
		}));
		add(valueS.setLabel(new ResourceModel("271")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
		add(valueN.setLabel(new ResourceModel("271")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
		add(valueB.setLabel(new ResourceModel("271")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		Configuration c = cfgDao.forceGet(getModelObject().getKey());
		if (c != null && c.isDeleted() && !c.getId().equals(getModelObject().getId())) {
			getModelObject().setId(c.getId());
		}
		setModelObject(getBean(ConfigurationDao.class).update(getModelObject(), WebSession.getUserId()));
		setNewVisible(false);
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
			conf = getBean(ConfigurationDao.class).get(conf.getId());
		} else {
			conf = new Configuration();
		}
		setModelObject(conf);
		refresh(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(ConfigurationDao.class).delete(getModelObject(), WebSession.getUserId());
		setModelObject(new Configuration());
		target.add(listContainer);
		refresh(target);
	}
}
