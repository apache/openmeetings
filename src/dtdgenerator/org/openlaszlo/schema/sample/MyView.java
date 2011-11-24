package org.openlaszlo.schema.sample;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MyView extends View {
	
	@XmlElement(required = true)
	public String customElement;
	
	@XmlAttribute(required = true)
	public String customAttribute;

}
