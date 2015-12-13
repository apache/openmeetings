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

if [ -z "$RED5_HOME" ]; then 
  export RED5_HOME=`pwd`; 
fi

P=":" # The default classpath separator
OS=`uname`
case "$OS" in
  CYGWIN*|MINGW*) # Windows Cygwin or Windows MinGW
  P=";" # Since these are actually Windows, let Java know
  ;;
  Darwin*)

  ;;
  *)
  # Do nothing
  ;;
esac

echo "Running on " $OS

# JAVA options
# You can set JAVA_OPTS to add additional options if you want
# Set up logging options
LOGGING_OPTS="-Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector -Dcatalina.useNaming=true"
# Set up security options
SECURITY_OPTS="-Djava.security.debug=failure"
if [ -z "$JAVA_OPTS" ]; then
	export JAVA_OPTS="-XX:PermSize=192m -XX:MaxPermSize=512m -XX:+CMSClassUnloadingEnabled"
fi
export JAVA_OPTS="$LOGGING_OPTS $SECURITY_OPTS $JAVA_OPTS"

if [ -z "$RED5_MAINCLASS" ]; then
  export RED5_MAINCLASS=org.red5.server.Bootstrap
fi

# Jython options
JYTHON="-Dpython.home=lib"

for JAVA in "${JAVA_HOME}/bin/java" "${JAVA_HOME}/Home/bin/java" "/usr/bin/java" "/usr/local/bin/java"
do
  if [ -x "$JAVA" ]
  then
    break
  fi
done

if [ ! -x "$JAVA" ]
then
  echo "Unable to locate Java. Please set JAVA_HOME environment variable."
  exit
fi

export RED5_CLASSPATH="${RED5_HOME}/boot.jar${P}${RED5_HOME}/conf${P}${CLASSPATH}"

# start Red5
echo "Starting Red5"
exec "$JAVA" "$JYTHON" -Dred5.root="${RED5_HOME}" $JAVA_OPTS -cp "${RED5_CLASSPATH}" "$RED5_MAINCLASS" $RED5_OPTS
