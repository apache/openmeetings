package org.openmeetings.app.data.flvrecord.listener.async;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.stream.IStreamPacket;

public class CachedEvent implements IStreamPacket {

	private byte dataType;
	private int timestamp; //this is the timeStamp, showing the time elapsed since the microphone was turned on
	private IoBuffer data;
	private Date currentTime; //this is the actually current timeStamp when the packet with audio data did enter the server

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setData(IoBuffer data) {
		this.data = data;
	}

	public byte getDataType() {
		return dataType;
	}

	public int getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	public IoBuffer getData() {
		// TODO Auto-generated method stub
		return data;
	}

}
