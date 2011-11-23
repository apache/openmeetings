package org.openlaszlo.generator.elements;

public class ClassAttribute implements Comparable<ClassAttribute> {

	private String name;
	private boolean required;

	public ClassAttribute(String name2, boolean required2) {
		this.name = name2;
		this.required = required2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ClassAttribute) {
			ClassAttribute attr = (ClassAttribute) obj;
			return attr.getName().equals(name);
		}
		
		return false;
	}

	public int compareTo(ClassAttribute attr) {
		return attr.getName().compareTo(name);
	}

}
