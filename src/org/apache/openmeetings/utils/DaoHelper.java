package org.apache.openmeetings.utils;

import org.apache.commons.lang.StringUtils;

public class DaoHelper {

	public static StringBuilder getWhereClause(String search, String alias, String... fields) {
		StringBuilder sb = new StringBuilder();
		getWhereClause(sb, search, alias, fields);
		return sb;
	}
	
	public static void getWhereClause(StringBuilder sb, String search, String alias, String... fields) {
		if (search != null) {
			boolean notEmpty = false;
			String[] searchItems = search.split(" ");
			for (int i = 0; i < searchItems.length; ++i) {
				if (searchItems[i].isEmpty()) {
					continue;
				}
				if (i == 0) {
					notEmpty = true;
					sb.append(" (");
				} else {
					sb.append(" OR ");
				}
				StringBuilder placeholder = new StringBuilder();
				placeholder.append("%").append(StringUtils.lowerCase(searchItems[i])).append("%");
	
				sb.append("(");
				for (int j = 0; j < fields.length; ++j) {
					if (j != 0) {
						sb.append(" OR ");
					}
					sb.append("lower(").append(alias).append(".").append(fields[j]).append(") LIKE '").append(placeholder).append("' ");
				}
				sb.append(")");
			}
			if (notEmpty) {
				sb.append(") ");
			}
		}
	}
}
