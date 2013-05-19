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
package org.apache.openmeetings.web.pages.install;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

//TODO maybe JQ wizard should be used
public class InstallWizard extends Wizard {
	private static final Logger log = Red5LoggerFactory.getLogger(InstallWizard.class, webAppRootKey);
	private static final long serialVersionUID = -8460835123671633121L;
	private InstallationConfig cfg;
	private CompoundPropertyModel<InstallWizard> model;
	private final static List<SelectOption> yesNoList = Arrays.asList(SelectOption.NO, SelectOption.YES);
	private final static List<String> allFonts = Arrays.asList("TimesNewRoman", "Verdana", "Arial");
	
	private final class WelcomeStep extends WizardStep {
		private static final long serialVersionUID = 323088743806847666L;

		public WelcomeStep() {
			//TODO localize
			//TODO add check for DB connection
			setTitleModel(Model.of(cfg.appName + " - Installation"));
            setSummaryModel(Model.of(""));
            add(new Label("app", cfg.appName));
		}
	}

	private final class ParamsStep extends WizardStep {
		private static final long serialVersionUID = -2420496234328471542L;
		
		//TODO separate to separate steps
		public ParamsStep() throws Exception {
			//TODO localize
			//TODO validation
			setTitleModel(Model.of(cfg.appName + " - Installation"));
            setSummaryModel(Model.of(""));
            add(new RequiredTextField<String>("cfg.username"));
            add(new PasswordTextField("cfg.password"));
            add(new RequiredTextField<String>("cfg.email"));
            add(new TzDropDown("ical_timeZone"));
            add(new RequiredTextField<String>("cfg.group"));
            add(new YesNoDropDown("allowFrontendRegister"));
            add(new YesNoDropDown("sendEmailAtRegister"));
            add(new YesNoDropDown("sendEmailWithVerficationCode"));
            add(new YesNoDropDown("createDefaultRooms"));
            add(new TextField<String>("cfg.mailReferer"));
            add(new TextField<String>("cfg.smtpServer"));
            add(new TextField<String>("cfg.smtpPort"));
            add(new TextField<String>("cfg.mailAuthName"));
            add(new PasswordTextField("cfg.mailAuthPass").setRequired(false));
            add(new YesNoDropDown("mailUseTls"));
            //TODO check mail server
            add(new YesNoDropDown("replyToOrganizer"));
            add(new LangDropDown("defaultLangId"));
            add(new DropDownChoice<String>("cfg.defaultExportFont", allFonts));
            add(new TextField<String>("cfg.swfZoom"));
            add(new TextField<String>("cfg.swfJpegQuality"));
            add(new TextField<String>("cfg.swfPath"));
            add(new TextField<String>("cfg.imageMagicPath"));
            add(new TextField<String>("cfg.ffmpegPath"));
            add(new TextField<String>("cfg.soxPath"));
            add(new TextField<String>("cfg.jodPath"));
            add(new TextField<String>("cfg.officePath"));
            add(new RequiredTextField<String>("cfg.cryptClassName")); //Validate class
            
            //TODO add check for red5sip connection
            add(new YesNoDropDown("red5SipEnable"));
            add(new TextField<String>("cfg.red5SipRoomPrefix"));
            add(new TextField<String>("cfg.red5SipExtenContext"));
		}
		
		@Override
		public void applyState() {
			// TODO Auto-generated method stub
			super.applyState();
		}
	}
	
	private final class CongradulationsStep extends WizardStep {
		private static final long serialVersionUID = 630278992275373536L;

		public CongradulationsStep() {
			add(new Link<Void>("url") {
				private static final long serialVersionUID = -8589303259501551418L;

				@Override
				public void onClick() {
					setResponsePage(Application.get().getHomePage());
				}
			});
		}
	}
	
