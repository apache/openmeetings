package org.openmeetings.app.conference.whiteboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WhiteBoardObject {
	
	private Date created = new Date();
	private Integer zoom = 100;
	private Boolean fullFit = true;
	private HashMap<String,List> objList = new HashMap<String,List>();
	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Integer getZoom() {
		return zoom;
	}
	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}
	public Boolean getFullFit() {
		return fullFit;
	}
	public void setFullFit(Boolean fullFit) {
		this.fullFit = fullFit;
	}
	public HashMap<String, List> getObjList() {
		return objList;
	}
	public void setObjList(HashMap<String, List> objList) {
		this.objList = objList;
	}

}
