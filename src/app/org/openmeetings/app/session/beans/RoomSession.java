package org.openmeetings.app.session.beans;

public class RoomSession {
	
	private boolean interviewStarted = false;
	private String startedBy; //publicSID of the user starting the Interview
	public boolean isInterviewStarted() {
		return interviewStarted;
	}
	public void setInterviewStarted(boolean interviewStarted) {
		this.interviewStarted = interviewStarted;
	}
	public String getStartedBy() {
		return startedBy;
	}
	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}
	
}
