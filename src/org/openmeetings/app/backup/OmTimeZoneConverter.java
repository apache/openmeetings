package org.openmeetings.app.backup;

import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class OmTimeZoneConverter implements Converter<OmTimeZone> {
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	
	public OmTimeZoneConverter() {
		//default constructor is for export
	}
	
	public OmTimeZoneConverter(OmTimeZoneDaoImpl omTimeZoneDaoImpl) {
		this.omTimeZoneDaoImpl = omTimeZoneDaoImpl;
	}
	
	public OmTimeZone read(InputNode node) throws Exception {
		OmTimeZone omTimeZone;
		try {
			omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(node.getValue());
		} catch (Exception e) {
			omTimeZone = new OmTimeZone();
		}

		return omTimeZone;
	}

	public void write(OutputNode node, OmTimeZone value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getJname());
	}
}