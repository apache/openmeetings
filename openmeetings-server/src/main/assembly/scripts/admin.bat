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
set RED5_HOME=%~dp0
set OM_CONTEXT=openmeetings

set CLASSPATH=%RED5_HOME%\*;%RED5_HOME%\conf;%RED5_HOME%\plugins\*;%RED5_HOME%\lib\*;%RED5_HOME%\webapps\%OM_CONTEXT%\WEB-INF\lib\*;%RED5_HOME%\webapps\%OM_CONTEXT%\WEB-INF;%RED5_HOME%\webapps\%OM_CONTEXT%\WEB-INF\classes

java -cp "%CLASSPATH%" -Dcontext=%OM_CONTEXT% -Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector org.apache.openmeetings.cli.Admin %*

