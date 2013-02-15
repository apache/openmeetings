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

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.servlet.BaseVelocityViewServlet;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class DefaultIndex extends BaseVelocityViewServlet {
	
	private static final long serialVersionUID = 3043617619650666432L;
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			DefaultIndex.class, OpenmeetingsVariables.webAppRootKey);

	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) {

		try {

			if (getBean(ConfigurationDao.class) == null) {
				return getVelocityView().getVelocityEngine().getTemplate(
						"booting.vm");
			}

			String template = "usual_template.vm";
			ctx.put("APP_NAME", getBean(ConfigurationDao.class).getAppName());
			// Parse the Param for the SWF URL
			String swf = httpServletRequest.getParameter("swf");
			if (swf == null) {
				ctx.put("SWF_URL", "main.as3.swf10.swf");
			} else {
				ctx.put("SWF_URL", swf);
			}

			String SWF_PARAMS = "";
			String SWF_FLASHVARS = "";

			// Load params from URL and set into wrapper code
			if (httpServletRequest.getParameterMap() != null) {
				for (Iterator<String> iter = httpServletRequest.getParameterMap()
						.keySet().iterator(); iter.hasNext();) {
					String paramKey = iter.next();
					SWF_FLASHVARS += paramKey
							+ "="
							+ httpServletRequest.getParameterMap()
									.get(paramKey)[0] + "&amp;";
					SWF_PARAMS += paramKey
							+ "="
							+ httpServletRequest.getParameterMap()
									.get(paramKey)[0] + "&amp;";
				}
			}

			HashMap<String, String> defaultValuesMap = new HashMap<String, String>();

			defaultValuesMap.put("lzt", "swf");
			defaultValuesMap.put("lzproxied", "solo");
			defaultValuesMap.put("lzr", "swf8");
			defaultValuesMap.put("bgcolor", "%23ffffff");
			defaultValuesMap.put("width", "100%25");
			defaultValuesMap.put("height", "100%25");
			// defaultValuesMap.put("__lzurl","main.lzx%3Flzt%3Dswf%26lzproxied%3Dsolo%26lzr%3Dswf8");
			defaultValuesMap.put("__lzminimumversion", "8");
			defaultValuesMap.put("id", "lzapp");

			for (Iterator<String> iter = defaultValuesMap.keySet().iterator(); iter
					.hasNext();) {
				String paramKey = iter.next();
				SWF_PARAMS += paramKey + "=" + defaultValuesMap.get(paramKey);
				SWF_FLASHVARS += paramKey + "="
						+ defaultValuesMap.get(paramKey);
				if (iter.hasNext()) {
					SWF_PARAMS += "&";
					SWF_FLASHVARS += "&amp;";
				}
			}

			ctx.put("SWF_PARAMS", SWF_PARAMS);
			ctx.put("SWF_FLASHVARS", SWF_FLASHVARS);

			return getVelocityView().getVelocityEngine().getTemplate(template);

		} catch (Exception er) {
			System.out.println("Error downloading: " + er);
			er.printStackTrace();
			log.error("[Calendar :: service]", er);
		}
		return null;
	}
}