set webAppDir=.\dist\red5\webapps\openmeetings\

set RED5_DIR=.\dist\red5

set JAVA_LIBS=.\bin

set JAVA_LIBS=%JAVA_LIBS%;.\lib\junit\junit.jar

set JAVA_LIBS=%JAVA_LIBS%;.\lib\red5_08rc3\red5.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\red5_08rc3\spring-core-2.5.6.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\red5_08rc3\servlet-api-2.5.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\velocity\velocity-1.6-dev.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\velocity\velocity-tools-view-1.3.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\xstream\xstream-SNAPSHOT.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\upload\xmlrpc-client-3.1.2.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\li\upload\xmlrpc-common-3.1.2.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\upload\xmlrpc-common-3.1.2.jar

set JAVA_LIBS=%JAVA_LIBS%;.\lib\slf4j\logback-classic-0.9.14.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\slf4j\logback-core-0.9.14.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\slf4j\slf4j-api-1.5.6.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\slf4j\log4j-over-slf4j-1.5.6.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\slf4j\jcl-over-slf4j-1.5.6.jar

set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\antlr-2.7.6.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\c3p0-0.9.1.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\commons-collections-3.1.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\dom4j-1.6.1.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\hibernate-core-3.5.6-Final.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\javassist-3.12.0.GA.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\jta-1.1.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\openjpa-2.1.0.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\openjpa-all-2.1.0.jar

set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\javassist-3.12.0.GA.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\openjpa\openjpa-all-2.1.0.jar

set JAVA_LIBS=%JAVA_LIBS%;.\lib\database\mysql-connector-java-5.1.15-bin.jar

set JAVA_LIBS=%JAVA_LIBS%;%RED5_DIR%\lib\commons-collections-3.2.1.jar
set JAVA_LIBS=%JAVA_LIBS%;%RED5_DIR%\lib\antlr-3.1.3.jar
set JAVA_LIBS=%JAVA_LIBS%;%RED5_DIR%\lib\servlet-api-2.5.jar
set JAVA_LIBS=%JAVA_LIBS%;.\lib\axis2-1.3\commons-httpclient-3.0.1.jar
set JAVA_LIBS=%JAVA_LIBS%;%RED5_DIR%\lib\javaee-api-5.1.1.jar

set JAVA_LIBS=%JAVA_LIBS%;%RED5_DIR%\webapps\openmeetings\screen\jta.jar

java -classpath %JAVA_LIBS% junit.textui.TestRunner org.openmeetings.CommonTestSuite
