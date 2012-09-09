package org.openmeetings.web.components.admin.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.user.Salutations;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.web.app.Application;
import org.openmeetings.web.app.WebSession;

public class UserForm extends Form<Users> {

	public UserForm(String id, Users user) {
		super(id, new CompoundPropertyModel<Users>(user));
		setOutputMarkupId(true);

		add(new TextField<String>("login"));

		add(new DropDownChoice<Salutations>("salutations", Application.getBean(
				Salutationmanagement.class).getUserSalutations(
				WebSession.getLanguage()), new ChoiceRenderer<Salutations>(
				"label.value", "salutations_id")));

		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));

		add(new DropDownChoice<OmTimeZone>("omTimeZone", Application.getBean(
				OmTimeZoneDaoImpl.class).getOmTimeZones(),
				new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname")));

		add(new DropDownChoice<FieldLanguage>("language", Application.getBean(
				FieldLanguageDaoImpl.class).getLanguages(),
				new ChoiceRenderer<FieldLanguage>("name", "language_id")));

		add(new CheckBox("forceTimeZoneCheck"));
		add(new TextField<String>("adresses.email"));
		add(new TextField<String>("adresses.phone"));
		add(new CheckBox("sendSMS"));
		DateTextField age = new DateTextField("age");
		DatePicker datePicker = new DatePicker() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getAdditionalJavaScript() {
				return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
			}
		};
		datePicker.setShowOnFieldClick(true);
		datePicker.setAutoHide(true);
		age.add(datePicker);
		add(age);

		// add a button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-button", this) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				// target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				// target.add(feedback);
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
