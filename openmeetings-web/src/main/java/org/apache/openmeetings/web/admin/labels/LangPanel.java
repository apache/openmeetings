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
package org.apache.openmeetings.web.admin.labels;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.label.StringLabel;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.openmeetings.web.util.BootstrapFileUploadBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

/**
 * Language Editor, add/insert/update {@link Fieldlanguagesvalues} and
 * add/delete {@link FieldLanguage} contains several Forms and one list
 * 
 * @author solomax, swagner
 * 
 */
public class LangPanel extends AdminPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(LangPanel.class, webAppRootKey);
	private static final long serialVersionUID = 1L;

	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");;
	private LangForm langForm;
	Map.Entry<Long, Locale> language;
	private FileUploadField fileUploadField;
	// Create feedback panels
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		super.onMenuPanelLoad(target);
		target.appendJavaScript("labelsInit();");
	}

	public LangPanel(String id) {
		super(id);

		// Create feedback panels
		add(feedback.setOutputMarkupId(true));
		language = new AbstractMap.SimpleEntry<Long, Locale>(1L, Locale.ENGLISH);

		final LabelsForm form = new LabelsForm("form", this, new StringLabel(null, null));
		form.showNewRecord();
		add(form);

		final SearchableDataView<StringLabel> dataView = new SearchableDataView<StringLabel>(
				"langList"
				, new SearchableDataProvider<StringLabel>(LabelDao.class) {
					private static final long serialVersionUID = 1L;

					@Override
					protected LabelDao getDao() {
						return (LabelDao)super.getDao();
					}
					
					@Override
					public long size() {
						return getDao().count(language.getValue(), search);
					}
					
					public Iterator<? extends StringLabel> iterator(long first, long count) {
						return getDao().get(language.getValue(), search, (int)first, (int)count, getSort()).iterator();
					}
				}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<StringLabel> item) {
				final StringLabel fv = item.getModelObject();
				item.add(new Label("name", fv.getKey()));
				item.add(new Label("value", fv.getValue()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(fv);
						form.hideNewRecord();
						target.add(form, listContainer);
						target.appendJavaScript("labelsInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable ui-widget-content" + (fv.getKey().equals(form.getModelObject().getKey()) ? " ui-state-active" : "")));
			}
		};

		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dataView.modelChanging();
				target.add(listContainer);
			}
		};
		DataViewContainer<StringLabel> container = new DataViewContainer<StringLabel>(listContainer, dataView, navigator);
		container
			.addLink(new OmOrderByBorder<StringLabel>("orderByName", "key", container))
			.addLink(new OmOrderByBorder<StringLabel>("orderByValue", "value", container));
		add(container.getLinks());
		add(navigator);
		langForm = new LangForm("langForm", listContainer, this);
		fileUploadField = new FileUploadField("fileInput");
		langForm.add(fileUploadField);
		langForm.add(new UploadProgressBar("progress", langForm, fileUploadField));
		fileUploadField.add(new AjaxFormSubmitBehavior(langForm, "onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				FileUpload download = fileUploadField.getFileUpload();
				try {
					if (download == null || download.getInputStream() == null) {
						feedback.error("File is empty");
						return;
					}
					LabelDao.upload(language.getValue(), download.getInputStream());
				} catch (Exception e) {
					log.error("Exception on panel language editor import ", e);
					feedback.error(e);
				}

				// repaint the feedback panel so that it is hidden
				target.add(listContainer, feedback);
			}
		});

		// Add a component to download a file without page refresh
		final AjaxDownload download = new AjaxDownload();
		langForm.add(download);

		langForm.add(new AjaxButton("export"){
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final String name = LabelDao.getLabelFileName(language.getValue());
				download.setFileName(name);
				download.setResourceStream(new AbstractResourceStream() {
					private static final long serialVersionUID = 1L;
					private transient InputStream is;
					
					public InputStream getInputStream() throws ResourceStreamNotFoundException {
						try {
							is = Application.class.getResourceAsStream(name);
							return is;
						} catch (Exception e) {
							throw new ResourceStreamNotFoundException(e);
						}
					}
					
					public void close() throws IOException {
						if (is != null) {
							is.close();
							is = null;
						}
					}
				});
				download.initiate(target);

				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
			
		});
		
		add(langForm);
		add(new AddLanguageForm("addLangForm", this));
		add(BootstrapFileUploadBehavior.INSTANCE);
	}

	public LangForm getLangForm() {
		return langForm;
	}
}
