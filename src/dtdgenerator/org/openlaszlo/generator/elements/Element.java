package org.openlaszlo.generator.elements;

import java.util.SortedSet;
import java.util.TreeSet;

public class Element {
	
	private SortedSet<String> attributes = new TreeSet<String>();
	private SortedSet<String> childelements = new TreeSet<String>();
	
	public SortedSet<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(SortedSet<String> attributes) {
		this.attributes = attributes;
	}
	public SortedSet<String> getChildelements() {
		return childelements;
	}
	public void setChildelements(SortedSet<String> childelements) {
		this.childelements = childelements;
	}

}
