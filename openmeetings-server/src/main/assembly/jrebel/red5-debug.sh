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

# Debug options
export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n $JAVA_OPTS -javaagent:@jrebel.home@/jrebel.jar -Drebel.remoting_plugin=true -Dproject.root=@project.home@"

# Start Red5
exec $RED5_HOME/red5.sh
