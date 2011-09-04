@echo off

if NOT DEFINED RED5_HOME set RED5_HOME=%CD%

set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n
%RED5_HOME%\red5.bat
