keytool -genkey -keystore screeencast.keystore -keyalg rsa -dname "CN=Sebastian Wagner, OU=Technology, O=org, L=pforzheim, ST=, C=DE" -alias screencastAlias
jarsigner -keystore screeencast.keystore commons-collections-3.1.jar screencastAlias 
jarsigner -keystore screeencast.keystore commons-logging-api.jar screencastAlias 
jarsigner -keystore screeencast.keystore commons-logging.jar screencastAlias 
jarsigner -keystore screeencast.keystore quartz-all-1.6.0.jar screencastAlias 
jarsigner -keystore screeencast.keystore jta.jar screencastAlias 
jarsigner -keystore screeencast.keystore screenviewer.jar screencastAlias 
jarsigner -keystore screeencast.keystore kunststoff.jar screencastAlias 