#!/bin/bash
export RED5DIR=$(cd $(dirname "$0"); pwd) 

export CLASSPATH=./*:conf:lib/*:$RED5DIR/webapps/openmeetings/WEB-INF/lib/*:$RED5DIR/webapps/openmeetings/WEB-INF:$RED5DIR/webapps/openmeetings/WEB-INF/classes

java -cp "$CLASSPATH" "-Dred5.home=$RED5DIR" org.openmeetings.app.Admin $*

