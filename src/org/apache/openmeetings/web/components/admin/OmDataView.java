package org.apache.openmeetings.web.components.admin;

import org.apache.openmeetings.persistence.beans.OmEntity;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.markup.repeater.data.DataView;

public abstract class OmDataView<T extends OmEntity> extends DataView<T> {
	private static final long serialVersionUID = -6251644410385789409L;
	private OmDataProvider<T> dp;
	
	public OmDataView(String id, OmDataProvider<T> dp) {
		super(id, dp);
		this.dp = dp;
	}
	
	@Override
	public OmDataProvider<T> getDataProvider() {
		return dp;
	}
}
