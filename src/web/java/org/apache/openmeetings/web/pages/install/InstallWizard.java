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

import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONE_SET;
import static org.apache.wicket.validation.validator.RangeValidator.range;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ErrorMessagePanel;
import org.apache.openmeetings.web.common.OmLabel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;

//TODO maybe JQ wizard should be used
public class InstallWizard extends Wizard {
	private static final long serialVersionUID = 1L;
	private InstallationConfig cfg;
	private CompoundPropertyModel<InstallWizard> model;
	private final static List<SelectOption> yesNoList = Arrays.asList(SelectOption.NO, SelectOption.YES);
	private final static List<SelectOption> yesNoTextList = Arrays.asList(SelectOption.NO_TEXT, SelectOption.YES_TEXT);
	private final static List<String> allFonts = Arrays.asList("TimesNewRoman", "Verdana", "Arial");
	private final IDynamicWizardStep welcomeStep;
	private final ParamsStep1 paramsStep1;
	private final IDynamicWizardStep paramsStep2;
	private final IDynamicWizardStep paramsStep3;
	private final IDynamicWizardStep paramsStep4;
	private final InstallStep installStep;
	private Throwable th = null;
	
	public void initTzDropDown() {
		paramsStep1.tzDropDown.init();
	}
	
	//onInit, applyState
	public InstallWizard(String id) throws Exception {
		super(id);
		//TODO enable install after first params
		cfg = new InstallationConfig();
		setDefaultModel(model = new CompoundPropertyModel<InstallWizard>(this));
		welcomeStep = new WelcomeStep();
		paramsStep1 = new ParamsStep1();
		paramsStep2 = new ParamsStep2();
		paramsStep3 = new ParamsStep3();
		paramsStep4 = new ParamsStep4();
		//TODO add install/progress step
		installStep = new InstallStep();

		DynamicWizardModel wmodel = new DynamicWizardModel(welcomeStep);
		wmodel.setCancelVisible(false);
		wmodel.setLastVisible(true);
		init(wmodel);
	}
	
	@Override
	protected Component newButtonBar(String id) {
		final Panel bBar = (Panel)super.newButtonBar(id);
		AjaxButton finish = new AjaxButton("finish", new ResourceModel("org.apache.wicket.extensions.wizard.finish")) {
			private static final long serialVersionUID = 1L;

			public boolean isEnabled() {
				IWizardStep activeStep = getWizardModel().getActiveStep();
				return ((activeStep != null) && getWizardModel().isLastStep(activeStep));
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				installStep.startInstallation(target);
				target.add(bBar.setEnabled(false));
			}
		};
		return bBar.replace(finish).setOutputMarkupId(true);
	}
	
	private abstract class BaseStep extends DynamicWizardStep {
		private static final long serialVersionUID = 1L;

		public BaseStep(IDynamicWizardStep prev) {
			super(prev);
			//TODO localize
			setTitleModel(Model.of(cfg.appName + " - Installation"));
            setSummaryModel(Model.of(""));
		}
	}
	
	private final class WelcomeStep extends BaseStep {
		private static final long serialVersionUID = 1L;

		public WelcomeStep() {
			super(null);
			//TODO localize
			//TODO add check for DB connection
            add(new OmLabel("step", "install.wizard.welcome.panel", cfg.appName).setEscapeModelStrings(false));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return paramsStep1;
		}
	}

	private final class ParamsStep1 extends BaseStep {
		private static final long serialVersionUID = 1L;
		private final TzDropDown tzDropDown;

		public ParamsStep1() throws Exception {
			super(welcomeStep);
			//TODO localize
            add(new RequiredTextField<String>("cfg.username").add(minimumLength(USER_LOGIN_MINIMUM_LENGTH)));
            add(new PasswordTextField("cfg.password").add(minimumLength(USER_PASSWORD_MINIMUM_LENGTH)));
            add(new RequiredTextField<String>("cfg.email").add(RfcCompliantEmailAddressValidator.getInstance()));
            add(tzDropDown = new TzDropDown("ical_timeZone"));
            add(new RequiredTextField<String>("cfg.group"));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return paramsStep2;
		}
		
