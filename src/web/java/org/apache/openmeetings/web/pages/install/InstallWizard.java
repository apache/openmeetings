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
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONE_SET;
import static org.apache.wicket.validation.validator.RangeValidator.range;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.cli.ConnectionProperties;
import org.apache.openmeetings.cli.ConnectionProperties.DbType;
import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ErrorMessagePanel;
import org.apache.openmeetings.web.common.OmLabel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import com.googlecode.wicket.kendo.ui.form.button.IndicatingAjaxButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

//TODO maybe JQ wizard should be used
public class InstallWizard extends Wizard {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InstallWizard.class, webAppRootKey);
	private InstallationConfig cfg;
	private CompoundPropertyModel<InstallWizard> model;
	private final static List<SelectOption> yesNoList = Arrays.asList(SelectOption.NO, SelectOption.YES);
	private final static List<SelectOption> yesNoTextList = Arrays.asList(SelectOption.NO_TEXT, SelectOption.YES_TEXT);
	private final static List<String> allFonts = Arrays.asList("TimesNewRoman", "Verdana", "Arial");
	private final IDynamicWizardStep welcomeStep;
	private final IDynamicWizardStep dbStep;
	private final ParamsStep1 paramsStep1;
	private final IDynamicWizardStep paramsStep2;
	private final IDynamicWizardStep paramsStep3;
	private final IDynamicWizardStep paramsStep4;
	private final InstallStep installStep;
	private Throwable th = null;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	
	public void initTzDropDown() {
		paramsStep1.tzDropDown.setOption();
	}
	
	//onInit, applyState
	public InstallWizard(String id) {
		super(id);
		//TODO enable install after first params
		cfg = new InstallationConfig();
		setDefaultModel(model = new CompoundPropertyModel<InstallWizard>(this));
		welcomeStep = new WelcomeStep();
		dbStep = new DbStep();
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

	protected Component newFeedbackPanel(final String id) {
		return feedback.setEscapeModelStrings(false);
	}
	
	@Override
	protected void onDetach() {
		model.detach();
		super.onDetach();
	}
	
	private abstract class BaseStep extends DynamicWizardStep {
		private static final long serialVersionUID = 1L;

		public BaseStep(IDynamicWizardStep prev) {
			super(prev);
			setTitleModel(Model.of(cfg.appName + " - " + getString("install.wizard.install.header")));
            setSummaryModel(Model.of(""));
		}
	}
	
	private final class WelcomeStep extends BaseStep {
		private static final long serialVersionUID = 1L;

		public WelcomeStep() {
			super(null);
			//TODO localize
            add(new Label("step", getString("install.wizard.welcome.panel")).setEscapeModelStrings(false));
		}

		public boolean isLastStep() {
			return false;
		}

		public IDynamicWizardStep next() {
			return dbStep;
		}
	}

	private final class DbStep extends BaseStep {
		private static final long serialVersionUID = 1L;
		private final WebMarkupContainer hostelem = new WebMarkupContainer("hostelem");
		private final WebMarkupContainer portelem = new WebMarkupContainer("portelem");
		private final RequiredTextField<String> host = new RequiredTextField<String>("host", Model.of(""));
		private final RequiredTextField<Integer> port = new RequiredTextField<Integer>("port", Model.of(0));
		private final RequiredTextField<String> dbname = new RequiredTextField<String>("dbname", Model.of(""));
		private final Form<ConnectionProperties> form = new Form<ConnectionProperties>("form", new CompoundPropertyModel<ConnectionProperties>(getProps(null))) {
			private static final long serialVersionUID = 1L;
			private final DropDownChoice<DbType> db = new DropDownChoice<DbType>("dbType", Arrays.asList(DbType.values()), new IChoiceRenderer<DbType>() {
				private static final long serialVersionUID = 1L;
				
				@Override
	        	public Object getDisplayValue(DbType object) {
	        		return getString(String.format("install.wizard.db.step.%s.name", object.name()));
	        	}
	        	
	        	@Override
	        	public String getIdValue(DbType object, int index) {
	        		return object.name();
	        	}
	        });
			private final RequiredTextField<String> user = new RequiredTextField<String>("login");
			private final TextField<String> pass = new TextField<String>("password");
			
        	{
        		add(db.add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(feedback);
						initForm(true, target);
					}
				}));
        		add(hostelem.add(host), portelem.add(port));
        		add(dbname, user, pass);
        		add(new IndicatingAjaxButton("check") {
					private static final long serialVersionUID = 1L;
					
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						target.add(feedback);
					}
					
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						target.add(feedback);
					}
        		});
        	}
        	
        	protected void onValidateModelObjects() {
				ConnectionProperties props = getModelObject();
				try {
					Class.forName(props.getDriver());
				} catch (Exception e) {
					form.error(new StringResourceModel("install.wizard.db.step.nodriver", InstallWizard.this, null, null, getString("install.wizard.db.step.instructions." + props.getDbType().name())).getObject());
					return;
				}
				Connection conn = null;
				boolean valid = true;
				try {
					ConnectionPropertiesPatcher.updateUrl(props, host.getModelObject(), "" + port.getModelObject(), dbname.getModelObject());
					DriverManager.setLoginTimeout(3);
					conn = DriverManager.getConnection(props.getURL(), props.getLogin(), props.getPassword());
					valid = conn.isValid(0); //no timeout
					String sql = null;
					switch (props.getDbType()) {
						case db2:
							sql = "select count(*) from systables";
							break;
						case oracle:
							sql = "SELECT 1 FROM DUAL";
							break;
						case derby:
							sql = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
							break;
						default:
							sql = "SELECT 1";
							break;
					}
					valid &= conn.prepareStatement(sql).execute();
					if (!valid) {
						form.error(getString("install.wizard.db.step.notvalid") + "<br/>" + getString("install.wizard.db.step.instructions." + props.getDbType().name()));
					}
				} catch (Exception e) {
					form.error(e.getMessage() + "<br/>" + getString("install.wizard.db.step.instructions." + props.getDbType().name()));
					log.error("error while testing DB", e);
					valid = false;
				} finally {
					if (conn != null) {
						try {
							conn.close();
						}  catch (Exception e) {
							//no-op
						}
					}
				}
				if (valid) {
					form.success(getString("install.wizard.db.step.valid"));
				}
        	}
        	
        	protected void onSubmit() {
        		try {
        			ConnectionPropertiesPatcher.patch(getModelObject());
        			XmlWebApplicationContext ctx = (XmlWebApplicationContext)getWebApplicationContext(Application.get().getServletContext());
        			AutowireCapableBeanFactory f = ctx.getBeanFactory();
        			LocalEntityManagerFactoryBean emb = f.getBean(LocalEntityManagerFactoryBean.class);
        			emb.afterPropertiesSet();
        		} catch (Exception e) {
					form.error(new StringResourceModel("install.wizard.db.step.error.patch", InstallWizard.this, null, null, e.getMessage()).getObject());
					log.error("error while patching", e);
        		}
        	}
        };
        
        private ConnectionProperties getProps(DbType type) {
			ConnectionProperties props = new ConnectionProperties();
			try {
				File conf = OmFileHelper.getPersistence(type);
				props = ConnectionPropertiesPatcher.getConnectionProperties(conf);
			} catch (Exception e) {
				form.warn(getString("install.wizard.db.step.errorprops"));
			}
        	return props;
        }
        
		private void initForm(boolean getProps, AjaxRequestTarget target) {
			ConnectionProperties props = getProps ? getProps(form.getModelObject().getDbType()) : form.getModelObject();
			form.setModelObject(props);
			hostelem.setVisible(props.getDbType() != DbType.derby);
			portelem.setVisible(props.getDbType() != DbType.derby);
			try {
				switch (props.getDbType()) {
					case mssql: {
						String url = props.getURL().substring("jdbc:sqlserver://".length());
						String[] parts = url.split(";");
						String[] hp = parts[0].split(":");
						host.setModelObject(hp[0]);
						port.setModelObject(Integer.parseInt(hp[1]));
						dbname.setModelObject(parts[1].substring(parts[1].indexOf('=') + 1));
						}
						break;
					case oracle: {
						String[] parts = props.getURL().split(":");
						host.setModelObject(parts[3].substring(1));
						port.setModelObject(Integer.parseInt(parts[4]));
						dbname.setModelObject(parts[5]);
						}
						break;
					case derby: {
						host.setModelObject("");
						port.setModelObject(0);
						String[] parts = props.getURL().split(";");
						String[] hp = parts[0].split(":");
						dbname.setModelObject(hp[2]);
						}
						break;
					default:
						URI uri = URI.create(props.getURL().substring(5));
						host.setModelObject(uri.getHost());
						port.setModelObject(uri.getPort());
						dbname.setModelObject(uri.getPath().substring(1));
						break;
				}
			} catch (Exception e) {
				form.warn(getString("install.wizard.db.step.errorprops"));
			}
			if (target != null) {
				target.add(form);
			}
		}
		
		public DbStep() {
			super(welcomeStep);
			//TODO localize
            add(new OmLabel("note", "install.wizard.db.step.note", cfg.appName, getString("install.wizard.db.step.instructions.derby")
            		, getString("install.wizard.db.step.instructions.mysql"), getString("install.wizard.db.step.instructions.postgresql")
            		, getString("install.wizard.db.step.instructions.db2"), getString("install.wizard.db.step.instructions.mssql")
            		, getString("install.wizard.db.step.instructions.oracle")).setEscapeModelStrings(false));
            add(form.setOutputMarkupId(true));
            initForm(false, null);
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

		public ParamsStep1() {
			super(dbStep);
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

		public ParamsStep2() {
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
						progressBar.refresh(target);
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
		private static final long serialVersionUID = 1L;
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
		private static final long serialVersionUID = 1L;
		T option;
		IModel<Object> propModel;
		
		WizardDropDown(String id) {
			super(id);
			propModel = InstallWizard.this.model.bind("cfg." + id);
			setModel(new PropertyModel<T>(this, "option"));
		}
		
		@Override
		protected void onDetach() {
			propModel.detach();
			super.onDetach();
		}
	}
	
	private final class TzDropDown extends WizardDropDown<String> {
		private static final long serialVersionUID = 1L;

		public void setOption() {
			String tzId = WebSession.get().getClientTZCode();
			option = AVAILABLE_TIMEZONE_SET.contains(tzId) ? tzId : AVAILABLE_TIMEZONES.get(0);
		}
		
		public TzDropDown(String id) {
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
		private static final long serialVersionUID = 1L;

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
		private static final long serialVersionUID = 1L;
		
		YesNoDropDown(String id) {
			super(id);
			setChoices(yesNoList);
			this.option = SelectOption.NO.key.equals(propModel.getObject()) ?
					SelectOption.NO : SelectOption.YES;
		}
	}
	
	private final class YesNoTextDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = 1L;
		
		YesNoTextDropDown(String id) {
			super(id);
			setChoices(yesNoTextList);
			this.option = SelectOption.NO_TEXT.key.equals(propModel.getObject()) ?
					SelectOption.NO_TEXT : SelectOption.YES_TEXT;
		}
	}
	
	private final class LangDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = 1L;

		public LangDropDown(String id) {
			super(id);
			
			List<SelectOption> list = new ArrayList<SelectOption>();
			
			for (Map.Entry<Long, Locale> me : LabelDao.languages.entrySet()) {
				SelectOption op = new SelectOption(me.getKey().toString(), me.getValue().getDisplayName());
				if (getSession().getLocale().equals(me.getValue())) {
					option = op;
				}
				list.add(op);
				if (option == null && me.getKey().toString().equals(cfg.defaultLangId)) {
					option = op;
				}
			}
			setChoices(list);
		}
	}
}
