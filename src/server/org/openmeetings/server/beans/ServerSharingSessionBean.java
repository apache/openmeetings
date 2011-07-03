package org.openmeetings.server.beans;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sebastianwagner
 *
 */
public class ServerSharingSessionBean {
	
	private Long sessionId;
	
	private Date sessionStarted = new Date();
	private Date lastUpdate = new Date();
	
	private String publicSID;
	
	private boolean isSharing;
	private boolean isRecording;
	
	private boolean deleted = false;
	
	private int xValue;
	private int yValue;
	private int width;
	private int height;
	
	private int numberOfImages;
	private boolean sendCompleteFlag;
	
	private int tileWidth;
	private int tileHeight;
	
	private ServerFrameCursorStatus serverFrameCursorStatus;
	
	private List<ServerFrameBean> serverFrameBeans = new LinkedList<ServerFrameBean>();

	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSessionStarted() {
		return sessionStarted;
	}

	public void setSessionStarted(Date sessionStarted) {
		this.sessionStarted = sessionStarted;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	public boolean isSharing() {
		return isSharing;
	}

	public void setSharing(boolean isSharing) {
		this.isSharing = isSharing;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public int getXValue() {
		return xValue;
	}

	public void setXValue(int value) {
		xValue = value;
	}

	public int getYValue() {
		return yValue;
	}

	public void setYValue(int value) {
		yValue = value;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<ServerFrameBean> getServerFrameBeans() {
		return serverFrameBeans;
	}

	public void setServerFrameBeans(List<ServerFrameBean> serverFrameBeans) {
		this.serverFrameBeans = serverFrameBeans;
	}

	public int getNumberOfImages() {
		return numberOfImages;
	}

	public void setNumberOfImages(int numberOfImages) {
		this.numberOfImages = numberOfImages;
	}

	public boolean isSendCompleteFlag() {
		return sendCompleteFlag;
	}

	public void setSendCompleteFlag(boolean sendCompleteFlag) {
		this.sendCompleteFlag = sendCompleteFlag;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public ServerFrameCursorStatus getServerFrameCursorStatus() {
		return serverFrameCursorStatus;
	}
	public void setServerFrameCursorStatus(
			ServerFrameCursorStatus serverFrameCursorStatus) {
		this.serverFrameCursorStatus = serverFrameCursorStatus;
	}
	
}
