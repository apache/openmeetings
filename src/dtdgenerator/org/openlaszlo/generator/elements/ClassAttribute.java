package org.openlaszlo.generator.elements;

public class ClassAttribute implements Comparable<ClassAttribute> {

	private String name;
	private boolean required = false;
	private String type;
	private String defaultValue;

	public ClassAttribute(String name2, boolean required2, String type2, String defaultValue2) {
		this.name = name2;
		this.required = required2;
		this.type = type2;
		this.defaultValue = defaultValue2;
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
