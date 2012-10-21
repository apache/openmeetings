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
package org.apache.openmeetings.web.data;

import java.io.Serializable;

import org.apache.openmeetings.persistence.beans.OmEntity;
import org.apache.openmeetings.web.components.admin.OmDataView;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class DataViewContainer<T extends OmEntity> implements Serializable {
	private static final long serialVersionUID = -1027478954223527890L;
	public WebMarkupContainer container;
	public OmDataView<T> view;
	public OmOrderByBorder<T>[] orderLinks;
	
	public DataViewContainer(WebMarkupContainer container, OmDataView<T> view) {
		this.container = container;
		this.view = view;
	}
	
	public void setLinks(OmOrderByBorder<T>... orderLinks) {
		this.orderLinks = orderLinks;
	}
}
