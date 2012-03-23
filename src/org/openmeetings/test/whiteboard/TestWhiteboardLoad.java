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
package org.openmeetings.test.whiteboard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.collections.ComparatorUtils;
import org.junit.Test;

public class TestWhiteboardLoad {

	@Test
	public void test() {

		try {

			File dir = new File(
					"C:/Users/swagner/workspaces/indigo_red6/ROOT/dist/red5/webapps/openmeetings/public/cliparts/math");

			String[] files = dir.list(new FilenameFilter() {
				public boolean accept(File b, String name) {
					String absPath = b.getAbsolutePath() + File.separatorChar
							+ name;
					File f = new File(absPath);
					return !f.isDirectory();
				}
			});

			@SuppressWarnings("unchecked")
			Comparator<String> comparator = ComparatorUtils.naturalComparator();
			Arrays.sort(files, comparator);

			String row = "";
			for (int i = 0; i < files.length; i++) {

				float tModulo20 = i % 20;
				if (tModulo20 == 0) {
					System.out.println(row);
					row = "";
				}

				row += "[" + i + "] " + files[i] + " | ";
			}

		} catch (Exception err) {
			err.printStackTrace();
		}

	}
}
