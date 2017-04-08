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
package org.apache.openmeetings.web.util;

import java.io.Serializable;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;

public abstract class NonClosableDialog<T extends Serializable> extends AbstractFormDialog<T> {
	private static final long serialVersionUID = 1L;

	public NonClosableDialog(String id, String title) {
		super(id, title);
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("classes", "{'ui-dialog-titlebar': 'ui-corner-all no-close'}");
	}
}
