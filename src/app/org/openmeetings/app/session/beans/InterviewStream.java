package org.openmeetings.app.session.beans;

import java.util.Date;

public class InterviewStream {
	
	public String streamName;
	public String userName;
	public String publicSID;
	public Date startDate;
	
	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
