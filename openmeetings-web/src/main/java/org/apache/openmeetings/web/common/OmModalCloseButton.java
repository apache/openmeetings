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
package org.apache.openmeetings.web.common;

import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.ModalCloseButton;

public class OmModalCloseButton extends ModalCloseButton {
	private static final long serialVersionUID = 1L;

	public static ModalCloseButton of() {
		return of("lbl.cancel");
	}

	public static ModalCloseButton of(String lblKey) {
		ModalCloseButton btn = new ModalCloseButton(new ResourceModel(lblKey)).type(Buttons.Type.Outline_Secondary);
		btn.setOutputMarkupId(false);
		return btn;
	}
}
