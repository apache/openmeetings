REM #############################################
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM #############################################
@echo off
set OM_HOME=%~dp0
set OM_CONTEXT=openmeetings

set CLASSPATH=%OM_HOME%\*;%OM_HOME%\lib\*;%OM_HOME%\webapps\%OM_CONTEXT%\WEB-INF\lib\*;%OM_HOME%\webapps\%OM_CONTEXT%\WEB-INF;%OM_HOME%\webapps\%OM_CONTEXT%\WEB-INF\classes

java -cp "%CLASSPATH%" -Dom_home=%OM_HOME% -Dcontext=%OM_CONTEXT% org.apache.openmeetings.cli.Admin %*

