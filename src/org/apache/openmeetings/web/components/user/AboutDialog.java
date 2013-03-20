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
package org.apache.openmeetings.web.components.user;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AboutDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1400355222295594321L;
	private static String version = null;
	private static String revision = null;
	private static String buildDate = null;
	
	private Attributes getAttributes() throws MalformedURLException, IOException {
		String jarUrl = getClass().getResource(getClass().getSimpleName() + ".class").toString();
		return new Manifest(new URL(jarUrl.substring(0, jarUrl.indexOf('!')) + "!/META-INF/MANIFEST.MF").openStream()).getMainAttributes();
	}
	
	private String getVersion() {
		if (version == null) {
			try {
				version = getAttributes().getValue("Product-Version");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return version;
	}
	
	private String getRevision() {
		if (revision == null) {
			try {
				revision = getAttributes().getValue("Svn-Revision");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return revision;
	}
	
	private String getBuildDate() {
		if (buildDate == null) {
			try {
				buildDate = getAttributes().getValue("Built-On");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buildDate;
	}
	
	public AboutDialog(String id) {
		super(id, "About"); //FIXME hardcoded
		
		add(new Label("name", Application.getBean(ConfigurationDao.class).getConfValue(
				"application.name"
				, String.class
				, ConfigurationDao.DEFAULT_APP_NAME)));
		add(new Label("version", getVersion()));
		add(new Label("revision", getRevision()));
		add(new Label("buildDate", getBuildDate()));
	}

	@Override
	protected List<DialogButton> getButtons() {
		return new ArrayList<DialogButton>();
	}
	
	@Override
	protected void onClose(AjaxRequestTarget target, DialogButton button) {
		//empty
	}
}
