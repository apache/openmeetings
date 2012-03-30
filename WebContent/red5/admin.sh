#!/bin/bash

export CLASSPATH=./*:conf:lib/*:webapps/openmeetings/WEB-INF/lib/*:webapps/openmeetings/WEB-INF:webapps/openmeetings/WEB-INF/classes

java -cp $CLASSPATH org.openmeetings.app.Admin $*

