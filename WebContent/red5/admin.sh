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
export RED5_HOME=$(cd $(dirname "$0"); pwd) 

export CLASSPATH=$RED5_HOME/*:$RED5_HOME/conf:$RED5_HOME/lib/*:$RED5_HOME/webapps/openmeetings/WEB-INF/lib/*:$RED5_HOME/webapps/openmeetings/WEB-INF:$RED5_HOME/webapps/openmeetings/WEB-INF/classes

java -cp "$CLASSPATH" -Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector org.openmeetings.cli.Admin $*