	//onInit, applyState
	public InstallWizard(String id) {
		super(id);
		
		cfg = new InstallationConfig();
		setDefaultModel(model = new CompoundPropertyModel<InstallWizard>(this));
		WizardModel wmodel = new WizardModel();
		try {
			wmodel.add(new WelcomeStep());
			wmodel.add(new ParamsStep());
			//TODO add install/progress step
			wmodel.add(new CongradulationsStep());
		} catch (Exception e) {
			log.error("Error while creating Wizard!", e);
		}
		init(wmodel);
	}
	
	@Override
	public void onCancel() {
		setResponsePage(Application.isInstalled() ? Application.get().getHomePage() : InstallWizardPage.class);
	}
	
	private static class SelectOption implements Serializable {
		private static final long serialVersionUID = 2559982745410615390L;
		private static SelectOption NO = new SelectOption("0", "No");
		private static SelectOption YES = new SelectOption("1", "Yes");
		public String key;
		@SuppressWarnings("unused")
		public String value;

		SelectOption(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	private abstract class WizardDropDown<T>  extends DropDownChoice<T> {
		private static final long serialVersionUID = 8870736740532631296L;
		T option;
		IModel<Object> propModel;
		
		WizardDropDown(String id) {
			super(id);
			propModel = InstallWizard.this.model.bind("cfg." + id);
			setModel(new PropertyModel<T>(this, "option"));
		}
	}
	
	private final class TzDropDown extends WizardDropDown<OmTimeZone> {
		private static final long serialVersionUID = 6084349711073918837L;

		public TzDropDown(String id) throws Exception {
			super(id);
            List<OmTimeZone> tzList = getBean(ImportInitvalues.class).getTimeZones();
			setChoices(tzList);
			setChoiceRenderer(new IChoiceRenderer<OmTimeZone>() {
				private static final long serialVersionUID = 1L;
				
				public Object getDisplayValue(OmTimeZone object) {
    				return object.getLabel() + " (" + object.getJname() + ")";
    			}
    			public String getIdValue(OmTimeZone object, int index) {
    				return object.getIcal();
    			}
			});
			option = tzList.get(0);
			for (OmTimeZone tz : tzList) {
				if (tz.getIcal().equals(propModel.getObject())) {
					option = tz;
					break;
				}
			}
		}
		
		@Override
		protected void onModelChanged() {
			if (propModel != null && option != null) {
				propModel.setObject(option.getIcal());
			}
		}
	}
	
	private class SelectOptionDropDown extends WizardDropDown<SelectOption> {
		private static final long serialVersionUID = -1433015274371279328L;

		SelectOptionDropDown(String id) {
			super(id);
			setChoiceRenderer(new ChoiceRenderer<SelectOption>("value", "key"));
		}
		
		@Override
		protected void onModelChanged() {
			if (propModel != null && option != null) {
				propModel.setObject(option.key);
			}
		}
		
	}
	private final class YesNoDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = 578375825530725477L;
		
		YesNoDropDown(String id) {
			super(id);
			setChoices(yesNoList);
			this.option = SelectOption.NO.key.equals(propModel.getObject()) ?
					SelectOption.NO : SelectOption.YES;
		}
	}
	
	private final class LangDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = -2826765890538795285L;

		public LangDropDown(String id) throws Exception {
			super(id);
			LinkedHashMap<Integer, LinkedHashMap<String, Object>> allLanguagesAll
				= getBean(ImportInitvalues.class).getLanguageFiles();
			
			List<SelectOption> list = new ArrayList<SelectOption>();
			
			for (Integer key : allLanguagesAll.keySet()) {
				String langName = (String) allLanguagesAll.get(key).get("name");
				String langCode = (String) allLanguagesAll.get(key).get("code");
				SelectOption op = new SelectOption(key.toString(), langName);
				if (langCode != null) {
					if (getSession().getLocale().toString().startsWith(langCode)) {
						option = op;
					}
					list.add(op);
				}
				if (option == null && key.toString().equals(cfg.defaultLangId)) {
					option = op;
				}
			}
			setChoices(list);
		}
	}
}
