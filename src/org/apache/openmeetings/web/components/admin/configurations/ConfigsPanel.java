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
package org.apache.openmeetings.web.components.admin.configurations;

import org.apache.openmeetings.data.basic.Configurationmanagement;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

public class ConfigsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private ConfigForm form;
	
	private StringBuilder sb = new StringBuilder();
	//Testing instance
	private StringBuilder sb2 = new StringBuilder();
	
	@Override
	public void renderHead(HtmlHeaderContainer container) {

		IHeaderResponse response = container.getHeaderResponse();
		response.render(CssHeaderItem.forUrl("media/css/demo_table_jui.css"));
		response.render(JavaScriptHeaderItem.forUrl("media/js/jquery.dataTables.min.js"));
		
		AjaxRequestTarget target = container.getRequestCycle().find(AjaxRequestTarget.class);
		
		if (target != null)
		{
			target.appendJavaScript(sb.toString());
			target.appendJavaScript(sb2.toString());
		}
		else
		{
			response.render(JavaScriptHeaderItem.forScript(sb.toString(), "datatable"));
			response.render(JavaScriptHeaderItem.forScript(sb2.toString(), "test2"));
		}
	}
	
	public ConfigsPanel(String id) {
		super(id);
		
		sb.append("$(function() { \n");
		sb.append(" $('#example').dataTable( { \n");
		sb.append("	\"sScrollY\": \"300px\", \n");
		sb.append("	\"bPaginate\": false \n");
		sb.append("} );\n");
		sb.append("} );");
		
		sb2.append("$(function() { \n");
		sb2.append("alert(\"TEST\"); \n");
		sb2.append("} );");
		
		DataView<Configuration> dataView = new DataView<Configuration>("configList"
			, new OmDataProvider<Configuration>(Configurationmanagement.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Configuration> item) {
				final Configuration configuration = item.getModelObject();
				item.add(new Label("configuration_id", "" + configuration.getConfiguration_id()));
				item.add(new Label("conf_key", "" + configuration.getConf_key()));
				item.add(new Label("conf_value", "" + configuration.getConf_value()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(configuration);
						target.add(form);
					}
				});
			}
		};
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
				target.appendJavaScript(sb.toString());
				target.appendJavaScript(sb2.toString());
			}
		});
		
		Configuration configuration = new Configuration();
		form = new ConfigForm("form", configuration);
        add(form);
		
	}
}
