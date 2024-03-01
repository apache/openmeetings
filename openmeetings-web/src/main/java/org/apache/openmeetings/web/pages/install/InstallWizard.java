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
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONE_SET;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.wicket.validation.validator.RangeValidator.range;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.ArrayList;
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
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ErrorMessagePanel;
import org.apache.openmeetings.web.common.OmLabel;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.util.OmTooltipBehavior;
import org.apache.openmeetings.web.util.ThreadHelper;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.WizardButton;
import org.apache.wicket.extensions.wizard.WizardButtonBar;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UpdatableProgressBar;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import de.agilecoders.wicket.extensions.wizard.BootstrapWizard;
import jakarta.inject.Inject;

public class InstallWizard extends BootstrapWizard {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(InstallWizard.class);
	private static final String LBL_CHECK = "install.wizard.db.step.check";
	private IDynamicWizardStep welcomeStep;
	private IDynamicWizardStep dbStep;
	private ParamsStep1 paramsStep1;
	private IDynamicWizardStep paramsStep2;
	private IDynamicWizardStep paramsStep3;
	private IDynamicWizardStep paramsStep4;
	private InstallStep installStep;
	private Throwable th = null;
	private DbType initDbType = null;
	private DbType dbType = null;
	private NotificationPanel feedback;
	private final CompoundPropertyModel<InstallationConfig> model;
	private final List<Button> buttons = new ArrayList<>(4);
	private WizardButtonBar btnBar;

	@Inject
	private ImportInitvalues initvalues;

	//onInit, applyState
	public InstallWizard(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setOutputMarkupId(true);
		model = new CompoundPropertyModel<>(new InstallationConfig());
		setDefaultModel(model);
	}

	@Override
	protected void onInitialize() {
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

		super.onInitialize();
	}

	public void initTzDropDown() {
		paramsStep1.tzDropDown.setOption();
	}

	@Override
	protected WebMarkupContainer newFeedbackPanel(String id) {
		feedback = (NotificationPanel)super.newFeedbackPanel(id);
		feedback.setEscapeModelStrings(false).setOutputMarkupId(true);
		return feedback;
	}

	@Override
	protected Component newButtonBar(String id) {
		btnBar = new WizardButtonBar(id, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected WizardButton newCancelButton(String id, IWizard wizard) {
				WizardButton button = super.newCancelButton(id, wizard);
				button.add(new ButtonBehavior(Type.Outline_Warning, Buttons.Size.Medium));
				return button;
			}

			@Override
			protected WizardButton newFinishButton(String id, IWizard wizard) {
				WizardButton button = super.newFinishButton(id, wizard);
				button.add(new ButtonBehavior(Type.Outline_Success, Buttons.Size.Medium));
				button.add(new AjaxFormSubmitBehavior("click") {
					private static final long serialVersionUID = 1L;


					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);

						// do not allow normal form submit to happen
						attributes.setPreventDefault(true);
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						button.onSubmit();
					}
				});
				buttons.add(button);
				return button;
			}

			@Override
			protected WizardButton newLastButton(String id, IWizard wizard) {
				WizardButton button = super.newLastButton(id, wizard);
				button.add(new ButtonBehavior(Type.Outline_Secondary, Buttons.Size.Medium));
				buttons.add(button);
				return button;
			}

			@Override
			protected WizardButton newNextButton(String id, IWizard wizard) {
				WizardButton button = super.newNextButton(id, wizard);
				button.add(new ButtonBehavior(Type.Outline_Secondary, Buttons.Size.Medium));
				buttons.add(button);
				return button;
			}

