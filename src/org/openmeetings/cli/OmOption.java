package org.openmeetings.cli;

import org.apache.commons.cli.Option;

public class OmOption extends Option {
	private static final long serialVersionUID = -544426769898915573L;
	private String group = null;
	private boolean optional = true;

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		this.setGroup(group);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}
