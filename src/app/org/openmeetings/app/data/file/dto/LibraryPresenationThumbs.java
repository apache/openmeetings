package org.openmeetings.app.data.file.dto;

public class LibraryPresenationThumbs {

	private String name = "";
	private LibraryPresentationThumb[] thumbs = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LibraryPresentationThumb[] getThumbs() {
		return thumbs;
	}
	public void setThumbs(LibraryPresentationThumb[] thumbs) {
		this.thumbs = thumbs;
	}
	
}
