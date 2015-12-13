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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseHttpServlet extends HttpServlet {
	private static final long serialVersionUID = -2713422496723115524L;
	
	private BeanUtil beanUtil = new BeanUtil();

	protected <T> T getBean(Class<T> beanClass) throws ServerNotInitializedException {
		return beanUtil.getBean(beanClass, getServletContext());
	}
	
	protected void handleNotBooted(HttpServletResponse response) throws IOException {
		OutputStream out = response.getOutputStream();

		String msg = "Server is not booted yet";

		out.write(msg.getBytes());

		out.flush();
		out.close();
	}
}
