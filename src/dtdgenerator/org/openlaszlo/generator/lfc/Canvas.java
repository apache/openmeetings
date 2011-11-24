package org.openlaszlo.generator.lfc;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Canvas {
	
//	@XmlElements({
//		@XmlElement(type = View.class)
//	})
//	public List<View> view;
//	
//	@XmlAnyElement
//	   List<View> any;

	@XmlAnyElement(lax=true)
	@XmlElementRefs({
	     @XmlElementRef(type=View.class),
	     @XmlElementRef(type=Node.class)
	   })
	List<Object> others;
	
}
