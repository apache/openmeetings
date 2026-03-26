/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.OptionFormatter;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

public class OmHelpFormatter extends HelpFormatter {
	final static String DELIMITER = "---------";
	private final OmTextHelpAppendable omAppendable;
	private final String shift;

	public OmHelpFormatter(int maxWidth) {
		super(HelpFormatter.builder()
				.setShowSince(false)
				.setOptionFormatBuilder(OptionFormatter.builder().setOptSeparator(","))
				.setHelpAppendable(new OmTextHelpAppendable(maxWidth)));
		omAppendable = (OmTextHelpAppendable)getHelpAppendable();
		shift = " ".repeat(omAppendable.getIndent());
	}

	// copy-pasted from HelpFormatter with customization
	@Override
	public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage)
			throws IOException
	{
		Args.notEmpty(cmdLineSyntax, "cmdLineSyntax");
		if (autoUsage) {
			omAppendable.appendParagraphFormat("%s %s %s", getSyntaxPrefix(), cmdLineSyntax, toSyntaxOptions(options));
		} else {
			omAppendable.appendParagraphFormat("%s %s", getSyntaxPrefix(), cmdLineSyntax);
		}
		if (!Strings.isEmpty(header)) {
			omAppendable.appendParagraph(header);
		}
		omAppendable.appendTable(getTableDefinition(options));
		if (!Strings.isEmpty(footer)) {
			omAppendable.appendParagraph(footer);
		}
	}

	@Override
	public String toSyntaxOptions(Options options) {
		return "["
				+ getReqOptions(options).map(o -> "-" + o.getOpt()).collect(Collectors.joining("|"))
				+ "] [options]";
	}

	private List<String> optionRow(OmOption grp, OmOption o) {
		final OptionFormatter fmt = getOptionFormatBuilder().build(o);
		String val = "";
		String opt = "";
		if (grp != null) {
			val += shift;
			opt = o.isOptional(grp.getOpt()) ? "(optional) " : "";
		}
		if (o.getOpt() == null) {
			val += shift;
		}
		return List.of(
				val + fmt.getBothOpt() + (o.hasArg() ? " " + fmt.getArgName() : "")
				, opt + fmt.getDescription());
	}

	private void addRows(List<List<String>> rows, OmOption grp, Options opts) {
		// add group first, assuming it has no arg by default
		if (grp != null) {
			final OptionFormatter grpFmt = getOptionFormatBuilder().build(grp);
			rows.add(List.of(grpFmt.getBothOpt(), grpFmt.getDescription()));
		}
		opts.getOptions().stream()
				.map(OmOption.class::cast)
				.filter(o -> !o.equals(grp))
				.filter(o -> grp == null ? o.getGroup() == null : o.getGroup() != null && o.getGroup().contains(grp.getOpt()))
				.forEach(o -> rows.add(optionRow(grp, o)));
		// delimiter
		rows.add(List.of(DELIMITER, DELIMITER));
	}

	// copy-pasted from HelpFormatter with `showSince` dropped (due to private)
	// + customization
	private TableDefinition getTableDefinition(Options opts) {
		// setup the rows for the table.
		final List<List<String>> rows = new ArrayList<>();
		// default group
		addRows(rows, null, opts);
		// other groups
		getReqOptions(opts).forEach(g -> addRows(rows, g, opts));

		// set up the base TextStyle for the columns configured for the Option opt and arg values.
		final TextStyle.Builder builder = TextStyle.builder()
				.setAlignment(TextStyle.Alignment.LEFT)
				.setIndent(DEFAULT_LEFT_PAD)
				.setScalable(false);
		// return the TableDefinition with the proper column headers.
		return TableDefinition.from(null
				, List.of(builder.get(), builder.setAlignment(TextStyle.Alignment.LEFT).get())
				, List.of("Option", "Description")
				, rows);
	}

	private static Stream<OmOption> getReqOptions(Options opts) {
		//suppose we have only 1 group (for now)
		var groups = opts.getRequiredOptions();
		if (!groups.isEmpty() && groups.get(0) instanceof OptionGroup g) {
			return g.getOptions().stream()
					.map(OmOption.class::cast);
		}
		return Stream.of();
	}
}
