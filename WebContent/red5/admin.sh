#!/bin/bash

export CLASSPATH=.:webapps/openmeetings/WEB-INF/classes:lib/*.jar:webapps/openmeetings/WEB-INF/lib/*.jar

java -cp $CLASSPATH org.openmeetings.app.Admin

