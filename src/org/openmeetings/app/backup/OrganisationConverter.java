package org.openmeetings.app.backup;

import java.util.Map;

import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class OrganisationConverter extends OmConverter<Organisation> {
	private Organisationmanagement organisationmanagement;
	private Map<Long, Long> idMap;
	
	public OrganisationConverter() {
		//default constructor is for export
	}
	
	public OrganisationConverter(Organisationmanagement organisationmanagement, Map<Long, Long> idMap) {
		this.organisationmanagement = organisationmanagement;
		this.idMap = idMap;
	}
	
	public Organisation read(InputNode node) throws Exception {
		long oldOrgId = getlongValue(node);
		long newId = idMap.containsKey(oldOrgId) ? idMap.get(oldOrgId) : oldOrgId;
		
		return organisationmanagement.getOrganisationByIdBackup(newId);
	}

	public void write(OutputNode node, Organisation value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getOrganisation_id());
	}
}