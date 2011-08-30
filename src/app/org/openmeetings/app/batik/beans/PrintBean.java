package org.openmeetings.app.batik.beans;

import java.util.List;

public class PrintBean {

	public String hash;
	@SuppressWarnings("rawtypes")
	public List map;
	public int width;
	public int height;

	public PrintBean(String hash, @SuppressWarnings("rawtypes") List map,
			int width, int height) {
		super();
		this.hash = hash;
		this.height = height;
		this.map = map;
		this.width = width;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@SuppressWarnings("rawtypes")
	public List getMap() {
		return map;
	}

	@SuppressWarnings("rawtypes")
	public void setMap(List map) {
		this.map = map;
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

}
