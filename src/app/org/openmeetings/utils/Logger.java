package org.openmeetings.utils;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;

public class Logger {
    private org.slf4j.Logger log;

    public Logger() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
        log = Red5LoggerFactory.getLogger(ste.getClass(),
                ScopeApplicationAdapter.webAppRootKey);
    }
    
    private String getMethodMessage() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        return "In the method " + ste.getMethodName();
    }

    public void debug(String s, Object o) {
        log.debug(s, o);
    }

    public void debug(Object o) {
        log.debug(getMethodMessage(), o);
    }

    public void error(Object o) {
        log.error(getMethodMessage(), o);
    }
}
