package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.lang.Fieldvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class LangExport extends HttpServlet {
	private static final long serialVersionUID = 243294279856160463L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			LangExport.class, ScopeApplicationAdapter.webAppRootKey);

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Usermanagement) context.getBean("userManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	public Fieldmanagment getFieldmanagment() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Fieldmanagment) context.getBean("fieldmanagment");
			}
		} catch (Exception err) {
			log.error("[getFieldmanagment]", err);
		}
		return null;
	}

	public FieldLanguageDaoImpl getFieldLanguageDaoImpl() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (FieldLanguageDaoImpl) context
						.getBean("fieldLanguageDaoImpl");
			}
		} catch (Exception err) {
			log.error("[getFieldLanguageDaoImpl]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {

			if (getUserManagement() == null
					|| getFieldLanguageDaoImpl() == null
					|| getFieldmanagment() == null
					|| getSessionManagement() == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			String language = httpServletRequest.getParameter("language");
			if (language == null) {
				language = "0";
			}
			Long language_id = Long.valueOf(language).longValue();
			log.debug("language_id: " + language_id);

			Long users_id = getSessionManagement().checkSession(sid);
			Long user_level = getUserManagement().getUserLevelByID(users_id);

			log.debug("users_id: " + users_id);
			log.debug("user_level: " + user_level);

			if (user_level != null && user_level > 0) {
				FieldLanguage fl = getFieldLanguageDaoImpl()
						.getFieldLanguageById(language_id);

				List<Fieldvalues> fvList = getFieldmanagment()
						.getMixedFieldValuesList(language_id);

				if (fl != null && fvList != null) {
					Document doc = this.createDocument(fvList);

					String requestedFile = fl.getName() + ".xml";

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					OutputStream out = httpServletResponse.getOutputStream();
					httpServletResponse
							.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition",
							"attachment; filename=\"" + requestedFile + "\"");
					// httpServletResponse.setHeader("Content-Length", ""+
					// rf.length());

					this.serializetoXML(out, "UTF-8", doc);

					out.flush();
					out.close();
				}
			} else {
				log.debug("ERROR LangExport: not authorized FileDownload "
						+ (new Date()));
			}

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

	public Document createDocument(List<Fieldvalues> fvList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment("###############################################\n"
				+ "This File is auto-generated by the LanguageEditor \n"
				+ "to add new Languages or modify/customize it use the LanguageEditor \n"
				+ "see http://code.google.com/p/openmeetings/wiki/LanguageEditor for Details \n"
				+ "###############################################");

		Element root = document.addElement("language");

		for (Iterator<Fieldvalues> it = fvList.iterator(); it.hasNext();) {
			Fieldvalues fv = it.next();
			Element eTemp = root.addElement("string")
					.addAttribute("id", fv.getFieldvalues_id().toString())
					.addAttribute("name", fv.getName());
			Element value = eTemp.addElement("value");
			if (fv.getFieldlanguagesvalue() != null) {
				value.addText(fv.getFieldlanguagesvalue().getValue());
			} else {
				// Add english default text
				Fieldlanguagesvalues flv = getFieldmanagment()
						.getFieldByIdAndLanguage(fv.getFieldvalues_id(), 1L);
				if (flv != null) {
					value.addText(flv.getValue());
				} else {
					value.addText("");
				}
			}
		}

		return document;
	}

	public void serializetoXML(OutputStream out, String aEncodingScheme,
			Document doc) throws Exception {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding(aEncodingScheme);
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
	}

}
