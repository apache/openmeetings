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
package org.apache.openmeetings.servlet.outputhandler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.Fieldmanagment;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ActivateUser extends VelocityViewServlet {
	private static final long serialVersionUID = -8892729047921796170L;
	private static Logger log = Red5LoggerFactory.getLogger(ActivateUser.class,
			OpenmeetingsVariables.webAppRootKey);

	private ConfigurationDao getConfigurationmanagement() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (ConfigurationDao) context
					.getBean("configurationDaoImpl");
		} catch (Exception err) {
			log.error("[getConfigurationmanagement]", err);
		}
		return null;
	}

	private Usermanagement getUsermanagement() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (Usermanagement) context.getBean("userManagement");
		} catch (Exception err) {
			log.error("[getUsermanagement]", err);
		}
		return null;
	}

	private Fieldmanagment getFieldmanagment() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (Fieldmanagment) context.getBean("fieldmanagment");
		} catch (Exception err) {
			log.error("[getgetFieldmanagment()]", err);
		}
		return null;
	}

	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) {

		try {

			if (getConfigurationmanagement() == null
					|| getUsermanagement() == null
					|| getFieldmanagment() == null) {
				return getVelocityView().getVelocityEngine().getTemplate(
						"booting.vm");
			}

			String hash = httpServletRequest.getParameter("u");
			String loginURL = OpenmeetingsVariables.webAppRootPath;

			Long default_lang_id = getConfigurationmanagement()
					.getConfValue("default_lang_id", Long.class, "1");
			ctx.put("APP_NAME", getConfigurationmanagement().getAppName());
			if (hash == null) {
				// No hash
				Fieldlanguagesvalues labelid669 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(669), default_lang_id);
				Fieldlanguagesvalues labelid672 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid669.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");
			}
			//
			User user = getUsermanagement().getUserByActivationHash(hash);

			if (user == null) {
				// No User Found with this Hash
				Fieldlanguagesvalues labelid669 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(669), default_lang_id);
				Fieldlanguagesvalues labelid672 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid669.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			} else if (user.getStatus() == 1) {
				// already activated
				Fieldlanguagesvalues labelid670 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(670), default_lang_id);
				Fieldlanguagesvalues labelid672 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid670.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			} else if (user.getStatus() == 0) {
				// activate
				user.setStatus(1);
				user.setUpdatetime(new Date());

				getUsermanagement().updateUser(user);

				Fieldlanguagesvalues labelid671 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(671), default_lang_id);
				Fieldlanguagesvalues labelid672 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid671.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			} else {
				// unkown Status
				Fieldlanguagesvalues labelid672 = getFieldmanagment()
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", "Unkown Status");
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			}

		} catch (Exception err) {
			log.error("[ActivateUser]", err);
			err.printStackTrace();
		}
		return null;
	}
}
