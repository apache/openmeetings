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
package org.apache.openmeetings.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.tools.view.VelocityViewServlet;

public abstract class BaseVelocityViewServlet extends VelocityViewServlet {
	private static final long serialVersionUID = 5075547695876679590L;
	
	private BeanUtil beanUtil = new BeanUtil();

	protected <T> T getBean(Class<T> beanClass) throws ServerNotInitializedException {
		return beanUtil.getBean(beanClass, getServletContext());
	}
	
	protected Template getBooting() {
		return getVelocityView().getVelocityEngine().getTemplate("booting_install.vm");
	}
	
	@Override
	protected void error(HttpServletRequest request, HttpServletResponse response, Throwable e) {
		if (e instanceof ServerNotInitializedException) {
            try {
				mergeTemplate(getBooting(), createContext(request, response), response);
				return;
			} catch (Exception e1) {
				// no-op
			}
		}
		super.error(request, response, e);
	}
}
