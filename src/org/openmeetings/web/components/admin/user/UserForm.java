package org.openmeetings.web.components.admin.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.openmeetings.app.persistence.beans.user.Users;

public class UserForm extends Form<Users> {

	public UserForm(String id, Users user) {
		super(id, new CompoundPropertyModel<Users>(user));
		setOutputMarkupId(true);
		
		add(new TextField<String>("login"));
		
        //add(new SimpleFormComponentLabel("login-label", fc));
        
        // add a button that can be used to submit the form via ajax
        add(new AjaxButton("ajax-button", this) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                // repaint the feedback panel so that it is hidden
                // target.add(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
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
