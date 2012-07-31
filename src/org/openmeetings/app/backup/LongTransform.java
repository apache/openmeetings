package org.openmeetings.app.backup;

import org.simpleframework.xml.transform.Transform;

public class LongTransform implements Transform<Long>{
	public Long read(String value) throws Exception {
		return OmConverter.getlongValue(value);
	}

	public String write(Long value) throws Exception {
		return "" + value;
	}

}
