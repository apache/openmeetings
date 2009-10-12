package org.openmeetings.app.conference.videobeans;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.recording.RoomClient;

public class RoomPollAnswers {
	
	RoomClient votedClients;
	Boolean answer;
	Integer pointList;
	Date voteDate;
	/**
	 * @return the answer
	 */
	public Boolean getAnswer() {
		return answer;
	}
	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(Boolean answer) {
		this.answer = answer;
	}
	/**
	 * @return the pointList
	 */
	public Integer getPointList() {
		return pointList;
	}
	/**
	 * @param pointList the pointList to set
	 */
	public void setPointList(Integer pointList) {
		this.pointList = pointList;
	}
	/**
	 * @return the voteDate
	 */
	public Date getVoteDate() {
		return voteDate;
	}
	/**
	 * @param voteDate the voteDate to set
	 */
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}
	/**
	 * @return the votedClients
	 */
	public RoomClient getVotedClients() {
		return votedClients;
	}
	/**
	 * @param votedClients the votedClients to set
	 */
	public void setVotedClients(RoomClient votedClients) {
		this.votedClients = votedClients;
	}

	

}
