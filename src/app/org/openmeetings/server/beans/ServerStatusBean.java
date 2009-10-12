package org.openmeetings.server.beans;

/**
 * @author sebastianwagner
 *
 */
public class ServerStatusBean {
	
	private Integer sequenceNumber = null;
	
	public int mode;
	private String publicSID;
	
	private int xValue;
	private int yValue;
	private int width;
	private int height;
	
	private int tileWidth;
	private int tileHeight;
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
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
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
}
