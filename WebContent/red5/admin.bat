set RED5_HOME=%~dp0

set CLASSPATH=%RED5_HOME%\*;%RED5_HOME%\conf;%RED5_HOME%\lib\*;%RED5_HOME%\webapps\openmeetings\WEB-INF\lib\*;%RED5_HOME%\webapps\openmeetings\WEB-INF;%RED5_HOME%\webapps\openmeetings\WEB-INF\classes

java -cp "%CLASSPATH%" org.openmeetings.app.Admin %*

