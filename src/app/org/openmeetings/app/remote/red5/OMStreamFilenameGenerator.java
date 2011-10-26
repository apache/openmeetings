package org.openmeetings.app.remote.red5;

import org.red5.server.api.IScope;
import org.red5.server.stream.DefaultStreamFilenameGenerator;;

public class OMStreamFilenameGenerator extends DefaultStreamFilenameGenerator {
	
	@Override
	public String generateFilename(IScope scope, String name, GenerationType type) {
		return "streams/hibernate/" + name;
	}
}
