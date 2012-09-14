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
package org.apache.openmeetings.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public abstract class ConfirmableAjaxLink extends AjaxLink<Void> {
	private static final long serialVersionUID = 7301747891668537168L;
	private String confirmText;

	public ConfirmableAjaxLink(String id, String text) {
		super(id);
		confirmText = text;
	}

	//TODO confirm need to be replaced with jQuery modal dialog
	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);
		attributes.getAjaxCallListeners().add(new AjaxCallListener() {
			private static final long serialVersionUID = 485123450543463471L;

			@Override
			public CharSequence getPrecondition(Component component) {
				return "if (!confirm('" + confirmText + "')) {hideBusyIndicator(); return false;}";
			}
		});
	}
}
