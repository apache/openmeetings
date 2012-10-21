#!/bin/bash

if [ -z "$RED5_HOME" ]; then export RED5_HOME=.; fi

# Debug options
export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n $JAVA_OPTS -javaagent:@jrebel.home@/jrebel.jar -Drebel.remoting_plugin=true -Dproject.root=@project.home@"

# Start Red5
exec $RED5_HOME/red5.sh
