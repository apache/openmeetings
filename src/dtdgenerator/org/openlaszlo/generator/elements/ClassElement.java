package org.openlaszlo.generator.elements;

import java.util.SortedSet;
import java.util.TreeSet;

public class ClassElement {
	
	private SortedSet<ClassAttribute> attributes = new TreeSet<ClassAttribute>();
	private String parentAsString; //It is not sure that the parent is already parsed
	public String getParentAsString() {
		return parentAsString;
	}
	public void setParentAsString(String parentAsString) {
		this.parentAsString = parentAsString;
	}
	private ClassElement parent ;
	
	public SortedSet<ClassAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(SortedSet<ClassAttribute> attributes) {
		this.attributes = attributes;
	}
	public ClassElement getParent() {
		return parent;
	}
	public void setParent(ClassElement parent) {
		this.parent = parent;
	}
	/**
	 * Get all class attributes including the ones from the parent
	 * @return
	 */
	public SortedSet<ClassAttribute> getAllClassAttributes() {
		SortedSet<ClassAttribute> attributes = this.attributes;
		
		if (this.parent != null) {
			attributes.addAll(parent.getAllClassAttributes());
		} else {
			System.err.println("Has no parent "+this.parentAsString);
		}
		
		return attributes;
	}

}
