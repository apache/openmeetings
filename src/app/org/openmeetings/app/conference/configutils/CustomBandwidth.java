package org.openmeetings.app.conference.configutils;

//import org.red5.server.api.stream.support.SimpleBandwidthConfigure;

/**
 * 
 * @author swagner
 *
 */
//extends SimpleBandwidthConfigure
public class CustomBandwidth {
 
	private String groupName = "";
	private String description = "";
	private int minpingTime = 0;
	private int maxpingTime = 0;
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the maxpingTime
	 */
	public int getMaxpingTime() {
		return maxpingTime;
	}
	/**
	 * @param maxpingTime the maxpingTime to set
	 */
	public void setMaxpingTime(int maxpingTime) {
		this.maxpingTime = maxpingTime;
	}
	/**
	 * @return the minpingTime
	 */
	public int getMinpingTime() {
		return minpingTime;
	}
	/**
	 * @param minpingTime the minpingTime to set
	 */
	public void setMinpingTime(int minpingTime) {
		this.minpingTime = minpingTime;
	}

	
	
	
}
