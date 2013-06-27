package org.apache.openmeetings.web.admin;

import java.util.List;

import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public abstract class AdminCommonUserForm<T> extends AdminBaseForm<T> {

	private static final long serialVersionUID = -827309847606809118L;

	@SuppressWarnings("unchecked")
	public AdminCommonUserForm(String id, IModel<?> object) {
		super(id, (IModel<T>) object);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void updateView(AjaxRequestTarget target);

	public abstract void submitView(AjaxRequestTarget target, List<User> usersToAdd);

}
