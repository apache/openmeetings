#!/bin/bash

if [ -z "$RED5_HOME" ]; then export RED5_HOME=.; fi

# Previous option set
export JAVA_OPTS="-Xrs -Xms512M -Xmx1024M -Xss128K -XX:NewSize=256m -XX:SurvivorRatio=16 -XX:MinHeapFreeRatio=20 -XX:+ExplicitGCInvokesConcurrent -XX:+UseConcMarkSweepGC -Djava.net.preferIPv4Stack=true -Xverify:none"

# start Red5
echo "Setting Hi Performance Options"
exec $RED5_HOME/red5.sh >> $RED5_HOME/log/jvm.stdout 2>&1 &
