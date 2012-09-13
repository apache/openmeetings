package org.apache.openmeetings.web.components;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation("USER")
public abstract class UserPanel extends BasePanel {
	private static final long serialVersionUID = 8607846885689465360L;

	public UserPanel(String id) {
		super(id);
	}
}
