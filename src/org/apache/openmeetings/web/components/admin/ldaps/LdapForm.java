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
package org.apache.openmeetings.web.components.admin.ldaps;

import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;

public class LdapForm extends AdminBaseForm<LdapConfig> {

	private static final long serialVersionUID = 1L;

	public LdapForm(String id, final LdapConfig ldapConfig) {
		super(id, new CompoundPropertyModel<LdapConfig>(ldapConfig));
		setOutputMarkupId(true);
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);
	}
}
