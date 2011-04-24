@ECHO OFF
REM
REM lzc.bat - Bash script to run laszlo compiler.
REM
REM * R_LZ_COPYRIGHT_BEGIN ***************************************************
REM * Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.         *
REM * Use is subject to license terms.                                       *
REM * R_LZ_COPYRIGHT_END *****************************************************

call "%LPS_HOME%\WEB-INF\lps\server\bin\lzenv.bat"

set CLASSPATH_SAVE=%CLASSPATH%
set CLASSPATH=%LZCP%
"%JAVA_HOME%\bin\java" %JAVA_OPTS% -DLPS_HOME="%LPS_HOME%" org.openlaszlo.media.Main %*
set CLASSPATH=%CLASSPATH_SAVE%
