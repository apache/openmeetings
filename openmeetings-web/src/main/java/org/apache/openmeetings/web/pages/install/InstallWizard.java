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

import static org.apache.openmeetings.core.converter.BaseConverter.EXEC_EXT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
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
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.core.converter.DocumentConverter;
import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ErrorMessagePanel;
import org.apache.openmeetings.web.common.OmLabel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import com.googlecode.wicket.jquery.ui.widget.tooltip.TooltipBehavior;
import com.googlecode.wicket.jquery.ui.widget.wizard.AbstractWizard;
import com.googlecode.wicket.kendo.ui.form.button.IndicatingAjaxButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class InstallWizard extends AbstractWizard<InstallationConfig> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InstallWizard.class, getWebAppRootKey());
	private final IDynamicWizardStep welcomeStep;
	private final IDynamicWizardStep dbStep;
	private final ParamsStep1 paramsStep1;
	private final IDynamicWizardStep paramsStep2;
	private final IDynamicWizardStep paramsStep3;
	private final IDynamicWizardStep paramsStep4;
	private final InstallStep installStep;
	private Throwable th = null;
	private DbType initDbType = null;
	private DbType dbType = null;

	//onInit, applyState
	public InstallWizard(String id, String title) {
		super(id, title, new CompoundPropertyModel<>(new InstallationConfig()), true);
		setTitle(Model.of(getModelObject().getAppName()));
		welcomeStep = new WelcomeStep();
		dbStep = new DbStep();
		paramsStep1 = new ParamsStep1();
		paramsStep2 = new ParamsStep2();
		paramsStep3 = new ParamsStep3();
		paramsStep4 = new ParamsStep4();
		installStep = new InstallStep();

		DynamicWizardModel wmodel = new DynamicWizardModel(welcomeStep);
		wmodel.setCancelVisible(false);
		wmodel.setLastVisible(true);
		init(wmodel);
	}

	public void initTzDropDown() {
		paramsStep1.tzDropDown.setOption();
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("classes", "{'ui-dialog-titlebar': 'ui-corner-all no-close'}");
		behavior.setOption("resizable", false);
	}

	@Override
	protected WebMarkupContainer newFeedbackPanel(String id) {
		KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
		feedback.setEscapeModelStrings(false);
		return feedback;
	}

	@Override
	public int getWidth() {
		return 1000;
	}

	@Override
	protected boolean closeOnFinish() {
		return false;
	}

	private abstract class BaseStep extends DynamicWizardStep {
		private static final long serialVersionUID = 1L;

		public BaseStep(IDynamicWizardStep prev) {
			super(prev);
			setSummaryModel(Model.of(""));
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			InstallWizard.this.setTitle(Model.of(getModelObject().getAppName() + " - " + getString("install.wizard.install.header")));
		}
	}

	private final class WelcomeStep extends BaseStep {
		private static final long serialVersionUID = 1L;

		public WelcomeStep() {
			super(null);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(new Label("step", getString("install.wizard.welcome.panel")).setEscapeModelStrings(false));
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			return dbStep;
		}
	}

	private final class DbStep extends BaseStep {
		private static final long serialVersionUID = 1L;
		private final RequiredTextField<String> host = new RequiredTextField<>("host", Model.of(""));
		private final RequiredTextField<Integer> port = new RequiredTextField<>("port", Model.of(0));
		private final RequiredTextField<String> dbname = new RequiredTextField<>("dbname", Model.of(""));
		private final RequiredTextField<String> user = new RequiredTextField<>("login");
		private final TextField<String> pass = new TextField<>("password");
		private final Form<ConnectionProperties> form = new Form<ConnectionProperties>("form", new CompoundPropertyModel<>(getProps(null))) {
			private static final long serialVersionUID = 1L;
			private final DropDownChoice<DbType> db = new DropDownChoice<>("dbType", Arrays.asList(DbType.values()), new ChoiceRenderer<DbType>() {
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

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(db.add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(getFeedbackPanel());
						initForm(true, target);
					}
				}));
				add(host, port, dbname, user, pass);
				add(new IndicatingAjaxButton("check") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						target.add(getFeedbackPanel());
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(getFeedbackPanel());
					}
				});
			}

			private String getDbName(ConnectionProperties props) {
				return getString("install.wizard.db.step.instructions." + props.getDbType().name());
			}

			@Override
			protected void onValidateModelObjects() {
				ConnectionProperties props = getModelObject();
				try {
					Class.forName(props.getDriver());
				} catch (Exception e) {
					form.error(new StringResourceModel("install.wizard.db.step.nodriver", InstallWizard.this).setParameters(getDbName(props)).getObject());
					return;
				}
				boolean valid = true;
				ConnectionPropertiesPatcher.updateUrl(props, host.getModelObject(), "" + port.getModelObject(), dbname.getModelObject());
				DriverManager.setLoginTimeout(3);
				try (Connection conn = DriverManager.getConnection(props.getURL(), props.getLogin(), props.getPassword())) {
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
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						valid &= ps.execute();
					}
					if (!valid) {
						form.error(getString("install.wizard.db.step.notvalid") + "<br/>" + getDbName(props));
					}
				} catch (Exception e) {
					form.error(e.getMessage() + "<br/>" + getDbName(props));
					log.error("error while testing DB", e);
					valid = false;
				}
				if (valid) {
					form.success(getString("install.wizard.db.step.valid"));
				}
			}

			@Override
			protected void onSubmit() {
				try {
					ConnectionPropertiesPatcher.patch(getModelObject());
					XmlWebApplicationContext ctx = (XmlWebApplicationContext)getWebApplicationContext(Application.get().getServletContext());
					if (ctx == null) {
						form.error(new StringResourceModel("install.wizard.db.step.error.patch", InstallWizard.this).setParameters("Web context is NULL").getObject());
						log.error("Web context is NULL");
						return;
					}
					LocalEntityManagerFactoryBean emb = ctx.getBeanFactory().getBean(LocalEntityManagerFactoryBean.class);
					emb.afterPropertiesSet();
					dbType = getModelObject().getDbType();
				} catch (Exception e) {
					form.error(new StringResourceModel("install.wizard.db.step.error.patch", InstallWizard.this).setParameters(e.getMessage()).getObject());
					log.error("error while patching", e);
				}
			}
		};

		public DbStep() {
			super(welcomeStep);
			add(form.setOutputMarkupId(true));
			initDbType = form.getModelObject().getDbType();
			initForm(false, null);
		}

		private ConnectionProperties getProps(DbType type) {
			ConnectionProperties props = new ConnectionProperties();
			try {
				File conf = OmFileHelper.getPersistence(type);
				if (!conf.exists() && type == null) {
					return props; // initial select
				}
				props = ConnectionPropertiesPatcher.getConnectionProperties(conf);
				if (DbType.derby != props.getDbType()) {
					// resetting default login/password
					props.setLogin(null);
					props.setPassword(null);
				}
			} catch (Exception e) {
				form.warn(getString("install.wizard.db.step.errorprops"));
			}
			return props;
		}

		private void initForm(boolean getProps, AjaxRequestTarget target) {
			ConnectionProperties props = getProps ? getProps(form.getModelObject().getDbType()) : form.getModelObject();
			form.setModelObject(props);
			host.setVisible(props.getDbType() != DbType.derby);
			port.setVisible(props.getDbType() != DbType.derby);
			user.setVisible(props.getDbType() != DbType.derby);
			pass.setVisible(props.getDbType() != DbType.derby);
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

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(new OmLabel("note", "install.wizard.db.step.note", getModelObject().getAppName(), getString("install.wizard.db.step.instructions.derby")
					, getString("install.wizard.db.step.instructions.mysql"), getString("install.wizard.db.step.instructions.postgresql")
					, getString("install.wizard.db.step.instructions.db2"), getString("install.wizard.db.step.instructions.mssql")
					, getString("install.wizard.db.step.instructions.oracle")).setEscapeModelStrings(false));
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			return paramsStep1;
		}
	}

	private final class ParamsStep1 extends BaseStep {
		private static final long serialVersionUID = 1L;
		private final TzDropDown tzDropDown;

		public ParamsStep1() {
			super(dbStep);
			add(tzDropDown = new TzDropDown("timeZone"));
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(new RequiredTextField<String>("username").setLabel(new ResourceModel("install.wizard.params.step1.username")).add(minimumLength(USER_LOGIN_MINIMUM_LENGTH)));
			add(new PasswordTextField("password")
					.setResetPassword(false).setLabel(new ResourceModel("install.wizard.params.step1.password"))
					.add(new StrongPasswordValidator(new User())));
			add(new RequiredTextField<String>("email").setLabel(new ResourceModel("install.wizard.params.step1.email")).add(RfcCompliantEmailAddressValidator.getInstance()));
			add(new RequiredTextField<String>("group").setLabel(new ResourceModel("install.wizard.params.step1.group")));
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public boolean isLastAvailable() {
			return true;
		}

		@Override
		public IDynamicWizardStep next() {
			return paramsStep2;
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
			add(new CheckBox("allowFrontendRegister"));
			add(new CheckBox("sendEmailAtRegister"));
			add(new CheckBox("sendEmailWithVerficationCode"));
			add(new CheckBox("createDefaultObjects"));
			add(new TextField<String>("mailReferer"));
			add(new TextField<String>("smtpServer"));
			add(new TextField<Integer>("smtpPort").setRequired(true));
			add(new TextField<String>("mailAuthName"));
			add(new PasswordTextField("mailAuthPass").setResetPassword(false).setRequired(false));
			add(new CheckBox("mailUseTls"));
			add(new CheckBox("replyToOrganizer"));
			add(new LangDropDown("defaultLangId"));
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			return paramsStep3;
		}

		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}
	}

	private final class ParamsStep3 extends BaseStep {
		private static final long serialVersionUID = 1L;
		private static final String REGEX = "\\r\\n|\\r|\\n";
		private static final String OPT_VERSION = "-version";
		private final TextField<String> ffmpegPath;
		private final TextField<String> imageMagicPath;
		private final TextField<String> soxPath;
		private final TextField<String> officePath;
		private boolean isAllChecked = false;

		public ParamsStep3() {
			super(paramsStep2);
			add(imageMagicPath = new TextField<>("imageMagicPath"));
			add(ffmpegPath = new TextField<>("ffmpegPath"));
			add(soxPath = new TextField<>("soxPath"));
			add(officePath = new TextField<>("officePath"));
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(new TextField<Integer>("docDpi").setRequired(true).add(range(50, 600)));
			add(new TextField<Integer>("docQuality").setRequired(true).add(range(1, 100)));
			add(new AjaxButton("validateImageMagic") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkMagicPath();
					target.add(getFeedbackPanel());
				}
			});
			add(new AjaxButton("validateFfmpeg") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkFfmpegPath();
					target.add(getFeedbackPanel());
				}
			});
			add(new AjaxButton("validateSox") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkSoxPath();
					target.add(getFeedbackPanel());
				}
			});
			add(new AjaxButton("validateOffice") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkOfficePath();
					target.add(getFeedbackPanel());
				}
			});
			add(new TooltipBehavior(".info-title"));
		}

		private boolean checkToolPath(TextField<String> path, String[] args) {
			ProcessResult result = ProcessHelper.executeScript(path.getInputName() + " path:: '" + path.getValue() + "'", args);
			if (!result.isOk()) {
				path.error(result.getError().replaceAll(REGEX, ""));
			}
			return result.isOk();
		}

		private boolean checkMagicPath() {
			return checkToolPath(imageMagicPath, new String[] {getToolPath(imageMagicPath.getValue(), "convert" + EXEC_EXT), OPT_VERSION});
		}

		private boolean checkFfmpegPath() {
			return checkToolPath(ffmpegPath, new String[] {getToolPath(ffmpegPath.getValue(), "ffmpeg" + EXEC_EXT), OPT_VERSION});
		}

		private boolean checkSoxPath() {
			return checkToolPath(soxPath, new String[] {getToolPath(soxPath.getValue(), "sox" + EXEC_EXT), "--version"});
		}

		private boolean checkOfficePath() {
			String err  = "";
			try {
				DocumentConverter.createOfficeManager(officePath.getValue(), null);
			} catch (Exception ex) {
				officePath.error(err = ex.getMessage().replaceAll(REGEX, ""));
			}
			return err.isEmpty();
		}

		private boolean checkAllPath() {
			boolean result = checkMagicPath();
			result = checkFfmpegPath() && result;
			result = checkSoxPath() && result;
			result = checkOfficePath() && result;
			isAllChecked = true;
			return result;
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			if (!isAllChecked && !checkAllPath()) {
				return this;
			}
			return paramsStep4;
		}

		@Override
		public boolean isLastAvailable() {
			return isAllChecked;
		}

		@Override
		public IDynamicWizardStep last() {
			return installStep;
		}

		private String getToolPath(String _path, String app) {
			StringBuilder path = new StringBuilder();
			if (!Strings.isEmpty(_path)) {
				path.append(_path);
				if (!_path.endsWith(File.separator)) {
					path.append(File.separator);
				}
			}
			path.append(app);
			return path.toString();
		}
	}

	private final class ParamsStep4 extends BaseStep {
		private static final long serialVersionUID = 1L;

		public ParamsStep4() {
			super(paramsStep3);
			add(new RequiredTextField<String>("cryptClassName")); //Validate class

			add(new CheckBox("sipEnable"));
			add(new TextField<String>("sipRoomPrefix"));
			add(new TextField<String>("sipExtenContext"));
			Options options = new Options();
			options.set("content", "function () { return $(this).prop('title'); }");
			add(new TooltipBehavior(".info-title", options));
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
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
		private final WebMarkupContainer container = new WebMarkupContainer("container");
		private final AbstractAjaxTimerBehavior timer;
		private final ProgressBar progressBar;
		private final Label desc = new Label("desc", "");
		private boolean started = false;

		public InstallStep() {
			super(paramsStep4);

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
						progressBar.setVisible(false);
						target.add(container.replace(new ErrorMessagePanel("status", getString("install.wizard.install.failed"), th))
							, desc.setVisible(false)
							);
					} else {
						progressBar.setModelObject(Application.get()._getBean(ImportInitvalues.class).getProgress());
						progressBar.refresh(target);
					}
				}
			});
			container.add(progressBar = new ProgressBar("progress", new Model<>(0)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComplete(AjaxRequestTarget target) {
					timer.stop(target);
					progressBar.setVisible(false);
					congrat.show(initDbType != dbType);
					target.add(container, desc.setVisible(false));
				}
			});

			container.add(congrat = new CongratulationsPanel("status"));
			congrat.setVisible(false);

			add(container.setOutputMarkupId(true));
		}

		public void startInstallation(AjaxRequestTarget target) {
			started = true;
			timer.restart(target);
			new Thread(new InstallProcess(Application.get()._getBean(ImportInitvalues.class))
				, "Openmeetings - Installation").start();
			desc.setDefaultModelObject(getString("install.wizard.install.started"));
			target.add(desc, container);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			desc.setDefaultModelObject(getString("install.wizard.install.desc"));
			add(desc.setOutputMarkupId(true));
		}

		@Override
		public boolean isLastStep() {
			return true;
		}

		@Override
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

		@Override
		public void run() {
			try {
				installer.loadAll(getModelObject(), true);
			} catch (Exception e) {
				th = e;
			}
		}
	}

	private static class SelectOption implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String key;
		@SuppressWarnings("unused")
		private final String value;

		SelectOption(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	private abstract class WizardDropDown<T extends Serializable>  extends DropDownChoice<T> {
		private static final long serialVersionUID = 1L;
		T option;
		IModel<Object> propModel;

		WizardDropDown(String id) {
			super(id);
			propModel = ((CompoundPropertyModel<InstallationConfig>)InstallWizard.this.getModel()).bind(id);
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

		public TzDropDown(String id) {
			super(id);
			setChoices(AVAILABLE_TIMEZONES);
			setChoiceRenderer(new ChoiceRenderer<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getDisplayValue(String object) {
					return object;
				}

				@Override
				public String getIdValue(String object, int index) {
					return object;
				}
			});
		}

		public void setOption() {
			String tzId = WebSession.get().getClientTZCode();
			option = AVAILABLE_TIMEZONE_SET.contains(tzId) ? tzId : AVAILABLE_TIMEZONES.get(0);
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

	private final class LangDropDown extends SelectOptionDropDown {
		private static final long serialVersionUID = 1L;

		public LangDropDown(String id) {
			super(id);

			List<SelectOption> list = new ArrayList<>();
			for (Map.Entry<Long, Locale> me : LabelDao.getLanguages()) {
				SelectOption op = new SelectOption(me.getKey().toString(), me.getValue().getDisplayName());
				if (getSession().getLocale().equals(me.getValue())) {
					option = op;
				}
				if (option == null && getSession().getLocale().getLanguage().equals(me.getValue().getLanguage())) {
					option = op;
				}
				list.add(op);
				if (option == null && me.getKey().intValue() == InstallWizard.this.getModelObject().getDefaultLangId()) {
					option = op;
				}
			}
			setChoices(list);
		}
	}

	@Override
	protected void onFinish(AjaxRequestTarget target) {
		for (DialogButton b : getButtons()) {
			b.setEnabled(false, target);
		}
		installStep.startInstallation(target);
	}
}
