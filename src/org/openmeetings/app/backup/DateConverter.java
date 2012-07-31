package org.openmeetings.app.backup;

import java.util.Date;

import org.openmeetings.utils.math.CalendarPatterns;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class DateConverter implements Converter<Date> {
	public Date read(InputNode node) throws Exception {
		String val = node.getValue();
		return val == null ? new Date() : CalendarPatterns.parseImportDate(val);
	}

	public void write(OutputNode node, Date value) throws Exception {
		node.setAttribute("class", "java.util.Date");
		node.setData(true);
		node.setValue(value == null ? "0" : CalendarPatterns.getExportDate(value));
	}
}