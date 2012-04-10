package org.openmeetings.cli;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class OmHelpFormatter extends HelpFormatter {

	@SuppressWarnings("unchecked")
	private Collection<Option> getReqOptions(Options opts) {
		//suppose we have only 1 group (for now)
		OptionGroup g = ((List<OptionGroup>)opts.getRequiredOptions()).get(0);
		return g.getOptions();
	}
	
	private String getReqOptionsString(Options opts) {
		String delim = "";
		StringBuilder result = new StringBuilder();
		for (Option o : getReqOptions(opts)) {
			result.append(delim).append(o.getOpt());
			delim = "|";
		}
		return result.toString();
	}
	
	@Override
	public void printOptions(PrintWriter pw, int width, Options options,
			int leftPad, int descPad) {
		// TODO Auto-generated method stub
		super.printOptions(pw, width, options, leftPad, descPad);
	}
	
	@Override
	public void printHelp(PrintWriter pw, int width, String cmdLineSyntax,
			String header, Options options, int leftPad, int descPad,
			String footer, boolean autoUsage) {
		if ((cmdLineSyntax == null) || (cmdLineSyntax.length() == 0)) {
			throw new IllegalArgumentException("cmdLineSyntax not provided");
		}

		printUsage(pw, width, cmdLineSyntax, options);

		if ((header != null) && (header.trim().length() > 0)) {
			printWrapped(pw, width, header);
		}

		printOptions(pw, width, options, leftPad, descPad);

		if ((footer != null) && (footer.trim().length() > 0)) {
			printWrapped(pw, width, footer);
		}
	}
    
	public void printUsage(PrintWriter pw, int width, String app, Options opts) {
		pw.println(String.format("usage: %1$s [%2$s] [options]", app, getReqOptionsString(opts)));
	}
}
