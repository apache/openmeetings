package org.openmeetings.app.backup;

import org.simpleframework.xml.transform.Transform;

public class IntegerTransform implements Transform<Integer>{
	public Integer read(String value) throws Exception {
		return OmConverter.getintValue(value, 0);
	}

	public String write(Integer value) throws Exception {
		return "" + value;
	}

}
