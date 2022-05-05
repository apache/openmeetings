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

import org.apache.wicket.Component;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

public class ConfirmationHelper {
	private ConfirmationHelper() {}

	public static ConfirmationBehavior newOkCancelConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelConfirmCfg(c, title));
	}

	public static ConfirmationBehavior newOkCancelDangerConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelDangerConfirmCfg(c, title));
	}

	public static ConfirmationConfig newOkCancelDangerConfirmCfg(Component c, String title) {
		return newOkCancelConfirmCfg(c, title)
				.withBtnOkClass("btn btn-sm btn-danger")
				.withBtnOkIconClass("fas fa-exclamation-triangle");
	}

	private static ConfirmationConfig newOkCancelConfirmCfg(Component c, String title) {
		return new ConfirmationConfig()
				.withBtnCancelLabel(c.getString("lbl.cancel"))
				.withBtnOkLabel(c.getString("54"))
				.withTitle(title);
	}
}
