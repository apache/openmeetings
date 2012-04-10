#!/bin/bash
export RED5_HOME=$(cd $(dirname "$0"); pwd) 

export CLASSPATH=$RED5_HOME/*:$RED5_HOME/conf:$RED5_HOME/lib/*:$RED5_HOME/webapps/openmeetings/WEB-INF/lib/*:$RED5_HOME/webapps/openmeetings/WEB-INF:$RED5_HOME/webapps/openmeetings/WEB-INF/classes

java -cp "$CLASSPATH" -Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector org.openmeetings.cli.Admin $*

