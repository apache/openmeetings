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
package org.apache.openmeetings.web.common.confirmation;

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.border.Border;

public abstract class ConfirmableAjaxBorder extends Border {
	private static final long serialVersionUID = 1L;
	private final ConfirmationDialog dialog;

	protected ConfirmableAjaxBorder(String id, ConfirmationDialog dialog) {
		super(id);
		this.dialog = dialog;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				ConfirmableAjaxBorder.this.updateAjaxAttributes(attributes);
			}

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if (isClickable()) {
					dialog.show(target);
				}
			}
		});
	}

	protected boolean isClickable() {
		return true;
	}

	/**
	 * Gives a chance to the specializations to modify the attributes.
	 *
	 * @param attributes - attributes
	 */
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
	}

	protected void onEvent(AjaxRequestTarget target) {
		dialog.show(target);
	}
}
