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
package org.apache.openmeetings.axis.services;

import javax.servlet.ServletContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.openmeetings.servlet.BeanUtil;
import org.apache.openmeetings.servlet.ServerNotInitializedException;

public abstract class BaseWebService {
	private BeanUtil beanUtil = new BeanUtil();
	
	private ServletContext getServletContext() throws AxisFault {
		try {
			MessageContext mc = MessageContext.getCurrentMessageContext();
			return (ServletContext) mc
					.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
		} catch (Exception err) {
			throw new AxisFault("Servlet context is not available yet, retry in couple of seconds");
		}
	}
	
	// package access
	<T> T getBean(Class<? extends T> clazz) throws AxisFault {
		try {
			return beanUtil.getBean(clazz, getServletContext());
		} catch (ServerNotInitializedException e) {
			throw new AxisFault(e.getMessage());
		}
	}
}
