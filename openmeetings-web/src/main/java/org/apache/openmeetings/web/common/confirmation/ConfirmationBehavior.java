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

import static de.agilecoders.wicket.jquery.JQuery.$;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.apache.wicket.util.lang.Args;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapJavascriptBehavior;
import de.agilecoders.wicket.core.util.References;

/**
 * A behavior that shows a popover with OK/Cancel buttons to confirm an action.
 *
 * @since 0.9.12
 */
public class ConfirmationBehavior extends BootstrapJavascriptBehavior {
	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** Configuration. */
	private final ConfirmationConfig config;
	/**
	 * Jquery Selector (if you don't want to use the one of the component for
	 * singleton for example).
	 */
	private final String selector;

	/**
	 * Constructor that uses the default configuration
	 */
	public ConfirmationBehavior() {
		this(null, new ConfirmationConfig());
	}

	/**
	 * Constructor that uses a custom configuration
	 *
	 * @param config
	 *            configuration to use
	 */
	public ConfirmationBehavior(ConfirmationConfig config) {
		this(null, config);
	}

	/**
	 * Constructor that uses a custom configuration
	 *
	 * @param config
	 *            configuration to use
	 * @param selector
	 *            Jquery selector to use instead of the one of the component
	 *            (for singleton's option)
	 */
	public ConfirmationBehavior(String selector, ConfirmationConfig config) {
		this.config = Args.notNull(config, "config");
		this.selector = selector;
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);

		References.renderWithFilter(response, JavaScriptHeaderItem
				.forReference(new JQueryPluginResourceReference(ConfirmationBehavior.class, "bootstrap-confirmation.js")));

		if (selector == null) {
			config.withRootSelector(component.getMarkupId());
			response.render($(component).chain("confirmation", config).asDomReadyScript());
		} else {
			config.withRootSelector(selector);
			response.render($(selector).chain("confirmation", config).asDomReadyScript());
		}
	}

	public static ConfirmationBehavior newOkCancelConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelConfirmCfg(c, title));
	}

	public static ConfirmationConfig newOkCancelConfirmCfg(Component c, String title) {
		return new ConfirmationConfig()
				.withBtnCancelLabel(c.getString("lbl.cancel"))
				.withBtnOkLabel(c.getString("54"))
				.withTitle(title);
	}

	public static ConfirmationBehavior newOkCancelDangerConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelConfirmCfg(c, title)
				.withBtnOkClass("btn btn-sm btn-danger")
				.withBtnOkIconClass("fas fa-exclamation-triangle")
				);
	}
}
