package org.openlaszlo.schema.sample;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class View {

	@XmlElement(required = true)
	public String name;
	
	@XmlElement
	public String onclick;
	
	@XmlAttribute(required = true)
	public String id;
	
	@XmlAttribute
	public boolean visible;
	
	@XmlAttribute
	public int width;
	
	@XmlAttribute
	public int height;
	
}
