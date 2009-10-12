package org.openmeetings.client.beans;

/**
 * @author sebastianwagner
 *
 */
public class ClientFrameBean {

	private Integer mode = null;
	private Integer sequenceNumber = null;
	private Integer lengthSecurityToken = null;
	private Integer xValue = null;
	private Integer yValue = null;
	private Integer width = null;
	private Integer height = null;
	private Integer lengthPayload = null;
	private String publicSID = null;
	private byte[] imageBytes = null;
	
	/**
	 * @param mode
	 * @param sequenceNumber
	 * @param lengthSecurityToken
	 * @param value
	 * @param value2
	 * @param width
	 * @param height
	 * @param lengthPayload
	 * @param publicSID
	 * @param imageBytes
	 *
	 * 12.09.2009 18:02:15
	 * sebastianwagner
	 * 
	 * 
	 */
	public ClientFrameBean(Integer mode, Integer sequenceNumber,
			Integer lengthSecurityToken, Integer value, Integer value2,
			Integer width, Integer height, Integer lengthPayload,
			String publicSID, byte[] imageBytes) {
		super();
		this.mode = mode;
		this.sequenceNumber = sequenceNumber;
		this.lengthSecurityToken = lengthSecurityToken;
		xValue = value;
		yValue = value2;
		this.width = width;
		this.height = height;
		this.lengthPayload = lengthPayload;
		this.publicSID = publicSID;
		this.imageBytes = imageBytes;
	}
	
	/**
	 * 
	 *
	 * 14.09.2009 20:10:44
	 * sebastianwagner
	 * 
	 * 
	 */
	public ClientFrameBean() {
		// TODO Auto-generated constructor stub
	}

	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public Integer getLengthSecurityToken() {
		return lengthSecurityToken;
	}
	public void setLengthSecurityToken(Integer lengthSecurityToken) {
		this.lengthSecurityToken = lengthSecurityToken;
	}
	public Integer getXValue() {
		return xValue;
	}
	public void setXValue(Integer value) {
		xValue = value;
	}
	public Integer getYValue() {
		return yValue;
	}
	public void setYValue(Integer value) {
		yValue = value;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getLengthPayload() {
		return lengthPayload;
	}
	public void setLengthPayload(Integer lengthPayload) {
		this.lengthPayload = lengthPayload;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	public byte[] getImageBytes() {
		return imageBytes;
	}
	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}
	
}
