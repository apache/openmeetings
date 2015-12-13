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
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.servlet.BaseVelocityViewServlet;
import org.apache.openmeetings.servlet.ServerNotInitializedException;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ActivateUser extends BaseVelocityViewServlet {
	
	private static final long serialVersionUID = -8892729047921796170L;
	
	private static Logger log = Red5LoggerFactory.getLogger(ActivateUser.class,
			OpenmeetingsVariables.webAppRootKey);

	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) {

		try {
			String hash = httpServletRequest.getParameter("u");
			String loginURL = OpenmeetingsVariables.webAppRootPath;

			Long default_lang_id = getBean(ConfigurationDao.class)
					.getConfValue("default_lang_id", Long.class, "1");
			ctx.put("APP_NAME", getBean(ConfigurationDao.class).getAppName());
			if (hash == null) {
				// No hash
				Fieldlanguagesvalues labelid669 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(669), default_lang_id);
				Fieldlanguagesvalues labelid672 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid669.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");
			}
			//
			User user = getBean(UserManager.class).getUserByActivationHash(hash);

			if (user == null) {
				// No User Found with this Hash
				Fieldlanguagesvalues labelid669 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(669), default_lang_id);
				Fieldlanguagesvalues labelid672 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid669.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			} else if (user.getStatus() == 1) {
				// already activated
				Fieldlanguagesvalues labelid670 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(670), default_lang_id);
				Fieldlanguagesvalues labelid672 = getBean(FieldManager.class)
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

				getBean(UserManager.class).updateUser(user);

				Fieldlanguagesvalues labelid671 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(671), default_lang_id);
				Fieldlanguagesvalues labelid672 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", labelid671.getValue());
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			} else {
				// unkown Status
				Fieldlanguagesvalues labelid672 = getBean(FieldManager.class)
						.getFieldByIdAndLanguage(new Long(672), default_lang_id);

				ctx.put("message", "Unkown Status");
				ctx.put("link",
						"<a href='" + loginURL + "'>" + labelid672.getValue()
								+ "</a>");
				return getVelocityView().getVelocityEngine().getTemplate(
						"activation_template.vm");

			}

		} catch (ServerNotInitializedException err) {
			return getBooting();
		} catch (Exception err) {
			log.error("[ActivateUser]", err);
			err.printStackTrace();
		}
		return null;
	}
}
