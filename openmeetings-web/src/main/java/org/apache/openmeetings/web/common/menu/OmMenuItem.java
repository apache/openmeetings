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
import java.util.stream.Collectors;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

	private String title;
	private String desc;
	private IconType icon;
	private List<INavbarComponent> items = new ArrayList<>(0);
	private boolean visible = true;

	public OmMenuItem(String title, List<INavbarComponent> items) {
		this.title = title;
		this.items = items;
	}

	public OmMenuItem(String title, String desc) {
		this.title = title;
		this.desc = desc;
	}

	public OmMenuItem add(INavbarComponent item) {
		items.add(item);
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setIcon(IconType icon) {
		this.icon = icon;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public AbstractLink create(String markupId) {
		AbstractLink item;
		if (Strings.isEmpty(title)) {
			item = new MenuDivider();
		} else if (items.isEmpty()) {
			item = new NavbarAjaxLink<String>(markupId, Model.of(title)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					OmMenuItem.this.onClick(target);
				}
			}.setIconType(icon);
			item.add(AttributeModifier.append(ATTR_CLASS, "nav-link"));
		} else {
			item = new NavbarDropDownButton(Model.of(title), Model.of(icon)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected List<AbstractLink> newSubMenuButtons(String markupId) {
					return items.stream().map(mItem -> ((OmMenuItem)mItem).create(markupId)).collect(Collectors.toList());
				}
			};
		}
		item.add(AttributeModifier.append(ATTR_TITLE, desc));
		item.setVisible(visible);
		return item;
	}

	@Override
	public ComponentPosition getPosition() {
		return ComponentPosition.LEFT; //FIXME TODO
	}

	public void onClick(AjaxRequestTarget target) {
	}
}
