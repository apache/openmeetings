package org.openmeetings.app.conference.whiteboard;

public class BrowserStatus {
	
	private boolean browserInited = false;
	private String currentURL = "";
	
	public boolean isBrowserInited() {
		return browserInited;
	}
	public void setBrowserInited(boolean browserInited) {
		this.browserInited = browserInited;
	}
	public String getCurrentURL() {
		return currentURL;
	}
	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
	}

}
