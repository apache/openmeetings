@echo off

if NOT DEFINED RED5_HOME set RED5_HOME=%~dp0

set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n -javaagent:@jrebel.home@\jrebel.jar -Drebel.remoting_plugin=true -Dproject.root=@project.home@
%RED5_HOME%\red5.bat