			@Override
			protected WizardButton newPreviousButton(String id, IWizard wizard) {
				WizardButton button = super.newPreviousButton(id, wizard);
				button.add(new ButtonBehavior(Type.Outline_Secondary, Buttons.Size.Medium));
				buttons.add(button);
				return button;
			}
		};
		btnBar.setOutputMarkupId(true);
		return btnBar;
	}

	private abstract class BaseStep extends DynamicWizardStep {
		private static final long serialVersionUID = 1L;

		public BaseStep(IDynamicWizardStep prev) {
			super(prev);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			setSummaryModel(Model.of(""));
			setTitleModel(Model.of(model.getObject().getAppName() + " - " + getString("install.wizard.installation")));
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
		private final Form<ConnectionProperties> form = new Form<>("form", new CompoundPropertyModel<>(getProps(null))) {
			private static final long serialVersionUID = 1L;
			private final DropDownChoice<DbType> db = new DropDownChoice<>("dbType", List.of(DbType.values())
					, new LambdaChoiceRenderer<>(dtb -> getString("install.wizard.db.step." + dtb.dbName() + ".name"), DbType::name));

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(db.add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(feedback);
						initForm(true, target);
					}
				}));
				add(host, port, dbname, user, pass);
				add(new SpinnerAjaxButton("check", new ResourceModel(LBL_CHECK), Buttons.Type.Outline_Primary) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						target.add(feedback);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				});
			}

			private String getDbName(ConnectionProperties props) {
				return getString("install.wizard.db.step.instructions." + props.getDbType().dbName());
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
						case DB2:
							sql = "select count(*) from systables";
							break;
						case ORACLE:
							sql = "SELECT 1 FROM DUAL";
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
		}

		private ConnectionProperties getProps(DbType type) {
			ConnectionProperties props = new ConnectionProperties();
			try {
				File conf = OmFileHelper.getPersistence(type);
				if (!conf.exists() && type == null) {
					return props; // initial select
				}
				props = ConnectionPropertiesPatcher.getConnectionProperties(conf);
				if (DbType.H2 != props.getDbType()) {
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
			host.setVisible(props.getDbType() != DbType.H2);
			port.setVisible(props.getDbType() != DbType.H2);
			user.setVisible(props.getDbType() != DbType.H2);
			pass.setVisible(props.getDbType() != DbType.H2);
			try {
				switch (props.getDbType()) {
					case MSSQL:
						dbMssql(props);
						break;
					case ORACLE:
						dbOracle(props);
						break;
					case H2:
						dbH2(props);
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

		private void dbMssql(ConnectionProperties props) {
			String url = props.getURL().substring("jdbc:sqlserver://".length());
			String[] parts = url.split(";");
			String[] hp = parts[0].split(":");
			host.setModelObject(hp[0]);
			port.setModelObject(Integer.parseInt(hp[1]));
			dbname.setModelObject(parts[1].substring(parts[1].indexOf('=') + 1));
		}

		private void dbOracle(ConnectionProperties props) {
			String[] parts = props.getURL().split(":");
			host.setModelObject(parts[3].substring(1));
			port.setModelObject(Integer.parseInt(parts[4]));
			dbname.setModelObject(parts[5]);
		}

		private void dbH2(ConnectionProperties props) {
			host.setModelObject("");
			port.setModelObject(0);
			String[] parts = props.getURL().split(";");
			dbname.setModelObject(parts[0].substring("jdbc:h2:".length()));
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(form.setOutputMarkupId(true));
			initDbType = form.getModelObject().getDbType();
			initForm(false, null);
			add(new OmLabel("note", "install.wizard.db.step.note", model.getObject().getAppName(), getString("install.wizard.db.step.instructions.h2")
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
		private final TzDropDown tzDropDown = new TzDropDown("timeZone");

		public ParamsStep1() {
			super(dbStep);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(tzDropDown);
			add(new RequiredTextField<String>("username").setLabel(new ResourceModel("install.wizard.params.step1.username")).add(minimumLength(USER_LOGIN_MINIMUM_LENGTH)));
			add(new PasswordTextField("password")
					.setResetPassword(false).setLabel(new ResourceModel("install.wizard.params.step1.password"))
					.add(new StrongPasswordValidator(new User())));
			add(new RequiredTextField<String>("email").setLabel(new ResourceModel("lbl.email")).add(RfcCompliantEmailAddressValidator.getInstance()));
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
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
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
		private final TextField<String> ffmpegPath = new TextField<>("ffmpegPath");
		private final TextField<String> imageMagicPath = new TextField<>("imageMagicPath");
		private final TextField<String> soxPath = new TextField<>("soxPath");
		private final TextField<String> officePath = new TextField<>("officePath");
		private boolean isAllChecked = false;

		public ParamsStep3() {
			super(paramsStep2);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(imageMagicPath.setLabel(new ResourceModel("install.wizard.params.step3.imageMagicPath")));
			add(ffmpegPath.setLabel(new ResourceModel("install.wizard.params.step3.ffmpegPath")));
			add(soxPath.setLabel(new ResourceModel("install.wizard.params.step3.soxPath")));
			add(officePath.setLabel(new ResourceModel("install.wizard.params.step3.officePath")));
			add(new TextField<Integer>("docDpi").setRequired(true).add(range(50, 600)));
			add(new TextField<Integer>("docQuality").setRequired(true).add(range(1, 100)));
			add(new BootstrapAjaxButton("validateImageMagic", new ResourceModel(LBL_CHECK), Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkMagicPath();
					target.add(feedback);
				}
			});
			add(new BootstrapAjaxButton("validateFfmpeg", new ResourceModel(LBL_CHECK), Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkFfmpegPath();
					target.add(feedback);
				}
			});
			add(new BootstrapAjaxButton("validateSox", new ResourceModel(LBL_CHECK), Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkSoxPath();
					target.add(feedback);
				}
			});
			add(new BootstrapAjaxButton("validateOffice", new ResourceModel(LBL_CHECK), Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					checkOfficePath();
					target.add(feedback);
				}
			});
			add(new OmTooltipBehavior());
		}

		private void reportSuccess(TextField<String> path) {
			path.success(path.getLabel().getObject() + " - " + getString("54"));
		}

		private boolean checkToolPath(TextField<String> path, List<String> args) {
			ProcessResult result = ProcessHelper.exec(path.getInputName() + " path:: '" + path.getValue() + "'", args);
			if (!result.isOk()) {
				path.error(result.getError().replaceAll(REGEX, ""));
			} else {
				reportSuccess(path);
			}
			return result.isOk();
		}

		private boolean checkMagicPath() {
			return checkToolPath(imageMagicPath, List.of(getToolPath(imageMagicPath.getValue(), "convert" + EXEC_EXT), OPT_VERSION));
		}

		private boolean checkFfmpegPath() {
			return checkToolPath(ffmpegPath, List.of(getToolPath(ffmpegPath.getValue(), "ffmpeg" + EXEC_EXT), OPT_VERSION));
		}

		private boolean checkSoxPath() {
			return checkToolPath(soxPath, List.of(getToolPath(soxPath.getValue(), "sox" + EXEC_EXT), "--version"));
		}

		private boolean checkOfficePath() {
			String err  = "";
			try {
				DocumentConverter.createOfficeManager(officePath.getValue(), null);
				reportSuccess(officePath);
			} catch (Exception ex) {
				String msg = ex.getMessage();
				officePath.error(err = (msg == null ? "" : msg.replaceAll(REGEX, "")));
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

		private String getToolPath(String inPath, String app) {
			StringBuilder path = new StringBuilder();
			if (!Strings.isEmpty(inPath)) {
				path.append(inPath);
				if (!inPath.endsWith(File.separator)) {
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
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(new RequiredTextField<String>("cryptClassName")); //Validate class

			add(new CheckBox("sipEnable"));
			add(new TextField<String>("sipRoomPrefix"));
			add(new TextField<String>("sipExtenContext"));
			add(new OmTooltipBehavior());
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
		private final CongratulationsPanel congrat = new CongratulationsPanel("status");
		private final WebMarkupContainer container = new WebMarkupContainer("container");
		private final UpdatableProgressBar progressBar = new UpdatableProgressBar("progress", new Model<>(0), BackgroundColorBehavior.Color.Info, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected IModel<Integer> newValue() {
				return Model.of(initvalues.getProgress());
			}

			@Override
			protected void onPostProcessTarget(IPartialPageRequestHandler target) {
				if (!started) {
					stop(target);
					return;
				}
				if (th != null) {
					stop(target);
					progressBar.setVisible(false);
					target.add(container.replace(new ErrorMessagePanel("status", getString("install.wizard.install.failed"), th))
						, desc.setVisible(false)
						);
				}
				super.onPostProcessTarget(target);
			}

			@Override
			protected void onComplete(IPartialPageRequestHandler target) {
				stop(target);
				progressBar.setVisible(false);
				congrat.show(initDbType != dbType);
				target.add(container, desc.setVisible(false), btnBar.setVisible(false));
			}
		};
		private final Label desc = new Label("desc", "");
		private boolean started = false;

		public InstallStep() {
			super(paramsStep4);
		}

		@Override
		public void applyState() {
			started = true;
			ThreadHelper.startRunnable(new InstallProcess(initvalues)
				, "Openmeetings - Installation");
			desc.setDefaultModelObject(getString("install.wizard.install.started"));
			RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> {
				progressBar.restart(target).setModelObject(0);
				buttons.forEach(b -> target.add(b.setEnabled(false)));
				target.add(desc, container);
			});
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			progressBar.updateInterval(Duration.ofSeconds(1)).stop(null).striped(false).setOutputMarkupId(true);
			container.add(progressBar, congrat);
			congrat.setVisible(false);

			add(container.setOutputMarkupId(true));
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
				installer.loadAll(model.getObject(), true);
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
			propModel = model.bind(id);
			setModel(new PropertyModel<>(this, "option"));
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
			setChoiceRenderer(new LambdaChoiceRenderer<>(str -> str, str -> str));
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
			setChoiceRenderer(new ChoiceRenderer<>("value", "key"));
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
				if (option == null && me.getKey().intValue() == model.getObject().getDefaultLangId()) {
					option = op;
				}
			}
			setChoices(list);
		}
	}

	@Override
	protected void onDetach() {
		model.detach();
		super.onDetach();
	}
}
