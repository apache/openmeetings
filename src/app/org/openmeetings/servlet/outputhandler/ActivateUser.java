package org.openmeetings.servlet.outputhandler;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ActivateUser extends VelocityViewServlet {
	private static final long serialVersionUID = -8892729047921796170L;
	private static Logger log = Red5LoggerFactory.getLogger(ActivateUser.class,
			ScopeApplicationAdapter.webAppRootKey);

	private Configurationmanagement getConfigurationmanagement() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (Configurationmanagement) context.getBean("cfgManagement");
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
			ServletContext context = getServletContext();
			String loginURL = context.getInitParameter("webAppRootKey");

			ctx.put("APPLICATION_NAME", context.getServletContextName());
			if (hash == null) {
				// No hash
				Long default_lang_id = Long.valueOf(
						getConfigurationmanagement().getConfKey(3,
								"default_lang_id").getConf_value()).longValue();
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
			Users user = getUsermanagement().getUserByActivationHash(hash);

			if (user == null) {
				// No User Found with this Hash
				Long default_lang_id = Long.valueOf(
						getConfigurationmanagement().getConfKey(3,
								"default_lang_id").getConf_value()).longValue();

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
				Long default_lang_id = Long.valueOf(
						getConfigurationmanagement().getConfKey(3,
								"default_lang_id").getConf_value()).longValue();

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

				Long default_lang_id = Long.valueOf(
						getConfigurationmanagement().getConfKey(3,
								"default_lang_id").getConf_value()).longValue();

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
				Long default_lang_id = Long.valueOf(
						getConfigurationmanagement().getConfKey(3,
								"default_lang_id").getConf_value()).longValue();

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
