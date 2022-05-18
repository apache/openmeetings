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
package org.apache.openmeetings.web.common.menu;

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;

public class OmMenuItem implements INavbarComponent {
	private static final long serialVersionUID = 1L;

	private final String title;
	private final String desc;
	private final IconType icon;
	private final List<INavbarComponent> items = new ArrayList<>(0);
	private boolean visible = true;

	public OmMenuItem(String title, String desc) {
		this(title, desc, null, List.of());
	}

	public OmMenuItem(String title, String desc, boolean visible) {
		this(title, desc);
		this.visible = visible;
	}

	public OmMenuItem(String title, List<INavbarComponent> items) {
		this(title, null, null, items);
	}

	public OmMenuItem(String title, String desc, IconType icon) {
		this(title, desc, icon, List.of());
	}

	public OmMenuItem(String title, String desc, IconType icon, List<INavbarComponent> items) {
		this.title = title;
		this.desc = desc;
		this.icon = icon;
		this.items.addAll(items);
	}

	public OmMenuItem add(INavbarComponent item) {
		items.add(item);
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}

	@Override
	public AbstractLink create(String markupId) {
		AbstractLink item;
		if (items.isEmpty()) {
			item = createLink(markupId, true);
		} else {
			item = new NavbarDropDownButton(Model.of(title), Model.of(icon)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected List<AbstractLink> newSubMenuButtons(String markupId) {
					return items.stream().map(mItem -> ((OmMenuItem)mItem).createLink(markupId, false)).toList();
				}
			};
			setAttributes(item);
		}
		return item;
	}

	private AbstractLink createLink(String markupId, boolean topLevel) {
		if (Strings.isEmpty(title)) {
			return new MenuDivider();
		}
		NavbarAjaxLink<String> link = new NavbarAjaxLink<>(markupId, Model.of(title)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				OmMenuItem.this.onClick(target);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				OmMenuItem.this.updateAjaxAttributes(attributes);
			}
		};
		if (topLevel) {
			link.add(AttributeModifier.append(ATTR_CLASS, "nav-link"));
		}
		setAttributes(link);
		return link.setIconType(icon);
	}

	private void setAttributes(Component comp) {
		comp.add(AttributeModifier.append(ATTR_TITLE, desc));
		comp.setVisible(visible);
	}

	@Override
	public ComponentPosition getPosition() {
		return ComponentPosition.LEFT;
	}

	protected void onClick(AjaxRequestTarget target) {
		// no-op by default
	}

	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		// no-op by default
	}
}
