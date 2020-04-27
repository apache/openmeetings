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

import org.apache.wicket.util.lang.Args;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;
import de.agilecoders.wicket.jquery.Key;

/**
 * The configuration of {@link ConfirmationBehavior}. <br/>
 * See
 * <a href="http://mistic100.github.io/Bootstrap-Confirmation/#usage">Bootstrap
 * Confirmation usage</a> for all possible options and their meaning.
 */
public class ConfirmationConfig extends AbstractConfig {
	private static final long serialVersionUID = 1L;
	private static final IKey<String> Title = new Key<>("title", "Are you sure?");
	private static final IKey<Boolean> Singleton = new Key<>("singleton", Boolean.FALSE);
	private static final IKey<Boolean> Popout = new Key<>("popout", Boolean.FALSE);
	private static final IKey<String> BtnOkClass = new Key<>("btnOkClass", "btn-xs btn-primary");
	private static final IKey<String> BtnOkIconClass = new Key<>("btnOkIconClass", "glyphicon glyphicon-ok");
	private static final IKey<String> BtnOkIconContent = new Key<>("btnOkIconContent");
	private static final IKey<String> BtnOkLabel = new Key<>("btnOkLabel", "Yes");
	private static final IKey<String> BtnCancelClass = new Key<>("btnCancelClass", "btn-xs btn-default");
	private static final IKey<String> BtnCancelIconClass = new Key<>("btnCancelIconClass", "glyphicon glyphicon-remove");
	private static final IKey<String> BtnCancelIconContent = new Key<>("btnCancelIconContent");
	private static final IKey<String> BtnCancelLabel = new Key<>("btnCancelLabel", "No");
	private static final IKey<TooltipConfig.Placement> Placement = new Key<>("placement", TooltipConfig.Placement.top);
	private static final IKey<String> RootSelector = new Key<>("rootSelector");

	public ConfirmationConfig withTitle(String title) {
		put(Title, title);
		return this;
	}

	ConfirmationConfig withRootSelector(String rootSelector) {
		put(RootSelector, rootSelector);
		return this;
	}

	public ConfirmationConfig withBtnOkClass(String btnOkClass) {
		put(BtnOkClass, btnOkClass);
		return this;
	}

	public ConfirmationConfig withBtnOkIconClass(String btnOkIconClass) {
		put(BtnOkIconClass, btnOkIconClass);
		return this;
	}

	public ConfirmationConfig withBtnOkIconContent(String btnOkIconContent) {
		put(BtnOkIconContent, btnOkIconContent);
		return this;
	}

	public ConfirmationConfig withBtnOkLabel(String btnOkLabel) {
		put(BtnOkLabel, btnOkLabel);
		return this;
	}

	public ConfirmationConfig withBtnCancelClass(String btnCancelClass) {
		put(BtnCancelClass, btnCancelClass);
		return this;
	}

	public ConfirmationConfig withBtnCancelIconClass(String btnCancelIconClass) {
		put(BtnCancelIconClass, btnCancelIconClass);
		return this;
	}

	public ConfirmationConfig withBtnCancelIconContent(String btnCancelIconContent) {
		put(BtnCancelIconContent, btnCancelIconContent);
		return this;
	}

	public ConfirmationConfig withBtnCancelLabel(String btnCancelLabel) {
		put(BtnCancelLabel, btnCancelLabel);
		return this;
	}

	public ConfirmationConfig withSingleton(boolean singleton) {
		put(Singleton, singleton);
		return this;
	}

	public ConfirmationConfig withPopout(boolean popout) {
		put(Popout, popout);
		return this;
	}

	public ConfirmationConfig withPlacement(TooltipConfig.Placement placement) {
		put(Placement, Args.notNull(placement, "placement"));
		return this;
	}
}
