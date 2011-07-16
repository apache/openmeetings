package org.openmeetings.test.dao.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfiguration {

	/**
	  * 	Default configuration file name we use
	  */

	static public final String DEFAULT_PROPERTIES_FILE_NAME  = "test";
	
	static public final String WEB_APP_PATH_PROPERTIES_NAME = "webAppPath";

	/** configuration file */
    private static final String configurationFilePath = DEFAULT_PROPERTIES_FILE_NAME + ".properties";
	
	private static Properties  props  = null;

    /**
     * return the value of the given property key
     *
     * @param key
     *            key
     *
     * @return
     */
    public static String getProperty(final String key) {
        if (props == null){
            props = loadFromResource(configurationFilePath);
        }
        return props.getProperty(key);
    }


    /**
     *
     * @param resource  resource
     *
     * @return
     */
    private static Properties loadFromResource(final String resource) {

    	props  = new Properties();
        final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
       
        if (stream != null) {
            try {

                try {
                    props.load(stream);
                }
                catch (final IOException ioe) {
                    props = null;
                }
            }
            finally {
                try {
                    stream.close();
                }
                catch (final IOException ex) {
                    props = null;
                }    // swallow
            }
        }
        else {
            props = null;
        }

        return props;
    }
}
