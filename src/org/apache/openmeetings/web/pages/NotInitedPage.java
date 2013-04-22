package org.apache.openmeetings.web.pages;

import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;

public class NotInitedPage extends WebPage {
	private static final long serialVersionUID = 5002281516467955607L;
	
	public NotInitedPage() {
		if (ScopeApplicationAdapter.initComplete) {
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}
}
