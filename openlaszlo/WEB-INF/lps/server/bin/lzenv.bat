@ECHO OFF
REM
REM lzenv.bat - script to set up laszlo env vars
REM
REM * R_LZ_COPYRIGHT_BEGIN ***************************************************
REM * Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.         *
REM * Use is subject to license terms.                                       *
REM * R_LZ_COPYRIGHT_END *****************************************************

set LZCP=%LPS_HOME%\WEB-INF\lps\server\build
for /f "usebackq delims=" %%d in (`dir /s /b "%LPS_HOME%\3rd-party\jars\dev\*.jar"`) do call :add %%~d
for /f "usebackq delims=" %%d in (`dir /s /b "%LPS_HOME%\WEB-INF\lib\*.jar"`) do call :add %%~d
set LZCP=%LZCP%;%ANT_HOME%\lib\junit.jar;%LPS_HOME%\WEB-INF\classes
goto :EOF

:add
set LZCP=%LZCP%;%*
goto :EOF

