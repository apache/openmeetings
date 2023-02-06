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
package org.apache.openmeetings.util.site;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.openmeetings.util.XmlExport;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogIndexGenerator {
	private static final Logger log = LoggerFactory.getLogger(BlogIndexGenerator.class);

	private static record Link (String title, String link, LocalDateTime published) {
		private static Link of(String title, String link, String published) {
			if (published == null) {
				return null;
			}
			return new Link(title, link, LocalDateTime.parse(
					published.replace("'", "")
					, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		}

		private boolean isValid() {
			boolean result = !Strings.isEmpty(title) && !Strings.isEmpty(link) && published != null;
			if (!result) {
				log.warn("Trying to add link with NULL values {}", this);
			}
			return result;
		}
	}

	private static void addLink(StringBuilder sb, Link link) {
		sb.append('[').append(link.title()).append("](")
				.append("https://openmeetings.apache.org/blog/").append(link.link()).append(".html)")
				.append(System.lineSeparator())
				.append(System.lineSeparator());
	}

	private static String getPrefixed(String line, String prefix) {
		if (line.startsWith(prefix)) {
			return line.substring(prefix.length()).trim();
		}
		return null;
	}

	private static Link toLink(Path post) {
		if (post.toFile().isDirectory()) {
			return null;
		}
		try {
			List<String> lines = Files.readAllLines(post);
			String title = lines.stream().map(line -> getPrefixed(line, "title:"))
					.filter(Objects::nonNull).findFirst().orElse(null);
			String link = lines.stream().map(line -> getPrefixed(line, "permalink:"))
					.filter(Objects::nonNull).findFirst().orElse(null);
			String published = lines.stream().map(line -> getPrefixed(line, "date:"))
					.filter(Objects::nonNull).findFirst().orElse(null);
			return Link.of(title, link, published);
		} catch (IOException e) {
			log.error("Enexpected error", e);
		}
		return null;
	}

	/**
	 * Required during build `generate-blog-idx-*` goal
	 *
	 * @param args - [0] path to ${project.basedir}/src/site/markdown/blog folder
	 *               [1] path to ${project.build.directory}/generated-site/markdown/blog folder
	 * @throws Exception - in case of any error
	 */
	public static void main(String[] args) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<!--").append(XmlExport.LICENSE).append("-->").append(System.lineSeparator())
				.append("# Apache OpenMeetings blog posts").append(System.lineSeparator())
				.append(System.lineSeparator());
		try (Stream<Path> walk = Files.walk(Paths.get(args[0]))) {
			walk.map(BlogIndexGenerator::toLink)
					.filter(Objects::nonNull)
					.filter(Link::isValid)
					.sorted((link1, link2) -> link1.published().compareTo(link2.published()))
					.forEach(link -> addLink(sb, link));
		}
		Path outDir = Paths.get(args[1]);
		Files.createDirectories(outDir);
		Files.write(outDir.resolve("index.md")
				, sb.toString().getBytes(StandardCharsets.UTF_8)
				, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
