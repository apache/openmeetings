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

			FilenameFilter getFilesOnly = new FilenameFilter() {
				public boolean accept(File b, String name) {
					String absPath = b.getAbsolutePath() + File.separatorChar
							+ name;
					File f = new File(absPath);
					return !f.isDirectory();
				}
			};

			String[] files = dir.list(getFilesOnly);

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
