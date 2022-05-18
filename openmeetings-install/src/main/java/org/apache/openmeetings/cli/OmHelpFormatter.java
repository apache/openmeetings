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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class OmHelpFormatter extends HelpFormatter {
	private static final String GENERAL_OPTION_GROUP = "";
	private int maxPrefixLength = 0;

	@SuppressWarnings("unchecked")
	private static List<OmOption> getReqOptions(Options opts) {
		//suppose we have only 1 group (for now)
		OptionGroup g = ((List<OptionGroup>)opts.getRequiredOptions()).get(0);
		List<OmOption> result = new ArrayList<>();
		for (Option o : g.getOptions()) {
			result.add((OmOption)o);
		}
		Collections.sort(result, (o1, o2) -> o1.getOrder() - o2.getOrder());
		return result;
	}

	private Map<String, List<OmOption>> getOptions(Options opts, int leftPad) {
		final String longOptSeparator = " ";
		final String lpad = createPadding(leftPad);
		final String lpadParam = createPadding(leftPad + 2);
		List<OmOption> reqOptions = getReqOptions(opts);
		Map<String, List<OmOption>> map = new LinkedHashMap<>(reqOptions.size());
		map.put(GENERAL_OPTION_GROUP, new ArrayList<>());
		for (OmOption o : reqOptions) {
			map.put(o.getOpt(), new ArrayList<>());
		}
		for (Option option : opts.getOptions()) {
			OmOption o = (OmOption)option;
			boolean skipOption = map.containsKey(o.getOpt());
			boolean mainOption = skipOption || o.getGroup() == null;

			// first create list containing only <lpad>-a,--aaa where
			// -a is opt and --aaa is long opt; in parallel look for
			// the longest opt string this list will be then used to
			// sort options ascending
			StringBuilder optBuf = new StringBuilder();
			if (o.getOpt() == null) {
				optBuf.append(mainOption ? lpad : lpadParam).append("   ").append(getLongOptPrefix())
						.append(o.getLongOpt());
			} else {
				optBuf.append(mainOption ? lpad : lpadParam).append(getOptPrefix())
						.append(o.getOpt());

				if (o.hasLongOpt()) {
					optBuf.append(',').append(getLongOptPrefix())
							.append(o.getLongOpt());
				}
			}

			if (o.hasArg()) {
				String argName = o.getArgName();
				if (argName != null && argName.length() == 0) {
					// if the option has a blank argname
					optBuf.append(' ');
				} else {
					optBuf.append(o.hasLongOpt() ? longOptSeparator : " ");
					optBuf.append("<")
							.append(argName != null ? o.getArgName()
									: getArgName()).append(">");
				}
			}

			o.setHelpPrefix(optBuf);
			maxPrefixLength = Math.max(optBuf.length(), maxPrefixLength);

			if (skipOption) {
				continue;
			}
			String grp = o.getGroup();
			grp = grp == null ? GENERAL_OPTION_GROUP : grp;
			String[] grps = grp.split(",");
			for(String g : grps) {
				map.get(g).add(o);
			}
		}
		for (Map.Entry<String, List<OmOption>> me : map.entrySet()) {
			final String key = me.getKey();
			List<OmOption> options = me.getValue();
			Collections.sort(options, (o1, o2) -> {
					boolean o1mandatory = !o1.isOptional(key);
					boolean o2mandatory = !o2.isOptional(key);
					int shortNameVal = o1.getOpt() == null ? 1 : -1;
					int mandatoryVal = o1mandatory ? -1 : 1;
					return (o1mandatory && o2mandatory || !o1mandatory && !o2mandatory) ? shortNameVal : mandatoryVal;
				});
			if (opts.hasOption(key)) {
				options.add(0, (OmOption)opts.getOption(key));
			}
		}
		return map;
	}

	private static StringBuilder getReqOptionsString(Options opts) {
		String delim = "";
		StringBuilder result = new StringBuilder();
		for (Option o : getReqOptions(opts)) {
			result.append(delim).append("-").append(o.getOpt());
			delim = "|";
		}
		return result;
	}

	@Override
	protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
		final String dpad = createPadding(descPad);
		final String optional = "(optional) ";

		Map<String, List<OmOption>> optList = getOptions(options, leftPad);

		char[] delimiter = new char[width - 2];
		Arrays.fill(delimiter, '-');

		for (Entry<String, List<OmOption>> me : optList.entrySet()) {
			if (GENERAL_OPTION_GROUP.equals(me.getKey())) {
				sb.append("General options:").append(getNewLine());
			}
			for (OmOption option : me.getValue()) {
				StringBuilder optBuf = new StringBuilder(option.getHelpPrefix());

				if (optBuf.length() < maxPrefixLength) {
					optBuf.append(createPadding(maxPrefixLength - optBuf.length()));
				}

				optBuf.append(dpad);

				int nextLineTabStop = maxPrefixLength + descPad;

				if (option.isOptional(me.getKey())) {
					optBuf.append(optional);
				}
				if (option.getDescription() != null) {
					optBuf.append(option.getDescription());
				}

				renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());

				sb.append(getNewLine());
			}
			sb.append(delimiter).append(getNewLine());
		}
		return sb;
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

	@Override
	public void printUsage(PrintWriter pw, int width, String app, Options opts) {
		pw.println(String.format("usage: %1$s [%2$s] [options]", app, getReqOptionsString(opts)));
	}
}
