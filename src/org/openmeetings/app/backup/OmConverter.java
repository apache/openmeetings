package org.openmeetings.app.backup;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;

public abstract class OmConverter<T> implements Converter<T> {
	static long getlongValue(InputNode node) throws Exception {
		return getlongValue(node.getValue());
	}

	static long getlongValue(String value) {
		return getlongValue(value, 0);
	}
	
	static long getlongValue(String value, long def) {
		long result = def;
		try {
			result = Long.valueOf(value).longValue();
		} catch (Exception e) {
			//no op
		}
		return result;
	}

	static int getintValue(String value, int def) {
		int result = def;
		try {
			result = Integer.valueOf(value).intValue();
		} catch (Exception e) {
			//no op
		}
		return result;
	}
}
