package org.apache.openmeetings.web.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;

/**
 * Map property values to OpenMeetings labelid's and get the string
 * 
 * @author swagner
 *
 */
public class LabelResourceLoader implements IStringResourceLoader {
	
	private static Map<String,Long> STRING_LABEL_MAPPING = new HashMap<String,Long>();
	
	static {
		STRING_LABEL_MAPPING.put("PagingNavigator.first", 368L);
		STRING_LABEL_MAPPING.put("PagingNavigator.previous", 369L);
		STRING_LABEL_MAPPING.put("PagingNavigator.next", 370L);
		STRING_LABEL_MAPPING.put("PagingNavigator.last", 371L);
	}

	public String loadStringResource(Class<?> clazz, String key, Locale locale,
			String style, String variation) {
		return getStringByPropertyKey(key);
	}

	public String loadStringResource(Component component, String key,
			Locale locale, String style, String variation) {
		return getStringByPropertyKey(key);
	}
	
	private String getStringByPropertyKey(String key) {
		Long labelId = STRING_LABEL_MAPPING.get(key);
		if (labelId == null) 
			return null;
		return WebSession.getString(labelId);
	}

}
