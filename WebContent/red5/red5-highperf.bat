@echo off

if NOT DEFINED RED5_HOME set RED5_HOME=%~dp0

set JAVA_OPTS= -Xmx768m -Xms512m -Xmn512m -Xss256k -XX:+AggressiveOpts -XX:+DisableExplicitGC -XX:ParallelGCThreads=4 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:SurvivorRatio=16 -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=31 -Djava.net.preferIPv4Stack=true

echo Running High Performance Red5
start cmd /c "%RED5_HOME%\red5.bat" >> "%RED5_HOME%\log\jvm.stdout"