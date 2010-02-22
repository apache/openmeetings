package org.openmeetings.app.session.beans;

import java.util.LinkedList;
import java.util.List;

public class RoomSession {
	
	private boolean interviewStarted = false;
	private String startedBy; //publicSID of the user starting the Interview
	
	private List<InterviewStream> currentInterviewStreams = new LinkedList<InterviewStream>();
	
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
	public List<InterviewStream> getCurrentInterviewStreams() {
		return currentInterviewStreams;
	}
	public void setCurrentInterviewStreams(
			List<InterviewStream> currentInterviewStreams) {
		this.currentInterviewStreams = currentInterviewStreams;
	}
	
}
