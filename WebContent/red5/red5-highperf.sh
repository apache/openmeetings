#!/bin/bash
# #############################################
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #############################################

if [ -z "$RED5_HOME" ]; then export RED5_HOME=.; fi

# Previous option set
export JAVA_OPTS="-Xrs -Xms512M -Xmx1024M -Xss512K -XX:NewSize=256m -XX:SurvivorRatio=16 -XX:MinHeapFreeRatio=20 -XX:+ExplicitGCInvokesConcurrent -XX:+UseConcMarkSweepGC -Djava.net.preferIPv4Stack=true -Xverify:none"

# start Red5
echo "Setting Hi Performance Options"
exec $RED5_HOME/red5.sh >> $RED5_HOME/log/jvm.stdout 2>&1 &
