package org.openmeetings.app.conference.whiteboard;

import java.util.List;

public class Cliparts {

	private String folderName;
	private String[] generalList;
	private List<Cliparts> subCategories;

	public String[] getGeneralList() {
		return generalList;
	}

	public void setGeneralList(String[] generalList) {
		this.generalList = generalList;
	}

	public List<Cliparts> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Cliparts> subCategories) {
		this.subCategories = subCategories;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}