		@Override
		public boolean isLastAvailable() {
			return true;
		}
		
		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}
	}
	
	private final class ParamsStep2 extends BaseStep {
		private static final long serialVersionUID = 1L;

		public ParamsStep2() throws Exception {
			super(paramsStep1);
			//TODO localize
			//TODO validation
            add(new YesNoDropDown("allowFrontendRegister"));
            add(new YesNoDropDown("sendEmailAtRegister"));
            add(new YesNoDropDown("sendEmailWithVerficationCode"));
            add(new YesNoDropDown("createDefaultRooms"));
            add(new TextField<String>("cfg.mailReferer"));
            add(new TextField<String>("cfg.smtpServer"));
            add(new TextField<Integer>("cfg.smtpPort").setRequired(true));
            add(new TextField<String>("cfg.mailAuthName"));
            add(new PasswordTextField("cfg.mailAuthPass").setRequired(false));
            add(new YesNoDropDown("mailUseTls"));
            //TODO check mail server
            add(new YesNoDropDown("replyToOrganizer"));
            add(new LangDropDown("defaultLangId"));
            add(new DropDownChoice<String>("cfg.defaultExportFont", allFonts));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return paramsStep3;
		}
		
		@Override
		public boolean isLastAvailable() {
			return true;
		}
		
		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}
	}
	
	private final class ParamsStep3 extends BaseStep {
		private static final long serialVersionUID = 1L;

		public ParamsStep3() {
			super(paramsStep2);
			
            add(new TextField<Integer>("cfg.swfZoom").setRequired(true).add(range(50, 600)));
            add(new TextField<Integer>("cfg.swfJpegQuality").setRequired(true).add(range(1, 100)));
            add(new TextField<String>("cfg.swfPath"));
            add(new TextField<String>("cfg.imageMagicPath"));
            add(new TextField<String>("cfg.ffmpegPath"));
            add(new TextField<String>("cfg.soxPath"));
            add(new TextField<String>("cfg.jodPath"));
            add(new TextField<String>("cfg.officePath"));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return paramsStep4;
		}
		
		@Override
		public boolean isLastAvailable() {
			return true;
		}
		
		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}
	}
	
	private final class ParamsStep4 extends BaseStep {
		private static final long serialVersionUID = 1L;

		public ParamsStep4() {
			super(paramsStep3);
            add(new RequiredTextField<String>("cfg.cryptClassName")); //Validate class
            
            //TODO add check for red5sip connection
            add(new YesNoTextDropDown("red5SipEnable"));
            add(new TextField<String>("cfg.red5SipRoomPrefix"));
            add(new TextField<String>("cfg.red5SipExtenContext"));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return installStep;
		}
		
		@Override
		public boolean isLastAvailable() {
			return true;
		}
		
		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}
}
	
	private final class InstallStep extends BaseStep {
		private static final long serialVersionUID = 1L;
		private final CongratulationsPanel congrat;
		private WebMarkupContainer container = new WebMarkupContainer("container");
		private AbstractAjaxTimerBehavior timer;
		private ProgressBar progressBar;
		private Label desc = new Label("desc", getString("install.wizard.install.desc"));
		private boolean started = false;
		
		public void startInstallation(AjaxRequestTarget target) {
			started = true;
			timer.restart(target);
			new Thread(new InstallProcess(Application.get()._getBean(ImportInitvalues.class))
				, "Openmeetings - Installation").start();
			//progressBar.setVisible(true);
			desc.setDefaultModelObject(getString("install.wizard.install.started"));
			target.add(desc, container);
		}
		
		public InstallStep() {
			super(paramsStep4);
			
			add(desc.setOutputMarkupId(true));
			// Timer //
			container.add(timer = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimer(AjaxRequestTarget target) {
					if (!started) {
						timer.stop(target);
						return;
					}
					if (th != null) {
						timer.stop(target);
						//TODO change text, localize
						progressBar.setVisible(false);
						target.add(container.replace(new ErrorMessagePanel("status", getString("install.wizard.install.failed"), th))
							, desc.setVisible(false)
							);
					} else {
						progressBar.setModelObject(Application.get()._getBean(ImportInitvalues.class).getProgress());
						progressBar.respond(target);
						//TODO uncomment later target.add(value);
						//TODO add current step result as info
					}
				}
			});
			container.add(progressBar = new ProgressBar("progress", new Model<Integer>(0)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComplete(AjaxRequestTarget target) {
					timer.stop(target);
					progressBar.setVisible(false);
					congrat.setVisible(true);
					target.add(container, desc.setVisible(false));
				}
			});
			//TODO uncomment later progressBar.add(value = new Label("value", progressBar.getModel()));
			//TODO uncomment later value.setOutputMarkupId(true);
			//progressBar.setVisible(false);
			
			container.add(congrat = new CongratulationsPanel("status"));
			congrat.setVisible(false);
			
			add(container.setOutputMarkupId(true));
		}

		public boolean isLastStep() {
			return true;
		}

		public IDynamicWizardStep next() {
			return null;
		}
	}
	
	private class InstallProcess implements Runnable {
		private ImportInitvalues installer;
		
		public InstallProcess(ImportInitvalues installer) {
			this.installer = installer;
			th = null;
		}
		
		public void run() {
			try {
				installer.loadAll(cfg, true);
			} catch (Exception e) {
				th = e;
			}
		}
	}
	
	private static class SelectOption implements Serializable {
		private static final long serialVersionUID = 2559982745410615390L;
		private static SelectOption NO = new SelectOption("0", "No");
		private static SelectOption NO_TEXT = new SelectOption("no", "No");
		private static SelectOption YES = new SelectOption("1", "Yes");
		private static SelectOption YES_TEXT = new SelectOption("yes", "Yes");
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
	
	private final class TzDropDown extends WizardDropDown<String> {
		private static final long serialVersionUID = 1L;

		public void init() {
			List<String> tzList = AVAILABLE_TIMEZONES;
			String tzId = WebSession.get().getClientTimeZone();
			option = AVAILABLE_TIMEZONE_SET.contains(tzId) ? tzId : tzList.get(0);
		}
		
		public TzDropDown(String id) throws Exception {
			super(id);
			setChoices(AVAILABLE_TIMEZONES);
			setChoiceRenderer(new IChoiceRenderer<String>() {
				private static final long serialVersionUID = 1L;
				
				public Object getDisplayValue(String object) {
    				return object.toString();
    			}
				public String getIdValue(String object, int index) {
					return object.toString();
				}
			});
		}
		
		@Override
		protected void onModelChanged() {
			if (propModel != null && option != null) {
				propModel.setObject(option);
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
	
	private final class YesNoTextDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = 578375825530725477L;
		
		YesNoTextDropDown(String id) {
			super(id);
			setChoices(yesNoTextList);
			this.option = SelectOption.NO_TEXT.key.equals(propModel.getObject()) ?
					SelectOption.NO_TEXT : SelectOption.YES_TEXT;
		}
	}
	
	private final class LangDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = -2826765890538795285L;

		public LangDropDown(String id) throws Exception {
			super(id);
			Map<Integer, Map<String, Object>> allLanguagesAll = ImportInitvalues.getLanguageFiles();
			
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
