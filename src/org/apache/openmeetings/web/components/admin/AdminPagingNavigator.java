package org.apache.openmeetings.web.components.admin;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;

public class AdminPagingNavigator extends AjaxPagingNavigator {

	private static final long serialVersionUID = 1L;
	
	public AdminPagingNavigator(String id, IPageable pageable) {
		super(id, pageable);
	}

}
