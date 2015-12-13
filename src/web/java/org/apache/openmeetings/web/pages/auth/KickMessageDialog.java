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
package org.apache.openmeetings.web.pages.auth;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class KickMessageDialog extends AbstractDialog<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KickMessageDialog(String id) {
		super(id, "");
		add(new Label("message", Application.getString(606)));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new JQueryBehavior(JQueryWidget.getSelector(this), "dialog") {
			private static final long serialVersionUID = 1L;

			@Override
            protected String $()  {
                return this.$(Options.asString("open"));
            }
        });
	};
	
	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("closeOnEscape", false);
        behavior.setOption("dialogClass", Options.asString("no-close"));
        behavior.setOption("resizable", false);
	}

	public String getOnClickJavaScript() {
		return "$('#" + getButtons().get(0).getMarkupId() +"').click(function(e){$('#" + getMarkupId() +"').close(); })";
	}

	@Override
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		WebSession.setKickedByAdmin(false);
		Application.get().restartResponseAtSignInPage();
	}

}
