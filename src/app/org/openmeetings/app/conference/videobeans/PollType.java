package org.openmeetings.app.conference.videobeans;

public class PollType {
	
	Integer pollTypesId;
	Integer pollTypeLabelid;
	String isNumericAnswer;
	
	
	/**
	 * @return the pollTypesId
	 */
	public Integer getPollTypesId() {
		return pollTypesId;
	}
	/**
	 * @param pollTypesId the pollTypesId to set
	 */
	public void setPollTypesId(Integer pollTypesId) {
		this.pollTypesId = pollTypesId;
	}
	/**
	 * @return the isNumericAnswer
	 */
	public String getIsNumericAnswer() {
		return isNumericAnswer;
	}
	/**
	 * @param isNumericAnswer the isNumericAnswer to set
	 */
	public void setIsNumericAnswer(String isNumericAnswer) {
		this.isNumericAnswer = isNumericAnswer;
	}
	/**
	 * @return the pollTypeLabelid
	 */
	public Integer getPollTypeLabelid() {
		return pollTypeLabelid;
	}
	/**
	 * @param pollTypeLabelid the pollTypeLabelid to set
	 */
	public void setPollTypeLabelid(Integer pollTypeLabelid) {
		this.pollTypeLabelid = pollTypeLabelid;
	}

}
