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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.wicket.request.resource.ContentDisposition.ATTACHMENT;
import static org.apache.wicket.util.time.Duration.NONE;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.label.StringLabel;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

/**
 * Language Editor, add/insert/update Label and add/delete language contains several Forms and one list
 *
 * @author solomax, swagner
 *
 */
public class LangPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LangPanel.class);
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private LangForm langForm;
	private final FileUploadField fileUploadField = new FileUploadField("fileInput");

	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	Map.Entry<Long, Locale> language;

	public LangPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		// Create feedback panels
		add(feedback.setOutputMarkupId(true));
		language = new AbstractMap.SimpleEntry<>(1L, Locale.ENGLISH);

		final LabelsForm form = new LabelsForm("form", this, new StringLabel(null, null));
		form.setNewVisible(true);
		add(form);

		final SearchableDataView<StringLabel> dataView = new SearchableDataView<StringLabel>(
				"langList"
				, new SearchableDataProvider<StringLabel>(LabelDao.class) {
					private static final long serialVersionUID = 1L;

					@Override
					public long size() {
						return LabelDao.count(language.getValue(), search);
					}

					@Override
					public Iterator<? extends StringLabel> iterator(long first, long count) {
						return LabelDao.get(language.getValue(), search, first, count, getSort()).iterator();
					}
				}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<StringLabel> item) {
				final StringLabel fv = item.getModelObject();
				item.add(new Label("key"));
				item.add(new Label("value"));
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(fv);
						form.setNewVisible(false);
						target.add(form, listContainer);
						reinitJs(target);
					}
				});
				item.add(AttributeModifier.append(ATTR_CLASS, getRowClass(fv.getId(), form.getModelObject().getId())));
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
		DataViewContainer<StringLabel> container = new DataViewContainer<>(listContainer, dataView, navigator);
		container
			.addLink(new OmOrderByBorder<>("orderByName", "key", container))
			.addLink(new OmOrderByBorder<>("orderByValue", "value", container));
		add(container.getLinks());
		add(navigator);
		langForm = new LangForm("langForm", listContainer, this);
		langForm.add(fileUploadField);
		langForm.add(new UploadProgressBar("progress", langForm, fileUploadField));
		fileUploadField.add(new AjaxFormSubmitBehavior(langForm, "change") {
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
		final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new ResourceStreamResource() {
			private static final long serialVersionUID = 1L;

			{
				setContentDisposition(ATTACHMENT);
				setCacheDuration(NONE);
			}

			@Override
			protected IResourceStream getResourceStream(Attributes attributes) {
				final String name = LabelDao.getLabelFileName(language.getValue());
				setFileName(name);
				return new AbstractResourceStream() {
					private static final long serialVersionUID = 1L;
					private transient InputStream is;

					@Override
					public InputStream getInputStream() throws ResourceStreamNotFoundException {
						try {
							is = Application.class.getResourceAsStream(name);
							return is;
						} catch (Exception e) {
							throw new ResourceStreamNotFoundException(e);
						}
					}

					@Override
					public void close() throws IOException {
						if (is != null) {
							is.close();
							is = null;
						}
					}
				};
			}
		});
		langForm.add(download);

		langForm.add(new AjaxButton("export"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				download.initiate(target);

				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}

		});

		add(langForm);
		final AddLanguageDialog addLang = new AddLanguageDialog("addLang", this);
		add(addLang, new AjaxLink<Void>("addLangBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				addLang.open(target);
			}
		});
		add(BootstrapFileUploadBehavior.INSTANCE);
		add(new ConfirmableAjaxBorder("deleteLangBtn", getString("80"), getString("833")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				LabelDao.delete(language.getValue());
				List<Map.Entry<Long, Locale>> langs = LangForm.getLanguages();
				language = langs.isEmpty() ? null : langs.get(0);
				langForm.updateLanguages(target);
				target.add(listContainer);
			}
		});
		super.onInitialize();
	}

	public LangForm getLangForm() {
		return langForm;
	}
}
