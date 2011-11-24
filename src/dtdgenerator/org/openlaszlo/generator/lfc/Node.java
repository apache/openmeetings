package org.openlaszlo.generator.lfc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Node extends Eventable {
	
	@XmlAttribute
	public String classroot;
	@XmlAttribute
	public String cloneManager;
	@XmlAttribute
	public String data;
	@XmlAttribute
	public String datapath;
	@XmlAttribute
	public String defaultplacement;
	@XmlAttribute
	public String id;
	@XmlAttribute
	public Boolean ignoreplacement;
	@XmlAttribute
	public String immediateparent;
	@XmlAttribute
	public Boolean inited;
	@XmlEnum
	public enum initstage {early, normal, late };
	@XmlAttribute
	public String name;
	@XmlAttribute
	public Integer nodeLevel;
	@XmlAttribute
	public String options;
	@XmlAttribute
	public String parent;
	@XmlAttribute
	public String placement;
	@XmlAttribute
	public String styleclass;
	@XmlAttribute
	public String subnodes;
	@XmlAttribute
	public String transition;
	@XmlAttribute
	public String onconstruct;
	@XmlAttribute
	public String ondata;
	@XmlAttribute
	public String oninit; 

}
