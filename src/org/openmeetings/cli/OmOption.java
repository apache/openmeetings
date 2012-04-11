package org.openmeetings.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;

public class OmOption extends Option {
	private static final long serialVersionUID = -544426769898915573L;
	private String group = null;
	private Map<String, Boolean> optional = null;
	private int order = 0;
	private String helpPrefix = "";

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description, boolean optional) throws IllegalArgumentException {
		this(group, 0, opt, longOpt, hasArg, description);
		setOptional(optional);
	}

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description, String optional) throws IllegalArgumentException {
		this(group, 0, opt, longOpt, hasArg, description);
		setOptional(optional);
	}

	public OmOption(String group, int order, String opt, String longOpt, boolean hasArg,
			String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		this.group = group;
		this.setOrder(order);
	}

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description) throws IllegalArgumentException {
		this(group, 0, opt, longOpt, hasArg, description);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isOptional(String group) {
		boolean result = false;
		if (optional != null) {
			String[] grps = group.split(",");
			for(String g : grps) {
				result |= optional.containsKey(g) ? optional.get(g) : false;
			}
		}
		return result;
	}

	public void setOptional(boolean val) {
		setOptional(group);
	}
	
	public void setOptional(String group) {
		String[] grps = group.split(",");
		optional = new HashMap<String, Boolean>(grps.length);
		for(String g : grps) {
			optional.put(g, true);
		}
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getHelpPrefix() {
		return helpPrefix;
	}

	public void setHelpPrefix(StringBuilder helpPrefix) {
		this.helpPrefix = helpPrefix.toString();
	}

	public void setHelpPrefix(String helpPrefix) {
		this.helpPrefix = helpPrefix;
	}
}
